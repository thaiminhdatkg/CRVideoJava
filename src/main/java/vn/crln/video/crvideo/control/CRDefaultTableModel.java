package vn.crln.video.crvideo.control;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CRDefaultTableModel extends DefaultTableModel {
    private boolean defaultEditable = false;
    private List<Boolean> editableColumns = new ArrayList<>();

    public boolean getDefaultEditable() {
        return defaultEditable;
    }
    public void setDefaultEditable(boolean defaultEditable) {
        this.defaultEditable = defaultEditable;
    }

    @Override
    public void addColumn(Object columnName) {
        super.addColumn(columnName);
        editableColumns.add(defaultEditable);
    }

    @Override
    public void addColumn(Object columnName, Vector columnData) {
        super.addColumn(columnName, columnData);
        editableColumns.add(defaultEditable);
    }

    @Override
    public void addColumn(Object columnName, Object[] columnData) {
        super.addColumn(columnName, columnData);
        editableColumns.add(defaultEditable);
    }

    @Override
    public void setColumnCount(int columnCount) {
        super.setColumnCount(columnCount);
        while (columnCount > editableColumns.size()) {
            editableColumns.remove(editableColumns.size() - 1);
        }
        for (int i = editableColumns.size(); i < columnCount; i++) {
            editableColumns.add(defaultEditable);
        }
    }

    public void setColumnEditable(int column, boolean editable) {
        editableColumns.set(column, editable);
    }

    public boolean isColumnEditable(int column) {
        return editableColumns.get(column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return isColumnEditable(column);
    }
}
