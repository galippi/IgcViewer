/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcViewer;

import igc.igc;
import java.awt.Color;

import hgt.HgtFile;
import hgt.HgtFileCache;
import utils.Sprintf;
import utils.dbg;
import utils.threadImage;
import igc.IGC_point;
import igc.IgcFiles;

/**
 *
 * @author liptak
 */
public class BaroImage extends threadImage
{
  public BaroImage(java.awt.Component parent, IgcFiles igcFiles)
  {
    super(parent);
    this.igcFiles = igcFiles;
  }
  IgcFiles igcFiles;
  @Override
    protected void Drawing()
  {
    java.awt.Graphics2D g = img.createGraphics();
    g.setBackground(Color.WHITE);
    g.clearRect(0, 0, img.getWidth(), img.getHeight());
    int dT = igcFiles.t_max - igcFiles.t_min;
    int dH = igcFiles.alt_max - igcFiles.alt_min;
    if ((igcFiles.size() != 0) && (dT != 0) && (dH != 0))
    { /* baro(s) shall be painted */
      AltitudeScale = (img.getHeight() - 1) / (double)(dH);
      double TimeScale = (img.getWidth() -1) / (double)(dT);
      if (igcFiles.size() == 1) {
          igc igc = igcFiles.get(0);
          g.setColor(new Color(207, 96, 6)); // brown
          HgtFile hgt = null;
          for (int t = 0; t < igc.size(); t++)
          {
            IGC_point pt = igc.get(t);
            int x1 = (int)((pt.t.t - igcFiles.t_min) * TimeScale + 0.5);
            //int h = -9999;
            if (hgt != null)
                if (!hgt.isIn(pt.lat.val(), pt.lon.val()))
                    hgt = null;
            if (hgt == null)
                hgt = HgtFileCache.get(pt.lat.val(), pt.lon.val(), true);
            if (hgt != null) {
                int h = hgt.get(pt.lat.val(), pt.lon.val());
                int y1 = img.getHeight() - (int)((h - igcFiles.alt_min) * AltitudeScale);
                g.fillRect(x1, y1, 3, 3);
            }
          }
      }
      for (int i = 0; i < igcFiles.size(); i++)
      {
        igc igc = igcFiles.get(i);
        if (igc.size() > 1)
        {
          g.setColor(igc.color);
          int x0 = (int)(((int)igc.getT(0) - igcFiles.t_min) * TimeScale + 0.5);
          int y0 = img.getHeight() - (int)((igc.get(0).Altitude.h - igcFiles.alt_min) * AltitudeScale);
          for (int t = 1; t < igc.size(); t++)
          {
            int x1 = (int)((igc.getT(t) - igcFiles.t_min) * TimeScale + 0.5);
            int y1 = img.getHeight() - (int)((igc.get(t).Altitude.h - igcFiles.alt_min) * AltitudeScale);
            g.drawLine(x0, y0, x1, y1);
            x0 = x1;
            y0 = y1;
          }
        }
      }
      g.setBackground(Color.WHITE);
      g.clearRect(0, 0, 20, img.getHeight());
      g.setColor(Color.BLACK);
      g.drawString(Sprintf.sprintf("%5d", igcFiles.alt_min), 0, img.getHeight() - 10);
      g.drawString(Sprintf.sprintf("%5d", igcFiles.alt_max), 0, 10);
    }else
    {
        if (dbg.get() >= 15)
        {
            g.setColor(Color.RED);
            g.fillOval(50, 50, 70, 30);
        }else
        {
            g.setColor(Color.BLACK);
            g.drawString("No file is loaded", img.getWidth() / 2, img.getHeight() / 2);
        }
    }
    if (dbg.get() >= 11)
    {
        g.setColor(Color.BLACK);
        g.drawString("BaroImage.Drawing", 0, 20);
    }
    g.dispose();
  }
  double AltitudeScale = -1;
  public int getY(int h)
  {
      return img.getHeight() - (int)((h - igcFiles.alt_min) * AltitudeScale);
  }
}
