package igcViewer;

import igc.IGC_point;
import igc.IgcCursor;
import java.util.ArrayList;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author liptakok
 */
class CompetitionId extends IgcFileTableColumnBase
{
  CompetitionId(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile)
  {
    return igcFile.getCompetitionId();
  }
}

class Pilot extends IgcFileTableColumnBase
{
  Pilot(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile)
  {
    return igcFile.getPilotsName();
  }
}

class GliderId extends IgcFileTableColumnBase
{
  GliderId(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile)
  {
    return igcFile.getGliderId();
  }
}

class GliderType extends IgcFileTableColumnBase
{
  GliderType(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile)
  {
    return igcFile.getGliderType();
  }
}

class TaskColor extends IgcFileTableColumnBase
{
  TaskColor(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile)
  {
    return igcFile.colorTask;
  }
}

class TrackColor extends IgcFileTableColumnBase
{
  TrackColor(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile)
  {
    return igcFile.color;
  }
}

class FileName extends IgcFileTableColumnBase
{
  FileName(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile)
  {
    return igcFile.getFileName();
  }
}

class Altitude extends IgcFileTableColumnDynamicBase
{
  Altitude(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile, int ptIdx)
  {
    return "" + igcFile.getAltitude(ptIdx) + " m";
  }
}

