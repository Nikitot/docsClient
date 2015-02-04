import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.*;

public class AddDocsTab extends JDialog {
    public JPanel contentPane;
    public JTable table1;
    public JTextArea textArea1;
    private JButton addUserButton;
    private JButton editUserButton;
    private JButton removeUserButton;

    public AddDocsTab() {
        setContentPane(contentPane);
        setModal(true);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //TODO
            }
        });

        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        editUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        removeUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }


    public static void main(String[] args) {
        AddDocsTab dialog = new AddDocsTab();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public JPanel getJPanel(){
        return contentPane;
    }

    public JTable getTable(){
        return table1;
    }

    public JTextArea getTextArea(){
        return textArea1;
    }
}
