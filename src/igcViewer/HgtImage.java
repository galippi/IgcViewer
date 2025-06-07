package igcViewer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import hgt.HgtFile;
import hgt.HgtFileCache;
import hgt.HgtFileState;
import igc.GeoUtil;
import igc.MapHeightColor;
import utils.dbg;
import utils.threadImage;

public class HgtImage  extends threadImage implements ActionListener {
    public HgtImage(java.awt.Component parent, GeoUtil gu)
    {
      super(parent);
      this.gu = new GeoUtil(gu);
      mapHeightColor = new MapHeightColor();
      HgtFileCache.setCacheFolder(IgcViewerPrefs.getSrtmCache());
      IgcViewerPrefs.setSrtmCacheChangeListener(this);
    }
    MapHeightColor mapHeightColor;

    @Override
      protected void Drawing()
    { /* drawing function */
        java.awt.Graphics2D g = img.createGraphics();
        g.setBackground(new Color(0, true));
        g.clearRect(0, 0, img.getWidth(), img.getHeight());
        Color baseColor = Color.gray;
        g.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 127));
        //g.fillOval(img.getWidth() / 2, img.getHeight() / 2, img.getWidth() / 2 - 5, img.getHeight() / 2 - 5);
        g.setColor(Color.BLUE);
        g.drawString("HgtImage!", 40, 300);
        g.dispose();
        boolean allFilesAreLoaded = false;
        while(allFilesAreLoaded == false)
        {
            allFilesAreLoaded = true;
            int lat = (int)gu.lat_min;
            while(lat < gu.lat_max)
            {
                int lon = (int)gu.lon_min;
                while(lon < gu.lon_max)
                {
                    if (isCancelled())
                        return;
                    HgtFile hgtFile = HgtFileCache.get(lat, lon, false);
                    if (hgtFile != null)
                    {
                        if (hgtFile.loadAsync() == HgtFileState.HgtFileLoaded)
                        {
                            g = img.createGraphics();
                            int x0 = gu.getPosX(lon);
                            int y1 = gu.getPosY(lat);
                            int x1 = gu.getPosX(lon + 1);
                            int y0 = gu.getPosY(lat + 1);
                            int dx = x1 - x0;
                            int dy = y1 - y0;
                            int xStart = (x0 < 0 ? 0 : x0);
                            int xEnd = (x1 >= img.getWidth() ? img.getWidth() - 1 : x1);
                            int yStart = (y0 < 0 ? 0 : y0);
                            int yEnd = (y1 >= img.getHeight() ? img.getHeight() - 1 : y1);
                            for (int y = yStart; y < yEnd; y++)
                            {
                                double pointLat = lat + (y1 - y) / (double)dy;
                                for(int x = xStart; x < xEnd; x++)
                                {
                                    double pointLon = lon + (x - x0) / (double)dx;
                                    Color c = mapHeightColor.get(hgtFile.get(pointLat, pointLon));
                                    g.setColor(c);
                                    g.drawLine(x, y, x, y);
                                }
                            }
                            g.dispose();
                            //BufferedImage tmpImg = img.getScaledInstance(img.getWidth(null), img.getHeight(null), Image.SCALE_DEFAULT);
                            //BufferedImage tmpImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
                            //java.awt.Graphics2D g2d = tmpImg.createGraphics();
                            //g2d.drawImage(img, 0, 0, null);
                            //g2d.dispose();
                            //setImg(tmpImg);
                            setImgTmp(img);
                        }else
                        {
                            if (hgtFile.getState() != HgtFileState.HgtFileError)
                                allFilesAreLoaded = false;
                        }
                    }
                    lon++;
                }
                lat++;
            }
            if (!allFilesAreLoaded)
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    dbg.println(1, "HgtImage - sleep exception e=" + e.toString());
                }
        }
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
        // hgtFileCache is changed
        HgtFileCache.setCacheFolder(IgcViewerPrefs.getSrtmCache());
        repaint();
    }

    GeoUtil gu;
    //HgtFileCache hgtFileCache = new HgtFileCache(IgcViewerPrefs.getSrtmCache());
}