class GroundSpeed extends IgcFileTableColumnDynamicBase
{
  GroundSpeed(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(IgcCursor igcCursor, int fileIdx, igc.igc igcFile, int ptIdx)
  {
    double v;
    if (igcCursor.isAuxValid())
    {
      IGC_point ptRef = igcFile.getIgcPoint(igcFile.getIdx(igcCursor.getTimeAux()));
      IGC_point pt = igcFile.get(ptIdx);
      double distance = ptRef.getDistance(pt);
      long dt = ptRef.t.t - pt.t.t;
      if (dt != 0)
        v = distance / dt;
      else
        v = 0;
    }else
      v = igcFile.getGroundSpeed(ptIdx);
    return "" + (int)(v * 3.6) + " km/h";
  }
}

class Direction extends IgcFileTableColumnDynamicBase
{
  Direction(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile, int ptIdx)
  {
    return (int)(igcFile.getDir(ptIdx) * 180 / Math.PI);
  }
}

class Vario extends IgcFileTableColumnDynamicBase
{
  Vario(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(IgcCursor igcCursor, int fileIdx, igc.igc igcFile, int ptIdx)
  {
    double w;
    if (igcCursor.isAuxValid())
    {
      IGC_point ptRef = igcFile.getIgcPoint(igcFile.getIdx(igcCursor.getTimeAux()));
      IGC_point pt = igcFile.get(ptIdx);
      long dt = ptRef.t.t - pt.t.t;
      int dh = ptRef.Altitude.h - pt.Altitude.h;
      if (dt != 0)
        w = (double)dh / dt;
      else
        w = 0;
    }else
      w = igcFile.getVario(ptIdx);
    return utils.Sprintf.sprintf("%3.1f m/s", w);
  }
}

class Vario30 extends IgcFileTableColumnDynamicBase
{
  Vario30(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile, int ptIdx)
  {
    double w;
    w = igcFile.getVario(ptIdx, 30);
    return utils.Sprintf.sprintf("%3.1f m/s", w);
  }
}

class Distance extends IgcFileTableColumnDynamicBase
{
  Distance(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(IgcCursor igcCursor, int fileIdx, igc.igc igcFile, int ptIdx, int selRow)
  {
    double distance;
    if (igcCursor.isAuxValid())
    {
      IGC_point ptRef = igcFile.getIgcPoint(igcFile.getIdx(igcCursor.getTimeAux()));
      IGC_point pt = igcFile.get(ptIdx);
      distance = ptRef.getDistance(pt);
    }else
    if ((selRow >= 0) && (fileIdx != selRow))
    {
      igc.igc igcFileRef = igcCursor.get(selRow);
      IGC_point ptRef = igcFileRef.getIgcPoint(igcFileRef.getIdx(igcCursor.getTime()));
      IGC_point pt = igcFile.get(ptIdx);
      distance = ptRef.getDistance(pt);
    }else
    {
      distance = -1;
    }
    String distanceStr;
    if (distance > 5000)
      distanceStr = String.format("%.2f", distance / 1000) + " km";
    else
    if (distance > 0)
      distanceStr = String.format("%.0f", distance) + " m";
    else
    {
      distanceStr = "";
    }
    return distanceStr;
  }
}

class LiftToDrag extends IgcFileTableColumnDynamicBase
{
  LiftToDrag(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile, int ptIdx)
  {
    double v = igcFile.getGroundSpeed(ptIdx);
    double w = igcFile.getVario(ptIdx);
    if (Math.abs(w) > 1e-3)
      return String.format("%.1f", (-v / w));
    else
      return "oo";
  }
  @Override
    public Object getValue(IgcCursor igcCursor, int fileIdx, igc.igc igcFile, int ptIdx)
  {
    if (igcCursor.isAuxValid())
    {
      int tAux = igcCursor.getTimeAux();
      IGC_point ptRef = igcFile.getIgcPoint(igcFile.getIdx(tAux));
      IGC_point pt = igcFile.get(ptIdx);
      double distance = ptRef.getDistance(pt);
      int dAltitude = ptRef.Altitude.h - pt.Altitude.h;
      if (dAltitude != 0)
      {
        double ld = distance / dAltitude;
        if (ptRef.t.t > pt.t.t)
            ld = -ld;
        return String.format("%.1f", ld);
      }else
        return "oo";
    }else
      return getValue(igcFile, ptIdx);
  }
}

class LD30 extends IgcFileTableColumnDynamicBase
{
  LD30(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile, int ptIdx)
  {
    double v = igcFile.getGroundSpeed(ptIdx);
    double w = igcFile.getVario(ptIdx, 30);
    if (Math.abs(w) > 1e-3)
      return String.format("%.1f", (-v / w));
    else
      return "oo";
  }
}
        
class TimeDisplay extends IgcFileTableColumnDynamicBase
{
  TimeDisplay(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile, int ptIdx)
  {
    int t = igcFile.getT(ptIdx);
    return String.format("%02d:%02d:%02d", t / 3600, (t / 60) % 60, t % 60);
  }
}

class TimeOffset extends IgcFileTableColumnBase
{
  TimeOffset(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile)
  {
    return igcFile.getTimeOffset() + " s";
  }
  @Override
  public boolean isEditable()
  {
    return true;
  }
}

class DeltaTime extends IgcFileTableColumnDynamicBase
{
  DeltaTime(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(IgcCursor igcCursor, int fileIdx, igc.igc igcFile, int ptIdx, int selRow)
  {
    int timeDiff;
    if (igcCursor.isAuxValid())
    {
      IGC_point ptRef = igcFile.getIgcPoint(igcFile.getIdx(igcCursor.getTimeAux()));
      IGC_point pt = igcFile.get(ptIdx);
      timeDiff = (int)(ptRef.t.t - pt.t.t);
    }else
    if ((selRow >= 0) && (fileIdx != selRow))
    {
      igc.igc igcFileRef = igcCursor.get(selRow);
      IGC_point ptRef = igcFileRef.getIgcPoint(igcFileRef.getIdx(igcCursor.getTime()));
      IGC_point pt = igcFile.get(ptIdx);
      timeDiff = (int)(ptRef.t.t - pt.t.t);
    }else
    {
      timeDiff = 0;
    }
    String timeStr;
    if (timeDiff < 0)
      timeDiff = -timeDiff;
    if (timeDiff > 120)
      timeStr = String.format("%d:%02d", timeDiff / 60, timeDiff % 60);
    else
    if (timeDiff > 0)
      timeStr = String.format("%d", timeDiff) + " s";
    else
    {
      timeStr = "";
    }
    return timeStr;
  }
}

class Date extends IgcFileTableColumnBase
{
  Date(String colName)
  {
    super(colName);
  }
  @Override
  public Object getValue(igc.igc igcFile)
  {
    return igcFile.getDate() + "";
  }
}

public class IgcFileTableColumnArray {
  public IgcFileTableColumnArray()
  {
    columns = new ArrayList<IgcFileTableColumnBase>();
    colIdx = new TreeMap<String, Integer>();
    add(new FileName("File name"));
    add(new CompetitionId("Competition ID"));
    add(new Pilot("Pilot"));
    add(new GliderId("Glider ID"));
    add(new GliderType("Glider type"));
    add(new Altitude("Altitude"));
    add(new GroundSpeed("Ground speed"));
    add(new Direction("Direction"));
    add(new Vario("Vario"));
    add(new TrackColor("Track color"));
    add(new TaskColor("Task color"));
    add(new Distance("Distance"));
    add(new LiftToDrag("L/D"));
    add(new TimeOffset("Time offset"));
    add(new TimeDisplay("Time"));
    add(new LD30("LD30"));
    add(new Vario30("Vario30"));
    add(new DeltaTime("delta time"));
    add(new Date("date"));
  }
  final void add(IgcFileTableColumnBase newCol)
  {
    colIdx.put(newCol.getColName(), Integer.valueOf(columns.size()));
    columns.add(newCol);
  }
  public IgcFileTableColumnBase get(int idx)
  {
    return columns.get(idx);
  }
  public int size()
  {
    return columns.size();
  }
  public int getColIdx(String colName)
  {
    Integer obj = (Integer)colIdx.get(colName);
    if (obj == null)
      return -1;
    else
      return obj.intValue();
  }
  ArrayList<IgcFileTableColumnBase> columns;
  TreeMap<String, Integer> colIdx;
}
