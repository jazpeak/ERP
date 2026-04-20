package ui.admin;

import service.AdminService;
import service.AuthService;
import domain.Section;
import domain.Course;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private final AdminService svc;
    private final AuthService authSvc;

    private final DefaultTableModel coursesModel = new DefaultTableModel(new Object[] { "Code", "Title", "Credits" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel sectionsModel = new DefaultTableModel(new Object[] { "ID", "Course", "Instructor",
            "Day", "Time", "Room", "Capacity", "Seats Left", "Semester", "Year", "Deadline" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel instructorUsersModel = new DefaultTableModel(
            new Object[] { "User ID", "Username", "Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel studentUsersModel = new DefaultTableModel(
            new Object[] { "Roll Number", "Username", "Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JCheckBox maintenanceCheckbox;
    private JTextArea noticeArea;
    private JLabel maintenanceBanner; 

    public AdminDashboard(AdminService svc, AuthService authSvc) {
        this.svc = svc;
        this.authSvc = authSvc;

        setTitle("Admin Portal - University ERP");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(245, 245, 245));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(231, 76, 60));
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Administrator Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutButton.setBackground(new Color(44, 62, 80));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            setVisible(false);
            dispose();
            ui.Main.showLogin();
        });

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(logoutButton, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBackground(Color.WHITE);

        JPanel dashboardPanel = createDashboardPanel();
        tabs.addTab("🏠 Dashboard", dashboardPanel);

        JPanel usersPanel = createUserManagementPanel();
        tabs.addTab("👥 User Management", usersPanel);

        JPanel coursesPanel = createCourseManagementPanel();
        tabs.addTab("📚 Courses", coursesPanel);

        JPanel sectionsPanel = createSectionManagementPanel();
        tabs.addTab("📝 Sections", sectionsPanel);

        JPanel settingsPanel = createSystemSettingsPanel();
        tabs.addTab("⚙️ System Settings", settingsPanel);

        maintenanceBanner = new JLabel(" ⚠ MAINTENANCE MODE ACTIVE - Read Only", SwingConstants.CENTER);
        maintenanceBanner.setFont(new Font("Segoe UI", Font.BOLD, 14));
        maintenanceBanner.setBackground(new Color(255, 193, 7));
        maintenanceBanner.setForeground(new Color(60, 60, 60));
        maintenanceBanner.setOpaque(true);
        maintenanceBanner.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        maintenanceBanner.setVisible(svc.isMaintenanceOn());

        mainContainer.add(maintenanceBanner, BorderLayout.NORTH);
        mainContainer.add(topBar, BorderLayout.CENTER); 
                                                        
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(maintenanceBanner, BorderLayout.NORTH);
        topWrapper.add(topBar, BorderLayout.CENTER);

        mainContainer.add(topWrapper, BorderLayout.NORTH);
        mainContainer.add(tabs, BorderLayout.CENTER);

        setContentPane(mainContainer);
        refreshCourses();
        refreshSections();
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("System Overview");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(52, 73, 94));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setPreferredSize(new Dimension(0, 300)); 
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsPanel.add(createStatCard("Total Courses", String.valueOf(svc.getTotalCourses()), new Color(52, 152, 219)));
        statsPanel
                .add(createStatCard("Total Sections", String.valueOf(svc.getTotalSections()), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Total Enrollments", String.valueOf(svc.getTotalEnrollments()),
                new Color(155, 89, 182)));

        contentPanel.add(headerLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        JPanel usersListPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        usersListPanel.setBackground(Color.WHITE);
        usersListPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        usersListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel instructorsPanel = createUsersTablePanel("Instructors", instructorUsersModel, "Instructor");
        JPanel studentsPanel = createUsersTablePanel("Students", studentUsersModel, "Student");
        usersListPanel.add(instructorsPanel);
        usersListPanel.add(studentsPanel);

        contentPanel.add(usersListPanel);
        contentPanel.add(Box.createVerticalGlue());

        panel.add(contentPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("User Management");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(52, 73, 94));

        JPanel addStudentPanel = new JPanel(new GridBagLayout());
        addStudentPanel.setBackground(new Color(236, 240, 241));
        addStudentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Add New Student"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField stuUsername = new JTextField(15);
        JPasswordField stuPassword = new JPasswordField(15);
        JTextField stuRollNo = new JTextField(15);
        JTextField stuProgram = new JTextField(15);
        JTextField stuYear = new JTextField(5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        addStudentPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        addStudentPanel.add(stuUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addStudentPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        addStudentPanel.add(stuPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addStudentPanel.add(new JLabel("Roll Number:"), gbc);
        gbc.gridx = 1;
        addStudentPanel.add(stuRollNo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addStudentPanel.add(new JLabel("Program:"), gbc);
        gbc.gridx = 1;
        addStudentPanel.add(stuProgram, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addStudentPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        addStudentPanel.add(stuYear, gbc);

        JButton addStudentBtn = createStyledButton("Add Student", new Color(46, 204, 113));
        gbc.gridx = 1;
        gbc.gridy = 5;
        addStudentPanel.add(addStudentBtn, gbc);

        JPanel addInstructorPanel = new JPanel(new GridBagLayout());
        addInstructorPanel.setBackground(new Color(236, 240, 241));
        addInstructorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Add New Instructor"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JTextField instUsername = new JTextField(15);
        JPasswordField instPassword = new JPasswordField(15);
        JTextField instDept = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        addInstructorPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        addInstructorPanel.add(instUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addInstructorPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        addInstructorPanel.add(instPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addInstructorPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        addInstructorPanel.add(instDept, gbc);

        JButton addInstructorBtn = createStyledButton("Add Instructor", new Color(52, 152, 219));
        gbc.gridx = 1;
        gbc.gridy = 3;
        addInstructorPanel.add(addInstructorBtn, gbc);

        addStudentBtn.addActionListener(e -> {
            String un = stuUsername.getText().trim();
            String pw = new String(stuPassword.getPassword());
            String roll = stuRollNo.getText().trim();
            String prog = stuProgram.getText().trim();
            String year = stuYear.getText().trim();

            if (un.isEmpty() || pw.isEmpty() || roll.isEmpty() || prog.isEmpty() || year.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int y = Integer.parseInt(year);
                String msg = svc.addStudentUser(un, pw, roll, prog, y);
                JOptionPane.showMessageDialog(this, msg, "Add Student", JOptionPane.INFORMATION_MESSAGE);
                stuUsername.setText("");
                stuPassword.setText("");
                stuRollNo.setText("");
                stuProgram.setText("");
                stuYear.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Year must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addInstructorBtn.addActionListener(e -> {
            String un = instUsername.getText().trim();
            String pw = new String(instPassword.getPassword());
            String dept = instDept.getText().trim();

            if (un.isEmpty() || pw.isEmpty() || dept.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String msg = svc.addInstructorUser(un, pw, dept);
            JOptionPane.showMessageDialog(this, msg, "Add Instructor", JOptionPane.INFORMATION_MESSAGE);
            instUsername.setText("");
            instDept.setText("");
        });

        JPanel addAdminPanel = new JPanel(new GridBagLayout());
        addAdminPanel.setBackground(new Color(236, 240, 241));
        addAdminPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Add New Admin"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JTextField adminUsername = new JTextField(15);
        JPasswordField adminPassword = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        addAdminPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        addAdminPanel.add(adminUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addAdminPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        addAdminPanel.add(adminPassword, gbc);

        JButton addAdminBtn = createStyledButton("Add Admin", new Color(155, 89, 182));
        gbc.gridx = 1;
        gbc.gridy = 2;
        addAdminPanel.add(addAdminBtn, gbc);

        addAdminBtn.addActionListener(e -> {
            String un = adminUsername.getText().trim();
            String pw = new String(adminPassword.getPassword());

            if (un.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String msg = svc.addAdminUser(un, pw);
            JOptionPane.showMessageDialog(this, msg, "Add Admin", JOptionPane.INFORMATION_MESSAGE);
            adminUsername.setText("");
            adminPassword.setText("");
        });

        JPanel formsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        formsPanel.setBackground(Color.WHITE);
        formsPanel.add(addStudentPanel);
        formsPanel.add(addInstructorPanel);
        formsPanel.add(addAdminPanel);

        JPanel accountAdminPanel = new JPanel(new GridBagLayout());
        accountAdminPanel.setBackground(new Color(236, 240, 241));
        accountAdminPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Account Administration"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.fill = GridBagConstraints.HORIZONTAL;
        gbc3.insets = new Insets(5, 5, 5, 5);

        JTextField unlockUsername = new JTextField(15);
        JButton unlockBtn = createStyledButton("Unlock User", new Color(230, 126, 34));
        JButton lockBtn = createStyledButton("Lock User", new Color(192, 57, 43));

        gbc3.gridx = 0;
        gbc3.gridy = 0;
        accountAdminPanel.add(new JLabel("Manage Account:"), gbc3);
        gbc3.gridx = 1;
        accountAdminPanel.add(unlockUsername, gbc3);
        gbc3.gridx = 1;
        gbc3.gridy = 1;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setBackground(new Color(236, 240, 241));
        btnPanel.add(unlockBtn);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(lockBtn);
        accountAdminPanel.add(btnPanel, gbc3);

        JTextField setPwUsername = new JTextField(15);
        JPasswordField setPwNew = new JPasswordField(15);
        JButton setPwBtn = createStyledButton("Set User Password", new Color(52, 152, 219));

        gbc3.gridx = 0;
        gbc3.gridy = 2;
        accountAdminPanel.add(new JLabel("Username:"), gbc3);
        gbc3.gridx = 1;
        accountAdminPanel.add(setPwUsername, gbc3);
        gbc3.gridx = 0;
        gbc3.gridy = 3;
        accountAdminPanel.add(new JLabel("New Password:"), gbc3);
        gbc3.gridx = 1;
        accountAdminPanel.add(setPwNew, gbc3);
        gbc3.gridx = 1;
        gbc3.gridy = 4;
        accountAdminPanel.add(setPwBtn, gbc3);

        unlockBtn.addActionListener(e -> {
            String un = unlockUsername.getText().trim();
            if (un.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String msg = svc.unlockUser(un);
            JOptionPane.showMessageDialog(this, msg, "Unlock User", JOptionPane.INFORMATION_MESSAGE);
            if (msg.toLowerCase().startsWith("account unlocked")) {
                unlockUsername.setText("");
            }
        });

        lockBtn.addActionListener(e -> {
            String un = unlockUsername.getText().trim();
            if (un.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String msg = svc.lockUser(un);
            JOptionPane.showMessageDialog(this, msg, "Lock User", JOptionPane.INFORMATION_MESSAGE);
            if (msg.toLowerCase().startsWith("account locked")) {
                unlockUsername.setText("");
            }
        });

        setPwBtn.addActionListener(e -> {
            String un = setPwUsername.getText().trim();
            String pw = new String(setPwNew.getPassword());
            if (un.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String msg = svc.setPasswordForUser(un, pw);
            JOptionPane.showMessageDialog(this, msg, "Set User Password", JOptionPane.INFORMATION_MESSAGE);
            if (msg.toLowerCase().startsWith("password set")) {
                setPwUsername.setText("");
                setPwNew.setText("");
            }
        });

        formsPanel.add(accountAdminPanel);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(formsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCourseManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Course Management");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(52, 73, 94));

        JTable coursesTable = new JTable(coursesModel);
        coursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        coursesTable.setRowHeight(35);
        coursesTable.setShowGrid(true);
        coursesTable.setGridColor(new Color(230, 230, 230));
        coursesTable.setSelectionBackground(new Color(231, 76, 60, 50));
        coursesTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel addCoursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addCoursePanel.setBackground(new Color(236, 240, 241));
        addCoursePanel.setBorder(BorderFactory.createTitledBorder("Add New Course"));

        JTextField codeField = new JTextField(10);
        JTextField titleField = new JTextField(20);
        JTextField creditsField = new JTextField(5);
        JButton addCourseBtn = createStyledButton("Add Course", new Color(46, 204, 113));
        JButton refreshBtn = createStyledButton("Refresh", new Color(52, 152, 219));

        addCoursePanel.add(new JLabel("Code:"));
        addCoursePanel.add(codeField);
        addCoursePanel.add(new JLabel("Title:"));
        addCoursePanel.add(titleField);
        addCoursePanel.add(new JLabel("Credits:"));
        addCoursePanel.add(creditsField);
        addCoursePanel.add(addCourseBtn);
        addCoursePanel.add(refreshBtn);

        addCourseBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            String title = titleField.getText().trim();
            String creditsStr = creditsField.getText().trim();

            if (code.isEmpty() || title.isEmpty() || creditsStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int credits = Integer.parseInt(creditsStr);
                if (credits <= 0) {
                    JOptionPane.showMessageDialog(this, "Credits must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String msg = svc.addCourse(code, title, credits);
                JOptionPane.showMessageDialog(this, msg, "Add Course", JOptionPane.INFORMATION_MESSAGE);
                codeField.setText("");
                titleField.setText("");
                creditsField.setText("");
                refreshCourses();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Credits must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshBtn.addActionListener(e -> refreshCourses());

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addCoursePanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSectionManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Section Management");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(52, 73, 94));

        JTable sectionsTable = new JTable(sectionsModel);
        sectionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sectionsTable.setRowHeight(35);
        sectionsTable.setShowGrid(true);
        sectionsTable.setGridColor(new Color(230, 230, 230));
        sectionsTable.setSelectionBackground(new Color(231, 76, 60, 50));
        sectionsTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel addSectionPanel = new JPanel(new GridBagLayout());
        addSectionPanel.setBackground(new Color(236, 240, 241));
        addSectionPanel.setBorder(BorderFactory.createTitledBorder("Add New Section"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField courseCodeField = new JTextField(10);
        JTextField instructorIdField = new JTextField(10);
        JTextField dayField = new JTextField(10);
        JTextField timeField = new JTextField(10);
        JTextField roomField = new JTextField(10);
        JTextField capacityField = new JTextField(5);
        JTextField semesterField = new JTextField(10);
        JTextField yearField = new JTextField(5);
        JTextField deadlineField = new JTextField(10); 

        gbc.gridx = 0;
        gbc.gridy = 0;
        addSectionPanel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        addSectionPanel.add(courseCodeField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        addSectionPanel.add(new JLabel("Instructor ID:"), gbc);
        gbc.gridx = 3;
        addSectionPanel.add(instructorIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addSectionPanel.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1;
        addSectionPanel.add(dayField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        addSectionPanel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 3;
        addSectionPanel.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addSectionPanel.add(new JLabel("Room:"), gbc);
        gbc.gridx = 1;
        addSectionPanel.add(roomField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        addSectionPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 3;
        addSectionPanel.add(capacityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addSectionPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        addSectionPanel.add(semesterField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        addSectionPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 3;
        addSectionPanel.add(yearField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addSectionPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        addSectionPanel.add(deadlineField, gbc);

        JButton addSectionBtn = createStyledButton("Add Section", new Color(46, 204, 113));
        JButton refreshBtn = createStyledButton("Refresh", new Color(52, 152, 219));
        JButton assignInstrBtn = createStyledButton("Assign Instructor", new Color(230, 126, 34));
        JButton removeSectionBtn = createStyledButton("Remove Section", new Color(231, 76, 60));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(addSectionBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(assignInstrBtn);
        buttonPanel.add(removeSectionBtn);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        addSectionPanel.add(buttonPanel, gbc);

        addSectionBtn.addActionListener(e -> {
            try {
                String courseCode = courseCodeField.getText().trim();
                Integer instructorId = instructorIdField.getText().trim().isEmpty() ? null
                        : Integer.parseInt(instructorIdField.getText().trim());
                String day = dayField.getText().trim();
                String time = timeField.getText().trim();
                String room = roomField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());
                String semester = semesterField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                String deadlineStr = deadlineField.getText().trim();
                java.sql.Date regDeadline = null;

                if (!deadlineStr.isEmpty()) {
                    try {
                        regDeadline = java.sql.Date.valueOf(deadlineStr);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid date format (YYYY-MM-DD)", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                if (courseCode.isEmpty() || day.isEmpty() || time.isEmpty() || room.isEmpty() || semester.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill required fields", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity must be positive", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int result = svc.createSection(courseCode, instructorId, day, time, room, capacity, semester, year,
                        regDeadline);
                if (result == -3) {
                    JOptionPane.showMessageDialog(this, "Invalid Course Code: Course does not exist", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (result == -2) {
                    JOptionPane.showMessageDialog(this, "Invalid Instructor ID: Instructor does not exist", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Section created successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    courseCodeField.setText("");
                    instructorIdField.setText("");
                    dayField.setText("");
                    timeField.setText("");
                    roomField.setText("");
                    capacityField.setText("");
                    semesterField.setText("");
                    yearField.setText("");
                    deadlineField.setText("");
                    refreshSections();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create section", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshBtn.addActionListener(e -> refreshSections());

        assignInstrBtn.addActionListener(e -> {
            String sectionIdStr = JOptionPane.showInputDialog(this, "Enter Section ID:");
            String instructorIdStr = JOptionPane.showInputDialog(this, "Enter Instructor ID:");

            if (sectionIdStr != null && instructorIdStr != null) {
                try {
                    int sectionId = Integer.parseInt(sectionIdStr);
                    int instructorId = Integer.parseInt(instructorIdStr);
                    String msg = svc.assignInstructorToSection(sectionId, instructorId);
                    JOptionPane.showMessageDialog(this, msg, "Assign Instructor", JOptionPane.INFORMATION_MESSAGE);
                    refreshSections();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid ID format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        removeSectionBtn.addActionListener(e -> {
            Integer sid = null;
            int row = sectionsTable.getSelectedRow();
            if (row >= 0) {
                int modelRow = sectionsTable.convertRowIndexToModel(row);
                Object val = sectionsModel.getValueAt(modelRow, 0);
                if (val != null)
                    sid = Integer.parseInt(val.toString());
            }
            if (sid == null) {
                String sectionIdStr = JOptionPane.showInputDialog(this, "Enter Section ID:");
                if (sectionIdStr == null || sectionIdStr.trim().isEmpty())
                    return;
                try {
                    sid = Integer.parseInt(sectionIdStr.trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid ID format", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            String msg = svc.removeSection(sid);
            int type = msg.toLowerCase().startsWith("section removed") ? JOptionPane.INFORMATION_MESSAGE
                    : JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(this, msg, "Remove Section", type);
            if (type == JOptionPane.INFORMATION_MESSAGE)
                refreshSections();
        });

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addSectionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void clearSectionFields() {
        
    }

    private JPanel createSystemSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel headerLabel = new JLabel("System Settings");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(52, 73, 94));

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 40, 40));
        gridPanel.setBackground(Color.WHITE);

        JPanel noticePanel = new JPanel(new BorderLayout(10, 10));
        noticePanel.setBackground(Color.WHITE);
        noticePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Global Notice/Announcement",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 16)));

        noticeArea = new JTextArea();
        noticeArea.setLineWrap(true);
        noticeArea.setWrapStyleWord(true);
        noticeArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noticeArea.setText(svc.getNotice() != null ? svc.getNotice() : "");

        JScrollPane noticeScroll = new JScrollPane(noticeArea);
        noticeScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JButton saveNoticeBtn = createStyledButton("Save Notice", new Color(52, 152, 219));
        saveNoticeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveNoticeBtn.addActionListener(e -> {
            String notice = noticeArea.getText().trim();
            svc.setNotice(notice);
            JOptionPane.showMessageDialog(this, "Global notice saved successfully", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        noticePanel.add(noticeScroll, BorderLayout.CENTER);
        noticePanel.add(saveNoticeBtn, BorderLayout.SOUTH);

        JPanel changePasswordPanel = new JPanel(new GridBagLayout());
        changePasswordPanel.setBackground(Color.WHITE);
        changePasswordPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Change Password",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 16)));

        JPanel pwForm = new JPanel(new GridLayout(3, 2, 10, 20));
        pwForm.setBackground(Color.WHITE);

        JPasswordField oldPw = new JPasswordField(15);
        JPasswordField newPw = new JPasswordField(15);
        JButton changePwBtn = createStyledButton("Change Password", new Color(52, 152, 219));
        changePwBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel oldPwLabel = new JLabel("Old Password:");
        oldPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel newPwLabel = new JLabel("New Password:");
        newPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        pwForm.add(oldPwLabel);
        pwForm.add(oldPw);
        pwForm.add(newPwLabel);
        pwForm.add(newPw);
        pwForm.add(new JLabel(""));
        pwForm.add(changePwBtn);

        changePasswordPanel.add(pwForm);

        changePwBtn.addActionListener(e -> {
            var session = authSvc.getCurrent();
            if (svc.isMaintenanceOn() && (session == null || session.role != access.Role.Admin)) {
                JOptionPane.showMessageDialog(this, "Maintenance mode is ON: cannot change password", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String o = new String(oldPw.getPassword());
            String n = new String(newPw.getPassword());
            if (o.isEmpty() || n.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            var s = authSvc.getCurrent();
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Not logged in", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = authSvc.changePassword(s.username, o, n);
            JOptionPane.showMessageDialog(this, ok ? "Password changed successfully" : "Incorrect old password",
                    ok ? "Success" : "Error", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (ok) {
                oldPw.setText("");
                newPw.setText("");
            }
        });

        JPanel backupPanel = new JPanel(new GridBagLayout());
        backupPanel.setBackground(Color.WHITE);
        backupPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Database Backup & Restore",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 16)));

        JPanel backupBtns = new JPanel(new GridLayout(1, 2, 20, 0));
        backupBtns.setBackground(Color.WHITE);

        JButton backupAllBtn = createStyledButton("Backup All (ZIP)", new Color(46, 204, 113));
        backupAllBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton restoreAllBtn = createStyledButton("Restore All (ZIP)", new Color(231, 76, 60));
        restoreAllBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        backupBtns.add(backupAllBtn);
        backupBtns.add(restoreAllBtn);
        backupPanel.add(backupBtns);

        backupAllBtn.addActionListener(e -> {
            try {
                byte[] data = svc.backupAllDatabases();
                javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                chooser.setSelectedFile(new java.io.File("databases_backup.zip"));
                if (chooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                    java.nio.file.Files.write(chooser.getSelectedFile().toPath(), data);
                    javax.swing.JOptionPane.showMessageDialog(this, "All databases backup saved", "Success",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Backup failed: " + ex.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });

        restoreAllBtn.addActionListener(e -> {
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
            chooser.setDialogTitle("Select ZIP backup");
            if (chooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File f = chooser.getSelectedFile();
                String msg = svc.restoreAllDatabases(f);
                javax.swing.JOptionPane.showMessageDialog(this, msg, "Restore All",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel maintenancePanel = new JPanel(new GridBagLayout());
        maintenancePanel.setBackground(Color.WHITE);
        maintenancePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Maintenance Mode",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 16)));

        maintenanceCheckbox = new JCheckBox("Enable Maintenance Mode");
        maintenanceCheckbox.setFont(new Font("Segoe UI", Font.BOLD, 16));
        maintenanceCheckbox.setBackground(Color.WHITE);
        maintenanceCheckbox.setSelected(svc.isMaintenanceOn());

        maintenanceCheckbox.addActionListener(e -> {
            boolean on = maintenanceCheckbox.isSelected();
            String msg = svc.toggleMaintenance(on);
            maintenanceBanner.setVisible(on);
            maintenanceBanner.revalidate();
            maintenanceBanner.repaint();
            getContentPane().revalidate();
            getContentPane().repaint();
            JOptionPane.showMessageDialog(this, msg, "Maintenance Mode", JOptionPane.INFORMATION_MESSAGE);
        });

        maintenancePanel.add(maintenanceCheckbox);

        gridPanel.add(noticePanel);
        gridPanel.add(changePasswordPanel);
        gridPanel.add(backupPanel);
        gridPanel.add(maintenancePanel);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 35));
        return button;
    }

    private void refreshCourses() {
        coursesModel.setRowCount(0);
        List<Course> courses = svc.getAllCourses();
        for (Course c : courses) {
            coursesModel.addRow(new Object[] { c.getCode(), c.getTitle(), c.getCredits() });
        }
    }

    private void refreshSections() {
        sectionsModel.setRowCount(0);
        List<Section> sections = svc.getAllSections();
        for (Section s : sections) {
            sectionsModel.addRow(new Object[] {
                    s.getSectionId(), s.getCourseCode(), s.getInstructorId(),
                    s.getDay(), s.getTimeSlot(), s.getRoom(),
                    s.getCapacity(), s.getSeatsLeft(), s.getSemester(), s.getYear(),
                    s.getRegDeadline() != null ? s.getRegDeadline().toString() : "None"
            });
        }
    }

    private JPanel createUsersTablePanel(String title, DefaultTableModel model, String role) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel searchBar = new JPanel(new BorderLayout(8, 8));
        searchBar.setBackground(Color.WHITE);
        JTextField search = new JTextField();
        search.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton refreshBtn = createStyledButton("Refresh", new Color(52, 152, 219));
        searchBar.add(new JLabel("Search username"), BorderLayout.WEST);
        searchBar.add(search, BorderLayout.CENTER);
        searchBar.add(refreshBtn, BorderLayout.EAST);

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        p.add(searchBar, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        Runnable load = () -> {
            model.setRowCount(0);
            java.util.List<domain.User> users = svc.listUsersByRole(role);
            for (domain.User u : users) {
                Object idVal = u.getUserId();
                if (u instanceof domain.Student) {
                    idVal = ((domain.Student) u).getRollNo();
                }
                model.addRow(new Object[] { idVal, u.getUsername(), u.getStatus() });
            }
        };
        load.run();

        refreshBtn.addActionListener(e -> load.run());

        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            private void apply() {
                String txt = search.getText().trim();
                javax.swing.RowFilter<DefaultTableModel, Object> rf;
                if (txt.isEmpty())
                    rf = null;
                else
                    rf = javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(txt), 1);
                javax.swing.table.TableRowSorter<DefaultTableModel> sorter = (javax.swing.table.TableRowSorter<DefaultTableModel>) table
                        .getRowSorter();
                sorter.setRowFilter(rf);
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                apply();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                apply();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                apply();
            }
        };
        search.getDocument().addDocumentListener(dl);

        return p;
    }
}
