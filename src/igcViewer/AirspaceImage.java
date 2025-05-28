/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcViewer;

import igc.GeoUtil;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import utils.dbg;
import utils.threadImage;
import xcm.airspace.AirSpaces;

/**
 *
 * @author liptakok
 */
public class AirspaceImage extends threadImage implements ActionListener
{
  public AirspaceImage(java.awt.Component parent, GeoUtil gu)
  {
    super(parent);
    this.gu = new GeoUtil(gu);
    airSpaces = new AirSpaces();
    IgcViewerPrefs.setAirSpaceFileChangeListener(this);
  }
  @Override
    protected void Drawing()
  { /* drawing function */
    java.awt.Graphics2D g = img.createGraphics();
    g.setBackground(new Color(0, true));
    g.clearRect(0, 0, img.getWidth(), img.getHeight());
    Color baseColor = Color.gray;
    g.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 127));
    //g.fillOval(img.getWidth() / 2, img.getHeight() / 2, img.getWidth() / 2 - 5, img.getHeight() / 2 - 5);
    g.setColor(Color.cyan);
    g.drawString("AirspaceImage!", 40, 100);
    airSpaces.draw(g, gu);
    g.dispose();
}
  public void setGeoUtil(GeoUtil gu)
  {
    if (!this.gu.isEqual(gu))
    {
      this.gu = new GeoUtil(gu);
      setImage(this.gu.getW(), this.gu.getH());
      repaint();
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
      // AirSpace file is changed
      String filename = IgcViewerPrefs.getAirSpaceFile();
      //try
      {
        airSpaces = new AirSpaces(filename);
        repaint();
      //}catch (IOException e1)
      //{
        //dbg.dprintf(1, "Error: unable to open/load AirSpace file \"%s\" (e1=%s)!", filename, e1.getMessage());
        //airSpaces = null;
      }
  }

  GeoUtil gu;
  AirSpaces airSpaces;
}
