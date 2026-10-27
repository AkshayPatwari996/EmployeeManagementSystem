
package com.employee.management.main;

import com.employee.management.util.DBConnection;
import com.employee.management.service.EmployeeService;

import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeManagementSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        EmployeeService service = new EmployeeService();

        try {
            // Initialize database tables and departments
            service.initialize();

            while (true) {
                System.out.println("\n===== EMPLOYEE MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Employee");
                System.out.println("2. View All Employees");
                System.out.println("3. Search Employee by ID");
                System.out.println("4. Update Employee Details");
                System.out.println("5. Delete Employee");
                System.out.println("6. View Employees Department-wise");
                System.out.println("7. Demonstrate Transaction Rollback");
                System.out.println("8. Exit");

                System.out.print("Enter your choice: ");

                String input = sc.nextLine().trim();
                int choice;

                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Invalid input. Please enter a number."
                    );
                    continue;
                }

                try {
                    switch (choice) {

                        case 1:
                            service.addEmployee(sc);
                            break;

                        case 2:
                            service.viewAllEmployees();
                            break;

                        case 3:
                            service.searchEmployee(sc);
                            break;

                        case 4:
                            service.updateEmployee(sc);
                            break;

                        case 5:
                            service.deleteEmployee(sc);
                            break;

                        case 6:
                            service.viewEmployeesDepartmentWise();
                            break;

                        case 7:
                            service.demonstrateRollback(sc);
                            break;

                        case 8:
                            System.out.println(
                                    "Thank you for using Employee Management System."
                            );
                            return;

                        default:
                            System.out.println(
                                    "Invalid choice. Please enter 1-8."
                            );
                    }

                } catch (SQLException e) {
                    System.out.println(
                            "Database operation failed: " + e.getMessage()
                    );

                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "Invalid input: " + e.getMessage()
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Could not initialize the database: " + e.getMessage()
            );

        } finally {
            sc.close();
            DBConnection.shutdown();
        }
    }
}
