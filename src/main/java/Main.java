import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/a3";

        String user = "postgres";
        String password = "db";

        Connection connection = null;

        Scanner s = new Scanner(System.in);

        //setup connection
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e){
            System.out.println(e);
        }

        String user_input = "";

        System.out.println("Welcome to the student database!");
        System.out.println("There are 5 commands:");
        System.out.println("\t 1. Display all students");
        System.out.println("\t 2. Add a student");
        System.out.println("\t 3. Update the email of a students");
        System.out.println("\t 4. Delete a student");
        System.out.println("\t 5. Quit the application");

        while (!user_input.equals("5")){
            user_input = getUserValue(s, "What would you like to do? (1-5): ");

            switch (user_input) {
                case "1":
                    try {
                        getAllStudents(connection);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case "2":
                    try {
                        String first_name = getUserValue(s, "Enter the first name: ");
                        String last_name = getUserValue(s, "Enter the last name: ");
                        String email = getUserValue(s, "Enter the email (something@example.com): ");
                        String enrollment_date = getUserValue(s, "Enter the enrollment date (YYYY-MM-DD): ");
                        addStudent(connection, first_name, last_name, email, enrollment_date);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case "3":
                    try {
                        String id = getUserValue(s, "Enter the id of the student whose email you would like to update: ");
                        String new_email = getUserValue(s, "Enter the new email (something@example.com): ");
                        updateStudentEmail(connection, id, new_email);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case "4":
                    try {
                        String id = getUserValue(s, "Enter the id of the student you would like to delete: ");
                        deleteStudent(connection, id);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case "5":
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter number between 1 and 5.");
                    break;
            }
        }
        connection.close();
        s.close();
    }

    public static String getUserValue(Scanner s, String prompt) {
        System.out.print(prompt);
        String val = s.nextLine();
        return val;
    }

    protected static void getAllStudents(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.executeQuery("SELECT * FROM students");
            try (ResultSet resultSet = statement.getResultSet()){
                while(resultSet.next()){
                    System.out.print(resultSet.getString("student_id") + "\t");
                    System.out.print(resultSet.getString("first_name") + "\t");
                    System.out.print(resultSet.getString("last_name") + "\t");
                    System.out.print(resultSet.getString("email") + "\t");
                    System.out.println(resultSet.getString("enrollment_date"));
                }
            }
        }
    }

    protected static void addStudent(Connection connection, String first_name, String last_name, String email, String enrollment_date) throws SQLException {
        String sql = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?)";
        try ( PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set values for the parameters
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            statement.setString(3, email);
            statement.setDate(4, Date.valueOf(enrollment_date));
            statement.executeUpdate();
            System.out.println("\nData inserted successfully.");
        }
    }

    protected static void updateStudentEmail(Connection connection, String id, String new_email) throws SQLException {
        String sql = "UPDATE students SET email = ? WHERE student_id = ?";

        try ( PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, new_email);
            statement.setInt(2, Integer.parseInt(id));
            // Execute the query and get the number of rows affected
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Email update successful for student with ID " + id + ".");
            } else {
                System.out.println("No student found with ID " + id + ". Email update failed.");
            }
        }
    }

    protected static void deleteStudent(Connection connection, String id) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try ( PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Integer.parseInt(id));
            // Execute the query and get the number of rows affected
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Deletion successful for student with ID " + id + ".");
            } else {
                System.out.println("No student found with ID " + id + ". Deletion failed.");
            }
        }
    }
}
