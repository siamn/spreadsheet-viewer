import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

public class View extends JFrame
{
    private JPanel topPanel, checkboxPanel, tablePanel, bottomPanel, searchPanel, buttonPanel;
    private JButton load, save, graph;
    private JTable table;
    private JTextField search;
    private JComboBox<String> dropdown;
    private TableRowSorter<TableModel> sorter;
    private final Model model;

    private final int width = 1200;
    private final int height = 900;
    private final int tableWidth = width - 50;
    private int widthMin = tableWidth;
    private int widthMax = tableWidth;
    private int widthPreferred = tableWidth;
    private boolean dropdownAdded = false;


    public View(Model model)
    {
        this.model = model;
        setTitle("Dashboard");
        makeView();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width,height);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void makeView()
    {
        makeTablePanel();
        makeCheckboxPanel();
        makeSearchPanel();
        makeButtonPanel();
        makeBottomPanel();
        makeMainPanel();
    }

    private void makeTablePanel()
    {
        tablePanel = new JPanel();
        makeTable(tablePanel);
    }

    private void makeTable(JPanel panel)
    {
        table = new JTable(55, 5); // displays a default empty grid when the program is started
        table.setPreferredScrollableViewportSize(new Dimension(tableWidth, height-150));
        table.setFillsViewportHeight(true);
        panel.add(table);
        panel.add(new JScrollPane(table));
    }

    private void makeCheckboxPanel()
    {
        checkboxPanel = new JPanel(new FlowLayout());
        for (int i = 0; i < table.getColumnCount(); i++)
        {
            String columnName = table.getColumnName(i);
            JCheckBox checkBox = new JCheckBox(columnName, true);
            int column = i;
            checkBox.addActionListener((ActionEvent e) -> showHideColumn(checkBox, column));
            checkboxPanel.add(checkBox);
        }
    }


    private void showHideColumn(JCheckBox checkBox, int column)
    {
        if (checkBox.isSelected())
        {
            showColumn(column);
        }
        else
        {
            hideColumn(column);
        }
    }

    private void hideColumn(int column)
    {
        // store column widths before hiding so that they can be used when revealing columns
        widthMin = table.getColumnModel().getColumn(column).getMinWidth();
        widthMax = table.getColumnModel().getColumn(column).getMaxWidth();
        widthPreferred = table.getColumnModel().getColumn(column).getPreferredWidth();
        widthChecker();

        // set widths to 0 to hide the column
        table.getColumnModel().getColumn(column).setMinWidth(0);
        table.getColumnModel().getColumn(column).setMaxWidth(0);
        table.getColumnModel().getColumn(column).setPreferredWidth(0);
    }

    // to avoid edge case when user resizes column width to 0 and then unchecks the checkbox making other columns
    // which are hidden still have a width of 0 trying to show them
    private void widthChecker()
    {
        if (widthPreferred < 20 || widthMax < 20)
        {
            widthMax = 20;
            widthPreferred = 20;
        }
    }

    private void showColumn(int column)
    {
        table.getColumnModel().getColumn(column).setMinWidth(widthMin);
        table.getColumnModel().getColumn(column).setMaxWidth(widthMax);
        table.getColumnModel().getColumn(column).setPreferredWidth(widthPreferred);
    }


    private void makeSearchPanel()
    {
        JLabel searchLabel;
        searchPanel = new JPanel();
        searchLabel = new JLabel("Search: ");
        searchPanel.add(searchLabel);
        search = new JTextField("",30);
        searchListener(search);
        searchLabel.setLabelFor(search);
        searchPanel.add(search);
    }

    // Access Date: 23.03.21
    // Reference: https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableFilterDemoProject/src/components/TableFilterDemo.java
    private void searchListener(JTextField search)
    {
        search.getDocument().addDocumentListener(
                new DocumentListener()
                {
                    @Override
                    public void insertUpdate(DocumentEvent e)
                    {
                        searchTable();
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e)
                    {
                        searchTable();
                    }
                    @Override
                    public void changedUpdate(DocumentEvent e)
                    {
                        searchTable();
                    }
                });
    }

    private void searchTable()
    {
        RowFilter<TableModel, Object>  searcher;
        try
        {
            searcher = RowFilter.regexFilter(search.getText());
            sorter.setRowFilter(searcher);
        }
        catch (PatternSyntaxException e) { showError("Invalid input detected. Please try again."); }
        catch (NullPointerException e) { showError("Please load a file first and try again."); }
    }

