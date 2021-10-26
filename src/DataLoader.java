import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader
{
    private final File file;

    public DataLoader(File file)
    {
        this.file = file;
    }

    public DataFrame readFile() throws IOException
    {
        String line;
        DataFrame frame = new DataFrame();
        BufferedReader csvFile = new BufferedReader(new FileReader(file));
        line = csvFile.readLine();
        String[] columns = line.split(",");
        for (String column : columns)
        {
            frame.addColumn(column);
        }
        while ((line = csvFile.readLine()) != null)
        {
            String[] values = line.split(",");
            int i = 0;
            for (String value : values)
            {
                frame.addValue(columns[i], value);
                i++;
            }
            int diff = columns.length - values.length;
            if (diff > 0)
            {
                for (int j = i; j < i + diff; j++)
                {
                    frame.addValue(columns[j], ""); // add empty strings so that all columns have the same number of rows
                }
            }
        }
        csvFile.close();
        return frame;
    }

}
