/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcViewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import utils.dbg;

/**
 *
 * @author liptakok
 */
public class OptionsDialog extends JDialog {
  JTextField debugLevel;
  JTextField SRTM_cacheFolder;
  JTextField Xcm_File;
  JTextField airSpace_File;

  OptionsDialog(JFrame parent)
  {
    super(parent, Dialog.ModalityType.APPLICATION_MODAL);
    this.setTitle("Options");

    JLabel l2 = new JLabel("Debug level:");
    l2.setHorizontalAlignment(JTextField.LEFT);
    debugLevel = new JTextField("" + dbg.get(), 5);
    //debugLevel.setSize(100,20);
    debugLevel.setHorizontalAlignment(JTextField.TRAILING);
    //debugLevel.

    JPanel jpDebugLevel = new JPanel();
    //jpDebugLevel.setLayout(new GroupLayout(jpDebugLevel));
    jpDebugLevel.add(l2);
    jpDebugLevel.add(debugLevel);

    // SRTM cache
    SRTM_cacheFolder = new JTextField(IgcViewerPrefs.getSrtmCache());
    JPanel jpSrtm = new JPanel();
    jpSrtm.add(new JLabel("SRTM cache folder:"));
    jpSrtm.add(SRTM_cacheFolder);

    // XCM file
    Xcm_File = new JTextField(IgcViewerPrefs.getXcmFile());
    JPanel jpXcm = new JPanel();
    jpXcm.add(new JLabel("XCM file:"));
    jpXcm.add(Xcm_File);
    JButton bXcmBrowse = new JButton("Browse");
    jpXcm.add(bXcmBrowse);

    // Air space file
    airSpace_File = new JTextField(IgcViewerPrefs.getAirSpaceFile());
    JPanel jpAirSpace = new JPanel();
    jpAirSpace.add(new JLabel("Air space file:"));
    jpAirSpace.add(airSpace_File);
    JButton bAirSpaceBrowse = new JButton("Browse");
    jpAirSpace.add(bAirSpaceBrowse);

    JButton bOk = new JButton("Ok");
    //b2.setHorizontalAlignment(SwingConstants.CENTER);
    bOk.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            okHandler();
        }
    });
    JButton bCancel = new JButton("Cancel");
    //b2.setHorizontalAlignment(SwingConstants.CENTER);
    bCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            cancelHandler();
        }
    });
    JPanel bOkCancel = new JPanel();
    bOkCancel.add(bOk);
    bOkCancel.add(bCancel);

    Container cp = getContentPane();
    // add label, text field and button one after another into a single column
    cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
    cp.add(jpDebugLevel);
    cp.add(jpSrtm);
    cp.add(jpXcm);
    cp.add(jpAirSpace);
    cp.add(bOkCancel, BorderLayout.SOUTH);

    Point pt = parent.getLocationOnScreen();
    int pw = parent.getWidth();
    setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
    this.setMinimumSize(new Dimension(350, 300));
    ActionListener escListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            cancelHandler();
        }
    };
    getRootPane().registerKeyboardAction(escListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
  }
  void okHandler()
  {
    boolean closable = true;
    try {
      int level = Integer.parseInt(debugLevel.getText());
      IgcViewerPrefs.put("Debug level", level);
      dbg.set(level);
    }catch (NumberFormatException e)
    {
      dbg.println(2, "OptionDialog.okHandler.NumberFormatException="+e.toString());
      closable = false;
    }
    if (closable)
    {
        IgcViewerPrefs.setSrtmCache(SRTM_cacheFolder.getText(), this);
        IgcViewerPrefs.setXcmFile(Xcm_File.getText(), this);
        IgcViewerPrefs.setAirSpaceFile(airSpace_File.getText(), this);
        setVisible(false);
    }
  }
  void cancelHandler()
  {
    setVisible(false);
    //dispose();
  }

  private static final long serialVersionUID = 4469562424472602766L;
}
