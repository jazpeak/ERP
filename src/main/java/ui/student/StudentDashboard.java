package ui.student;

import service.StudentService;
import service.AuthService;
import service.AdminService;
import domain.Section;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;

public class StudentDashboard extends JFrame {
    private final StudentService svc;
    private final int studentId;
    private final AdminService adminSvc;
    private final AuthService authSvc;
    private boolean maintenanceWarnShown = false;
    private final DefaultTableModel catalogModel = new DefaultTableModel(
            new Object[] { "Section ID", "Code", "Title", "Credits", "Capacity", "Instructor", "Deadline" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel ttModel = new DefaultTableModel(
            new Object[] { "Course", "Section", "Day", "Time", "Room" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel grModel = new DefaultTableModel(
            new Object[] { "Section", "Course", "Endsem", "Midsem", "Quiz", "Final Grade" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel regModel = new DefaultTableModel(
            new Object[] { "Section ID", "Code", "Title", "Credits", "Instructor", "Schedule" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JLabel maintenanceBanner;
    private JTextArea noticesArea;
    private JPasswordField settingsOldPw;
    private JPasswordField settingsNewPw;
    private JButton settingsChangePwBtn;

    public StudentDashboard(StudentService svc, int studentId, AdminService adminSvc, AuthService authSvc) {
        this.svc = svc;
        this.studentId = studentId;
        this.adminSvc = adminSvc;
        this.authSvc = authSvc;

        setTitle("Student Portal - University ERP");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(245, 245, 245));

        maintenanceBanner = new JLabel(" ⚠ MAINTENANCE MODE ACTIVE - Read Only", SwingConstants.CENTER);
        maintenanceBanner.setFont(new Font("Segoe UI", Font.BOLD, 14));
        maintenanceBanner.setBackground(new Color(255, 193, 7));
        maintenanceBanner.setForeground(new Color(60, 60, 60));
        maintenanceBanner.setOpaque(true);
        maintenanceBanner.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        maintenanceBanner.setVisible(adminSvc.isMaintenanceOn());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(41, 128, 185));
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Student Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutButton.setBackground(new Color(231, 76, 60));
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

        JPanel catalogPanel = createCatalogPanel();
        tabs.addTab("📚 Course Catalog", catalogPanel);

        JPanel registrationsPanel = createRegistrationsPanel();
        tabs.addTab("📝 Registrations", registrationsPanel);

        JPanel timetablePanel = createTimetablePanel();
        tabs.addTab("📅 My Timetable", timetablePanel);

        JPanel gradesPanel = createGradesPanel();
        tabs.addTab("📊 Grades", gradesPanel);

        JPanel settingsPanel = createSettingsPanel();
        tabs.addTab("⚙️ Settings", settingsPanel);
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == settingsPanel) {
                applyMaintenanceStateToSettings();
            }
        });

        JPanel noticePanel = createNotificationsPanel();

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tabs, BorderLayout.CENTER);
        centerPanel.add(noticePanel, BorderLayout.SOUTH);

        JPanel topContainer = new JPanel(new BorderLayout());

        if (adminSvc.isMaintenanceOn()) {
            topContainer.add(maintenanceBanner, BorderLayout.NORTH);
        }
        topContainer.add(topBar, BorderLayout.CENTER);

        mainContainer.add(topContainer, BorderLayout.NORTH);
        mainContainer.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainContainer);
        refreshCatalog();
        refreshRegistrations();
        refreshTimetable();
        refreshGrades();
        refreshNotifications();
    }

    private JPanel createRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("My Registrations");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(52, 73, 94));

