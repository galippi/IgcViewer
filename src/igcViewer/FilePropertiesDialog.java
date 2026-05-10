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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingConstants;

/**
 *
 * @author liptakok
 */
public class FilePropertiesDialog extends JDialog {
  FilePropertiesDialog(JFrame parent, igc.igc file)
  {
    super(parent, Dialog.ModalityType.APPLICATION_MODAL);
    this.setTitle("File details");

    JLabel l2 = new JLabel("File name:");
    l2.setHorizontalAlignment(SwingConstants.LEFT);
    JLabel l3 = new JLabel(file.getPilotsName());
    l3.setHorizontalAlignment(SwingConstants.LEFT);

    JButton b2 = new JButton("Close");
    b2.addKeyListener(new KeyListener() {
        @Override
        public void keyPressed(KeyEvent arg0) {
            System.out.println("FilePropertiesDialog - keyPressed - " + arg0.toString());
            if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                setVisible(false);
            }
        }
        
        @Override
        public void keyReleased(KeyEvent arg0) {
            System.out.println("FilePropertiesDialog - keyReleased - " + arg0.toString());
        }
        
        @Override
        public void keyTyped(KeyEvent arg0) {
            System.out.println("FilePropertiesDialog - keyTyped - " + arg0.toString());
        }
    });
    //b2.setHorizontalAlignment(SwingConstants.CENTER);
    b2.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    });

    Container cp = getContentPane();
    // add label, text field and button one after another into a single column
    cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
    cp.add(l2);
    cp.add(l3);
    b2.setAlignmentX(cp.CENTER_ALIGNMENT);
    cp.add(b2);
    Point pt = parent.getLocationOnScreen();
    int pw = parent.getWidth();
    setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
    this.setMinimumSize(new Dimension(350, 300));
  }
}
