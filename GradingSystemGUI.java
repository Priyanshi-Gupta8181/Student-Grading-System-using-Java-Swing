import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Comparator;

class Student {
    String name;
    int rollNo;
    int[] marks;
    int total;
    double percentage;
    char grade;
    static String branch;
    static String section;

    public Student(String name, int rollNo, int numSubjects) {
            this.rollNo = rollNo;
        this.marks = new int[numSubjects];
        this.name = name;
    }

    public void calculateResults() {
        total = 0;
        for (int m : marks) total += m;
        percentage = (double) total / marks.length;
    }

    public void assignGrade() {
        if (percentage >= 90) grade = 'A';
        else if (percentage >= 75) grade = 'B';
        else if (percentage >= 60) grade = 'C';
        else if (percentage >= 40) grade = 'D';
        else grade = 'F';
    }
}

class HonorsStudent extends Student {
    public HonorsStudent(String name, int rollNo, int numSubjects) {
        super(name, rollNo, numSubjects);
    }

    @Override
    public void assignGrade() {
        if (percentage >= 95) grade = 'A';
        else if (percentage >= 85) grade = 'B';
        else if (percentage >= 70) grade = 'C';
        else if (percentage >= 50) grade = 'D';
        else grade = 'F';
    }
}

public class GradingSystemGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel container;
    private JTextField branchField, sectionField, numStudentsField, numSubjectsField;
    private JTextField[] subjectFields;
    private JTextField nameField, rollField;
    private JTextField[] marksFields;
    private JButton nextStudentButton;
    private JLabel statusLabel;

    private int numStudents, numSubjects, currentStudent = 0;
    private String[] subjectNames;
    private Student[] students;

    public GradingSystemGUI() {
        setTitle("Student Grading System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Step 1: Input general info
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        branchField = new JTextField();
        sectionField = new JTextField();
        numStudentsField = new JTextField();
        numSubjectsField = new JTextField();

        infoPanel.add(new JLabel("Branch:"));
        infoPanel.add(branchField);
        infoPanel.add(new JLabel("Section:"));
        infoPanel.add(sectionField);
        infoPanel.add(new JLabel("Number of Students:"));
        infoPanel.add(numStudentsField);
        infoPanel.add(new JLabel("Number of Subjects:"));
        infoPanel.add(numSubjectsField);

        JButton nextButton = new JButton("Next ➜");
        nextButton.addActionListener(e -> openSubjectPanel());
        infoPanel.add(new JLabel());
        infoPanel.add(nextButton);

        container.add(infoPanel, "info");

        // Step 2: Subject entry panel (will be built later)
        // Step 3: Student entry panel (will be built later)

        add(container);
        setVisible(true);
    }

    private void openSubjectPanel() {
        try {
            numStudents = Integer.parseInt(numStudentsField.getText());
            numSubjects = Integer.parseInt(numSubjectsField.getText());

            Student.branch = branchField.getText();
            Student.section = sectionField.getText();

            JPanel subjectPanel = new JPanel(new BorderLayout(10, 10));
            subjectPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JPanel grid = new JPanel(new GridLayout(numSubjects, 2, 10, 10));
            subjectFields = new JTextField[numSubjects];
            for (int i = 0; i < numSubjects; i++) {
                grid.add(new JLabel("Subject " + (i + 1) + ":"));
                subjectFields[i] = new JTextField();
                grid.add(subjectFields[i]);
            }

            JButton nextButton = new JButton("Next ➜");
            nextButton.addActionListener(e -> openStudentPanel());

            subjectPanel.add(new JLabel("Enter Subject Names", SwingConstants.CENTER), BorderLayout.NORTH);
            subjectPanel.add(grid, BorderLayout.CENTER);
            subjectPanel.add(nextButton, BorderLayout.SOUTH);

            container.add(subjectPanel, "subjects");
            cardLayout.show(container, "subjects");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values!");
        }
    }

    private void openStudentPanel() {
        subjectNames = new String[numSubjects];
        for (int i = 0; i < numSubjects; i++) {
            subjectNames[i] = subjectFields[i].getText();
        }

        students = new Student[numStudents];
        showStudentForm();
    }

    private void showStudentForm() {
        JPanel studentPanel = new JPanel(new BorderLayout(10, 10));
        studentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(numSubjects + 3, 2, 10, 10));
        form.add(new JLabel("Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Roll No:"));
        rollField = new JTextField();
        form.add(rollField);

        marksFields = new JTextField[numSubjects];
        for (int i = 0; i < numSubjects; i++) {
            form.add(new JLabel(subjectNames[i] + " Marks:"));
            marksFields[i] = new JTextField();
            form.add(marksFields[i]);
        }

        nextStudentButton = new JButton("Save & Next ➜");
        nextStudentButton.addActionListener(e -> saveStudentData());

        statusLabel = new JLabel("Student " + (currentStudent + 1) + " of " + numStudents, SwingConstants.CENTER);

        studentPanel.add(statusLabel, BorderLayout.NORTH);
        studentPanel.add(form, BorderLayout.CENTER);
        studentPanel.add(nextStudentButton, BorderLayout.SOUTH);

        container.add(studentPanel, "student");
        cardLayout.show(container, "student");
    }

    private void saveStudentData() {
        try {
            String name = nameField.getText();
            int roll = Integer.parseInt(rollField.getText());

            Student s = (currentStudent == 0) ? new HonorsStudent(name, roll, numSubjects)
                                              : new Student(name, roll, numSubjects);

            for (int i = 0; i < numSubjects; i++) {
                s.marks[i] = Integer.parseInt(marksFields[i].getText());
            }

            s.calculateResults();
            s.assignGrade();
            students[currentStudent] = s;

            currentStudent++;

            if (currentStudent < numStudents) {
                nameField.setText("");
                rollField.setText("");
                for (JTextField tf : marksFields) tf.setText("");
                statusLabel.setText("Student " + (currentStudent + 1) + " of " + numStudents);
            } else {
                showResults();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for roll/marks!");
        }
    }

    private void showResults() {
        Arrays.sort(students, Comparator.comparingDouble((Student s) -> s.percentage).reversed());

        String[] columns = {"Rank", "Name", "Roll No", "Percentage", "Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = 0; i < students.length; i++) {
            Student s = students[i];
            model.addRow(new Object[]{
                    i + 1, s.name, s.rollNo,
                    String.format("%.2f", s.percentage), s.grade
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel topperLabel = new JLabel(
                "Topper: " + students[0].name + " (Roll " + students[0].rollNo + ") - " +
                String.format("%.2f", students[0].percentage) + "%", SwingConstants.CENTER);
        topperLabel.setFont(new Font("Arial", Font.BOLD, 14));

        resultPanel.add(new JLabel("Final Ranking", SwingConstants.CENTER), BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        resultPanel.add(topperLabel, BorderLayout.SOUTH);

        container.add(resultPanel, "results");
        cardLayout.show(container, "results");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GradingSystemGUI::new);
    }
}
