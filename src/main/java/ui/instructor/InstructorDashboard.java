package ui.instructor;

import service.InstructorService;
import service.AuthService;
import service.AdminService;
import domain.Section;
import domain.Enrollment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InstructorDashboard extends JFrame {
    private final InstructorService svc;
    private final int instructorId;
    private final AdminService adminSvc;
    private final AuthService authSvc;

    private final DefaultTableModel sectionsModel = new DefaultTableModel(
            new Object[] { "Section ID", "Course", "Day", "Time", "Room", "Capacity", "Seats Left", "Semester",
                    "Year", "Deadline" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JLabel maintenanceBanner;
    private JTable sectionsTable;
    private JPasswordField settingsOldPw;
    private JPasswordField settingsNewPw;
    private JButton settingsChangePwBtn;
    private boolean maintenanceWarnShown = false;

    public InstructorDashboard(InstructorService svc, int instructorId, AdminService adminSvc, AuthService authSvc) {
        this.svc = svc;
        this.instructorId = instructorId;
        this.adminSvc = adminSvc;
        this.authSvc = authSvc;

        setTitle("Instructor Portal - University ERP");
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
        topBar.setBackground(new Color(142, 68, 173));
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Instructor Dashboard");
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

        JPanel sectionsPanel = createSectionsPanel();
        tabs.addTab("📚 My Sections", sectionsPanel);

        JPanel gradesPanel = createGradeManagementPanel();
        tabs.addTab("📊 Grade Management", gradesPanel);

        JPanel settingsPanel = createSettingsPanel();
        tabs.addTab("⚙️ Settings", settingsPanel);
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == settingsPanel) {
                applyMaintenanceStateToSettings();
            }
        });

        JPanel topContainer = new JPanel(new BorderLayout());

        if (adminSvc.isMaintenanceOn()) {
            topContainer.add(maintenanceBanner, BorderLayout.NORTH);
        }
        topContainer.add(topBar, BorderLayout.CENTER);

        mainContainer.add(topContainer, BorderLayout.NORTH);
        mainContainer.add(tabs, BorderLayout.CENTER);

        setContentPane(mainContainer);
        refreshSections();
    }

    private JPanel createSectionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("My Teaching Sections");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(52, 73, 94));

        sectionsTable = new JTable(sectionsModel);
        sectionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sectionsTable.setRowHeight(35);
        sectionsTable.setShowGrid(true);
        sectionsTable.setGridColor(new Color(230, 230, 230));
        sectionsTable.setSelectionBackground(new Color(142, 68, 173, 50));
        sectionsTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JButton refreshBtn = createStyledButton("Refresh", new Color(52, 152, 219));
        JButton viewRosterBtn = createStyledButton("View Roster", new Color(46, 204, 113));

        refreshBtn.addActionListener(e -> {
            maintenanceBanner.setVisible(adminSvc.isMaintenanceOn());
            refreshSections();
        });

        viewRosterBtn.addActionListener(e -> {
            int row = sectionsTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a section", "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int sectionId = Integer
                    .parseInt(sectionsModel.getValueAt(sectionsTable.convertRowIndexToModel(row), 0).toString());
            showRoster(sectionId);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewRosterBtn);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createGradeManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Grade Entry & Management");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(52, 73, 94));

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select Section"));

        JComboBox<String> sectionCombo = new JComboBox<>();
        sectionCombo.setPreferredSize(new Dimension(200, 30));
        JButton loadSectionBtn = createStyledButton("Load Gradebook", new Color(52, 152, 219));

        selectionPanel.add(new JLabel("Section:"));
        selectionPanel.add(sectionCombo);
        selectionPanel.add(loadSectionBtn);

        JTable gradebookTable = new JTable();
        gradebookTable.setRowHeight(30);
        gradebookTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gradebookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(gradebookTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Gradebook (Double-click to edit)"));

        JButton computeFinalBtn = createStyledButton("Compute Final Grades", new Color(230, 126, 34));
        JButton showStatsBtn = createStyledButton("Show Statistics", new Color(155, 89, 182));
        JButton configBtn = createStyledButton("Configure Grading", new Color(41, 128, 185));
        JButton exportBtn = createStyledButton("Export CSV", new Color(46, 204, 113));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(computeFinalBtn);
        btnPanel.add(showStatsBtn);
        btnPanel.add(configBtn);
        btnPanel.add(exportBtn);

        JTextArea statsArea = new JTextArea(5, 40);
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane statsScroll = new JScrollPane(statsArea);
        statsScroll.setBorder(BorderFactory.createTitledBorder("Statistics"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(selectionPanel, BorderLayout.WEST);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.add(headerLabel, BorderLayout.NORTH);
        headerContainer.add(topPanel, BorderLayout.CENTER);

        panel.add(headerContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statsScroll, BorderLayout.SOUTH);

        Runnable maintenanceControls = () -> {
            boolean on = adminSvc.isMaintenanceOn();
            computeFinalBtn.setEnabled(!on);
            configBtn.setEnabled(!on);
        };
        maintenanceControls.run();

        loadSectionBtn.addActionListener(e -> {
            sectionCombo.removeAllItems();
            List<Section> sections = svc.getMySections(instructorId);
            for (Section s : sections) {
                sectionCombo.addItem(s.getSectionId() + " - " + s.getCourseCode());
            }
            maintenanceControls.run();
        });

        sectionCombo.addActionListener(e -> {
            if (sectionCombo.getSelectedItem() == null)
                return;
            String selected = sectionCombo.getSelectedItem().toString();
            int sectionId = Integer.parseInt(selected.split(" - ")[0]);
            refreshGradebook(gradebookTable, sectionId);
            maintenanceControls.run();
        });

        configBtn.addActionListener(e -> {
            if (sectionCombo.getSelectedItem() == null)
                return;
            String selected = sectionCombo.getSelectedItem().toString();
            int sectionId = Integer.parseInt(selected.split(" - ")[0]);
            showConfigDialog(sectionId);
            refreshGradebook(gradebookTable, sectionId);
        });

        computeFinalBtn.addActionListener(e -> {
            if (adminSvc.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(this, "Maintenance mode is ON.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (sectionCombo.getSelectedItem() == null)
                return;
            String selected = sectionCombo.getSelectedItem().toString();
            int sectionId = Integer.parseInt(selected.split(" - ")[0]);
            String msg = svc.computeFinalGrades(sectionId);
            JOptionPane.showMessageDialog(this, msg);
            refreshGradebook(gradebookTable, sectionId);
        });

        showStatsBtn.addActionListener(e -> {
            if (sectionCombo.getSelectedItem() == null)
                return;
            String selected = sectionCombo.getSelectedItem().toString();
            int sectionId = Integer.parseInt(selected.split(" - ")[0]);
            statsArea.setText(svc.getClassStats(sectionId));
        });

        exportBtn.addActionListener(e -> {
            if (sectionCombo.getSelectedItem() == null)
                return;
            String selected = sectionCombo.getSelectedItem().toString();
            int sectionId = Integer.parseInt(selected.split(" - ")[0]);

            byte[] data = svc.exportGradesCsv(sectionId);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Grades CSV");
            fileChooser.setSelectedFile(new java.io.File("grades_section_" + sectionId + ".csv"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                    fos.write(data);
                    JOptionPane.showMessageDialog(this, "Grades exported successfully!");
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void refreshGradebook(JTable table, int sectionId) {
        Map<String, Integer> weights = svc.getGradingWeights(sectionId);
        if (weights.isEmpty()) {
            weights.put("Quiz", 20);
            weights.put("Midsem", 30);
            weights.put("Endsem", 50);
        }

        java.util.Vector<String> columns = new java.util.Vector<>();
        columns.add("Enroll ID");
        columns.add("Student ID");
        columns.add("Roll No");
        List<String> components = new java.util.ArrayList<>(weights.keySet());
        java.util.Collections.sort(components); 
        columns.addAll(components);
        columns.add("Final Score");
        columns.add("Final Grade");

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                
                int finalScoreCol = 3 + components.size();
                if (adminSvc.isMaintenanceOn())
                    return false;
                return column >= 3 && column != finalScoreCol;
            }
        };

        List<Enrollment> enrollments = svc.getSectionEnrollments(sectionId);
        List<domain.Student> students = svc.getStudentsInSection(sectionId);
        Map<Integer, domain.Student> studentMap = new HashMap<>();
        for (domain.Student s : students)
            studentMap.put(s.getUserId(), s);

        Map<Integer, Map<String, Object>> allGrades = svc.getStudentGrades(sectionId);

        for (Enrollment e : enrollments) {
            java.util.Vector<Object> row = new java.util.Vector<>();
            row.add(e.getEnrollId());
            row.add(e.getStudentId());
            domain.Student s = studentMap.get(e.getStudentId());
            row.add(s != null ? s.getRollNo() : "N/A");

            Map<String, Object> studentGrades = allGrades.getOrDefault(e.getEnrollId(), new HashMap<>());
            double calculatedScore = 0.0;

            for (String comp : components) {
                Object val = studentGrades.getOrDefault(comp, 0.0);
                row.add(val);

                try {
                    double score = Double.parseDouble(val.toString());
                    int weight = weights.getOrDefault(comp, 0);
                    calculatedScore += (score * weight / 100.0);
                } catch (NumberFormatException ex) {
                    
                }
            }

            row.add(String.format("%.2f", calculatedScore)); 
            row.add(studentGrades.getOrDefault("Final Grade", "")); 
            model.addRow(row);
        }

        table.setModel(model);

        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (row < 0 || col < 0)
                    return;

                int enrollId = (int) model.getValueAt(row, 0);
                String colName = model.getColumnName(col);
                Object val = model.getValueAt(row, col);

                if (colName.equals("Final Grade")) {
                    svc.enterGrade(enrollId, components.get(0), 0, val.toString()); 
                } else if (components.contains(colName)) {
                    try {
                        double score = Double.parseDouble(val.toString());
                        svc.enterGrade(enrollId, colName, score);
                        
                    } catch (NumberFormatException ex) {
                        
                    }
                }
            }
        });
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
                        JOptionPane.showMessageDialog(InstructorDashboard.this,
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
                    JOptionPane.showMessageDialog(InstructorDashboard.this,
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

    private void showRoster(int sectionId) {
        List<Enrollment> enrollments = svc.getSectionEnrollments(sectionId);

        DefaultTableModel rosterModel = new DefaultTableModel(
                new Object[] { "Enrollment ID", "Student ID", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Enrollment e : enrollments) {
            rosterModel.addRow(new Object[] { e.getEnrollId(), e.getStudentId(), e.getStatus() });
        }

        JTable rosterTable = new JTable(rosterModel);
        rosterTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rosterTable.setRowHeight(30);

        JOptionPane.showMessageDialog(this,
                new JScrollPane(rosterTable),
                "Class Roster - Section " + sectionId,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 35));
        return button;
    }

    private void showConfigDialog(int sectionId) {
        JDialog dialog = new JDialog(this, "Configure Grading Weights", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        Map<String, Integer> weights = svc.getGradingWeights(sectionId);
        if (weights.isEmpty()) {
            weights.put("Quiz", 20);
            weights.put("Midsem", 30);
            weights.put("Endsem", 50);
        }

        DefaultTableModel model = new DefaultTableModel(new Object[] { "Component", "Weight (%)" }, 0);
        for (var entry : weights.entrySet()) {
            model.addRow(new Object[] { entry.getKey(), entry.getValue() });
        }

        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton removeBtn = new JButton("Remove");
        JButton saveBtn = new JButton("Save");

        addBtn.addActionListener(e -> model.addRow(new Object[] { "New Component", 0 }));
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0)
                model.removeRow(row);
        });

        saveBtn.addActionListener(e -> {
            if (adminSvc.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(dialog, "Maintenance mode is ON: cannot save grading weights", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (table.isEditing())
                table.getCellEditor().stopCellEditing();
            Map<String, Integer> newWeights = new HashMap<>();
            int sum = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                String comp = model.getValueAt(i, 0).toString().trim();
                int w = Integer.parseInt(model.getValueAt(i, 1).toString());
                newWeights.put(comp, w);
                sum += w;
            }
            if (sum != 100) {
                JOptionPane.showMessageDialog(dialog, "Weights must sum to 100. Current sum: " + sum, "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            svc.saveGradingWeights(sectionId, newWeights);
            JOptionPane.showMessageDialog(dialog, "Weights saved!");
            dialog.dispose();
        });

        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(saveBtn);

        dialog.add(sp, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void refreshSections() {
        sectionsModel.setRowCount(0);
        List<Section> sections = svc.getMySections(instructorId);
        for (Section s : sections) {
            sectionsModel.addRow(new Object[] {
                    s.getSectionId(), s.getCourseCode(), s.getDay(),
                    s.getTimeSlot(), s.getRoom(), s.getCapacity(),
                    s.getSeatsLeft(),
                    s.getSemester(), s.getYear(),
                    s.getRegDeadline() != null ? s.getRegDeadline().toString() : "None"
            });
        }
    }
}
