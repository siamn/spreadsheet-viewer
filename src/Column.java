import java.util.ArrayList;

public class Column
{
    private String name;
    private final ArrayList<String> rows = new ArrayList<>();

    public Column(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getSize()
    {
        return rows.size();
    }

    public String getRowValue(int index)
    {
        return rows.get(index);
    }

    public void setRowValue(int index, String value)
    {
        rows.set(index, value);
    }

    public void addRowValue(String value)
    {
        rows.add(value);
    }

}
