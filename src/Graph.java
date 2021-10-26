import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Graph extends JPanel
{
    HashMap<String, Integer> matches;

    public Graph(HashMap<String, Integer> matches)
    {
        this.matches = matches;
    }

    public void paintComponent(Graphics g)
    {
        final int xStart = 100;
        final int xEnd = 1100;
        final int yStart = 50;
        final int yEnd = 750;
        final int width = xEnd - xStart;
        final int height = yEnd - yStart;
        int max = 0;

        super.paintComponent(g);
        this.setBackground(Color.WHITE);

        g.setColor(Color.BLACK);
        g.drawLine(xStart,yStart,xStart,yEnd);  // draw y axis
        g.drawLine(xStart,yEnd,xEnd,yEnd); // draw x axis
        int length = matches.keySet().size();
        Set<String> keys = matches.keySet();
        for (String key : keys)   // finds max value for use in creating y-axis labels
        {
            int value = matches.get(key);
            if (max < value)
            {
                max = value;
            }
        }

        // draw bars and their labels
        int barWidth = width / length;
        int x = xStart;
        Random random = new Random();
        for (String key : keys)
        {
            int value = matches.get(key);
            float barHeight = ((float) value / (float) max) * height;
            int y = (int) (yStart + (height - barHeight));
            g.setColor(Color.getHSBColor(random.nextFloat(), 1.0f, 0.8f));
            g.fillRect(x, y, barWidth, (int) barHeight);
            g.setColor(Color.BLACK);
            FontMetrics metrics = g.getFontMetrics();
            String label = key;
            if (key.equals(""))
            {
                label = "None";
            }
            int labelWidth = metrics.stringWidth(label);
            if (labelWidth >= barWidth)
            {
                label = labelFitter(label, barWidth, metrics);
                labelWidth = metrics.stringWidth(label);
            }
            int labelXPos = (x + barWidth / 2) - (labelWidth / 2); // centre the label
            g.drawString(label, labelXPos, yEnd+25);
            x += barWidth;
        }

        // draw y-axis marks
        int y = yStart;
        for (int j = 0; j < 10; j++)
        {
            g.setColor(Color.BLACK);
            g.drawLine(xStart-5, y, xStart, y);
            int labelVal = Math.round(((float) max / 10) * (10-j));
            String label = String.valueOf(labelVal);
            FontMetrics metrics = g.getFontMetrics();
            int labelXPos = (xStart-20) - (metrics.stringWidth(label) / 2);
            g.drawString(label, labelXPos, y);
            y += (height / 10);
        }

    }

    // trims the label as it is wider than the bar width
    private String labelFitter(String label, int width, FontMetrics metrics)
    {
        int length = label.length();
        for (int i = length-1; i > 0; i--)
        {
            label = label.substring(0, i);
            int labelWidth = metrics.stringWidth(label);
            if (labelWidth <= width)
            {
                return label;
            }
        }
        return "";
    }

}