    private void makeButtonPanel()
    {
        buttonPanel = new JPanel(new FlowLayout());
        makeButtons();
        buttonPanel.add(load);
        buttonPanel.add(save);
        buttonPanel.add(graph);
    }

    private void makeButtons()
    {
        makeLoad();
        makeSave();
        makeGraph();
    }

    private void makeLoad()
    {
        load = new JButton("Load");
        load.addActionListener((ActionEvent click) -> loadFile());
    }

    private void makeSave()
    {
        save = new JButton("Save");
        save.addActionListener((ActionEvent click) -> saveFile());
    }

    private void makeGraph()
    {
        graph = new JButton("Graph");
        graph.addActionListener((ActionEvent click) -> drawDropdown());
    }

    private void drawDropdown()
    {
        if (dropdownAdded)
        {
            return;
        }
        try
        {
            createDropdown();
        }
        catch (NullPointerException e)
        {
            showError("Please load a file first!");
        }
    }

    private void createDropdown()
    {
        String[] columns = model.getColNames();
        dropdown = new JComboBox<>(columns);
        dropdown.addActionListener((ActionEvent click) -> getSelectedColumn(click, columns));
        buttonPanel.add(dropdown);
        buttonPanel.revalidate();
        dropdownAdded = true;
    }

    // ethnicity, gender, race, marital and prefix work fairly well in patient files for displaying graphs
    // most other column selections will trigger an error as they have too many keys to display
    private void drawGraph(int column)
    {
        HashMap<String, Integer> data = model.countMatches(column);
        if (data.size() > 30)  // prevent bar graphs with too many keys ("bars") from being drawn
        {
            showError("Column has too many keys. Please select another column.");
            return;
        }
        Graph graph = new Graph(data);
        JFrame frame = new JFrame("Graph");
        frame.add(graph);
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    private void getSelectedColumn(ActionEvent click, String[] columns)
    {
        JComboBox dropdown = (JComboBox) click.getSource();
        String column = (String) dropdown.getSelectedItem();
        int columnIndex = 0;
        for (String columnName : columns)
        {
            if (columnName.equals(column))
            {
                break;
            }
            columnIndex++;
        }
        drawGraph(columnIndex);
    }

    private void makeBottomPanel()
    {
        bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(searchPanel);
        bottomPanel.add(buttonPanel);
    }

    private void makeMainPanel()
    {
        topPanel = new JPanel(new BorderLayout());
        topPanel.add(tablePanel, BorderLayout.CENTER);
        topPanel.add(checkboxPanel, BorderLayout.NORTH);
        topPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(topPanel);
    }

    // Accessed: 23.03.21
    // Reference: https://github.com/UCLComputerScience/COMP0004SwingExamples/blob/master/src/VerySimpleEditor.java
    private void loadFile()
    {
        JFileChooser fileChooser = new JFileChooser("./src");
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.csv, *.json", "csv","json"));
        int selection = fileChooser.showOpenDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            try
            {
                readFile(file);
                rebuildView();
            }
            catch (IOException e) { showError("File could not be loaded. Please select another file"); }
            catch (NullPointerException e) { showError("File is empty. Please select another file."); }
            catch (IndexOutOfBoundsException e) { showError("Invalid syntax. Please select a file with a valid syntax."); }
        }
    }

    // when a new file is loaded, the old checkbox panel and dropdown is removed
    private void rebuildView()
    {
        checkboxPanel.setVisible(false);
        topPanel.remove(checkboxPanel);
        makeCheckboxPanel();
        topPanel.add(checkboxPanel, BorderLayout.NORTH);
        if (dropdownAdded)
        {
            buttonPanel.remove(dropdown);
            dropdownAdded = false;
        }
    }

    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Dashboard", JOptionPane.ERROR_MESSAGE);
    }

    private void readFile(File file) throws IOException
    {
        model.setFrame(file);
        TableModel tableModel = new Controller(model);
        sorter = new TableRowSorter<>(tableModel);
        table.setModel(tableModel);
        table.setRowSorter(sorter);
    }

    private void saveFile()
    {
        JFileChooser fileChooser = new JFileChooser("./src");
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.json", "json"));
        int selection = fileChooser.showSaveDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            try
            {
                model.saveFile(file);
            }
            catch (IOException | IndexOutOfBoundsException e)
            {
                showError("Unable to save the file. Please try again.");
            }
            catch (NullPointerException e)
            {
                showError("File not loaded. Please load a valid first before saving.");
            }
        }
    }
}
