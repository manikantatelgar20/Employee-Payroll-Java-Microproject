import java.util.List;

public class PayrollService implements PayrollCalculable {

    private Employee employee;

    public PayrollService(Employee employee) {
        this.employee = employee;
    }

    @Override
    public double calculateGrossSalary() {
        return employee.calculateGrossSalary();
    }

    @Override
    public double calculateTax() {
        return employee.calculateTax();
    }

    @Override
    public double calculateNetSalary() {
        return employee.calculateNetSalary();
    }

    public static void generateDepartmentReport(
            List<Employee> employees, String department) {

        double total = 0;
        int count = 0;

        for (Employee e : employees) {
            if (e.getDepartment().equalsIgnoreCase(department)) {
                e.display();
                total += e.calculateNetSalary();
                count++;
            }
        }

        System.out.println("\nDepartment Report: " + department);
        System.out.println("Employees: " + count);
        System.out.println("Total Net Payroll: " + Main.money(total));

        if (count > 0) {
            System.out.println("Average Net Salary: "
                    + Main.money(total / count));
        } else {
            System.out.println("No employees found.");
        }
    }
}