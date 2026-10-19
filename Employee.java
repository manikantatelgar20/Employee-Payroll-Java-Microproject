import java.io.Serializable;

public class Employee extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String designation;
    private String department;
    private double basicSalary;
    private double allowances;
    private double otherDeductions;
    private double taxRate;

    public Employee(int id, String name, String designation,
                    String department, double basicSalary,
                    double allowances, double otherDeductions,
                    double taxRate) {
        super(name);
        this.id = id;
        this.designation = designation;
        this.department = department;
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.otherDeductions = otherDeductions;
        this.taxRate = taxRate;
    }

    public int getId() {
        return id;
    }

    public String getDepartment() {
        return department;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getAllowances() {
        return allowances;
    }

    public double getOtherDeductions() {
        return otherDeductions;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setSalaryDetails(double basicSalary,
                                 double allowances,
                                 double deductions,
                                 double taxRate) {
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.otherDeductions = deductions;
        this.taxRate = taxRate;
    }

    public double calculateGrossSalary() {
        return basicSalary + allowances;
    }

    public double calculateTax() {
        return calculateGrossSalary() * taxRate / 100.0;
    }

    public double calculateNetSalary() {
        return calculateGrossSalary()
                - calculateTax()
                - otherDeductions;
    }

    public void display() {
        System.out.println("\n------------------------------");
        System.out.println("Employee ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Designation: " + designation);
        System.out.println("Department: " + department);
        System.out.println("Basic Salary: "
                + Main.money(basicSalary));
        System.out.println("Allowances: "
                + Main.money(allowances));
        System.out.println("Other Deductions: "
                + Main.money(otherDeductions));
        System.out.println("Tax Rate: " + taxRate + "%");
        System.out.println("Gross Salary: "
                + Main.money(calculateGrossSalary()));
        System.out.println("Tax Amount: "
                + Main.money(calculateTax()));
        System.out.println("Net Salary: "
                + Main.money(calculateNetSalary()));
        System.out.println("------------------------------");
    }
}