import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JSONReader
{
    private final File file;
    private BufferedReader jsonFile;
    public DataFrame frame = new DataFrame();
    private String line;
    private int pass = 0;
    private int columns = 0;

    public JSONReader(File file)
    {
        this.file = file;
    }

    public DataFrame readFile() throws IOException
    {
        jsonFile = new BufferedReader(new FileReader(file));
        while ((line = jsonFile.readLine()) != null)
        {
            line = line.replaceAll("\\s+", "");
            if (line.equals("{"))
            {
                addRow();
            }
        }
        jsonFile.close();
        return frame;
    }

    private void addRow() throws IOException
    {
        int column = 0;
        while (!((line = jsonFile.readLine().replaceAll("[\\s+,\"]","")).equals("}")))
        {
            String[] columnRow = line.split(":");
            if (pass == 0)
            {
                frame.addColumn(columnRow[0]);
                columns++;
            }
            switch (columnRow.length)
            {
                case 1 -> frame.addValue(column, "");
                case 2 -> frame.addValue(column, columnRow[1]);
                default -> throw new IndexOutOfBoundsException();
            }
            column++;
        }
        if (columns != column)
        {
            throw new IndexOutOfBoundsException();
        }
        pass++;
    }

}
