// StudEase – A system that makes student data management easy.
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

class Student 
{
    int id;
    long contactno;
    String name, year, department ;

    public Student(int id, String name,  String year, String department, long contactno) 
    {
        this.id = id;
        this.name = name;
        this.year = year;
        this.department = department;
        this.contactno = contactno;
        
    }
}

public class StudentInformationSystem 
{
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField idField, nameField, yearField, departmentField, contactnoField ;
    private Connection conn;
    private ArrayList<Student> students;

    public StudentInformationSystem() 
    {
        students = new ArrayList<>();
        frame = new JFrame("Student Information System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        String[] columns = {"Student ID", "Name", "Year", "Department", "Contact No"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        loadStudents();

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("Student ID: "));
        idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("Name: "));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Year: "));
        yearField = new JTextField();
        panel.add(yearField);

        panel.add(new JLabel("Department: "));
        departmentField = new JTextField();
        panel.add(departmentField);

        panel.add(new JLabel("Contact No: "));
        contactnoField = new JTextField();
        panel.add(contactnoField);

        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> addStudent());
        panel.add(addButton);

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(e -> deleteStudent());
        panel.add(deleteButton);

        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);

        connectDatabase();
    }

    private void connectDatabase() 
    {
        try 
        {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_db", "root", "root");
            System.out.println("Database Connected");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    private boolean isDuplicate(int id) 
    {
        for (Student s : students) 
        {
            if (s.id == id) 
            {
                return true;
            }
        }
        return false;
    }
    
    private void loadStudents() 
    {
        try 
        {
            connectDatabase();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
            
            while (rs.next()) 
            {
                Student student = new Student(rs.getInt("id"), rs.getString("name"), rs.getString("year"), rs.getString("department"), rs.getLong("contactno"));
                students.add(student);
                model.addRow(new Object[]{student.id, student.name, student.year, student.department, student.contactno});
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    private void addStudent() 
    {
        try 
        {
            int id = Integer.parseInt(idField.getText());
            
            if (isDuplicate(id)) 
            {
                JOptionPane.showMessageDialog(frame, "Student ID already exists!");
                return;
            }
            
            String name = nameField.getText();
            String year = yearField.getText();
            String department = departmentField.getText();
            long contactno = Long.parseLong(contactnoField.getText());
            
            String sql = "INSERT INTO students (id, name, year, department, contactno) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, name);
            pst.setString(3, year);
            pst.setString(4, department);
            pst.setLong(5, contactno);
            pst.executeUpdate();
            
            Student student = new Student(id, name, year, department, contactno);
            students.add(student);
            model.addRow(new Object[]{id, name, year, department, contactno});
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    private void deleteStudent() 
    {
        int row = table.getSelectedRow();
        if (row == -1) 
        {
            JOptionPane.showMessageDialog(frame, "Select a student to delete");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        try 
        {
            PreparedStatement pst = conn.prepareStatement("DELETE FROM students WHERE id = ?");
            pst.setInt(1, id);
            pst.executeUpdate();
            
            students.removeIf(student -> student.id == id);
            model.removeRow(row);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) 
    {
        new StudentInformationSystem();
    }
}
