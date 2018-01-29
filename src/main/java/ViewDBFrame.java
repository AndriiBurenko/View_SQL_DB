import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class ViewDBFrame extends JFrame {
    private JButton previousButton;
    private JButton nextButton;
    private JButton deleteButton;
    private JButton saveButton;
    private JButton newRowsButton;
    private DataPanel dataPanel;
    private Component scrollPane;
    private JComboBox<String> tableNames;
    private Properties props;
    private CachedRowSet crs;


    public ViewDBFrame() {

        tableNames = new JComboBox<String>();
        tableNames.addActionListener(e -> showTable((String) tableNames.getSelectedItem()));
        add(tableNames, BorderLayout.NORTH);

        try {
            readDatabaseProperties();
            try (Connection conn = getConnection()){
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet mrs = meta.getTables(null, null, null, new String[] {"TABLE"});
                while (mrs.next())
                    tableNames.addItem(mrs.getString(3));
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, e);
        } catch (IOException e){
            JOptionPane.showMessageDialog(this, e);
        }

        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        previousButton = new JButton("Previous");
        previousButton.addActionListener(e -> showPreviousRow());
        buttonPanel.add(previousButton);

        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> showNextRow());
        buttonPanel.add(nextButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteRow());
        buttonPanel.add(deleteButton);

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveChanges());
        buttonPanel.add(saveButton);

        newRowsButton = new JButton("Save new row");
        newRowsButton.addActionListener(e -> saveNewRow());
        buttonPanel.add(newRowsButton);
        pack();
    }

    public void showTable(String tableName){
        try {
            try (Connection conn = getConnection()){
                Statement stat = conn.createStatement();
                ResultSet result = stat.executeQuery("SELECT * FROM " + tableName);
                RowSetFactory factory = RowSetProvider.newFactory();
                crs = factory.createCachedRowSet();
                crs.populate(result);
            }

            if (scrollPane != null) remove(scrollPane);
            dataPanel = new DataPanel(crs);
            scrollPane = new JScrollPane(dataPanel);
            add(scrollPane, BorderLayout.CENTER);
            validate();
            showNextRow();
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, e);
//            e.printStackTrace();
        }
    }

    public void showPreviousRow(){
        try {
            if (crs == null || crs.isFirst()) return;
            crs.previous();
            dataPanel.showRow(crs);
        }
        catch (SQLException e){
            for (Throwable t : e)
                t.printStackTrace();
        }
    }

    public void showNextRow(){
        try {
            if (crs == null || crs.isLast()) return;
            crs.next();
            dataPanel.showRow(crs);
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, e);
//            e.printStackTrace();
        }
    }

    public void deleteRow(){
        try {
            try (Connection conn = getConnection()){
                conn.setAutoCommit(false);
                crs.deleteRow();
                crs.acceptChanges(conn);
                if (crs.isAfterLast())
                    if (!crs.last()) crs = null;
                dataPanel.showRow(crs);
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, e);
//            e.printStackTrace();
        }
    }

    public void saveChanges(){
        try {
            try (Connection conn = getConnection()){
                conn.setAutoCommit(false);
                dataPanel.setRow(crs);
                crs.acceptChanges(conn);
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, e);
//            e.printStackTrace();
        }
    }

    public void saveNewRow(){
        try{
            try(Connection conn = getConnection()){
                conn.setAutoCommit(false);
                dataPanel.addRow(crs, conn);
                crs.acceptChanges(conn);
                if (crs.isAfterLast())
                    if (!crs.last()) crs = null;
                dataPanel.showRow(crs);
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, e);
//            e.printStackTrace();
        }
    }

    private void readDatabaseProperties() throws IOException{
        props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("/home/andrii/IdeaProjects/View_SQL_DB/src/main/resources/database.properties"))){
            props.load(in);
        }
        String drivers = props.getProperty("jdbc.drivers");
        if (drivers != null) System.setProperty("jdbc.drivers", drivers);
    }

    private Connection getConnection() throws SQLException{
        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        return DriverManager.getConnection(url, username, password);
    }
}
