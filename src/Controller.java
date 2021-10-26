import javax.swing.table.AbstractTableModel;

public class Controller extends AbstractTableModel
{
    private final Model model;

    public Controller(Model model)
    {
        this.model = model;
    }

    @Override
    public int getRowCount()
    {
        return model.getRowCount();
    }

    @Override
    public int getColumnCount()
    {
        return model.getColumnCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return model.getValueAt(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column)
    {
        return model.getColumnName(column);
    }
}
