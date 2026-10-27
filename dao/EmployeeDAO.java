
package com.employee.management.dao;

import com.employee.management.util.DBConnection;
import com.employee.management.model.Department;
import com.employee.management.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {

    public void createTables() throws SQLException {
        String departmentSql =
                "CREATE TABLE IF NOT EXISTS departments (" +
                        "department_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "department_name VARCHAR(100) NOT NULL UNIQUE)";

        String employeeSql =
                "CREATE TABLE IF NOT EXISTS employees (" +
                        "employee_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "salary DECIMAL(10,2) NOT NULL, " +
                        "department_id INT NOT NULL, " +
                        "CONSTRAINT chk_employee_salary CHECK (salary > 0), " +
                        "CONSTRAINT fk_employee_department FOREIGN KEY (department_id) " +
                        "REFERENCES departments(department_id) " +
                        "ON UPDATE CASCADE ON DELETE RESTRICT)";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(departmentSql);
            st.executeUpdate(employeeSql);
        }
    }

    public void seedDepartments() throws SQLException {
        String sql = "INSERT IGNORE INTO departments (department_name) VALUES (?)";

        String[] departments = {
                "Java Development", "Testing", "HR", "Finance"
        };

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (String department : departments) {
                ps.setString(1, department);
                ps.executeUpdate();
            }
        }
    }

    public boolean departmentExists(int departmentId) throws SQLException {
        String sql = "SELECT 1 FROM departments WHERE department_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, departmentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void addEmployee(Employee employee) throws SQLException {
        String sql =
                "INSERT INTO employees (name, salary, department_id) " +
                        "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setBigDecimal(2, employee.getSalary());
            ps.setInt(3, employee.getDepartmentId());

            ps.executeUpdate();
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();

        String sql =
                "SELECT e.employee_id, e.name, e.salary, " +
                        "e.department_id, d.department_name " +
                        "FROM employees e " +
                        "INNER JOIN departments d " +
                        "ON e.department_id = d.department_id " +
                        "ORDER BY e.employee_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
        }

        return employees;
    }

    public Employee getEmployeeById(int employeeId)
            throws SQLException {

        String sql =
                "SELECT e.employee_id, e.name, e.salary, " +
                        "e.department_id, d.department_name " +
                        "FROM employees e " +
                        "INNER JOIN departments d " +
                        "ON e.department_id = d.department_id " +
                        "WHERE e.employee_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs);
                }
            }
        }

        return null;
    }

    public boolean updateEmployee(Employee employee)
            throws SQLException {

        String sql =
                "UPDATE employees SET name = ?, salary = ?, " +
                        "department_id = ? WHERE employee_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setBigDecimal(2, employee.getSalary());
            ps.setInt(3, employee.getDepartmentId());
            ps.setInt(4, employee.getEmployeeId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteEmployee(int employeeId)
            throws SQLException {

        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Department> getDepartmentsWithEmployees()
            throws SQLException {

        Map<Integer, Department> departments = new LinkedHashMap<>();

        String sql =
                "SELECT d.department_id, d.department_name, " +
                        "e.employee_id, e.name, e.salary, e.department_id AS emp_department_id " +
                        "FROM departments d " +
                        "LEFT JOIN employees e " +
                        "ON d.department_id = e.department_id " +
                        "ORDER BY d.department_id, e.employee_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int departmentId = rs.getInt("department_id");
                String departmentName = rs.getString("department_name");

                Department department = departments.computeIfAbsent(
                        departmentId,
                        id -> new Department(id, departmentName)
                );

                int employeeId = rs.getInt("employee_id");

                if (!rs.wasNull()) {
                    Employee employee = new Employee(
                            employeeId,
                            rs.getString("name"),
                            rs.getBigDecimal("salary"),
                            rs.getInt("emp_department_id"),
                            departmentName
                    );

                    department.getEmployees().add(employee);
                }
            }
        }

        return new ArrayList<>(departments.values());
    }

    public void demonstrateRollback(int validDepartmentId)
            throws SQLException {

        String sql =
                "INSERT INTO employees (name, salary, department_id) " +
                        "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, "Rollback Test One");
                    ps.setBigDecimal(2, new java.math.BigDecimal("1000.00"));
                    ps.setInt(3, validDepartmentId);
                    ps.executeUpdate();

                    // Deliberately invalid foreign key.
                    ps.setString(1, "Rollback Test Two");
                    ps.setBigDecimal(2, new java.math.BigDecimal("2000.00"));
                    ps.setInt(3, -999);
                    ps.executeUpdate();
                }

                con.commit();
                System.out.println("Unexpected: transaction committed.");

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Transaction rolled back successfully.");
                System.out.println("Reason: " + e.getMessage());

            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("employee_id"),
                rs.getString("name"),
                rs.getBigDecimal("salary"),
                rs.getInt("department_id"),
                rs.getString("department_name")
        );
    }
}
