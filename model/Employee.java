
package com.employee.management.model;

import java.math.BigDecimal;

public class Employee {

    private int employeeId;
    private String name;
    private BigDecimal salary;
    private int departmentId;
    private String departmentName;

    public Employee() {
    }

    public Employee(String name, BigDecimal salary, int departmentId) {
        this.name = name;
        this.salary = salary;
        this.departmentId = departmentId;
    }

    public Employee(int employeeId, String name,
                    BigDecimal salary, int departmentId,
                    String departmentName) {
        this.employeeId = employeeId;
        this.name = name;
        this.salary = salary;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
