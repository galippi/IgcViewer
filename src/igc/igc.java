package igc;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gabor Liptak 2016
 */
import java.io.*;
import java.util.ArrayList;

import igc.IGC_point;
import utils.dbg;

class IGC_FileException extends Exception
{
  public IGC_FileException(String msg)
  {
    super(msg);
  }
}
/*public*/ class String_ref
{
  String var;
  public String_ref(String var)
  {
    this.var = var;
  }
  public void set(String new_val)
  {
    var = new_val;
  }
}

/*public*/ class IGC_Points extends ArrayList
{
  public IGC_Points()
  {
    super(1000);
  }
  public IGC_point get(int idx)
  {
    return (IGC_point)super.get(idx);
  }
}

public class igc {
  boolean valid;
  String fileName;
  String_ref PilotsName;
  String_ref competitionId;
  String_ref GliderType;
  String_ref GliderId;
  String_ref DateStr;
  IGC_Points IGC_points;
  public IGC_Time t_min, t_max;
  public IGC_Coordinate lon_min, lon_max;
  public IGC_Coordinate lat_min, lat_max;
  public IGC_Altitude alt_min, alt_max;
  public ArrayList<TaskPoint> TaskPoints;
  public java.awt.Color color;
  public java.awt.Color colorTask;
  int timeOffsetSec;

  public void Reinit()
  {
    valid = false;
    fileName = null;
    PilotsName = new String_ref("");
    competitionId = new String_ref("");
    GliderType = new String_ref("");
    GliderId = new String_ref("");
    DateStr = new String_ref("");
    IGC_points.clear();
    TaskPoints.clear();
    timeOffsetSec = 0;
  }
  public int size()
  {
    return IGC_points.size();
  }
  public IGC_point get(int idx)
  {
    return IGC_points.get(idx);
  }
  public int getIdx(int t)
  {
    return t_2_idx(new IGC_Time(t));
  }
  public String getFileName()
  {
    return fileName;
  }
  public String getPilotsName()
  {
    return PilotsName.var;
  }
  public String getCompetitionId()
  {
    return competitionId.var;
  }
  public String getGliderType()
  {
    return GliderType.var;
  }
  public String getGliderId()
  {
    return GliderId.var;
  }
  public String getDate()
  {
    return DateStr.var;
  }

  boolean keyword(String line, String keyword, String var)
  {
    return keyword(line, keyword, new String_ref(var));
  }
  boolean keyword(String line, String keyword, String_ref var)
  {
    int len = keyword.length();
    //if (line.startsWith(keyword))
    if ((line.length() >= len) && (line.substring(0, len).equalsIgnoreCase(keyword)))
    {
      var.set(line.substring(len).trim());
      return true;
    }
    return false;
  }
  
  int str_to_int16(String str)
  {
    return Integer.parseInt(str);
  }
  
  IGC_Altitude str_to_int32(String str)
  {
    return new IGC_Altitude(Integer.parseInt(str));
  }

  IGC_Coordinate ToDouble(String str, int int_len)
  {
    return new IGC_Coordinate(
           str_to_int16(str.substring(0, int_len)) + 
           str_to_int16(str.substring(int_len, int_len+2))/60.0 + 
           str_to_int16(str.substring(int_len+2, int_len+2+3))/(60.0*1000));
  }

