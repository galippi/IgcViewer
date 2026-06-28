package igc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.lang.Math;
import utils.dbg;

/**
 *
 * @author liptakok
 */
/* public*/ class GeoPoint2D
{
  public double lon, lat;
}
        
public class GeoUtil
{
  public GeoUtil(GeoUtil gu)
  {
    Set(gu.lon_min, gu.lon_max, gu.lat_min, gu.lat_max, gu.w, gu.h);
  }

  public GeoUtil()
  {
    Set(19.2, 19.4, 47.2, 47.4, 1024, 768);
  }
  GeoUtil(double _lon_min, double _lon_max, double _lat_min, double _lat_max, int width, int height)
  {
    Set(_lon_min, _lon_max, _lat_min, _lat_max, width, height);
  }

  public void Set(double _lon_min, double _lon_max, double _lat_min, double _lat_max, int width, int height)
  {
    redraw_forced = true;
    double diff = Math.max(_lon_max - _lon_min, 0.01);
    double zoom_x = width / diff;
    diff = Math.max((_lat_max - _lat_min), 0.01);
    double zoom_y = height / diff;
    if (zoom_x < zoom_y)
    {
      zoom = zoom_x;
      lon_min = _lon_min;
      lon_max = _lon_max;
      double lat_delta = ((height / zoom) - (_lat_max - _lat_min)) / 2;
      lat_min = _lat_min - lat_delta;
      lat_max = _lat_max + lat_delta;
    }
    else
    {
      zoom = zoom_y;
      lat_min = _lat_min;
      lat_max = _lat_max;
      double lon_delta = ((width / zoom) - (_lon_max - _lon_min)) / 2;
      lon_min = _lon_min - lon_delta;
      lon_max = _lon_max + lon_delta;
    }
    w = (int)(zoom * (lon_max - lon_min) + 0.5);
    h = (int)(zoom * (lat_max - lat_min) + 0.5);
    dbg.println(11, dbg.d_format("GeoUtil::Set() lon_min=%f lat_min=%f lon_max=%f lat_max=%f zoom=%f w=%d h=%d", lon_min, lat_min, lon_max, lat_max, zoom, w, h));
    redraw = true;
  }
  public void Set(double _lon_min, double _lon_max, double _lat_min, double _lat_max)
  {
    Set(_lon_min, _lon_max, _lat_min, _lat_max, w, h);
  }
  public void setSize(int width, int height)
  {
    Set(lon_min, lon_max, lat_min, lat_max, width, height);
  }
  public void Zoom(int x, int y, double zoom_new)
  {
    Double lon = getPosLon(x), lat = getPosLat(y);
    dbg.println(11, dbg.d_format("Zoom x=%d y=%d lon=%f lat=%f", x, y, lon, lat));
    lon_min = lon - (x / zoom_new);
    lat_max = lat + (y / zoom_new);
    zoom = zoom_new;
    lon_max = lon_min + w / zoom;
    lat_min = lat_max - h / zoom;
    redraw_forced = true;
    dbg.println(11, dbg.d_format("GeoUtil::Zoom lon_min=%f lat_min=%f lon_max=%f lat_max=%f zoom=%f w=%d h=%d", lon_min, lat_min, lon_max, lat_max, zoom, w, h));
  }
  public void Move(int dx, int dy)
  {
    Move(dx, dy, false);
  }
  public void Move(int dx, int dy, boolean forced)
  {
    if (forced)
    {
      lon_min = getPosLon(-x_offs);
      lat_max = getPosLat(-y_offs);
      lon_max = lon_min + w / zoom;
      lat_min = lat_max - h / zoom;
      dbg.dprintf(9, "lon_min=%f lat_min=%f lon_max=%f lat_max=%f zoom=%f w=%d h=%d", lon_min, lat_min, lon_max, lat_max, zoom, w, h);
      x_offs = dx;
      y_offs = dy;
      redraw_forced = true;
    }else
    {
      x_offs += dx;
      y_offs += dy;
      redraw_forced = false;
    }
    dbg.dprintf(9, "GeoUtil::Move dx=%d dy=%d forced=%s x_offs=%d y_offs=%d", dx, dy, forced ? "true" : "false", x_offs, y_offs);
  }
  public void Move(double lon, double lat, double zoom)
  {
    lon_min = lon;
    lat_max = lat;
    lon_max = lon_min + w / zoom;
    lat_min = lat_max - h / zoom;
    dbg.dprintf(9, "Move - double - lon_min=%f lat_min=%f lon_max=%f lat_max=%f zoom=%f w=%d h=%d", lon_min, lat_min, lon_max, lat_max, zoom, w, h);
    x_offs = 0;
    y_offs = 0;
    redraw_forced = true;
  }
  public double getPosLon(int x)
  {
    return lon_min + (x / zoom);
  }
  public double getPosLat(int y)
  {
    return lat_max - (y / zoom);
  }
  public int getPosX(double lon)
  {
    return (int)((lon - lon_min) * zoom + 0.5);
  };
  public int getPosY(double lat)
  {
    return (int)((lat_max - lat) * zoom + 0.5);
  }

  public int getRadius(double radius_m) {
      return (int)(radius_m * zoom * 50);
  }

  public int getPosXOffs(double lon)
  {
    return getPosX(lon) + x_offs;
  }
  public int getPosYOffs(double lat)
  {
    return getPosY(lat) + y_offs;
  }
  public boolean isEqual(GeoUtil gu)
  {
    return (
            (Math.abs(gu.lon_min - lon_min) < 1e-9) &&
            (Math.abs(gu.lon_max - lon_max) < 1e-9) &&
            (Math.abs(gu.lat_min - lat_min) < 1e-9) &&
            (Math.abs(gu.lat_max - lat_max) < 1e-9) &&
            (Math.abs(gu.zoom - zoom) < 1e-9) &&
            // (gu.x_offs == x_offs) &&
            // (gu.y_offs == y_offs) &&
            true
           );
  }
  public String toString()
  {
    return ""+lon_min + ","+lat_min+","+zoom;
  }
  public int getW()
  {
    return w;
  }
  public int getH()
  {
    return h;
  }
  public double lon_min, lon_max, lat_min, lat_max, zoom;
  int w, h;
  public int x_offs = 0, y_offs = 0;
  boolean redraw_forced;
  boolean redraw;
}
