import javax.sql.RowSet;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class DataPanel extends JPanel {
    private java.util.List<JTextField> fields;
    private java.util.List<JTextField> fieldsNR;

    public DataPanel(RowSet rs) throws SQLException{
        fields = new ArrayList<>();
        fieldsNR = new ArrayList<>();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.top = 5;
        gbc.insets.left = 5;
        gbc.insets.bottom = 5;

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

        for (int i = 1; i <= rsmd.getColumnCount(); ++i){
            gbc.gridy = rsmd.getColumnCount() + i;
            String columnName = rsmd.getColumnLabel(i);
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.EAST;
            add(new JLabel("new " + columnName), gbc);

            int columnWidth = rsmd.getColumnDisplaySize(i);
            JTextField tb = new JTextField(columnWidth);
            if (!rsmd.getColumnClassName(i).equals("java.lang.String"))
                tb.setEditable(false);
            fieldsNR.add(tb);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            if (i == 1) {
                gbc.insets.top = 15;
                add(tb, gbc);
                gbc.insets.top = 5;
            } else
                add(tb, gbc);
        }
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

    public void addRow(ResultSet rs, Connection connection) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        String tableName = rsmd.getTableName(1);
        String exprCount = "? ";
        for (int i = 1; i <= rsmd.getColumnCount() - 1; ++i) {
            exprCount += ",? ";
        }

        PreparedStatement prepStat = connection
                .prepareStatement("INSERT INTO " + tableName + " VALUES (" + exprCount + ");");

        for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
            JTextField tb = fieldsNR.get(i - 1);
            String prepExpr = tb.getText();
            if (tb.getText().equals(""))
                JOptionPane.showMessageDialog(this, "The field " + i + " is empty!");
            else
                prepStat.setString(i, prepExpr);
        }
        prepStat.executeUpdate();
    }
}
