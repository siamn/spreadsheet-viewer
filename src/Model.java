import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Model
{
    private DataFrame frame;

    public void setFrame(File file) throws IOException
    {
        String name = file.getName();
        String extension = name.substring(name.length()-4);
        if (extension.equals(".csv"))
        {
            frame = new DataLoader(file).readFile();
        }
        else if (extension.equals("json"))
        {
            frame = new JSONReader(file).readFile();
        }
        else
        {
            throw new IOException();
        }
    }

    public void saveFile(File file) throws  IOException, NullPointerException, IndexOutOfBoundsException
    {
        new JSONWriter(file, frame).saveFile();
    }

    public HashMap<String, Integer> countMatches(int column)
    {
        HashMap<String, Integer> matches = new HashMap<>();
        for (int i = 0; i < frame.getRowCount(); i++)
        {
            String word = frame.getValueAt(i, column);
            matches.merge(word, 1, Integer::sum);
        }
        return matches;
    }


    public String[] getColNames()
    {
        return frame.getColumnNames();
    }

    public String getValueAt(int row, int column)
    {
        return frame.getValueAt(row, column);
    }

    public int getRowCount()
    {
        return frame.getRowCount();
    }

    public int getColumnCount()
    {
        return frame.getColumnCount();
    }

    public String getColumnName(int column)
    {
        return frame.getColumnName(column);
    }


}
