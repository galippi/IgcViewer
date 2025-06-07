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
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import utils.dbg;

/**
 *
 * @author liptakok
 */
abstract class RowHandler
{
    abstract String getName();
    abstract Object getValue();
    abstract void setValue(Object newValue) throws Exception;
    abstract void update();

    public TableCellEditor getCellEditor() { // this has to be overwritten, if the row handler is not a text box based
        return null;
    }

    int getId()
    {
        return id;
    }

    int id;
}

class OptionDialogRowListHandler
{
    RowHandler addRow(RowHandler row)
    {
        row.id = rows.size();
        rows.add(row);
        return row;
    }

    int getRowCount()
    {
        return rows.size();
    }

    RowHandler get(int idx)
    {
        return rows.get(idx);
    }

    Vector<RowHandler> rows = new Vector<>();
}

class RowHandlerDebugLevel extends RowHandler
{
    @Override
    String getName() {
        return "Debug level";
    }

    @Override
    Object getValue() {
        return "" + dbg.get();
    }

    @Override
    void setValue(Object newValue) throws Exception {
        newLevel = Integer.parseInt((String) newValue);
        if (newLevel < 0)
            throw new Exception("RowHandlerDebugLevel.setValue - the new level (" + newLevel + ") shall be > 0!");
    }

    @Override
    void update() {
        if (newLevel < 0)
            throw new Error("RowHandlerDebugLevel.update (newLevel=" + newLevel + ")!");
        IgcViewerPrefs.put("Debug level", newLevel);
        dbg.set(newLevel);
    }

    int newLevel = -1;
}

class RowHandlerSrtmCacheFolder extends RowHandler
{
    @Override
    String getName() {
        return "SRTM cache folder:";
    }

    @Override
    Object getValue() {
        return IgcViewerPrefs.getSrtmCache();
    }

    @Override
    void setValue(Object _newValue) throws Exception {
        newValue = (String)_newValue;
    }

    @Override
    void update() {
        if (newValue != null)
            IgcViewerPrefs.setSrtmCache(newValue, this);
    }

    String newValue = null;
}

class RowHandlerXcmFile extends RowHandler
{
    @Override
    String getName() {
        return "XCM file:";
    }

    @Override
    Object getValue() {
        return IgcViewerPrefs.getXcmFile();
    }

    @Override
    void setValue(Object _newValue) throws Exception {
        newValue = (String)_newValue;
    }

    @Override
    void update() {
        if (newValue != null)
            IgcViewerPrefs.setXcmFile(newValue, this);
    }

    String newValue = null;
}

class RowHandlerAirSpaceFile extends RowHandler
{
    @Override
    String getName() {
        return "Air space file:";
    }

    @Override
    Object getValue() {
        return IgcViewerPrefs.getAirSpaceFile();
    }

    @Override
    void setValue(Object _newValue) throws Exception {
        newValue = (String)_newValue;
    }

    @Override
    void update() {
        if (newValue != null)
            IgcViewerPrefs.setAirSpaceFile(newValue, this);
    }

    String newValue = null;
}


public class OptionsDialog extends JDialog {
  JTextField debugLevel;
  JTextField SRTM_cacheFolder;
  JTextField Xcm_File;
  JTextField airSpace_File;
  JTable table;

  OptionDialogRowListHandler odrlh = new OptionDialogRowListHandler();

  //headers for the table
  final String[] columnNames = new String[] {
      "Property name", "Property value"
  };
  final int colProperty = 0;
  final int colValue = 1;

  OptionsDialog(JFrame parent)
  {
    super(parent, Dialog.ModalityType.APPLICATION_MODAL);
    this.setTitle("Options");

    odrlh.addRow(new RowHandlerDebugLevel());
    odrlh.addRow(new RowHandlerSrtmCacheFolder());
    odrlh.addRow(new RowHandlerXcmFile());
    odrlh.addRow(new RowHandlerAirSpaceFile());

    //create table with data
    table = new JTable(new DefaultTableModel(odrlh.getRowCount(), columnNames.length) {
        @Override
        public boolean isCellEditable(int row, int column)
        {
            return (column == 1);
        }
        private static final long serialVersionUID = 1L;
    }) {
        public TableCellRenderer getCellRenderer(int row, int column) {
            if (column == 1) {
                try {
                    TableCellRenderer cellRenderer = (TableCellRenderer)odrlh.get(row);
                    return cellRenderer;
                }catch (Exception e) {
                    // do nothing
                }
            }
            return super.getCellRenderer(row, column);
        }
        public TableCellEditor getCellEditor(int row, int column) {
            if (column == 1) {
                TableCellEditor ce = odrlh.get(row).getCellEditor();
                if (ce != null)
                    return ce;
            }
            return super.getCellEditor(row, column);
        }
        private static final long serialVersionUID = 2641690847759012960L;
    };

    javax.swing.table.TableColumnModel columnModel = table.getColumnModel();
    for (int i = 0; i < columnNames.length; i++)
    {
        TableColumn column = columnModel.getColumn(i);
        column.setMinWidth(10);
        column.setMaxWidth(200);
        column.setWidth(10);
        column.setResizable(true);
        column.setHeaderValue(columnNames[i]);
    }

    for (int i = 0; i < odrlh.getRowCount(); i++)
    {
        RowHandler row = odrlh.get(i);
        table.setValueAt(row.getName() + ":", i, colProperty);
        table.setValueAt(row.getValue(),      i, colValue);
    }

    //add the table to the frame
    this.add(new JScrollPane(table));

    JLabel l2 = new JLabel("Debug level:");
    l2.setHorizontalAlignment(JTextField.LEFT);
    debugLevel = new JTextField("" + dbg.get(), 5);
    //debugLevel.setSize(100,20);
    debugLevel.setHorizontalAlignment(JTextField.TRAILING);
    //debugLevel.

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
    cp.add(bOkCancel, BorderLayout.SOUTH);

    setLocation(IgcViewerPrefs.get("OptionsDialogX", 0), IgcViewerPrefs.get("OptionsDialogY", 0));
    setSize(IgcViewerPrefs.get("OptionsDialogW", 350), IgcViewerPrefs.get("OptionsDialogH", 300));
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
    try {
        for (int i = 0; i < odrlh.getRowCount(); i++)
        {
            RowHandler row = odrlh.get(i);
            row.setValue(table.getValueAt(i, colValue));
        }

        for (int i = 0; i < odrlh.getRowCount(); i++)
        {
            RowHandler row = odrlh.get(i);
            row.update();
        }

        //IotComPort.reinit();

        setVisible(false);

        IgcViewerPrefs.put("OptionsDialogX", getX());
        IgcViewerPrefs.put("OptionsDialogY", getY());
        IgcViewerPrefs.put("OptionsDialogH", getHeight());
        IgcViewerPrefs.put("OptionsDialogW", getWidth());

    }catch (Exception e)
    {
      dbg.println(2, "OptionDialog.okHandler.Exception="+e.toString());
      JOptionPane.showMessageDialog(this, e.getMessage(),
                                    "Options", JOptionPane.WARNING_MESSAGE);
    }
  }

  void cancelHandler()
  {
    setVisible(false);
    //dispose();
  }

  private static final long serialVersionUID = 4469562424472602766L;
}
