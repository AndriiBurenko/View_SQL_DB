import javax.swing.*;
import java.awt.*;

public class ViewDB {
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new ViewDBFrame();
                frame.setTitle("ViewDB");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}

//    INSERT INTO Authors VALUES ('BREE', 'Brook', 'Fred P.');
