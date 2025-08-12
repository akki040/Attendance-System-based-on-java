import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;

public class AttendanceSystemGUI extends JFrame {
    private static final String DATA_FILE = "attendance_records.txt";
    private JTextField nameField, dateField, statusField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public AttendanceSystemGUI() {
        setTitle("Student Attendance Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Mark Attendance"));

        nameField = new JTextField();
        dateField = new JTextField();
        statusField = new JTextField();

        formPanel.add(new JLabel("Student Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Status (Present/Absent):"));
        formPanel.add(statusField);

        JButton markButton = new JButton("Mark Attendance");
        formPanel.add(markButton);

        markButton.addActionListener(e -> markAttendance());

        // Table panel
        tableModel = new DefaultTableModel(new String[]{"Name", "Date", "Status"}, 0);
        table = new JTable(tableModel);
        loadAttendance();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Attendance Records"));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Attendance"));
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        searchPanel.add(new JLabel("Student Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        searchButton.addActionListener(e -> searchAttendance());
        refreshButton.addActionListener(e -> loadAttendance());

        // Layout
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);
    }

    private void markAttendance() {
        String name = nameField.getText().trim();
        String date = dateField.getText().trim();
        String status = statusField.getText().trim();

        if (name.isEmpty() || date.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(DATA_FILE, true)))) {
            out.println(name + "," + date + "," + status);
            JOptionPane.showMessageDialog(this, "Attendance marked successfully!");
            loadAttendance();
            nameField.setText("");
            dateField.setText("");
            statusField.setText("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
        }
    }

    private void loadAttendance() {
        tableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    tableModel.addRow(new Object[]{parts[0], parts[1], parts[2]});
                }
            }
        } catch (FileNotFoundException e) {
            // No records yet
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private void searchAttendance() {
        String searchName = searchField.getText().trim().toLowerCase();
        if (searchName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search.");
            return;
        }

        tableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].trim().toLowerCase().equals(searchName)) {
                    tableModel.addRow(new Object[]{parts[0], parts[1], parts[2]});
                    found = true;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(this, "No records found for: " + searchName);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AttendanceSystemGUI().setVisible(true);
        });
    }
}
