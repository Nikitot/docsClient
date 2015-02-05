import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by Nikitot on 18.11.14.
 */

public class ClientGUI {
    JTextField loginFiled;
    JPasswordField passwordField;
    JButton CreateUserButton;
    JPanel mainPanel;
    JButton loginOrCreateButton;
    JTable tableDocs;
    JTabbedPane homeTabbedPane;
    JPasswordField rePasswordField;
    JRadioButton lioginRadioButton;
    JRadioButton registrationRadioButton;
    JLabel rePasswordLabel;
    JLabel statusLabel;
    JButton newDocButton;
    JTextField newDocNameField;
    boolean logined = false;

    ArrayList<JTextArea> docsList = new ArrayList<JTextArea>();

    ArrayList<Boolean> writeStatuses = new ArrayList<Boolean>();


    public ClientGUI() throws NotBoundException, MalformedURLException, NamingException {
        newDocButton.setEnabled(false);
        newDocNameField.setEnabled(false);

        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        props.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");

        props.setProperty("org.omg.CORBA.ORBInitialHost", "192.168.43.118");//ur server ip
        props.setProperty("org.omg.CORBA.ORBInitialPort", "3700"); //default is 3700



        InitialContext ic = new InitialContext(props);
        final ClientSessionBeanRemote ci = (ClientSessionBeanRemote) ic.lookup("java:global/nikitot_ee/nikitot-ejb/RoolaccessLogic!nikejbpac.ClientSessionBeanRemote"/*"corbaname:iiop:192.168.43.118:3700#global/nikitot_ee/nikitot-ejb/RoolaccessLogic"*/);


        loginOrCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (registrationRadioButton.isSelected()) {
                        if (ci.createUser(loginFiled.getText(), passwordField.getText())) {
                            statusLabel.setText("You have successfully registered");
                            statusLabel.setForeground(new Color(0, 128, 0));
                        } else {
                            statusLabel.setText("This username already registered");
                            statusLabel.setForeground(new Color(200, 0, 0));
                        }
                    } else {
                        if (ci.loginUser(loginFiled.getText(), passwordField.getText())) {
                            statusLabel.setText("Hello master " + loginFiled.getText() + "!");
                            statusLabel.setForeground(new Color(0, 128, 0));
                            newDocButton.setEnabled(true);
                            newDocNameField.setEnabled(true);
                            logined = true;
                        } else {
                            statusLabel.setText("Username or password is wrong!");
                            statusLabel.setForeground(new Color(200, 0, 0));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        });

        Timer updateTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (logined) {
                    passwordField.setEnabled(false);
                    rePasswordField.setEnabled(false);
                    lioginRadioButton.setEnabled(false);
                    registrationRadioButton.setEnabled(false);
                    loginFiled.setEnabled(false);

                    try {
                        updateDocsTable(ci);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }

                    if (homeTabbedPane.getTabCount() > 1) {
                        for (int i = 1; i < homeTabbedPane.getTabCount(); i++) {
                            String[] tokens = homeTabbedPane.getTitleAt(i).split(":");
                            String author = tokens[0];
                            String docName = tokens[1];

                            if (homeTabbedPane.getSelectedIndex() > 0) {
                                writeStatuses.set(i - 1, ci.writeStatus(
                                        homeTabbedPane.getTitleAt(i), loginFiled.getText()
                                ));

                                if (writeStatuses.get(i - 1)) {
                                    homeTabbedPane.setForegroundAt(i, Color.blue);
                                    String textClient = docsList.get(i - 1).getText();
                                    ci.updateDocumentText(textClient, docName, author);
                                } else {
                                    homeTabbedPane.setForegroundAt(i, Color.gray);
                                    String textServer = ci.getDocumentText(docName, author);
                                    docsList.get(i - 1).setText(textServer);                    //Обновляем текст документа в клиенте
                                }
                            }
                        }
                    }
                }


                if (homeTabbedPane.getTabCount() > 1) {
                    for (int i = 1; i < homeTabbedPane.getTabCount(); i++) {
                        String fullDocname = homeTabbedPane.getTitleAt(i);
                        String masterlogin = loginFiled.getText();

                        ci.writeStatus(fullDocname, masterlogin);
                    }
                }

                if (!passwordField.getText().isEmpty()) {
                    if (passwordField.getText().length() < 4)
                        passwordField.setBackground(Color.orange);
                    else
                        passwordField.setBackground(Color.green);
                } else
                    rePasswordField.setBackground(Color.white);

                if (!loginFiled.getText().isEmpty()) {
                    if (loginFiled.getText().length() < 4)
                        loginFiled.setBackground(Color.orange);
                    else
                        loginFiled.setBackground(Color.green);
                } else
                    rePasswordField.setBackground(Color.white);

                if (!rePasswordField.getText().isEmpty()) {
                    if (rePasswordField.getText().equals(passwordField.getText())) {
                        rePasswordField.setBackground(Color.green);
                    } else {
                        rePasswordField.setBackground(Color.orange);
                    }
                } else
                    rePasswordField.setBackground(Color.white);
            }
        });
        updateTimer.start();

        lioginRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrationRadioButton.setSelected(false);
                rePasswordField.setVisible(false);
                rePasswordLabel.setVisible(false);
                loginOrCreateButton.setText("Login");
            }
        });
        registrationRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lioginRadioButton.setSelected(false);
                rePasswordField.setVisible(true);
                rePasswordLabel.setVisible(true);
                loginOrCreateButton.setText("Registration");
            }
        });
        rePasswordField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                checkField(e);
                if (rePasswordField.getText().length() > 10)
                    e.consume();
            }
        });


        passwordField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                checkField(e);
                if (passwordField.getText().length() > 10)
                    e.consume();
            }
        });

        loginFiled.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                checkField(e);
                if (loginFiled.getText().length() > 10)
                    e.consume();
            }
        });
        newDocButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ci.createDocument(newDocNameField.getText(), loginFiled.getText(), "");
            }
        });
        tableDocs.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableDocs.getSelectedRow();
                try {
                    String docName = (String) tableDocs.getValueAt(selectedRow, 0);
                    String author = (String) tableDocs.getValueAt(selectedRow, 1);

                    for (int i = 0; i < homeTabbedPane.getTabCount(); i++) {
                        if ((author + ":" + docName).equals(homeTabbedPane.getTitleAt(i))) {
                            homeTabbedPane.setSelectedIndex(i);
                            break;
                        }
                        if (i == homeTabbedPane.getTabCount() - 1) {
                            AddDocsTab addDocsTab = new AddDocsTab();
                            JPanel panel = addDocsTab.getJPanel();
                            JTextArea docsArea = addDocsTab.getTextArea();
                            JTable usersTable = addDocsTab.getTable();

                            //ArrayList<String> usersDoc = ci.getDocumentInfo(docName);
                            String usersData[][] = ci.getAllUserFromDoc(docName, author);

                            Vector<String> colsName = new Vector<String>();
                            colsName.add("User");
                            colsName.add("Exclusive access");

                            DefaultTableModel usersTableModel = new DefaultTableModel(colsName, 0);
                            usersTable.setModel(usersTableModel);

                            for (String[] anUsersData : usersData) {
                                try {
                                    Vector<String> row = new Vector<String>();
                                    row.add(usersData[i][0]);
                                    row.add(usersData[i][0]);

                                    usersTableModel.addRow(row);
                                    usersTable.setModel(usersTableModel);
                                } catch (Exception ignore) {
                                }
                            }
                            docsArea.setText(ci.getDocumentText(docName, author));
                            docsList.add(docsArea);
                            homeTabbedPane.addTab(author + ":" + docName, panel);
                        }
                    }
                    ci.setWriter(author + ":" + docName, "NULL");
                    writeStatuses.add(false);

                } catch (Exception ignore) {
                }
            }
        });
        homeTabbedPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (homeTabbedPane.getSelectedIndex() > 0) {
                        String docFullName = homeTabbedPane.getTitleAt(homeTabbedPane.getSelectedIndex());
                        String masterlogin = loginFiled.getText();
                        writeStatuses.set(homeTabbedPane.getSelectedIndex() - 1, ci.setWriter(docFullName, masterlogin));

                    }
                }
            }
        });

        newDocNameField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                checkField(e);
                if (passwordField.getText().length() > 30)
                    e.consume();
            }
        });
    }

    private void updateDocsTable(ClientSessionBeanRemote ci) throws RemoteException {
        Vector<String> colsName = new Vector<String>();
        colsName.add("Name");
        colsName.add("Author");
        colsName.add("Exclusive access");

        String userDocList[][] = ci.getAllDocsFromUser(loginFiled.getText());
        DefaultTableModel tableDocsModel = new DefaultTableModel(colsName, 0);
        tableDocs.setModel(tableDocsModel);

        for (int i = 0; i < userDocList.length; i++) {
            Vector<String> row = new Vector<String>();
            row.add(userDocList[i][0]);
            row.add(userDocList[i][1]);
            row.add(userDocList[i][2]);

            tableDocsModel.addRow(row);
            tableDocs.setModel(tableDocsModel);
        }
    }

    private void checkField(KeyEvent e) {
        if (Character.isWhitespace(e.getKeyChar())
                || e.getKeyChar() == '.'
                || e.getKeyChar() == ','
                || e.getKeyChar() == '-'
                || e.getKeyChar() == ':'
                || e.getKeyChar() == ';'
                || e.getKeyChar() == '\"'
                || e.getKeyChar() == '/'
                || e.getKeyChar() == '"'
                || e.getKeyChar() == '\'')
            e.consume();
    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Client Docs");
        frame.setContentPane(new ClientGUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
