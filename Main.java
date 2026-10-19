import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Employee> employees =
            FileStorage.loadEmployees();

    static ArrayList<PayrollRecord> payrollRecords =
            FileStorage.loadPayroll();

    static Pattern monthPattern =
            Pattern.compile("^(0[1-9]|1[0-2])-[0-9]{4}$");

    public static String money(double amount) {
        NumberFormat nf =
                NumberFormat.getNumberInstance(
                        new Locale("en", "IN"));

        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        return "\u20B9" + nf.format(amount);
    }

    static int readInt(String message) {
        while (true) {
            System.out.print(message);

            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    static double readAmount(String message) {
        while (true) {
            System.out.print(message);

            try {
                double value =
                        Double.parseDouble(sc.nextLine().trim());

                if (Double.isFinite(value) && value >= 0) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Invalid amount is handled below.
            }

            System.out.println(
                    "Enter a valid non-negative amount.");
        }
    }

    static double readTaxRate() {
        while (true) {
            double rate = readAmount("Tax rate (0-100%): ");

            if (rate <= 100) {
                return rate;
            }

            System.out.println("Tax rate cannot exceed 100%.");
        }
    }

    static String readText(String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    static String readMonth() {
        while (true) {
            String month =
                    readText("Enter month (MM-YYYY): ");

            if (monthPattern.matcher(month).matches()) {
                return month;
            }

            System.out.println(
                    "Invalid month. Example: 10-2026");
        }
    }

    static Employee findEmployee(int id) {
        for (Employee e : employees) {
            if (e.getId() == id) {
                return e;
            }
        }

        return null;
    }

    static void addEmployee() {
        int id = readInt("Employee ID: ");

        if (id <= 0 || findEmployee(id) != null) {
            System.out.println(
                    "ID must be positive and unique.");
            return;
        }

        String name = readText("Name: ");
        String designation = readText("Designation: ");
        String department = readText("Department: ");

        double basic = readAmount("Basic salary: ");
        double allowances = readAmount("Allowances: ");
        double deductions = readAmount("Other deductions: ");
        double tax = readTaxRate();

        Employee e = new Employee(
                id, name, designation, department,
                basic, allowances, deductions, tax);

        employees.add(e);
        FileStorage.saveEmployees(employees);

        System.out.println("Employee added successfully.");
    }

    static void displayEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No employee records found.");
            return;
        }

        for (Employee e : employees) {
            e.display();
        }
    }

    static void searchEmployee() {
        int id = readInt("Enter employee ID: ");
        Employee e = findEmployee(id);

        if (e == null) {
            System.out.println("Employee not found.");
        } else {
            e.display();
        }
    }

    static void updateSalary() {
        int id = readInt("Enter employee ID: ");
        Employee e = findEmployee(id);

        if (e == null) {
            System.out.println("Employee not found.");
            return;
        }

        double basic = readAmount("New basic salary: ");
        double allowances = readAmount("New allowances: ");
        double deductions = readAmount("New deductions: ");
        double tax = readTaxRate();

        e.setSalaryDetails(
                basic, allowances, deductions, tax);

        FileStorage.saveEmployees(employees);

        System.out.println("Salary details updated.");
    }

    static void processPayroll() {
        if (employees.isEmpty()) {
            System.out.println("Add employees first.");
            return;
        }

        String month = readMonth();
        int created = 0;

        for (Employee e : employees) {
            boolean exists = false;

            for (PayrollRecord r : payrollRecords) {
                if (r.getEmployeeId() == e.getId()
                        && r.getMonth().equals(month)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                payrollRecords.add(
                        new PayrollRecord(e, month));
                created++;
            }
        }

        FileStorage.savePayroll(payrollRecords);

        System.out.println(
                "Payroll processed for " + month);
        System.out.println(
                "New payroll records: " + created);

        Thread worker = new Thread(
                new PayrollWorker("Payroll report task"));

        worker.start();

        try {
            worker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Waiting was interrupted.");
        }
    }

    static void viewPayrollHistory() {
        if (payrollRecords.isEmpty()) {
            System.out.println("No payroll history found.");
            return;
        }

        for (PayrollRecord r : payrollRecords) {
            r.display();
        }
    }

    static void generateSalarySlip() {
        int id = readInt("Employee ID: ");
        String month = readMonth();

        for (PayrollRecord r : payrollRecords) {
            if (r.getEmployeeId() == id
                    && r.getMonth().equals(month)) {
                r.display();
                return;
            }
        }

        System.out.println("Salary slip not found.");
    }

    static void monthlyReport() {
        String month = readMonth();

        int count = 0;
        double grossTotal = 0;
        double taxTotal = 0;
        double netTotal = 0;

        for (PayrollRecord r : payrollRecords) {
            if (r.getMonth().equals(month)) {
                r.display();
                count++;

                Employee e = findEmployee(r.getEmployeeId());

                if (e != null) {
                    grossTotal += e.calculateGrossSalary();
                    taxTotal += e.calculateTax();
                    netTotal += e.calculateNetSalary();
                }
            }
        }

        System.out.println("\nMONTHLY REPORT: " + month);
        System.out.println("Records: " + count);
        System.out.println("Gross total: " + money(grossTotal));
        System.out.println("Tax total: " + money(taxTotal));
        System.out.println("Net total: " + money(netTotal));
    }

    static void departmentReport() {
        String department = readText("Department name: ");

        PayrollService.generateDepartmentReport(
                employees, department);
    }

    public static void main(String[] args) {
        System.out.println("PAYROLL PROCESSING SYSTEM");
        System.out.println("Employees loaded: " + employees.size());
        System.out.println(
                "Payroll records loaded: " + payrollRecords.size());

        int choice;

        do {
            System.out.println("\n========== MENU ==========");
            System.out.println("1. Add Employee");
            System.out.println("2. Display All Employees");
            System.out.println("3. Search Employee");
            System.out.println("4. Update Salary Details");
            System.out.println("5. Process Monthly Payroll");
            System.out.println("6. View Payroll History");
            System.out.println("7. Generate Salary Slip");
            System.out.println("8. Monthly Salary Report");
            System.out.println("9. Department Report");
            System.out.println("10. Exit");

            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    addEmployee();
                    break;

                case 2:
                    displayEmployees();
                    break;

                case 3:
                    searchEmployee();
                    break;

                case 4:
                    updateSalary();
                    break;

                case 5:
                    processPayroll();
                    break;

                case 6:
                    viewPayrollHistory();
                    break;

                case 7:
                    generateSalarySlip();
                    break;

                case 8:
                    monthlyReport();
                    break;

                case 9:
                    departmentReport();
                    break;

                case 10:
                    System.out.println("Program terminated.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 10);

        sc.close();
    }
}