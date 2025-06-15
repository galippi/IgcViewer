/**
 *
 * @author liptakok
 * 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcViewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import utils.dbg;

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

    void setRowHeight(JTable parent, int rowId) {
        // do nothing by default
    }

    public void setParent(OptionDialogRowListHandler optionDialogRowListHandler) {
        parent = optionDialogRowListHandler;
    }

    int id;
    OptionDialogRowListHandler parent;
}

abstract class RowHandlerComplex extends RowHandler implements TableCellRenderer, TableCellEditor
{
    abstract String getName();
    abstract Object getValue();
    abstract void setValue(Object newValue) throws Exception;
    abstract void update();

    public TableCellEditor getCellEditor() { // this has to be overwritten, if the row handler is not a text box based
        return this;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        return component;
    }

    void setComponent(Component _component) {
        component = _component;
    }

    Component component;
}

class PanelFileBrowse extends JPanel
{
    JTextField filename;
    RowHandlerFileBrowse parent;
    PanelFileBrowse(RowHandlerFileBrowse _parent, String defaultValue) {
        parent = _parent;
        add(filename = new JTextField(defaultValue));
        JButton bBrowse = new JButton("Browse");
        add(bBrowse);
        bBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseHandler();
            }
        });
    }

    void browseHandler() {
        dbg.println(9, "PanelFileBrowse.browseHandler");
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(parent.getName());
        parent.setFilter(fc);

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            java.io.File file = fc.getSelectedFile();
            dbg.println(9, "Browse ok " + file.getAbsolutePath() + ".");
            filename.setText(file.getAbsolutePath());
            try {
                parent.set(filename.getText());
            } catch (Exception e) {
                dbg.println(9, "Browse exception e=" + e.toString());
            }
        } else
        {
            dbg.println(9, "Browse cancelled by user.");
        }
    }

    private static final long serialVersionUID = -8707830580091056209L;
}

abstract class RowHandlerFileBrowse extends RowHandlerComplex
{
    RowHandlerFileBrowse() {
        setComponent(new PanelFileBrowse(this, (String)getValue()));
    }

    protected abstract void setFilter(JFileChooser fc);

    public void set(String text) {
        parent.parent.setValueAt(text, getId(), OptionsDialog.colValue);
    }

    @Override
    void setRowHeight(JTable parent, int rowId) {
        parent.setRowHeight(rowId, 40);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        dbg.println(9, "RowHandlerFileBrowse.getTableCellEditorComponent");
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        dbg.println(9, "RowHandlerFileBrowse.getCellEditorValue");
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        dbg.println(9, "RowHandlerFileBrowse.isCellEditable");
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        dbg.println(9, "RowHandlerFileBrowse.shouldSelectCell");
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        dbg.println(9, "RowHandlerFileBrowse.stopCellEditing");
        return false;
    }

    @Override
    public void cancelCellEditing() {
        dbg.println(9, "RowHandlerFileBrowse.getTableCellEditorComponent");
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        dbg.println(9, "RowHandlerFileBrowse.cancelCellEditing");
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        dbg.println(9, "RowHandlerFileBrowse.removeCellEditorListener");
    }
}

class OptionDialogRowListHandler
{
    RowHandler addRow(RowHandler row)
    {
        row.setParent(this);
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

    public void setTable(JTable table) {
        parent = table;
    }

    JTable parent;
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

class RowHandlerXcmFile extends RowHandlerFileBrowse
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

    @Override
    protected void setFilter(JFileChooser fc) {
        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                    "XCM file", "xcm"));
    }

    String newValue = null;
}

class RowHandlerAirSpaceFile extends RowHandlerFileBrowse
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

    @Override
    protected void setFilter(JFileChooser fc) {
        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                    "XCM file", "xcm"));

        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                    "Open air file", "txt"));
    }

    String newValue = null;
}


public class OptionsDialog extends JDialog {
  JTable table;

  OptionDialogRowListHandler odrlh = new OptionDialogRowListHandler();

  //headers for the table
  final String[] columnNames = new String[] {
      "Property name", "Property value"
  };
  final static int colProperty = 0;
  final static int colValue = 1;

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

    odrlh.setTable(table);

    javax.swing.table.TableColumnModel columnModel = table.getColumnModel();
    for (int i = 0; i < columnNames.length; i++)
    {
        TableColumn column = columnModel.getColumn(i);
        //column.setMinWidth(10);
        //column.setMaxWidth(200);
        //column.setWidth(10);
        //column.setPreferredWidth(10);
        column.setResizable(true);
        column.setHeaderValue(columnNames[i]);
    }
    columnModel.getColumn(0).setPreferredWidth(IgcViewerPrefs.get("OptionsDialogCol0W", 100));
    columnModel.getColumn(1).setPreferredWidth(IgcViewerPrefs.get("OptionsDialogCol1W", 200));

    for (int i = 0; i < odrlh.getRowCount(); i++)
    {
        RowHandler row = odrlh.get(i);
        table.setValueAt(row.getName() + ":", i, colProperty);
        table.setValueAt(row.getValue(),      i, colValue);
        row.setRowHeight(table, i);
    }

    table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
    //table.setEditingColumn(0);
    //table.setEditingRow(0);
    table.setMaximumSize(new java.awt.Dimension(1000, 1000));
    table.setMinimumSize(new java.awt.Dimension(100, 100));
    table.setPreferredSize(new java.awt.Dimension(200, 120));

    //add the table to the frame
    if (false) {
        this.add(new JScrollPane(table));
    }else {
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(table);
        this.add(jScrollPane);
    }

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
        javax.swing.table.TableColumnModel columnModel = table.getColumnModel();
        IgcViewerPrefs.put("OptionsDialogCol0W", columnModel.getColumn(0).getWidth());
        IgcViewerPrefs.put("OptionsDialogCol1W", columnModel.getColumn(1).getWidth());

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
