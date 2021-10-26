import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSONWriter
{
    private final File file;
    private final DataFrame frame;

    public JSONWriter(File file, DataFrame frame)
    {
        this.file = file;
        this.frame = frame;
    }

    public void saveFile() throws IOException, NullPointerException, IndexOutOfBoundsException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("[");
        writer.newLine();
        int rowCount = frame.getRowCount();
        int columnCount = frame.getColumnCount();
        for (int row = 0; row < rowCount; row++)
        {
            writer.write(" {");
            writer.newLine();
            for (int col = 0; col < columnCount; col++)
            {
                StringBuilder line = new StringBuilder("   \"");
                line.append(frame.getColumnName(col));
                line.append("\": \"");
                line.append(frame.getValueAt(row, col));
                line.append("\"");
                String comma = ",";
                if (col == columnCount-1) { comma = ""; }
                line.append(comma);
                writer.write(line.toString());
                writer.newLine();
            }
            writer.write(" }");
            String comma = ",";
            if (row == rowCount-1) { comma = ""; }
            writer.write(comma);
            writer.newLine();
        }
        writer.write("]");
        writer.newLine();
        writer.close();
    }
}
