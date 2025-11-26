import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class StudentManagementSystem extends JFrame {
    private DataManager dataManager;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentManagementSystem() {
        dataManager = new DataManager();
        initializeUI();
        loadStudentData();
    }

    private void initializeUI() {
        setTitle("Student Information Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Student Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Email", "Phone", "Course", "Avg Grade", "Attendance %"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addBtn = new JButton("Add Student");
        JButton editBtn = new JButton("Edit Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton gradesBtn = new JButton("Manage Grades");
        JButton attendanceBtn = new JButton("Mark Attendance");
        JButton viewBtn = new JButton("View Details");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> showAddStudentDialog());
        editBtn.addActionListener(e -> showEditStudentDialog());
        deleteBtn.addActionListener(e -> deleteStudent());
        gradesBtn.addActionListener(e -> showGradesDialog());
        attendanceBtn.addActionListener(e -> showAttendanceDialog());
        viewBtn.addActionListener(e -> showStudentDetails());
        refreshBtn.addActionListener(e -> loadStudentData());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(gradesBtn);
        buttonPanel.add(attendanceBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(refreshBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadStudentData() {
        tableModel.setRowCount(0);
        for (Student student : dataManager.getAllStudents()) {
            Object[] row = {
                    student.getId(),
                    student.getName(),
                    student.getEmail(),
                    student.getPhone(),
                    student.getCourse(),
                    String.format("%.2f", student.getAverageGrade()),
                    String.format("%.1f%%", student.getAttendancePercentage())
            };
            tableModel.addRow(row);
        }
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add Student", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField courseField = new JTextField();

        panel.add(new JLabel("Student ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Course:"));
        panel.add(courseField);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String course = courseField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "ID and Name are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dataManager.studentExists(id)) {
                JOptionPane.showMessageDialog(dialog, "Student ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = new Student(id, name, email, phone, course);
            dataManager.addStudent(student);
            loadStudentData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Student added successfully!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditStudentDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Student student = dataManager.getStudent(id);

        JDialog dialog = new JDialog(this, "Edit Student", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField idField = new JTextField(student.getId());
        idField.setEditable(false);
        JTextField nameField = new JTextField(student.getName());
        JTextField emailField = new JTextField(student.getEmail());
        JTextField phoneField = new JTextField(student.getPhone());
        JTextField courseField = new JTextField(student.getCourse());

        panel.add(new JLabel("Student ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Course:"));
        panel.add(courseField);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            student.setName(nameField.getText().trim());
            student.setEmail(emailField.getText().trim());
            student.setPhone(phoneField.getText().trim());
            student.setCourse(courseField.getText().trim());

            dataManager.updateStudent(student);
            loadStudentData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Student updated successfully!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dataManager.deleteStudent(id);
            loadStudentData();
            JOptionPane.showMessageDialog(this, "Student deleted successfully!");
        }
    }

    private void showGradesDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Student student = dataManager.getStudent(id);

        JDialog dialog = new JDialog(this, "Manage Grades - " + student.getName(), true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Subject", "Grade"};
        DefaultTableModel gradeModel = new DefaultTableModel(columns, 0);
        JTable gradeTable = new JTable(gradeModel);

        for (Map.Entry<String, Double> entry : student.getGrades().entrySet()) {
            gradeModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        JScrollPane scrollPane = new JScrollPane(gradeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField subjectField = new JTextField(15);
        JTextField gradeField = new JTextField(8);
        JButton addBtn = new JButton("Add Grade");

        inputPanel.add(new JLabel("Subject:"));
        inputPanel.add(subjectField);
        inputPanel.add(new JLabel("Grade:"));
        inputPanel.add(gradeField);
        inputPanel.add(addBtn);

        addBtn.addActionListener(e -> {
            String subject = subjectField.getText().trim();
            String gradeStr = gradeField.getText().trim();

            if (subject.isEmpty() || gradeStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
                return;
            }

            try {
                double grade = Double.parseDouble(gradeStr);
                if (grade < 0 || grade > 100) {
                    JOptionPane.showMessageDialog(dialog, "Grade must be between 0 and 100!");
                    return;
                }

                student.addGrade(subject, grade);
                dataManager.updateStudent(student);
                gradeModel.addRow(new Object[]{subject, grade});
                loadStudentData();

                subjectField.setText("");
                gradeField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid grade value!");
            }
        });

        panel.add(inputPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAttendanceDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Student student = dataManager.getStudent(id);

        JDialog dialog = new JDialog(this, "Mark Attendance - " + student.getName(), true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JTextField dateField = new JTextField(sdf.format(new Date()));
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Present", "Absent"});

        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Status:"));
        panel.add(statusBox);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            String date = dateField.getText().trim();
            boolean present = statusBox.getSelectedIndex() == 0;

            student.markAttendance(date, present);
            dataManager.updateStudent(student);
            loadStudentData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Attendance marked successfully!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(saveBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showStudentDetails() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Student student = dataManager.getStudent(id);

        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(student.getId()).append("\n");
        details.append("Name: ").append(student.getName()).append("\n");
        details.append("Email: ").append(student.getEmail()).append("\n");
        details.append("Phone: ").append(student.getPhone()).append("\n");
        details.append("Course: ").append(student.getCourse()).append("\n\n");

        details.append("=== GRADES ===\n");
        if (student.getGrades().isEmpty()) {
            details.append("No grades recorded\n");
        } else {
            for (Map.Entry<String, Double> entry : student.getGrades().entrySet()) {
                details.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            details.append("\nAverage: ").append(String.format("%.2f", student.getAverageGrade())).append("\n");
        }

        details.append("\n=== ATTENDANCE ===\n");
        if (student.getAttendance().isEmpty()) {
            details.append("No attendance records\n");
        } else {
            details.append("Total Days: ").append(student.getAttendance().size()).append("\n");
            details.append("Attendance: ").append(String.format("%.1f%%", student.getAttendancePercentage()));
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Student Details", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentManagementSystem system = new StudentManagementSystem();
            system.setVisible(true);
        });
    }
}