import javax.sql.RowSet;
import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class DataPanel extends JPanel {
    private java.util.List<JTextField> fields;
    JTextField entry_text = new JTextField(30);

    public DataPanel(RowSet rs) throws SQLException{
        fields = new ArrayList<>();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); ++i){
            gbc.gridy = i-1;
            String columnName = rsmd.getColumnLabel(i);
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.EAST;
            add(new JLabel(columnName), gbc);

            int columnWidth = rsmd.getColumnDisplaySize(i);
            JTextField tb = new JTextField(columnWidth);
            if (!rsmd.getColumnClassName(i).equals("java.lang.String"))
                tb.setEditable(false);
            fields.add(tb);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            add(tb, gbc);
        }
        int clmCount = rsmd.getColumnCount();
        gbc.gridx = 0;
        gbc.gridy = clmCount;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("new entry"), gbc);


        gbc.gridx = 1;
        add(entry_text, gbc);
    }

    public void showRow(ResultSet rs) throws SQLException{
        for (int i = 1; i <= fields.size(); ++i){
            String field = rs == null ? "" : rs.getString(i);
            JTextField tb = fields.get(i - 1);
            tb.setText(field);
        }
    }

    public void setRow(RowSet rs) throws SQLException{
        for (int i = 1; i <= fields.size(); ++i){
            String field = rs.getString(i);
            JTextField tb = fields.get(i - 1);
            if (!field.equals(tb.getText()))
                rs.updateString(i, tb.getText());
        }
        rs.updateRow();
    }

    public String addRow() throws SQLException{
        String newRow = entry_text.getText();
        return newRow;
    }
}
