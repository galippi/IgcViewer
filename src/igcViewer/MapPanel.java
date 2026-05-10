/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcViewer;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
//import javafx.scene.input.ZoomEvent;
//import oracle.adf.view.faces.bi.event.ZoomEvent;
        
import igc.GeoUtil;
import igc.IGC_point;
import igc.IgcCursor;
import igc.IgcFiles;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import hgt.HgtFileCache;
import utils.dbg;

/**
 *
 * @author liptakok
 */
class EventHandler<T>
{
  
}

class ZoomEvent
{
  
}

class RepainterMapPanel extends igc.Repainter {
  MapPanel parent;
  RepainterMapPanel(MapPanel parent) {
    this.parent = parent;
  }
  @Override
    public void repaint(boolean forced)
  {
    parent.repaint(forced);
  }
}

public class MapPanel extends javax.swing.JPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1118843037333193588L;
	RepainterMapPanel repainter;
    IgcFiles igcFiles;
    IgcCursor igcCursor;
    int igcFileCnt = 0;
    MapImage map;
    AirspaceImage airspaceImage;
    HgtImage hgtImage;
    void addEventHandler(EventHandler ev)
    {
      
    }
    public MapPanel(IgcCursor igcCursor)
    {
      super();
      repainter = new RepainterMapPanel(this);
      this.igcCursor = igcCursor;
      igcCursor.set(repainter);
      igcFiles = igcCursor.igcFiles;
      gu = new GeoUtil();
      ctr = 0;
      igc = new igcImage(this, this.igcFiles);
      hgtImage = new HgtImage(this, gu);
      map = new MapImage(this, gu);
      airspaceImage = new AirspaceImage(this, gu);
      //Register for mouse-wheel events on the map area.
      addMouseWheelListener(new MouseWheelListener() {
        public void mouseWheelMoved(MouseWheelEvent e) {
          mouseWheelMovedHandler(e);
        }
      });
      //Register for mouse-wheel events on the map area.
      addMouseMotionListener(new MouseMotionListener() {
        public void mouseMoved(MouseEvent e) {
          mouseHandler(e);
        }
        public void mouseDragged(MouseEvent e) {
          mouseHandler(e);
        }
      });      //Register for mouse events on the map area.
      addMouseListener(new MouseListener() {
        public void mouseMoved(MouseEvent e) {
          mouseHandler(e);
        }
        public void mouseClicked(MouseEvent e) {
          mouseHandler(e);
        }
        public void mousePressed(MouseEvent e) {
          mouseHandler(e);
        }
        public void mouseReleased(MouseEvent e) {
          mouseHandler(e);
        }
        public void mouseEntered(MouseEvent e) {
          mouseHandler(e);
        }
        public void mouseExited(MouseEvent e) {
          mouseHandler(e);
        }
      });
      addEventHandler(new EventHandler<ZoomEvent>() {
        public void zoomIn(ZoomEvent e) {
            zoomHandler(e);
        }
      });
    }
    public void mouseWheelMovedHandler(MouseWheelEvent e) {
          dbg.println(9, "mouseWheelMovedHandler="+e.getWheelRotation() + " x=" + e.getX() + " y=" + e.getY());
          double zoom_new = (e.getWheelRotation() < 0) ? (gu.zoom * zoom_factor) : (gu.zoom / zoom_factor);
          if (((zoom_new > 40) || (e.getWheelRotation() < 0)) &&
              ((zoom_new < 13000) || (e.getWheelRotation() > 0)))
          { // limit the zooming
            gu.Zoom(e.getX(), e.getY(), zoom_new);
            repaint();
          }
    }
    public void zoomHandler(ZoomEvent e) {
        
    }
    public void mouseHandler(MouseEvent e) {
      dbg.println(19, "mouseHandler "+e.toString()+" x=" + e.getX() + " y=" + e.getY() + " button=" + e.getButton());
      {
          double lon = gu.getPosLon(e.getX());
          double lat = gu.getPosLat(this.getHeight() - e.getY());
          dbg.println(19, "pos x=" + e.getX() + " y=" + e.getY() + " lon=" + lon + " lat=" + lat + " h=" + HgtFileCache.get(lat,  lon));
      }
      if (e.getID() == e.MOUSE_PRESSED)
      {
        if ((!m_capture) && (e.getButton() == e.BUTTON1))
        {
          m_capture = true;
          //CaptureMouse();
          x_start = e.getX();
          y_start = e.getY();
        }
      }else
      if ((e.getID() == e.MOUSE_MOVED) || (e.getID() == e.MOUSE_DRAGGED))
      {
        if (m_capture)
        {
          int dx, dy;
          dx = e.getX() - x_start;
          dy = e.getY() - y_start;
          if ((dx != 0) || (dy != 0))
          {
            //x_pos += dx;
            //y_pos += dy;
            x_start = e.getX();
            y_start = e.getY();
            gu.Move(dx, dy);
            //AutoPos = false;
            repaint();
          }
          dbg.dprintf(9, "IGCMapCanvas::OnMouse(EVT_MOTION x_pos=%d y_pos=%d)\n", gu.x_offs, gu.y_offs);
        }
      }else
      if (e.getID() == e.MOUSE_RELEASED)
      {
        if (m_capture)
        {
          //ReleaseMouse();
          m_capture = false;
          if ((gu.x_offs != 0) || (gu.y_offs != 0))
          {
            gu.Move(0, 0, true);
            //x_pos = 0;
            //y_pos = 0;
            repaint();
          }
        }
      }else
      { // not used event
      }
    }
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        dbg.println(9, "MapPanel - paintComponent");
        dbg.dprintf(21, "MapPanel - paintComponent(%d, %d)\n", gu.x_offs, gu.y_offs);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        ctr++;
        gu.setSize(getWidth(), getHeight());
        hgtImage.setGeoUtil(gu);
        BufferedImage img = hgtImage.getImage();
        if (img != null)
        {
          dbg.dprintf(21, "MapPanel - paintComponent hgtImage(%d, %d)\n", gu.x_offs, gu.y_offs);
          g.drawImage(img, gu.x_offs, gu.y_offs, null);
        }
        map.setGeoUtil(gu);
        if (map.isReady())
        {
          dbg.dprintf(21, "MapPanel - paintComponent(%d, %d)\n", gu.x_offs, gu.y_offs);
          g.drawImage(map.getImage(), gu.x_offs, gu.y_offs, null);
        }
        airspaceImage.setGeoUtil(gu);
        if (airspaceImage.isReady())
        {
          dbg.dprintf(21, "MapPanel - airspaceImage - paintComponent(%d, %d)\n", gu.x_offs, gu.y_offs);
          g.drawImage(airspaceImage.getImage(), gu.x_offs, gu.y_offs, null);
        }
        igc.setGeoUtil(gu);
        if (igc.setImage(getWidth(), getHeight()))
          igc.repaint();
        if (igc.isReady())
        {
          g.drawImage(igc.getImage(), gu.x_offs, gu.y_offs, null);
        }
        if (igcCursor.getTime() >= 0)
        {
          for (int i = 0; i < igcCursor.size(); i++)
          {
            igc.igc igcFile = igcCursor.igcFiles.get(i);
            drawAircraft(g, igcFile, igcCursor.getTime(), igcFile.color);
            drawAircraft(g, igcFile, igcCursor.getTimeAux(), Color.GRAY);
          }
        }
        if (dbg.get(13))
        {
            g.setColor(Color.BLACK);
            g.drawString("mapPanel ctr=" + ctr + " size=" + igcFiles.size() + " zoom=" + gu.zoom, 5, 10);
            g.drawString("gu.x_offs=" + gu.x_offs + " gu.y_offs=" + gu.y_offs, 5, 20);
        }
    }
    public void drawAircraft(java.awt.Graphics g, igc.igc igcFile, int t, Color c)
    {
      if (t < 0)
        return;
      g.setColor(c);
      int idx = igcFile.getIdx(t);
      IGC_point pt = igcFile.get(idx);
      dbg.println(9, "DrawAircraft idx=" + idx + 
                      " lon="+pt.lon.val()+" lat="+pt.lat.val());
      int x = gu.getPosXOffs(pt.lon.val());
      int y = gu.getPosYOffs(pt.lat.val());
      double dir = igcFile.getDir(idx);
      drawAircraft(g, x, y, dir);
    }
    public void drawAircraft(java.awt.Graphics g, int x, int y, double dir)
    {
      double sin_dir = Math.sin(dir);
      double cos_dir = Math.cos(dir);

      final int plane_len_per_2 = 10;
      //pen.SetColour(255, 0, 0);
      //dc.SetPen(pen);
      ItemDraw(g, x, y, sin_dir, cos_dir, 0, plane_len_per_2, false);

      //pen.SetColour(0, 255, 0);
      //dc.SetPen(pen);
      final int wing_len_per_2 = (int)(plane_len_per_2 * 1.0);
      final int wing_pos_per_2 = (int)(plane_len_per_2 * 0.4);
      ItemDraw(g, x, y, sin_dir, cos_dir, wing_pos_per_2, wing_len_per_2, true);

      //pen.SetColour(255, 255, 0);
      //dc.SetPen(pen);
      final int tail_len_per_2 = (int)(plane_len_per_2 * 0.4);
      final int tail_pos_per_2 = (int)(plane_len_per_2 * -0.7);
      ItemDraw(g, x, y, sin_dir, cos_dir, tail_pos_per_2, tail_len_per_2, true);
    }
    void ItemDraw(java.awt.Graphics g, int x, int y, double sin_dir, double cos_dir, int ItemPos_per_2, int ItemLen_per_2, boolean ItemDir_Orto)
    {
      int dx, dy;
      dx = (int)(ItemPos_per_2 * sin_dir);
      dy = (int)(ItemPos_per_2 * cos_dir);
      int x_item, y_item;
      x_item = x + dx;
      y_item = y - dy;
      if (ItemDir_Orto)
      {
        dx =   (int)(ItemLen_per_2 * cos_dir);
        dy =   (int)(ItemLen_per_2 * sin_dir);
      }else
      {
        dx = - (int)(ItemLen_per_2 * sin_dir);
        dy =   (int)(ItemLen_per_2 * cos_dir);
      }
      int x0, y0, x1, y1;
      x0 = x_item - dx; x1 = x_item + dx;
      y0 = y_item - dy; y1 = y_item + dy;
      g.drawLine(x0, y0, x1, y1);
    }
    public void Repaint()
    {
      gu.setSize(getWidth(), getHeight());
      if ((igcFileCnt != igcFiles.size()) && (igcFiles.size() != 0))
      {
        gu.Set(igcFiles.lon_min, igcFiles.lon_max, igcFiles.lat_min, igcFiles.lat_max, getWidth(), getHeight());
        igcFileCnt = igcFiles.size();
      }
      igc.repaint();
    }
    public void repaint(boolean forced)
    {
      if (forced)
        Repaint();
      repaint();
    }
    public GeoUtil getGeoUtil()
    {
      return gu;
    }
    int ctr;
    igcImage igc;
    GeoUtil gu;
    double zoom_factor = 1.3;
    boolean m_capture = false;
    int x_start, y_start;
    //int x_pos = 0, y_pos = 0;
}
