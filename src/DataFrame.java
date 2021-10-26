import java.util.ArrayList;

public class DataFrame
{
    private final ArrayList<Column> columns = new ArrayList<>();


    public void addColumn(String name)
    {
        columns.add(new Column(name));
    }


    public String[] getColumnNames()
    {
        String[] columnNames = new String[columns.size()];
        int i = 0;
        for (Column column : columns)
        {
            columnNames[i] = column.getName();
            i++;
        }
        return columnNames;
    }

    public String getColumnName(int column)
    {
        return columns.get(column).getName();
    }

    private Column getColumn(String columnName)
    {
        for (Column column : columns)
        {
            if (column.getName().equals(columnName))
            {
                return column;
            }
        }
        return null;
    }


    public int getRowCount()
    {
        return columns.get(0).getSize(); // as all columns have the same number of rows
    }


    public int getColumnCount()
    {
        return columns.size();
    }


    public String getValueAt(int row, int column)
    {
        return columns.get(column).getRowValue(row);
    }


    public String getValue(String columnName, int row)
    {
        return getColumn(columnName).getRowValue(row);
    }


    public void putValue(String columnName, int row, String value)
    {
        getColumn(columnName).setRowValue(row, value);
    }


    public void addValue(String columnName, String value)
    {
        getColumn(columnName).addRowValue(value);
    }


    public void addValue(int column, String value)
    {
        columns.get(column).addRowValue(value);
    }
}
