import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/a3";

        String user = "postgres";
        String password = "db";

        Connection connection = null;

        //setup connection
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e){
            System.out.println(e);
        }

        Scanner sc = new Scanner(System.in);
        int user_input = 0;

        System.out.println("Welcome to the student database!");
        System.out.println("There are 5 commands:");
        System.out.println("\t 1. Display all students");
        System.out.println("\t 2. Add a student");
        System.out.println("\t 3. Update the email of a students");
        System.out.println("\t 4. Delete a student");
        System.out.println("\t 5. Quit the application");

        while (user_input != 5){
            try {
                System.out.print("What would you like to do? (1-5): ");
                user_input = sc.nextInt();
                sc.nextLine();
            } catch (Exception e){
                System.out.println(e);
            }

            switch (user_input) {
                case 1:
                    try {
                        getAllStudents(connection);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case 2:
                    try {
                        addStudent(connection);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case 3:
                    try {
                        updateStudentEmail(connection);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case 4:
                    try {
                        deleteStudent(connection);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case 5:
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                    break;
            }
        }
        sc.close();
        connection.close();
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

    protected static void addStudent(Connection connection) throws SQLException {

        Scanner scanner = new Scanner(System.in);

        String first_name = "", last_name = "", email = "", enrollment_date = "";
        try {
            System.out.print("Enter the first name: ");
            first_name = scanner.nextLine();

            System.out.print("Enter the last name: ");
            last_name = scanner.nextLine();

            System.out.print("Enter the email (something@example.com): ");
            email = scanner.nextLine();

            System.out.print("Enter the enrollment date (YYYY-MM-DD): ");
            enrollment_date = scanner.nextLine();
        } catch (Exception e){
            System.out.println(e);
        } finally {
            scanner.close();
        }

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

    protected static void updateStudentEmail(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        String id = "", new_email = "";

        try {
            System.out.print("Enter the id of the student whose email you would like to update: ");
            id = scanner.nextLine();

            System.out.print("Enter the new email (something@example.com): ");
            new_email = scanner.nextLine();
        } catch (Exception e){
            System.out.println(e);
        } finally {
            scanner.close();
        }

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

    protected static void deleteStudent(Connection connection) throws SQLException {

        Scanner scanner = new Scanner(System.in);

        String id = "";

        try {
            System.out.print("Enter the id of the student whose email you would like to update: ");
            id = scanner.nextLine();
        } catch (Exception e){
            System.out.println(e);
        } finally {
            scanner.close();
        }

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