  public void read(String file)
  {
    dbg.println(9, "igc.read(" + file + ")");
    Reinit();
    fileName = file;
    try
    {
      BufferedReader fin = new BufferedReader(new FileReader(file));
      String line;
      boolean FirstTaskRecord = true;
      while ((line = fin.readLine()) != null)
      {
        dbg.println(19, "  " + line);
        if (line.charAt(0) == 'H')
        { /* H-record - not yet processed */
            if (keyword(line, "HFPLTPILOT:", PilotsName)) ;
            else
            if (keyword(line, "HFPLTPilotincharge:", PilotsName)) ;
            else
            if (keyword(line, "HFGTYGLIDERTYPE:", GliderType)) ;
            else
            if (keyword(line, "HFGIDGLIDERID:", GliderId)) ;
            else
            if (keyword(line, "HFDTE", DateStr)) ;
            else
            if (keyword(line, "HFCIDCompetitionID:", competitionId)) ;
            else
            {
              dbg.println(9, "igc - Not processed H-record in line " + line + "!");
            }
        }else
        if ((line.charAt(0) == 'B') && (line.length() >= 30))
        { /* B-record - enough long */
          /* B1053294737060N01908217EA001480014813009302 */
          /* B1053334737102N01908126EA001620016212032305 */
          /* 105333 : hhmmss */
          /* 34737102N  :  North 34.737102 */
          /* 01908126E : East 019.08126 */
          /* 00162: height 162m */
          //DEBUG_MSG(9, d_format("c_igc_file::Load B record %s", buf));
          IGC_point point_data = new IGC_point();
          point_data.t = new IGC_Time(
                         str_to_int16(line.substring(1,1+2)) * 3600 +
                         str_to_int16(line.substring(3, 3+2)) * 60 +
                         str_to_int16(line.substring(5, 5+2)) );
          point_data.lat = ToDouble(line.substring(7, 7+7), 2);
          if (line.charAt(14) == 'S')
            point_data.lat = point_data.lat.neg();
          else if (line.charAt(14) == 'N') /* do nothing */ ;
          else
          {
            throw new IGC_FileException("Error in line (latitude): " + line);
            //dbg.println(1, "Error in line (latitude): " + line);
            //Reinit();
            //return 2;
          }
          point_data.lon = ToDouble(line.substring(15, 15+8), 3);
          if (line.charAt(23) == 'W')
            point_data.lon = point_data.lon.neg();
          else if (line.charAt(23) == 'E') /* do nothing */ ;
          else
          {
            throw new IGC_FileException("Error in line (longitudinal): " + line);
            //dbg.println(1, "Error in line (longitudinal): " + line);
            //Reinit();
            //return 2;
          }
          point_data.Altitude = str_to_int32(line.substring(25, 25+5));
          dbg.println(21, line + dbg.d_format(": lat=%lf, lon=%lf alt=%d", point_data.lat.val(), point_data.lon.val(), point_data.Altitude.val()));
          IGC_points.add(point_data);
          if (IGC_points.size() == 1)
          { /* first point -> init the limits of the file */
            t_min = new IGC_Time(point_data.t);
            t_max = new IGC_Time(point_data.t);
            lon_min = lon_max = point_data.lon;
            lat_min = lat_max = point_data.lat;
            alt_min = new IGC_Altitude(point_data.Altitude);
            alt_max = new IGC_Altitude(point_data.Altitude);
            
          }else
          { /* not the first point -> update the limits of the file */
            t_min.min(point_data.t);
            t_max.max(point_data.t);
            lon_min = lon_min.min(point_data.lon);
            lon_max = lon_max.max(point_data.lon);
            lat_min = lat_min.min(point_data.lat);
            lat_max = lat_max.max(point_data.lat);
            alt_min.min(point_data.Altitude);
            alt_max.max(point_data.Altitude);
          }
        }else
        if ((line.charAt(0) == 'C') && (line.length() >= 18))
        { /* task declaration */
          /* C4726318N01854577ETOROKBALINT */
          if (FirstTaskRecord)
          { /* skip it */
            if (line.substring(0, 18).equalsIgnoreCase("C0000000N00000000E"))
              FirstTaskRecord = false;
            else
                dbg.dprintf(9, "igc skipped task line: %s\n", line);
          }else
          {
            IGC_Coordinate lat = ToDouble(line.substring(1, 1+7), 2);
            IGC_Coordinate lon = ToDouble(line.substring(9, 9+8), 3);
            if (line.charAt(8) == 'S') lat = lat.neg();
            else if (line.charAt(8) == 'N') /* do nothing */ ;
            else
            {
              throw new IGC_FileException("Error in task line (latitude): " + line);
              //Reinit();
              //return 2;
            }
            if (line.charAt(17) == 'W') lon = lon.neg();
            else if (line.charAt(17) == 'E') /* do nothing */ ;
            else
            {
              throw new IGC_FileException("Error in task line (longitude): " + line);
              //Reinit();
              //return 2;
            }
            if ((lon.abs() > 1e-9) || (lat.abs() > 1e-9))
            {
              dbg.dprintf(9, "TaskPoint lon=%lf lat=%lf\n", lon, lat);
              TaskPoint taskPoint = new TaskPoint(line.substring(18), lon, lat);
              TaskPoints.add(taskPoint);
            }
          }
        }else
        if (line.charAt(0) == 'A')
        { /* A-record - not yet processed */
        }else
        if (line.charAt(0) == 'I')
        { /* I-record - not yet processed */
        }else
        if (line.charAt(0) == 'E')
        { /* E-record - not yet processed */
        }else
        if (line.charAt(0) == 'F')
        { /* F-record - not yet processed */
        }else
        if (line.charAt(0) == 'G')
        { /* G-record - not yet processed */
        }else
        if (line.charAt(0) == 'L')
        { /* L-record - not yet processed */
        }else
        if (line.charAt(0) == 'J')
        { /* J-record - not yet processed */
        }else
        if (line.charAt(0) == 'K')
        { /* K-record - not yet processed */
        }else
        {
          if (!line.trim().isEmpty())
            throw new IGC_FileException("Error in line (invalid record): " + line);
        };
      }
      fin.close();
      valid = true;
    }catch (IOException e)
    {
      Reinit();
      dbg.println(1, "ERROR: unable to open file \"" + file + "\"!");
    }catch (IGC_FileException e)
    {
      Reinit();
      dbg.println(1, "ERROR: in file \"" + file + "\" in line \"" + e.getMessage() + "\"!");
    }
  }
  final java.awt.Color defaultColors[] = {java.awt.Color.BLUE, java.awt.Color.RED, java.awt.Color.ORANGE};
  static int defColIdx = 0;
  public igc()
  {
    IGC_points = new IGC_Points();
    TaskPoints = new ArrayList();
    color = defaultColors[defColIdx++];
    if (defColIdx >= defaultColors.length)
      defColIdx = 0;
    colorTask = java.awt.Color.GREEN;

    Reinit();
  }
  public igc(String file)
  {
    this();
    read(file);
  }
  public void setTimeOffset(int offset)
  {
    t_min.t = t_min.t - timeOffsetSec;
    t_max.t = t_max.t - timeOffsetSec;
    timeOffsetSec = offset;
    t_min.t = t_min.t + timeOffsetSec;
    t_max.t = t_max.t + timeOffsetSec;
  }
  public int getTimeOffset()
  {
    return timeOffsetSec;
  }
  public int getT(int idx)
  {
    int t = (int)IGC_points.get(idx).t.t + timeOffsetSec;
    return t;
  }
  int t_2_idx(IGC_Time t)
  {
    t.t = t.t - timeOffsetSec;
    int num = IGC_points.size();
    if (num <= 0)
    {
      dbg.println(1, "igc.t_2_idx: Error - array is empty!");
      return -1;
    }
    if (t.isSmaller(t_min))
    {
      return 0;
    }
    if (t.isLargerOrEqual(t_max))
    {
      return num - 1;
    }
    int i_low = 0, i_high = num - 1;
    while ((i_high - i_low) > 1)
    {
      int i = ((i_high - i_low) / 2) + i_low;
      if (t.isSmaller(IGC_points.get(i).t))
      {
        i_high = i;
      }else
      {
        i_low = i;
      }
    }
    return i_low;
  }
  int GetPoint(int pos, int offset)
  {
    if (offset > 0)
    {
      if ((pos + offset) < size())
      {
        pos += offset;
      }else
      {
        pos = size() - 1;
      }
    }else
    { /* offset < 0) */
      if (pos >= -offset)
      {
        pos += offset;
      }else
      {
        pos = 0;
      }
    }
    return pos;
  }
  public boolean isValid()
  {
    return valid;
  }
  int getOtherPointIdx(int idx)
  {
    if ((idx < 0) && (idx >= size()))
      return -1; /* no other point exists */
    int idxOther = idx - 1;
    if (idxOther < 0)
    {
      idxOther = idx + 1;
      if (idxOther >= size())
      {
        return -1; /* no other point exists */
      }
    }
    return idxOther;
  }
  public double getDir(int idx)
  {
    if ((idx < 0) && (idx >= size()))
      return 0.0; /* direction is not calculable -> return an invalid value */
    int idxOther = getOtherPointIdx(idx);
    if (idxOther < 0)
    {
      return 0.0; /* direction is not calculable -> return an invalid value */
    }
    return IGC_points.get(idxOther).getDir(IGC_points.get(idx));
  }
  public int getAltitude(int idx)
  {
    return IGC_points.get(idx).Altitude.h;
  }
  public double getVario(int idx)
  {
    int idxOther = getOtherPointIdx(idx);
    if (idxOther < 0)
    {
      return 0.0; /* direction is not calculable -> return an invalid value */
    }
    IGC_point pt = IGC_points.get(idx);
    IGC_point ptOther = IGC_points.get(idxOther);
    int dh = pt.Altitude.h - ptOther.Altitude.h;
    return (double)dh/(pt.t.t - ptOther.t.t);
  }
  public double getVario(int idx, int dt)
  {
    int idx0 = idx;
    int idx1 = idx;
    IGC_point pt0 = null;
    IGC_point pt1;
    while(true)
    {
        boolean changed = false;
        if (idx0 != 0)
        {
            idx0--;
            changed = true;
        }
        idx1++;
        if (idx1 >= size())
            idx1--;
        else
            changed = true;
        if (changed)
        {
            pt0 = IGC_points.get(idx0);
            pt1 = IGC_points.get(idx1);
            if ((pt1.t.t - pt0.t.t) > dt)
                break;
        }
    }
    if (pt0 == null)
    {
      return 0.0; /* vario is not calculable -> return an invalid value */
    }
    int dh = pt1.Altitude.h - pt0.Altitude.h;
    return (double)dh/(pt1.t.t - pt0.t.t);
  }
  public double getGroundSpeed(int idx)
  {
    final int SpeedCalcOffset = 5;
    int idx0 = GetPoint(idx, -SpeedCalcOffset);
    int idx1 = GetPoint(idx, +SpeedCalcOffset);
    if (idx0 != idx1)
    {
      IGC_point pt0 = IGC_points.get(idx0);
      IGC_point pt1 = IGC_points.get(idx1);
      long dt = pt1.t.t - pt0.t.t;
      if (dt != 0) return new GeoPoint(pt0).getDistance(new GeoPoint(pt1)) / dt;
      else         return 0;
    }else
    {
      return 0;
    }
  }
  public int getGroundSpeed_km_per_h(int idx)
  {
    return (int)(getGroundSpeed(idx) * 3.6);
  }
  public IGC_point getIgcPoint(int idx)
  {
    return IGC_points.get(idx);
  }
}
