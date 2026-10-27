
package com.employee.management.service;

import com.employee.management.dao.EmployeeDAO;
import com.employee.management.model.Department;
import com.employee.management.model.Employee;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    // 1. Initialize database tables and departments
    public void initialize() throws SQLException {
        employeeDAO.createTables();
        employeeDAO.seedDepartments();
        System.out.println("Database initialized successfully.");
    }

    // 2. Add Employee
    public void addEmployee(Scanner sc) throws SQLException {
        System.out.print("Enter employee name: ");
        String name = sc.nextLine().trim();

        validateName(name);

        System.out.print("Enter salary: ");
        BigDecimal salary = readSalary(sc);

        System.out.print("Enter department ID: ");
        int departmentId = readPositiveInt(sc, "Department ID");

        validateDepartment(departmentId);

        Employee employee = new Employee(name, salary, departmentId);
        employeeDAO.addEmployee(employee);

        System.out.println("Employee added successfully.");
    }

    // 3. View All Employees
    public void viewAllEmployees() throws SQLException {
        List<Employee> employees = employeeDAO.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("\nID\tName\tSalary\tDepartment");

        for (Employee employee : employees) {
            System.out.println(
                    employee.getEmployeeId() + "\t" +
                            employee.getName() + "\t" +
                            employee.getSalary() + "\t" +
                            employee.getDepartmentName()
            );
        }
    }

    // 4. Search Employee by ID
    public void searchEmployee(Scanner sc) throws SQLException {
        System.out.print("Enter employee ID: ");
        int id = readPositiveInt(sc, "Employee ID");

        Employee employee = employeeDAO.getEmployeeById(id);

        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        displayEmployee(employee);
    }

    // 5. Update Employee
    public void updateEmployee(Scanner sc) throws SQLException {
        System.out.print("Enter employee ID to update: ");
        int id = readPositiveInt(sc, "Employee ID");

        Employee existing = employeeDAO.getEmployeeById(id);

        if (existing == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.print("Enter new name: ");
        String name = sc.nextLine().trim();
        validateName(name);

        System.out.print("Enter new salary: ");
        BigDecimal salary = readSalary(sc);

        System.out.print("Enter new department ID: ");
        int departmentId = readPositiveInt(sc, "Department ID");

        validateDepartment(departmentId);

        Employee updated = new Employee(
                id, name, salary, departmentId, null
        );

        if (employeeDAO.updateEmployee(updated)) {
            System.out.println("Employee updated successfully.");
        } else {
            System.out.println("Employee was not updated.");
        }
    }

    // 6. Delete Employee
    public void deleteEmployee(Scanner sc) throws SQLException {
        System.out.print("Enter employee ID to delete: ");
        int id = readPositiveInt(sc, "Employee ID");

        if (employeeDAO.deleteEmployee(id)) {
            System.out.println("Employee deleted successfully.");
        } else {
            System.out.println("Employee not found.");
        }
    }

    // 7. View Employees Department-wise
    public void viewEmployeesDepartmentWise() throws SQLException {
        List<Department> departments =
                employeeDAO.getDepartmentsWithEmployees();

        for (Department department : departments) {
            System.out.println(
                    "\nDepartment: " + department.getDepartmentName()
            );

            if (department.getEmployees().isEmpty()) {
                System.out.println("No employees in this department.");
                continue;
            }

            for (Employee employee : department.getEmployees()) {
                System.out.println(
                        "ID: " + employee.getEmployeeId() +
                                ", Name: " + employee.getName() +
                                ", Salary: " + employee.getSalary()
                );
            }
        }
    }

    // 8. Demonstrate transaction rollback
    public void demonstrateRollback(Scanner sc) throws SQLException {
        System.out.print("Enter an existing department ID for testing: ");
        int departmentId = readPositiveInt(sc, "Department ID");

        validateDepartment(departmentId);
        employeeDAO.demonstrateRollback(departmentId);
    }

    // Validate department
    private void validateDepartment(int departmentId)
            throws SQLException {

        if (!employeeDAO.departmentExists(departmentId)) {
            throw new IllegalArgumentException(
                    "Department ID does not exist."
            );
        }
    }

    // Validate employee name
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Employee name cannot be empty."
            );
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException(
                    "Employee name cannot exceed 100 characters."
            );
        }
    }

    // Read and validate salary
    private BigDecimal readSalary(Scanner sc) {
        BigDecimal salary;

        try {
            salary = new BigDecimal(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Enter a valid numeric salary."
            );
        }

        if (salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Salary must be greater than zero."
            );
        }

        if (salary.scale() > 2 ||
                salary.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException(
                    "Salary must fit DECIMAL(10,2)."
            );
        }

        return salary;
    }

    // Read a positive integer
    private int readPositiveInt(Scanner sc, String fieldName) {
        try {
            int value = Integer.parseInt(sc.nextLine().trim());

            if (value <= 0) {
                throw new IllegalArgumentException(
                        fieldName + " must be greater than zero."
                );
            }

            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Enter a valid numeric " + fieldName + "."
            );
        }
    }

    // Display one employee
    private void displayEmployee(Employee employee) {
        System.out.println("Employee ID: " + employee.getEmployeeId());
        System.out.println("Name: " + employee.getName());
        System.out.println("Salary: " + employee.getSalary());
        System.out.println("Department ID: " + employee.getDepartmentId());
        System.out.println("Department: " + employee.getDepartmentName());
    }
}
