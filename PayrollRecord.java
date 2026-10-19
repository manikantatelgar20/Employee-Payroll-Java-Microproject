import java.io.Serializable;

public class PayrollRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int employeeId;
    private String employeeName;
    private String month;
    private double gross;
    private double tax;
    private double deductions;
    private double net;

    public PayrollRecord(Employee employee, String month) {
        this.employeeId = employee.getId();
        this.employeeName = employee.getName();
        this.month = month;
        this.gross = employee.calculateGrossSalary();
        this.tax = employee.calculateTax();
        this.deductions = employee.getOtherDeductions();
        this.net = employee.calculateNetSalary();
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getMonth() {
        return month;
    }

    public void display() {
        System.out.println("\n========== SALARY SLIP ==========");
        System.out.println("Month: " + month);
        System.out.println("Employee: " + employeeName);
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Gross Salary: " + Main.money(gross));
        System.out.println("Tax: " + Main.money(tax));
        System.out.println("Deductions: " + Main.money(deductions));
        System.out.println("Net Salary: " + Main.money(net));
        System.out.println("=================================");
    }
}