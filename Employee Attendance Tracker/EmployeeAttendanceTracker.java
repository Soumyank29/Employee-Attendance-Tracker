import java.io.*;
import java.nio.file.*;
import java.util.*;

public class EmployeeAttendanceTracker {

    private static final String FILE_NAME = "attendance.txt";

    private ArrayList<Employee> employees = new ArrayList<>();
    private HashMap<String, Employee> employeeMap = new HashMap<>();

    private Scanner sc = new Scanner(System.in);
    private int nextId = 101;

    static class Employee {
        String id;
        String name;
        int present;
        int absent;

        Employee(String id, String name) {
            this.id = id;
            this.name = name;
            this.present = 0;
            this.absent = 0;
        }

        double getAttendancePercentage() {
            int total = present + absent;

            if (total == 0) {
                return 0.0;
            }

            return (present * 100.0) / total;
        }
    }

    public static void main(String[] args) {
        EmployeeAttendanceTracker app = new EmployeeAttendanceTracker();

        app.loadData();
        app.runMenu();
    }

    void runMenu() {
        while (true) {
            System.out.println("\n===== EMPLOYEE ATTENDANCE TRACKER =====");
            System.out.println("1. Add Employee");
            System.out.println("2. Mark Attendance (P/A)");
            System.out.println("3. Search Employee");
            System.out.println("4. Attendance Report");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addEmployee();
                    break;

                case "2":
                    markAttendance();
                    break;

                case "3":
                    searchEmployee();
                    break;

                case "4":
                    generateReport();
                    break;

                case "5":
                    saveData();
                    System.out.println("Data saved successfully. Exiting...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Enter 1 to 5.");
            }
        }
    }

    void addEmployee() {
        System.out.print("Enter employee name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Employee name cannot be empty.");
            return;
        }

        name = name.replace("|", " ");

        String id = String.format(Locale.US, "EMP%03d", nextId);
        nextId++;

        Employee emp = new Employee(id, name);

        employees.add(emp);
        employeeMap.put(id, emp);

        System.out.println("Employee added. ID: " + id + " | "
                + name + " | Present: 0 | Absent: 0");

        saveData();
    }

    void markAttendance() {
        System.out.print("Enter employee ID: ");
        String id = sc.nextLine().trim().toUpperCase();

        Employee emp = employeeMap.get(id);

        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.print("Enter status (P for Present / A for Absent): ");
        String status = sc.nextLine().trim().toUpperCase();

        if (status.equals("P")) {
            emp.present++;
        } else if (status.equals("A")) {
            emp.absent++;
        } else {
            System.out.println("Invalid status. Enter P or A.");
            return;
        }

        System.out.println("Attendance marked. " + emp.id
                + " | " + emp.name
                + " | Present: " + emp.present
                + " | Absent: " + emp.absent);

        saveData();
    }

    void searchEmployee() {
        System.out.print("Enter employee ID to search: ");
        String id = sc.nextLine().trim().toUpperCase();

        Employee emp = employeeMap.get(id);

        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        displayEmployee(emp);
    }

    void generateReport() {
        System.out.println("\n========== ATTENDANCE REPORT ==========");

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        for (Employee emp : employees) {
            displayEmployee(emp);
        }
    }

    void displayEmployee(Employee emp) {
        int total = emp.present + emp.absent;

        if (total == 0) {
            System.out.println(emp.id + " | " + emp.name
                    + " | No attendance recorded");
        } else {
            System.out.printf(
                    Locale.US,
                    "%s | %s | Present: %d | Absent: %d | Attendance: %.2f%%%n",
                    emp.id,
                    emp.name,
                    emp.present,
                    emp.absent,
                    emp.getAttendancePercentage()
            );
        }
    }

    void loadData() {
        Path path = Paths.get(FILE_NAME);

        if (!Files.exists(path)) {
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length != 4) {
                    continue;
                }

                try {
                    String id = data[0].trim().toUpperCase();
                    String name = data[1].trim();

                    int present = Integer.parseInt(data[2].trim());
                    int absent = Integer.parseInt(data[3].trim());

                    if (!id.startsWith("EMP") || name.isEmpty()
                            || present < 0 || absent < 0
                            || employeeMap.containsKey(id)) {
                        continue;
                    }

                    Employee emp = new Employee(id, name);

                    emp.present = present;
                    emp.absent = absent;

                    employees.add(emp);
                    employeeMap.put(id, emp);

                    int number = Integer.parseInt(id.substring(3));

                    if (number >= nextId) {
                        nextId = number + 1;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid record skipped.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    void saveData() {
        try (BufferedWriter bw = Files.newBufferedWriter(
                Paths.get(FILE_NAME))) {

            for (Employee emp : employees) {

                bw.write(emp.id + "|" + emp.name + "|"
                        + emp.present + "|" + emp.absent);

                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}