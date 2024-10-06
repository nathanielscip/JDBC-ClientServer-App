/*
Name: Nathaniel Scipio
Course: CNT 4714 Summer 2024
Assignment title: Project 2 – A Two-tier Client-Server Application
Date: July 10, 2024
Class: CNT 4714
*/

package developmentalVersion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import com.mysql.cj.jdbc.MysqlDataSource;


public class SQLClientApplication3 extends JFrame {
    // default query
    //static final String DEFAULT_QUERY = "SELECT * FROM bikes";

    private ResultSetTableModel tableModel;
    private JTextArea commandArea;
    private JTable resultTable;
    private boolean connectedToDatabase = false;

    // Dropdown menus
    private JComboBox<String> dbPropertiesDropdown;
    private JComboBox<String> userPropertiesDropdown;

    // Text fields for username and password
    private JTextField usernameField;
    private JPasswordField passwordField;

    private String loggedInUser = "";

    public SQLClientApplication3() {
        super("SQL Client Application");

        // Define dropdown menus
        String[] dbProperties = {"bikedb.properties", "project2.properties"};
        String[] userProperties = {"root.properties", "client1.properties", "client2.properties"};

        dbPropertiesDropdown = new JComboBox<>(dbProperties);
        userPropertiesDropdown = new JComboBox<>(userProperties);

        // Define buttons
        JButton connectButton = new JButton("Connect to Database");
        connectButton.setFont(new Font("Arial", Font.BOLD, 12));
        connectButton.setBackground(Color.BLUE);
        connectButton.setForeground(Color.WHITE);
        connectButton.setBorderPainted(false);
        connectButton.setOpaque(true);

        JButton disconnectButton = new JButton("Disconnect from Database");
        disconnectButton.setFont(new Font("Arial", Font.BOLD, 12));
        disconnectButton.setBackground(Color.RED);
        disconnectButton.setForeground(Color.BLACK);
        disconnectButton.setBorderPainted(false);
        disconnectButton.setOpaque(true);

        JButton clearSQLButton = new JButton("Clear SQL Command");
        clearSQLButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearSQLButton.setBackground(Color.YELLOW);
        clearSQLButton.setForeground(Color.BLACK);
        clearSQLButton.setBorderPainted(false);
        clearSQLButton.setOpaque(true);

        JButton executeButton = new JButton("Execute SQL Command");
        executeButton.setFont(new Font("Arial", Font.BOLD, 12));
        executeButton.setBackground(Color.GREEN);
        executeButton.setForeground(Color.BLACK);
        executeButton.setBorderPainted(false);
        executeButton.setOpaque(true);

        JButton clearResButton = new JButton("Clear Result Window");
        clearResButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearResButton.setBackground(Color.YELLOW);
        clearResButton.setForeground(Color.BLACK);
        clearResButton.setBorderPainted(false);
        clearResButton.setOpaque(true);

        // Labels
        JLabel commandLabel = new JLabel("Enter An SQL Command");
        commandLabel.setFont(new Font("Arial", Font.BOLD, 14));
        commandLabel.setForeground(Color.BLUE);

        JLabel detailsLabel = new JLabel("Connection Details");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsLabel.setForeground(Color.BLUE);

        JLabel execLabel = new JLabel("SQL Execution Result Window");
        execLabel.setFont(new Font("Arial", Font.BOLD, 14));
        execLabel.setForeground(Color.BLUE);

        JLabel dbPropertiesLabel = new JLabel("DB URL Properties");
        dbPropertiesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dbPropertiesLabel.setBackground(Color.GRAY);
        dbPropertiesLabel.setForeground(Color.BLACK);
        dbPropertiesLabel.setOpaque(true);

        JLabel userPropertiesLabel = new JLabel("User Properties");
        userPropertiesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userPropertiesLabel.setBackground(Color.GRAY);
        userPropertiesLabel.setForeground(Color.BLACK);
        userPropertiesLabel.setOpaque(true);

        JLabel statusLabel = new JLabel("NO CONNECTION");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBackground(Color.BLACK);
        statusLabel.setForeground(Color.RED);
        statusLabel.setOpaque(true);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setBackground(Color.GRAY);
        usernameLabel.setForeground(Color.BLACK);
        usernameLabel.setOpaque(true);
        usernameField = new JTextField(20);    

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setBackground(Color.GRAY);
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setOpaque(true);
        passwordField = new JPasswordField(10);

        // Define user areas and result areas
        commandArea = new JTextArea("", 10, 30);
        commandArea.setWrapStyleWord(true);
        commandArea.setLineWrap(true);
        resultTable = new JTable();

        // Window size and layout
        setLayout(new BorderLayout());

        // Create and configure scroll panes
        JScrollPane resultScrollPane = new JScrollPane(resultTable);
        resultScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JScrollPane queryScrollPane = new JScrollPane(commandArea);
        queryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        queryScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Define panels
        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));
        commandPanel.add(commandLabel);
        commandPanel.add(queryScrollPane);
        commandPanel.add(executeButton);
        commandPanel.add(clearSQLButton);

        JPanel connectionPanel = new JPanel();
        connectionPanel.setLayout(new BoxLayout(connectionPanel, BoxLayout.Y_AXIS));
        connectionPanel.add(detailsLabel);
        connectionPanel.add(dbPropertiesLabel);
        connectionPanel.add(dbPropertiesDropdown);
        connectionPanel.add(userPropertiesLabel);
        connectionPanel.add(userPropertiesDropdown);
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);
        connectionPanel.add(usernameLabel);
        connectionPanel.add(usernameField);
        connectionPanel.add(passwordLabel);
        connectionPanel.add(passwordField);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.add(execLabel);
        resultPanel.add(resultScrollPane);
        resultPanel.add(clearResButton);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.add(statusLabel);

        // Add components to frame
        add(commandPanel, BorderLayout.NORTH);
        add(connectionPanel, BorderLayout.WEST);
        add(resultPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    String dbPropertyFile = (String) dbPropertiesDropdown.getSelectedItem();
                    String userPropertyFile = (String) userPropertiesDropdown.getSelectedItem();

                    Properties dbProps = new Properties();
                    try (FileInputStream dbInputStream = new FileInputStream("resources/" + dbPropertyFile)) {
                        dbProps.load(dbInputStream);
                    }

                    Properties userProps = new Properties();
                    try (FileInputStream userInputStream = new FileInputStream("resources/" + userPropertyFile)) {
                        userProps.load(userInputStream);
                    }

                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());

                    // Validate username and password 
                    //root password = CNT4714
                    if (!username.equals(userProps.getProperty("MYSQL_DB_USERNAME")) || !password.equals(userProps.getProperty("MYSQL_DB_PASSWORD"))) {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.", "Authentication error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    MysqlDataSource dataSource = new MysqlDataSource();
                    dataSource.setURL(dbProps.getProperty("MYSQL_DB_URL"));
                    dataSource.setUser(username);
                    dataSource.setPassword(password);

                    loggedInUser = username + "@localhost";

                    tableModel = new ResultSetTableModel(dataSource);
                    connectedToDatabase = true;

                    if (connectedToDatabase) {
                        statusLabel.setForeground(Color.GREEN);
                        statusLabel.setText("Connected to Database: " + dbProps.getProperty("MYSQL_DB_URL"));
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Properties File error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException | ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                tableModel.disconnectFromDatabase();
                connectedToDatabase = false;
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("NO CONNECTION");
            }
        });

        clearResButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // Clear results in result table
                resultTable.setModel(new DefaultTableModel());
            }
        });

        clearSQLButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                commandArea.setText("");
            }
        });

        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (connectedToDatabase) {
                    try {
                        String command = commandArea.getText().trim();
                        resultTable.setModel(tableModel);
                        int rowsAffected = 0;
                        // Enable grid lines for the result table
                        resultTable.setShowGrid(true);
                        resultTable.setGridColor(Color.BLACK);
                        // command type is determined for logoperation method
                        if (command.toLowerCase().startsWith("insert") || command.toLowerCase().startsWith("update") || command.toLowerCase().startsWith("delete")) {
                            rowsAffected = tableModel.executeUpdate(command);
                            JOptionPane.showMessageDialog(null, "Successful Update..." + rowsAffected + " rows updated.", "Successful Update", JOptionPane.INFORMATION_MESSAGE);
                            logOperation(command, "update");
                        } else {
                            tableModel.setQuery(command);
                            logOperation(command, "query");
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Not connected to a database.", "Connection error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setSize(800, 600);
        setVisible(true);

        // Ensure database connection is closed when user quits application
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent event) {
                if (tableModel != null) {
                    tableModel.disconnectFromDatabase();
                }
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        new SQLClientApplication3();
    }

    private void logOperation(String command, String type) {
        String updateLogCommand;
        MysqlDataSource operationSource = new MysqlDataSource();
        Properties properties = new Properties();

        try (FileInputStream filein = new FileInputStream("resources/operationslog.properties")) {
            properties.load(filein);
            operationSource.setURL(properties.getProperty("MYSQL_DB_URL"));
            operationSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
            operationSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading properties file: " + e.getMessage(), "Logging error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (type.equals("query")) {
            updateLogCommand = "UPDATE operationscount SET num_queries = num_queries + 1 WHERE login_username = ?";
        } else {
            updateLogCommand = "UPDATE operationscount SET num_updates = num_updates + 1 WHERE login_username = ?";
        }

        try (Connection connection = operationSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(updateLogCommand);
            statement.setString(1, loggedInUser);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                String insertLogCommand = "INSERT INTO operationscount (login_username, num_queries, num_updates) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertLogCommand);
                insertStatement.setString(1, loggedInUser);
                insertStatement.setInt(2, type.equals("query") ? 1 : 0);
                insertStatement.setInt(3, type.equals("update") ? 1 : 0);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error logging operation: " + e.getMessage(), "Logging error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class ResultSetTableModel extends AbstractTableModel {
        private Connection connection;
        private Statement statement;
        private ResultSet resultSet;
        private ResultSetMetaData metaData;
        private int numberOfRows;

        // Keep track of database connection status
        private boolean connectedToDatabase = false;

        public ResultSetTableModel(MysqlDataSource dataSource) throws SQLException, ClassNotFoundException {
            connection = dataSource.getConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            connectedToDatabase = true;
        }

        // Get class that represents column type
        public Class<?> getColumnClass(int column) throws IllegalStateException {
            if (!connectedToDatabase)
                throw new IllegalStateException("Not Connected to Database");

            try {
                String className = metaData.getColumnClassName(column + 1);
                return Class.forName(className);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return Object.class;
        }

        // Get number of columns in ResultSet
        public int getColumnCount() throws IllegalStateException {
            if (!connectedToDatabase)
                throw new IllegalStateException("Not Connected to Database");

            try {
                return metaData.getColumnCount();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }

            return 0;
        }

        // Get name of a particular column in ResultSet
        public String getColumnName(int column) throws IllegalStateException {
            if (!connectedToDatabase)
                throw new IllegalStateException("Not Connected to Database");

            try {
                return metaData.getColumnName(column + 1);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }

            return "";
        }

        // Return number of rows in ResultSet
        public int getRowCount() throws IllegalStateException {
            if (!connectedToDatabase)
                throw new IllegalStateException("Not Connected to Database");

            return numberOfRows;
        }

        // Obtain value in particular row and column
        public Object getValueAt(int row, int column) throws IllegalStateException {
            if (!connectedToDatabase)
                throw new IllegalStateException("Not Connected to Database");

            try {
                resultSet.next();
                resultSet.absolute(row + 1);
                return resultSet.getObject(column + 1);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }

            return "";
        }

        // Set new database query string
        public void setQuery(String query) throws SQLException, IllegalStateException {
            if (!connectedToDatabase)
                throw new IllegalStateException("Not Connected to Database");

            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();
            resultSet.last();
            numberOfRows = resultSet.getRow();
            fireTableStructureChanged();
        }

        public int executeUpdate(String command) throws SQLException, IllegalStateException {
            if (!connectedToDatabase)
                throw new IllegalStateException("Not Connected to Database");
        
            return statement.executeUpdate(command);
        }

        // Close Statement and Connection
        public void disconnectFromDatabase() {
            if (!connectedToDatabase)
                return;
            else {
                try {
                    statement.close();
                    connection.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                } finally {
                    connectedToDatabase = false;
                }
            }
        }
    }
}
