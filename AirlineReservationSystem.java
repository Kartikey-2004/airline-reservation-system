import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import java.util.Random;

public class AirlineReservationSystem {
    private JFrame frame;
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton, signUpButton;
    private static final String BACKGROUND_PATH = "/graphics/Airline_Boarding_pass.jpg";
    private static final String BACKGROUND_PATH2 = "/graphics/Airline_Customer.png";
    private static final String BACKGROUND_PATH3 = "/graphics/Airline_Booking.png";
    // Database connection details
    private static final String DB_URL = "jdbc:sqlite:database/airline.db";
    public AirlineReservationSystem() {
        createLoginWindow();
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                // Load from resources in the same JAR/classpath
                backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
            } catch (Exception e) {
                System.out.println("Background image not found: " + imagePath);
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // 🔹 Custom Label with Drop Shadow & Lighter Text
    class ShadowLabel extends JLabel {
        public ShadowLabel(String text) {
            super(text);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK); // Shadow
            g2d.drawString(getText(), 2, 14); // Shadow Offset
            g2d.setColor(getForeground());
            g2d.drawString(getText(), 0, 12);
        }
    }

    private void createLoginWindow() {
        if (frame != null)
            frame.dispose(); // Close previous window if open

        frame = new JFrame("Airline Reservation - Login");
        frame.setSize(350, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // 🔹 Input Panel with GridBagLayout for label-field alignment
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        userField = new JTextField(15);
        inputPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        passField = new JPasswordField(15);
        inputPanel.add(passField, gbc);

        // 🔹 Button Panel for Login & Sign-Up buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");

        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        loginButton.addActionListener(e -> checkLogin());
        signUpButton.addActionListener(e -> {
            frame.dispose();
            openSignUpWindow();
        });

        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        centerWindow(frame);
        frame.setVisible(true);
    }

    private void checkLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both Username and Password!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = connectToDatabase()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                frame.dispose();
                openMainSystem(username);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Username or Password!", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSignUpWindow() {
        frame.dispose(); // Close login window

        JFrame signUpFrame = new JFrame("Sign Up");
        signUpFrame.setSize(350, 180);
        signUpFrame.setLayout(new BorderLayout(10, 10));

        // 🔹 Input Panel with GridBagLayout for label-field alignment
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 🔹 Adds padding between components

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("New Username:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JTextField newUserField = new JTextField(15);
        inputPanel.add(newUserField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JPasswordField newPassField = new JPasswordField(15);
        inputPanel.add(newPassField, gbc);

        // 🔹 Button Panel for Register & Back buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        registerButton.addActionListener(
                e -> registerUser(newUserField.getText(), new String(newPassField.getPassword()), signUpFrame));
        backButton.addActionListener(e -> {
            signUpFrame.dispose();
            createLoginWindow(); // Return to login screen
        });

        signUpFrame.add(inputPanel, BorderLayout.CENTER);
        signUpFrame.add(buttonPanel, BorderLayout.SOUTH);

        centerWindow(signUpFrame);
        signUpFrame.setVisible(true);
    }

    private void registerUser(String username, String password, JFrame signUpFrame) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(signUpFrame, "Fields cannot be empty!");
            return;
        }

