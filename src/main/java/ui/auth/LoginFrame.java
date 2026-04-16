package ui.auth;

import service.AuthService;
import service.StudentService;
import service.InstructorService;
import service.AdminService;
import access.Role;
import service.AuthService.Session;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService auth;
    private final StudentService student;
    private final InstructorService instructor;
    private final AdminService admin;

    public LoginFrame(AuthService auth, StudentService student, InstructorService instructor, AdminService admin) {
        this.auth = auth;
        this.student = student;
        this.instructor = instructor;
        this.admin = admin;

        setTitle("University ERP - Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(450, 550);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(250, 250, 250));

        JLabel maintenanceBanner = new JLabel(" ⚠ MAINTENANCE MODE ACTIVE - Read Only", SwingConstants.CENTER);
        maintenanceBanner.setFont(new Font("Segoe UI", Font.BOLD, 14));
        maintenanceBanner.setBackground(new Color(255, 193, 7));
        maintenanceBanner.setForeground(new Color(60, 60, 60));
        maintenanceBanner.setOpaque(true);
        maintenanceBanner.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        if (admin.isMaintenanceOn()) {
            GridBagConstraints bannerGbc = new GridBagConstraints();
            bannerGbc.gridx = 0;
            bannerGbc.gridy = 0;
            bannerGbc.weightx = 1.0;
            bannerGbc.fill = GridBagConstraints.HORIZONTAL;
            bannerGbc.anchor = GridBagConstraints.NORTH;
            mainPanel.add(maintenanceBanner, bannerGbc);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1; 
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 40, 40, 40);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(250, 250, 250));
        contentPanel.setPreferredSize(new Dimension(450, 600));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(250, 250, 250));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("University ERP System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Student Management Portal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginLabel = new JLabel("Sign In");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setMaximumSize(new Dimension(350, 45));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setMaximumSize(new Dimension(350, 45));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setMaximumSize(new Dimension(350, 50));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        loginButton.setEnabled(true);

        formPanel.add(loginLabel);
        formPanel.add(Box.createVerticalStrut(35));
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(loginButton);

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(250, 250, 250));
        footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel footerLabel = new JLabel("© 2025 University ERP System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(150, 150, 150));
        footerPanel.add(footerLabel);

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(footerPanel);

        mainPanel.add(contentPanel, gbc);
        setContentPane(mainPanel);

        Runnable doLogin = () -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Session s = auth.login(username, password);
            if (s == null) {
                String errorMsg = auth.getLastError();
                if (errorMsg == null)
                    errorMsg = "Login Failed";
                JOptionPane.showMessageDialog(this, errorMsg, "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            setVisible(false);
            dispose();

            if (s.role == Role.Student)
                new ui.student.StudentDashboard(student, s.userId, admin, auth).setVisible(true);
            else if (s.role == Role.Instructor)
                new ui.instructor.InstructorDashboard(instructor, s.userId, admin, auth).setVisible(true);
            else if (s.role == Role.Admin)
                new ui.admin.AdminDashboard(admin, auth).setVisible(true);
        };

        loginButton.addActionListener(e -> doLogin.run());
        passwordField.addActionListener(e -> doLogin.run());
    }
}
