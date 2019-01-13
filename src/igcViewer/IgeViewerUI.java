/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcViewer;

import igc.IgcCursor;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import igc.igc;
import igc.IgcFiles;
import utils.dbg;

/**
 *
 * @author liptakok
 */

public class IgeViewerUI extends javax.swing.JFrame {

  IgcFiles igcFiles;
  IgcCursor igcCursor;

    private void m_RecentFileActionPerformed(java.awt.event.ActionEvent evt) {
      dbg.println(9, "m_RecentFileActionPerformed " + evt.toString());
      String val = evt.getActionCommand();
      dbg.dprintf(9, "  val=%s\n", val);
      if (val.charAt(1) == ':')
      {
        int idx = val.charAt(0) - '0';
        String file = val.substring(3);
        dbg.dprintf(9, "  idx=%d file=%s\n", idx, file);
        openIgcFile(file);
      }
    }
    static public IgeViewerUI mainWindow;
    /**
     * Creates new form NumberAdditionUI
     */
    public IgeViewerUI() {
        mainWindow = this;
        igcFiles = new IgcFiles();
        igcCursor = new IgcCursor(igcFiles);

        initComponents();
        igcFileTable.setupTable();
        int nextRecentFile = 0;
        for (int i = 0; i < 10; i++)
        {
          String val = IgcViewerPrefs.getRecentFile(i, "");
          if (!val.isEmpty())
          {
            javax.swing.JMenuItem jMenuItem = new javax.swing.JMenuItem();
            jMenuItem.setText(nextRecentFile + ": " + val);
            nextRecentFile++;
            jMenuItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_RecentFileActionPerformed(evt);
              }
            });
            jMenu3.add(jMenuItem);
          }
        }
        setLocation(IgcViewerPrefs.get("MainWindowX", 0), IgcViewerPrefs.get("MainWindowY", 0));
        setSize(IgcViewerPrefs.get("MainWindowW", 600), IgcViewerPrefs.get("MainWindowH", 400));
        setExtendedState(IgcViewerPrefs.get("MainWindowState", NORMAL));
        jSplitPane1.setDividerLocation(IgcViewerPrefs.get("MainWindowSplitPane1Pos", 100));
        jSplitPane2.setDividerLocation(IgcViewerPrefs.get("MainWindowSplitPane2Pos", 100));
        //pack();
        //dbg.dprintf(9, "mapPanel=%d,%d\n", mapPanel.getWidth(), mapPanel.getHeight());
        //mapPanel.getGeoUtil().setSize(mapPanel.getWidth(), mapPanel.getHeight());
        mapPanel.getGeoUtil().Set(
          IgcViewerPrefs.get("MainWindowGeoPosLonMin", 19.2),
          IgcViewerPrefs.get("MainWindowGeoPosLonMax", 19.4),
          IgcViewerPrefs.get("MainWindowGeoPosLatMin", 47.2),
          IgcViewerPrefs.get("MainWindowGeoPosLatMax", 47.4),
          IgcViewerPrefs.get("MapWindowW", 1024),
          IgcViewerPrefs.get("MapWindowH",  768)
        );
        mapPanel.Repaint();
        instrumentPanels = new InstrumentExternal(igcCursor);
    }
    InstrumentExternal instrumentPanels;
    public void windowClose(java.awt.event.WindowEvent e)
    {
      dbg.println(9, "windowClose");
      IgcViewerPrefs.put("MainWindowX", getX());
      IgcViewerPrefs.put("MainWindowY", getY());
      IgcViewerPrefs.put("MainWindowH", getHeight());
      IgcViewerPrefs.put("MainWindowW", getWidth());
      IgcViewerPrefs.put("MainWindowState", getExtendedState());
      IgcViewerPrefs.put("MainWindowSplitPane1Pos", jSplitPane1.getDividerLocation());
      IgcViewerPrefs.put("MainWindowSplitPane2Pos", jSplitPane2.getDividerLocation());
      IgcViewerPrefs.put("MainWindowGeoPosLonMin", mapPanel.getGeoUtil().lon_min);
      IgcViewerPrefs.put("MainWindowGeoPosLatMin", mapPanel.getGeoUtil().lat_min);
      IgcViewerPrefs.put("MainWindowGeoPosLonMax", mapPanel.getGeoUtil().lon_max);
      IgcViewerPrefs.put("MainWindowGeoPosLatMax", mapPanel.getGeoUtil().lat_max);
      IgcViewerPrefs.put("MapWindowH", mapPanel.getHeight());
      IgcViewerPrefs.put("MapWindowW", mapPanel.getWidth());
      //IgcViewerPrefs.put("MainWindowGeoPosZoom", mapPanel.getGeoUtil().zoom);
      igcFileTable.saveColumnSet();
      dbg.dprintf(9, "mapPanel=%d,%d\n", mapPanel.getWidth(), mapPanel.getHeight());
      System.exit(0);
    }

    void repaintMap()
    {
      //jPanel1.Repaint();
      mapPanel.Repaint();
      dbg.println(9, "repaintMap() size=" + igcFiles.size());
      igcFileTable.updateStaticData();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel1 = mapPanel = new MapPanel(igcCursor);
    jSplitPane2 = new javax.swing.JSplitPane();
    jPanel2 = baroPanel = new BaroPanel(igcCursor);
    jScrollPane1 = new javax.swing.JScrollPane();
    jTable1 = igcFileTable = new IgcFileTable(igcCursor);
    jMenuBar1 = new javax.swing.JMenuBar();
    jMenu1 = new javax.swing.JMenu();
    m_FileOpen = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JPopupMenu.Separator();
    jMenu3 = new javax.swing.JMenu();
    MenuCloseFile = new javax.swing.JMenuItem();
    MenuCloseAll = new javax.swing.JMenuItem();
    m_FileExit = new javax.swing.JMenuItem();
    jMenu2 = new javax.swing.JMenu();
    jMenuOption = new javax.swing.JMenuItem();
    jMenu4 = new javax.swing.JMenu();
    jMenuItem1 = new javax.swing.JMenuItem();
    jMenuItem2 = new javax.swing.JMenuItem();
    jMenu5 = new javax.swing.JMenu();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    jSplitPane1.setDividerLocation(150);
    jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.setResizeWeight(1.0);
    jSplitPane1.setMinimumSize(new java.awt.Dimension(400, 300));

    jPanel1.setMinimumSize(new java.awt.Dimension(95, 97));

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 95, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 97, Short.MAX_VALUE)
    );

    jSplitPane1.setTopComponent(jPanel1);

    jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    jSplitPane2.setTopComponent(jPanel2);
    jPanel2.getAccessibleContext().setAccessibleParent(jSplitPane2);

    jScrollPane1.setMaximumSize(new java.awt.Dimension(32767, 200));
    jScrollPane1.setMinimumSize(new java.awt.Dimension(50, 50));
    jScrollPane1.setNextFocusableComponent(jSplitPane1);

    jTable1.setAutoCreateColumnsFromModel(false);
    jTable1.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Competition ID", "Pilot", "Glider ID", "Glider type", "Altitude", "Ground speed", "Direction", "Vario", "Track color", "Task color", "Distance", "L/D", "Time offset"
      }
    ) {
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false, false, false, false, false, false, false, true
      };

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    jTable1.setEditingColumn(0);
    jTable1.setEditingRow(0);
    jTable1.setMaximumSize(new java.awt.Dimension(1000, 1000));
    jTable1.setMinimumSize(new java.awt.Dimension(100, 100));
    jTable1.setPreferredSize(new java.awt.Dimension(200, 120));
    jScrollPane1.setViewportView(jTable1);

    jSplitPane2.setBottomComponent(jScrollPane1);

    jSplitPane1.setBottomComponent(jSplitPane2);

    jMenu1.setText("File");

    m_FileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, 0));
    m_FileOpen.setText("File open");
    m_FileOpen.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        m_FileOpenActionPerformed(evt);
      }
    });
    jMenu1.add(m_FileOpen);
    jMenu1.add(jSeparator1);

    jMenu3.setText("Recent Files");
    jMenu3.setToolTipText("");
    jMenu3.setActionCommand("recentFiles");
    jMenu1.add(jMenu3);

    MenuCloseFile.setText("Close file");
    jMenu1.add(MenuCloseFile);

    MenuCloseAll.setText("Close all files");
    MenuCloseAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        MenuCloseAllActionPerformed(evt);
      }
    });
    jMenu1.add(MenuCloseAll);

    m_FileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
    m_FileExit.setText("Exit");
    m_FileExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        m_FileExitActionPerformed(evt);
      }
    });
    jMenu1.add(m_FileExit);

    jMenuBar1.add(jMenu1);

    jMenu2.setText("Edit");

    jMenuOption.setText("Options");
    jMenuOption.setActionCommand("OptionsMenu");
    jMenu2.add(jMenuOption);

    jMenuBar1.add(jMenu2);

    jMenu4.setText("View");

    jMenuItem1.setText("Zoom+");
    jMenu4.add(jMenuItem1);

    jMenuItem2.setText("Zoom-");
    jMenu4.add(jMenuItem2);

    jMenuBar1.add(jMenu4);

    jMenu5.setText("Help");
    jMenuBar1.add(jMenu5);

    setJMenuBar(jMenuBar1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void m_FileExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_FileExitActionPerformed
    dbg.println(1, "m_FileExitActionPerformed");
    dispose();
    System.exit(0);
  }//GEN-LAST:event_m_FileExitActionPerformed

  private void openIgcFile(String fileName)
  {
    igc igco = new igc(fileName);
    if (!igco.isValid())
    { /* error message */
      JOptionPane.showMessageDialog(this, "Unable to load file " + fileName);
    }else
    { /* file is loaded -> add it to the list */
      igcCursor.add(igco);
      repaintMap();
      int i;
      for (i = 0; i < 10; i++)
      { /* check the existence of the file on the recent list */
        if (fileName.equals(IgcViewerPrefs.getRecentFile(i, "")))
        { // the file is already on the recent list -> move it to the first position
          break;
        }
      }
      if (i != 0)
      {
        for (; i > 0; i--)
        { /* move recent file lower */
          IgcViewerPrefs.putRecentFile(i, IgcViewerPrefs.getRecentFile(i - 1, ""));
        }
        IgcViewerPrefs.putRecentFile(0, fileName);
      }
    }
  }

  private void m_FileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_FileOpenActionPerformed
    dbg.println(9, "m_FileOpenActionPerformed");
    //Create a file chooser
    final JFileChooser fc = new JFileChooser();
    javax.swing.filechooser.FileNameExtensionFilter filter =
            new javax.swing.filechooser.FileNameExtensionFilter(
        "IGC file", "igc");
    fc.setFileFilter(filter);
    //In response to a button click:
    int returnVal = fc.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      java.io.File file = fc.getSelectedFile();
      //This is where a real application would open the file.
      dbg.println(9, "Opening: " + file.getName() + ".");
      openIgcFile(file.getPath());
    } else
    {
      dbg.println(9, "Open command cancelled by user.");
    }
  }//GEN-LAST:event_m_FileOpenActionPerformed

    private void MenuCloseAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuCloseAllActionPerformed
      igcCursor.reinit();
    }//GEN-LAST:event_MenuCloseAllActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
      dbg.set(9); /* enable debug */
      /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IgeViewerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IgeViewerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IgeViewerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IgeViewerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame = new IgeViewerUI();
                frame.setVisible(true);
            // ------------------------------------------------------------
            // Window listener to close application when Window gets closed
            // ------------------------------------------------------------
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    dbg.println(9, "windowClosing");
                    frame.windowClose(e);
                }
            });
            }
        });
    }
  static IgeViewerUI frame;
  // Variables
  MapPanel mapPanel;
  BaroPanel baroPanel;
  IgcFileTable igcFileTable;

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenuItem MenuCloseAll;
  private javax.swing.JMenuItem MenuCloseFile;
  private javax.swing.JMenu jMenu1;
  private javax.swing.JMenu jMenu2;
  private javax.swing.JMenu jMenu3;
  private javax.swing.JMenu jMenu4;
  private javax.swing.JMenu jMenu5;
  private javax.swing.JMenuBar jMenuBar1;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JMenuItem jMenuOption;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JPopupMenu.Separator jSeparator1;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JSplitPane jSplitPane2;
  private javax.swing.JTable jTable1;
  private javax.swing.JMenuItem m_FileExit;
  private javax.swing.JMenuItem m_FileOpen;
  // End of variables declaration//GEN-END:variables
}