        JTable regTable = new JTable(regModel);
        regTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regTable.setRowHeight(35);
        regTable.setShowGrid(true);
        regTable.setGridColor(new Color(230, 230, 230));
        regTable.setSelectionBackground(new Color(41, 128, 185, 50));
        regTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(regTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JButton dropBtn = createStyledButton("Drop", new Color(231, 76, 60));
        JButton refreshBtn = createStyledButton("Refresh", new Color(52, 152, 219));
        refreshBtn.addActionListener(e -> refreshRegistrations());
        dropBtn.addActionListener(e -> {
            if (adminSvc.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(this, "Maintenance mode is ON. Drop is disabled.", "Maintenance Mode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = regTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a registration to drop", "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int sectionId = Integer
                    .parseInt(regModel.getValueAt(regTable.convertRowIndexToModel(row), 0).toString());
            String msg = svc.dropSection(studentId, sectionId);
            JOptionPane.showMessageDialog(this, msg, "Drop Course", JOptionPane.INFORMATION_MESSAGE);
            refreshRegistrations();
            refreshTimetable();
            refreshCatalog();
            grModel.setRowCount(0);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(dropBtn);
        buttonPanel.add(refreshBtn);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshRegistrations() {
        regModel.setRowCount(0);
        for (Object[] row : svc.getRegistrationsWithDetails(studentId)) {
            regModel.addRow(row);
        }
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Available Courses");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(52, 73, 94));

        JTable catalogTable = new JTable(catalogModel);
        catalogTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        catalogTable.setRowHeight(35);
        catalogTable.setShowGrid(true);
        catalogTable.setGridColor(new Color(230, 230, 230));
        catalogTable.setSelectionBackground(new Color(41, 128, 185, 50));
        catalogTable.setRowSorter(new TableRowSorter<>(catalogModel));

        catalogTable.getColumnModel().getColumn(0).setMinWidth(0);
        catalogTable.getColumnModel().getColumn(0).setMaxWidth(0);
        catalogTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(catalogTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton registerBtn = createStyledButton("Register", new Color(46, 204, 113));
        JButton refreshBtn = createStyledButton("Refresh", new Color(52, 152, 219));

        registerBtn.addActionListener(e -> {
            if (adminSvc.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(this, "Maintenance mode is ON. Registration is disabled.",
                        "Maintenance Mode", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = catalogTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a course to register", "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int sectionId = Integer
                    .parseInt(catalogModel.getValueAt(catalogTable.convertRowIndexToModel(row), 0).toString());
            String msg = svc.registerSection(studentId, sectionId);
            JOptionPane.showMessageDialog(this, msg, "Registration", JOptionPane.INFORMATION_MESSAGE);
            refreshCatalog();
            refreshRegistrations();
            refreshTimetable();
        });

        refreshBtn.addActionListener(e -> {
            maintenanceBanner.setVisible(adminSvc.isMaintenanceOn());
            refreshCatalog();
        });

        buttonPanel.add(registerBtn);
        buttonPanel.add(refreshBtn);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("My Weekly Schedule");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(52, 73, 94));

        JTable ttTable = new JTable(ttModel);
        ttTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ttTable.setRowHeight(35);
        ttTable.setShowGrid(true);
        ttTable.setGridColor(new Color(230, 230, 230));
        ttTable.setSelectionBackground(new Color(41, 128, 185, 50));
        ttTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(ttTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JButton loadBtn = createStyledButton("Refresh Timetable", new Color(52, 152, 219));
        loadBtn.addActionListener(e -> refreshTimetable());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loadBtn);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshTimetable() {
        List<Section> list = svc.timetable(studentId);
        ttModel.setRowCount(0);
        for (Section s : list) {
            ttModel.addRow(
                    new Object[] { s.getCourseCode(), s.getSectionId(), s.getDay(), s.getTimeSlot(), s.getRoom() });
        }
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("My Grades");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(52, 73, 94));

        JTable grTable = new JTable(grModel);
        grTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        grTable.setRowHeight(35);
        grTable.setShowGrid(true);
        grTable.setGridColor(new Color(230, 230, 230));
        grTable.setSelectionBackground(new Color(41, 128, 185, 50));
        grTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(grTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JButton loadBtn = createStyledButton("Refresh Grades", new Color(52, 152, 219));
        JButton exportBtn = createStyledButton("Export Transcript (CSV)", new Color(155, 89, 182));

        loadBtn.addActionListener(e -> refreshGrades());

        exportBtn.addActionListener(e -> {
            byte[] data = svc.exportTranscriptCsv(studentId);
            JFileChooser ch = new JFileChooser();
            ch.setSelectedFile(new java.io.File("transcript.csv"));
            if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileOutputStream fos = new FileOutputStream(ch.getSelectedFile())) {
                    fos.write(data);
                    JOptionPane.showMessageDialog(this, "Transcript exported successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to save transcript", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loadBtn);
        buttonPanel.add(exportBtn);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshGrades() {
        List<Object[]> rows = svc.gradesPivotTableData(studentId);
        grModel.setRowCount(0);
        for (Object[] r : rows)
            grModel.addRow(r);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel headerLabel = new JLabel("Account Settings");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(52, 73, 94));

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);

        JPanel changePasswordPanel = new JPanel(new GridBagLayout());
        changePasswordPanel.setBackground(Color.WHITE);
        changePasswordPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Change Password",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 16)));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setBackground(Color.WHITE);

        settingsOldPw = new JPasswordField(15);
        settingsNewPw = new JPasswordField(15);
        settingsChangePwBtn = createStyledButton("Change Password", new Color(52, 152, 219));
        settingsChangePwBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        boolean mOn = adminSvc.isMaintenanceOn();
        settingsOldPw.setEditable(!mOn);
        settingsNewPw.setEditable(!mOn);
        settingsOldPw.setFocusable(!mOn);
        settingsNewPw.setFocusable(!mOn);

        java.awt.event.FocusListener fl = new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (adminSvc.isMaintenanceOn()) {
                    if (!maintenanceWarnShown) {
                        maintenanceWarnShown = true;
                        JOptionPane.showMessageDialog(StudentDashboard.this,
                                "Maintenance mode is ON: cannot change password", "Warning",
                                JOptionPane.WARNING_MESSAGE);
                        javax.swing.Timer t = new javax.swing.Timer(500, ev -> maintenanceWarnShown = false);
                        t.setRepeats(false);
                        t.start();
                    }
                    ((javax.swing.JComponent) e.getComponent()).transferFocus();
                }
            }
        };
        settingsOldPw.addFocusListener(fl);
        settingsNewPw.addFocusListener(fl);

        java.awt.event.MouseAdapter ml = new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (adminSvc.isMaintenanceOn() && !maintenanceWarnShown) {
                    maintenanceWarnShown = true;
                    JOptionPane.showMessageDialog(StudentDashboard.this,
                            "Maintenance mode is ON: cannot change password", "Warning", JOptionPane.WARNING_MESSAGE);
                    javax.swing.Timer t = new javax.swing.Timer(500, ev -> maintenanceWarnShown = false);
                    t.setRepeats(false);
                    t.start();
                }
            }
        };
        settingsOldPw.addMouseListener(ml);
        settingsNewPw.addMouseListener(ml);
        settingsChangePwBtn.setEnabled(!adminSvc.isMaintenanceOn());

        JLabel oldPwLabel = new JLabel("Old Password:");
        oldPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel newPwLabel = new JLabel("New Password:");
        newPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formPanel.add(oldPwLabel);
        formPanel.add(settingsOldPw);
        formPanel.add(newPwLabel);
        formPanel.add(settingsNewPw);
        formPanel.add(new JLabel(""));
        formPanel.add(settingsChangePwBtn);

        changePasswordPanel.add(formPanel);

        settingsChangePwBtn.addActionListener(e -> {
            if (adminSvc.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(this, "Maintenance mode is ON: cannot change password", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String o = new String(settingsOldPw.getPassword());
            String n = new String(settingsNewPw.getPassword());
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
                settingsOldPw.setText("");
                settingsNewPw.setText("");
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        contentPanel.add(changePasswordPanel, gbc);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void applyMaintenanceStateToSettings() {
        boolean on = adminSvc.isMaintenanceOn();
        if (settingsChangePwBtn != null)
            settingsChangePwBtn.setEnabled(!on);
        if (settingsOldPw != null) {
            settingsOldPw.setEditable(!on);
            settingsOldPw.setFocusable(!on);
        }
        if (settingsNewPw != null) {
            settingsNewPw.setEditable(!on);
            settingsNewPw.setFocusable(!on);
        }
        maintenanceWarnShown = false;
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        panel.setPreferredSize(new Dimension(0, 150));

        JLabel noticeLabel = new JLabel("📢 Notifications");
        noticeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        noticesArea = new JTextArea();
        noticesArea.setEditable(false);
        noticesArea.setLineWrap(true);
        noticesArea.setWrapStyleWord(true);
        noticesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noticesArea.setBackground(new Color(252, 252, 252));

        JScrollPane scrollPane = new JScrollPane(noticesArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JButton refreshBtn = createSmallButton("Refresh");
        JButton clearBtn = createSmallButton("Clear Seen");

        refreshBtn.addActionListener(e -> refreshNotifications());
        clearBtn.addActionListener(e -> {
            svc.markAdminNoticeRead(studentId);
            List<Integer> ids = svc.getUnreadSectionIds(studentId);
            for (Integer id : ids)
                svc.markSectionNoticeRead(studentId, id);
            refreshNotifications();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(noticeLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(refreshBtn);
        btnPanel.add(clearBtn);
        topPanel.add(btnPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

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
        button.setPreferredSize(new Dimension(150, 35));
        return button;
    }

    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void refreshCatalog() {
        catalogModel.setRowCount(0);
        for (Object[] row : svc.browseCatalogWithDetails()) {
            catalogModel.addRow(row);
        }
    }

    private void refreshNotifications() {
        StringBuilder sb = new StringBuilder();
        String adminTxt = adminSvc.getNotice();
        if (svc.hasUnreadAdminNotice(studentId) && adminTxt != null && !adminTxt.isBlank()) {
            sb.append("[Admin Notification]\n").append(adminTxt).append("\n\n");
        }
        List<String> secs = svc.getSectionNotices(studentId);
        for (String s : secs) {
            sb.append(s).append("\n\n");
        }
        noticesArea.setText(sb.length() > 0 ? sb.toString() : "No new notifications");
    }
}