        try (Connection con = connectToDatabase()) {
            String checkQuery = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(signUpFrame, "You have already registered! Please Go Login!");
            } else {
                String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement stmt = con.prepareStatement(insertQuery);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(signUpFrame, "Registration Successful!");
                signUpFrame.dispose();
                createLoginWindow(); // Reopen login window after signup
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(signUpFrame, "Database Error: " + ex.getMessage());
        }
    }

    private void openMainSystem(String username) {
        JFrame mainFrame = new JFrame("Airline Reservation System");
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        // 🔹 Dashboard Panel (Top)
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(new Color(30, 50, 90));
        dashboardPanel.setPreferredSize(new Dimension(mainFrame.getWidth(), 80));

        JLabel dashboardLabel = new JLabel("DASHBOARD");
        dashboardLabel.setFont(new Font("Arial", Font.BOLD, 28));
        dashboardLabel.setForeground(Color.WHITE);
        dashboardLabel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        dashboardPanel.add(dashboardLabel, BorderLayout.WEST);
        dashboardPanel.add(welcomeLabel, BorderLayout.EAST);

        // 🔹 Menu Panel (Left)
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(new Color(30, 50, 90));
        menuPanel.setPreferredSize(new Dimension(250, mainFrame.getHeight()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Putting all buttons names in an array
        String[] menuItems = {
                "FLIGHTS DETAILS", "CUSTOMER DETAILS", "FLIGHT BOOKING",
                "JOURNEY DETAILS", "BOARDING PASS", "CANCELATION"
        };

        // Create each button and its animation using a loop
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(40, 60, 110));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setPreferredSize(new Dimension(200, 40));

            // Hover Animation
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setPreferredSize(new Dimension(210, 45));
                    button.revalidate();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setPreferredSize(new Dimension(200, 40));
                    button.revalidate();
                }
            });

            // Functionality for each Button when Clicked
            if (item.equals("FLIGHTS DETAILS")) {
                button.addActionListener(e -> checkFlights());
            }
            if (item.equals("CUSTOMER DETAILS")) {
                button.addActionListener(e -> CustomerDetailsGUI());
            }
            if (item.equals("FLIGHT BOOKING")) {
                button.addActionListener(e -> bookingFlight());
            }
            if (item.equals("JOURNEY DETAILS")) {
                button.addActionListener(e -> bookingDetails());
            }
            if (item.equals("BOARDING PASS")) {
                button.addActionListener(e -> boardingPassGUI());
            }
            if (item.equals("CANCELATION")) {
                button.addActionListener(e -> ticketCancel());
            }
            menuPanel.add(button, gbc);
            gbc.gridy++;
        }

        // 🔹 Background Image Panel (Center)
        JPanel centerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon(
                        getClass().getResource("/graphics/Airline_Main_Window.jpg"));
                Image img = backgroundImage.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        centerPanel.setLayout(new BorderLayout()); // Allows adding components over image

        // 🔹 Adding Panels to Frame
        mainFrame.add(dashboardPanel, BorderLayout.NORTH);
        mainFrame.add(menuPanel, BorderLayout.WEST);
        mainFrame.add(centerPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    private void checkFlights() {
        JFrame flightFrame = new JFrame("Flights Detail");
        flightFrame.setSize(600, 300);
        flightFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columnNames = { "Flight No", "Flight Name", "Departure", "Destination" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable flightTable = new JTable(tableModel);

        try (Connection con = connectToDatabase()) {
            String query = "SELECT * FROM Flights";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String flightNo = rs.getString("flight_no");
                String flightName = rs.getString("flight_name");
                String from = rs.getString("departure");
                String destination = rs.getString("destination");

                tableModel.addRow(new Object[] { flightNo, flightName, from, destination });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(flightFrame, "Database Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        flightFrame.add(new JScrollPane(flightTable), BorderLayout.CENTER);
        flightFrame.setLocationRelativeTo(null);
        flightFrame.setVisible(true);
    }

    private void CustomerDetailsGUI() {
        JFrame customerFrame = new JFrame("Customer Details");
        customerFrame.setSize(600, 400);
        customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customerFrame.setLayout(new GridLayout(1, 2)); // Split into two panels

        // 🔹 Left Panel (Form Fields)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;

        leftPanel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Nationality:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Aadhaar No:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Address:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Phone No:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;

        JTextField nameField = new JTextField(15);
        JTextField nationalityField = new JTextField(15);
        JTextField adhaarField = new JTextField(15);
        JTextField addressField = new JTextField(15);

        JRadioButton maleButton = new JRadioButton("Male");
        JRadioButton femaleButton = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        JTextField phoneField = new JTextField(15);

        leftPanel.add(nameField, gbc);
        gbc.gridy++;
        leftPanel.add(nationalityField, gbc);
        gbc.gridy++;
        leftPanel.add(adhaarField, gbc);
        gbc.gridy++;
        leftPanel.add(addressField, gbc);
        gbc.gridy++;

        JPanel genderPanel = new JPanel();
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        leftPanel.add(genderPanel, gbc);

        gbc.gridy++;
        leftPanel.add(phoneField, gbc);

        // Submit Button
        gbc.gridy++;
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            String nationality = nationalityField.getText();
            String adhaar = adhaarField.getText();
            String address = addressField.getText();
            String gender = maleButton.isSelected() ? "Male" : "Female";
            String phone = phoneField.getText();

            saveCustomer(name, nationality, adhaar, address, gender, phone, customerFrame);
        });

        leftPanel.add(submitButton, gbc);

        // Right Panel (Image Placeholder)
        BackgroundPanel rightPanel = new BackgroundPanel(BACKGROUND_PATH2);
        rightPanel.setLayout(new BorderLayout(10, 10));
        customerFrame.add(leftPanel);
        customerFrame.add(rightPanel);
        customerFrame.setLocationRelativeTo(null);
        customerFrame.setVisible(true);
    }

    private void saveCustomer(String name, String nationality, String adhaar, String address, String gender,
            String phone, JFrame CustFrame) {
        if (name.isEmpty() || adhaar.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name, Aadhaar, and Phone No. are required fields!");
            return;
        }

        try (Connection con = connectToDatabase()) {
            String query = "INSERT INTO Customers (name, nationality, adhaar, address, gender, phone) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, nationality);
            stmt.setString(3, adhaar);
            stmt.setString(4, address);
            stmt.setString(5, gender);
            stmt.setString(6, phone);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Customer Added Successfully!");
            CustFrame.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
        }
    }

    private void bookingFlight() {
        JFrame bookingFrame = new JFrame("Flight Booking");
        bookingFrame.setSize(700, 500); // Adjusted for better spacing
        bookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bookingFrame.setLayout(new BorderLayout());

        // 🔹 Left Panel (Form Fields)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 5; // Reduce vertical space between rows

        JLabel adhaarLabel = new JLabel("Aadhaar No:");
        JTextField adhaarField = new JTextField(10);
        JButton fetchCustomerBtn = new JButton("Fetch");

        JPanel adhaarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        adhaarPanel.add(adhaarLabel);
        adhaarPanel.add(adhaarField);
        adhaarPanel.add(fetchCustomerBtn);

        gbc.gridwidth = 2; // Make Aadhaar panel span two columns
        leftPanel.add(adhaarPanel, gbc);

        // 🔹 Labels & Fields
        gbc.gridy++;
        gbc.gridwidth = 1; // Reset grid width
        leftPanel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Nationality:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Address:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Departure:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Flight No:"), gbc);
        gbc.gridy++;
        leftPanel.add(new JLabel("Flight Name:"), gbc);

        // 🔹 Input Fields
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField nameField = new JTextField(15);
        JTextField nationalityField = new JTextField(15);
        JTextField addressField = new JTextField(15);
        JTextField genderField = new JTextField(15);

        JComboBox<String> departureDropdown = new JComboBox<>();
        loadDropdownValues(departureDropdown, "SELECT DISTINCT departure FROM Flights");
        departureDropdown.setPreferredSize(new Dimension(150, 25));

        JComboBox<String> destinationDropdown = new JComboBox<>();
        loadDropdownValues(destinationDropdown, "SELECT DISTINCT destination FROM Flights");
        destinationDropdown.setPreferredSize(new Dimension(150, 25));

        JTextField flightNoField = new JTextField(15);
        JTextField flightNameField = new JTextField(15);
        JButton fetchFlightsBtn = new JButton("Fetch Flights");

        leftPanel.add(nameField, gbc);
        gbc.gridy++;
        leftPanel.add(nationalityField, gbc);
        gbc.gridy++;
        leftPanel.add(addressField, gbc);
        gbc.gridy++;
        leftPanel.add(genderField, gbc);
        gbc.gridy++;
        leftPanel.add(departureDropdown, gbc);
        gbc.gridy++;
        leftPanel.add(destinationDropdown, gbc);
        gbc.gridy++;
        leftPanel.add(flightNoField, gbc);
        gbc.gridy++;
        leftPanel.add(flightNameField, gbc);
        gbc.gridy++;
        leftPanel.add(fetchFlightsBtn, gbc);
        gbc.gridy++;
        JButton submitBookingBtn = new JButton("Submit Booking");
        leftPanel.add(submitBookingBtn, gbc);

        // 🔹 Right Panel (Smaller Image Section)
        BackgroundPanel rightPanel = new BackgroundPanel(BACKGROUND_PATH3);
        rightPanel.setPreferredSize(new Dimension(200, bookingFrame.getHeight())); // Smaller width

        bookingFrame.add(leftPanel, BorderLayout.CENTER);
        bookingFrame.add(rightPanel, BorderLayout.EAST);
        bookingFrame.setLocationRelativeTo(null);
        bookingFrame.setVisible(true);

        fetchCustomerBtn.addActionListener(
                e -> fetchCustomerDetails(adhaarField, nameField, nationalityField, addressField, genderField));
        fetchFlightsBtn.addActionListener(
                e -> fetchFlightDetails(departureDropdown, destinationDropdown, flightNoField, flightNameField));
        submitBookingBtn.addActionListener(e -> saveBooking(adhaarField, nameField, nationalityField, addressField,
                genderField, departureDropdown, destinationDropdown, flightNoField, flightNameField));
    }

    private void fetchCustomerDetails(JTextField adhaarField, JTextField nameField, JTextField nationalityField,
            JTextField addressField, JTextField genderField) {
        String adhaar = adhaarField.getText();
        if (adhaar.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter Aadhaar number!");
            return;
        }

        try (Connection con = connectToDatabase()) {
            String query = "SELECT name, nationality, address, gender FROM Customers WHERE adhaar = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, adhaar);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                nationalityField.setText(rs.getString("nationality"));
                addressField.setText(rs.getString("address"));
                genderField.setText(rs.getString("gender"));
            } else {
                JOptionPane.showMessageDialog(null, "Customer not found!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
        }
    }

    private void loadDropdownValues(JComboBox<String> dropdown, String query) {
        try (Connection con = connectToDatabase();
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            Vector<String> values = new Vector<>();
            while (rs.next()) {
                values.add(rs.getString(1));
            }
            dropdown.setModel(new DefaultComboBoxModel<>(values));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
        }
    }

    private void fetchFlightDetails(JComboBox<String> departureDropdown, JComboBox<String> destinationDropdown,
            JTextField flightNoField, JTextField flightNameField) {
        String departure = (String) departureDropdown.getSelectedItem();
        String destination = (String) destinationDropdown.getSelectedItem();

        if (departure == null || destination == null) {
            JOptionPane.showMessageDialog(null, "Select Departure and Destination!");
            return;
        }

        try (Connection con = connectToDatabase()) {
            String query = "SELECT flight_no, flight_name FROM Flights WHERE departure = ? AND destination = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, departure);
            stmt.setString(2, destination);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                flightNoField.setText(rs.getString("flight_no"));
                flightNameField.setText(rs.getString("flight_name"));
            } else {
                JOptionPane.showMessageDialog(null, "No flights available!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
        }
    }

    private void saveBooking(JTextField adhaarField, JTextField nameField, JTextField nationalityField,
            JTextField addressField, JTextField genderField, JComboBox<String> departureDropdown,
            JComboBox<String> destinationDropdown, JTextField flightNoField, JTextField flightNameField) {
        try (Connection con = connectToDatabase()) {
            // Generate Unique 4-digit PNR
            String PNR;
            do {
                PNR = String.format("%04d", new Random().nextInt(10000)); // Generate random 4-digit number
            } while (pnrExists(con, PNR)); // Ensure it's unique

            String query = "INSERT INTO Bookings (PNR, adhaar, name, nationality, address, gender, departure, destination, flight_no, flight_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, PNR);
            stmt.setString(2, adhaarField.getText());
            stmt.setString(3, nameField.getText());
            stmt.setString(4, nationalityField.getText());
            stmt.setString(5, addressField.getText());
            stmt.setString(6, genderField.getText());
            stmt.setString(7, (String) departureDropdown.getSelectedItem());
            stmt.setString(8, (String) destinationDropdown.getSelectedItem());
            stmt.setString(9, flightNoField.getText());
            stmt.setString(10, flightNameField.getText());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Booking Successful! PNR: " + PNR);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
        }
    }

    // Helper function to check if PNR exists
    private boolean pnrExists(Connection con, String PNR) throws SQLException {
        String query = "SELECT PNR FROM Bookings WHERE PNR = ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, PNR);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    private void bookingDetails() {
        JFrame detailsFrame = new JFrame("Journey Details");
        detailsFrame.setSize(800, 500);
        detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailsFrame.setLayout(new BorderLayout());

        // Search Panel
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel searchLabel = new JLabel("Enter PNR:");
        JTextField searchField = new JTextField(5);
        JButton searchButton = new JButton("Search");
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        // Table to Display Booking Details
        String[] columnNames = { "PNR", "Name", "Departure", "Destination", "Flight No" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        detailsFrame.add(topPanel, BorderLayout.NORTH);
        detailsFrame.add(scrollPane, BorderLayout.CENTER);
        detailsFrame.setLocationRelativeTo(null);
        detailsFrame.setVisible(true);

        // Fetch Booking Details
        searchButton.addActionListener(e -> fetchBookingDetails(searchField.getText(), tableModel));
    }

    // Fetch and Display Booking Details
    private void fetchBookingDetails(String PNR, DefaultTableModel tableModel) {
        try (Connection con = connectToDatabase()) {
            String query = "SELECT PNR, name, departure, destination, flight_no FROM Bookings WHERE PNR = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, PNR);
            ResultSet rs = stmt.executeQuery();

            boolean found = false;

            while (rs.next()) {
                // Check if PNR already exists in the table
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(PNR)) {
                        // Update existing row
                        tableModel.setValueAt(rs.getString("name"), i, 1);
                        tableModel.setValueAt(rs.getString("departure"), i, 2);
                        tableModel.setValueAt(rs.getString("destination"), i, 3);
                        tableModel.setValueAt(rs.getString("flight_no"), i, 4);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Add new row only if PNR is not found
                    tableModel.addRow(new Object[] {
                            rs.getString("PNR"),
                            rs.getString("name"),
                            rs.getString("departure"),
                            rs.getString("destination"),
                            rs.getString("flight_no")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
        }
    }

    private void boardingPassGUI() {
        // Create the frame
        JFrame frame = new JFrame("Boarding Pass - AIR INDIA");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create BackgroundPanel and set its layout to BorderLayout
        BackgroundPanel bgPanel = new BackgroundPanel(BACKGROUND_PATH);
        bgPanel.setLayout(new BorderLayout(10, 10));

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false); // Set header panel to be transparent

        JLabel titleLabel = new JLabel("AIR INDIA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel subTitleLabel = new JLabel("BOARDING PASS", SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        headerPanel.add(titleLabel);
        headerPanel.add(subTitleLabel);
        bgPanel.add(headerPanel, BorderLayout.NORTH);

        // MAIN PANEL USING GRIDBAGLAYOUT
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false); // Set main panel to be transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Input Fields
        JTextField pnrField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField nationalityField = new JTextField();
        JTextField departureField = new JTextField();
        JTextField destinationField = new JTextField();
        JTextField flightNoField = new JTextField();
        JTextField flightNameField = new JTextField();

        // Set preferred sizes
        Dimension textFieldSize = new Dimension(150, 25);
        Dimension buttonSize = new Dimension(100, 30);

        pnrField.setPreferredSize(textFieldSize);
        nameField.setPreferredSize(textFieldSize);
        nationalityField.setPreferredSize(textFieldSize);
        departureField.setPreferredSize(textFieldSize);
        destinationField.setPreferredSize(textFieldSize);
        flightNoField.setPreferredSize(textFieldSize);
        flightNameField.setPreferredSize(textFieldSize);

        JButton fetchButton = new JButton("Fetch");
        fetchButton.setPreferredSize(buttonSize);

        // Make Fields Non-Editable (Except PNR)
        nameField.setEditable(false);
        nationalityField.setEditable(false);
        departureField.setEditable(false);
        destinationField.setEditable(false);
        flightNoField.setEditable(false);
        flightNameField.setEditable(false);

        // Adding components using GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new ShadowLabel("PNR:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(pnrField, gbc);
        gbc.gridx = 2;
        mainPanel.add(fetchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new ShadowLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(new ShadowLabel("Nationality:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(nationalityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        mainPanel.add(new ShadowLabel("Departure:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(departureField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        mainPanel.add(new ShadowLabel("Destination:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(destinationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        mainPanel.add(new ShadowLabel("Flight No.:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(flightNoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        mainPanel.add(new ShadowLabel("Flight Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(flightNameField, gbc);

        bgPanel.add(mainPanel, BorderLayout.CENTER);

        // Fetch Data from Database
        fetchButton.addActionListener(e -> fetchBoardingPassDetails(
                pnrField, nameField, nationalityField, departureField, destinationField, flightNoField,
                flightNameField));

        // Set the background panel as the main panel in the frame
        frame.add(bgPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Fetch Data from Database (Without Date Field)
    private void fetchBoardingPassDetails(JTextField pnrField, JTextField nameField, JTextField nationalityField,
            JTextField departureField, JTextField destinationField,
            JTextField flightNoField, JTextField flightNameField) {
        String PNR = pnrField.getText().trim();
        if (PNR.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a PNR number.");
            return;
        }

        try (Connection con = connectToDatabase()) {
            String query = "SELECT name, nationality, departure, destination, flight_no, flight_name FROM Bookings WHERE PNR = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, PNR);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                nationalityField.setText(rs.getString("nationality"));
                departureField.setText(rs.getString("departure"));
                destinationField.setText(rs.getString("destination"));
                flightNoField.setText(rs.getString("flight_no"));
                flightNameField.setText(rs.getString("flight_name"));
            } else {
                JOptionPane.showMessageDialog(null, "No details found for PNR: " + PNR);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
        }
    }

    private void ticketCancel() {
        // Create frame
        JFrame frame = new JFrame("Ticket Cancellation");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // PNR Input
        JLabel pnrLabel = new JLabel("Enter PNR:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(pnrLabel, gbc);

        JTextField pnrField = new JTextField();
        pnrField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        frame.add(pnrField, gbc);

        // Cancel Button
        JButton cancelButton = new JButton("Cancel Ticket");
        cancelButton.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(cancelButton, gbc);

        // Add ActionListener to Delete Ticket
        cancelButton.addActionListener(e -> {
            String PNR = pnrField.getText().trim();
            if (PNR.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a PNR number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel this ticket?",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteBooking(PNR);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Method to delete a booking from the database
    private void deleteBooking(String PNR) {
        try (Connection con = connectToDatabase()) {
            String query = "DELETE FROM BOOKINGS WHERE PNR = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, PNR);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Ticket canceled successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No ticket found for PNR: " + PNR, "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void centerWindow(JFrame frame) {
        frame.setLocationRelativeTo(null); // Centers the window on screen
    }

    private static Connection connectToDatabase() throws SQLException {
        final Connection[] connectionRef = { null }; // Store connection
        final SQLException[] exceptionRef = { null }; // Store exception if any

        // Create a loading dialog
        JDialog loadingDialog = new JDialog();
        loadingDialog.setTitle("Connecting...");
        loadingDialog.setSize(250, 100);
        loadingDialog.setLocationRelativeTo(null);
        loadingDialog.setUndecorated(true); // Remove title bar
        loadingDialog.setModal(true); // Block user interaction
        loadingDialog.getContentPane().setBackground(Color.WHITE); // Set background white
        // Panel for loading bar animation
        JPanel loadingPanel = new JPanel() {
            private int barX = 0;
            private boolean movingRight = true;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight()); // White background
                // Draw loading bar outline
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(20, getHeight() / 2 - 10, getWidth() - 40, 20);
                // Draw moving fill
                g2.setColor(Color.BLACK);
                g2.fillRect(barX, getHeight() / 2 - 10, 50, 20);
                // Update bar position
                if (movingRight) {
                    barX += 5;
                    if (barX + 50 >= getWidth() - 20) {
                        movingRight = false;
                    }
                } else {
                    barX -= 5;
                    if (barX <= 20) {
                        movingRight = true;
                    }
                }
                repaint(); // Keep animating
                try {
                    Thread.sleep(50); // Smooth animation speed
                } catch (InterruptedException ignored) {
                }
            }
        };
        loadingPanel.setPreferredSize(new Dimension(250, 60));
        loadingPanel.setBackground(Color.WHITE);

        // Label for text
        JLabel loadingLabel = new JLabel("Connecting to Database...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loadingLabel.setForeground(Color.BLACK);
        loadingLabel.setBackground(Color.WHITE);
        loadingLabel.setOpaque(true);

        // Adding components to dialog
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.add(loadingPanel, BorderLayout.CENTER);
        loadingDialog.add(loadingLabel, BorderLayout.SOUTH);

        // Background task to connect to the database
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    connectionRef[0] = DriverManager.getConnection(DB_URL);
                    System.out.println("Database connection successful!");
                } catch (SQLException e) {
                    System.out.println("Database connection failed!");
                    exceptionRef[0] = e; // Store the import exception
                }
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); // Close the loading dialog
            }
        };
        worker.execute();
        loadingDialog.setVisible(true); // Show the loading popup
        // If an exception occurred, throw it
        if (exceptionRef[0] != null) {
            throw exceptionRef[0];
        }
        return connectionRef[0]; // Return the connection
    }

    public static void main(String[] args) {
        new AirlineReservationSystem();
    }
}