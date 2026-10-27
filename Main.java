import java.io.*;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final String EMPLOYEE_FILE = "employees.dat";
    private static final String PAYROLL_FILE = "payroll.dat";
    private static ArrayList<Employee> employees = new ArrayList<>();
    private static ArrayList<PayrollRecord> payrollRecords = new ArrayList<>();

    static class Employee implements Serializable {
        private static final long serialVersionUID = 1L;
        int id;
        String name;
        String designation;
        String department;
        double basicSalary;
        double allowances;
        double otherDeductions;
        double taxRate;

        Employee(int id, String name, String designation, String department,
                 double basicSalary, double allowances, double otherDeductions, double taxRate) {
            this.id = id;
            this.name = name;
            this.designation = designation;
            this.department = department;
            this.basicSalary = basicSalary;
            this.allowances = allowances;
            this.otherDeductions = otherDeductions;
            this.taxRate = taxRate;
        }

        double grossSalary() { return basicSalary + allowances; }
        double taxAmount() { return grossSalary() * taxRate / 100.0; }
        double totalDeductions() { return otherDeductions + taxAmount(); }
        double netSalary() { return grossSalary() - totalDeductions(); }

        void display() {
            System.out.println("\nEmployee ID       : " + id);
            System.out.println("Name              : " + name);
            System.out.println("Designation       : " + designation);
            System.out.println("Department        : " + department);
            System.out.println("Basic Salary      : " + money(basicSalary));
            System.out.println("Allowances        : " + money(allowances));
            System.out.println("Other Deductions  : " + money(otherDeductions));
            System.out.printf("Tax Rate          : %.2f%%%n", taxRate);
            System.out.println("Gross Salary      : " + money(grossSalary()));
            System.out.println("Net Salary        : " + money(netSalary()));
        }
    }

    static class PayrollRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        int employeeId;
        String employeeName;
        String designation;
        String department;
        String month;
        double basicSalary;
        double allowances;
        double grossSalary;
        double taxRate;
        double taxAmount;
        double otherDeductions;
        double totalDeductions;
        double netSalary;

        PayrollRecord(Employee e, String month) {
            employeeId = e.id;
            employeeName = e.name;
            designation = e.designation;
            department = e.department;
            this.month = month;
            basicSalary = e.basicSalary;
            allowances = e.allowances;
            grossSalary = e.grossSalary();
            taxRate = e.taxRate;
            taxAmount = e.taxAmount();
            otherDeductions = e.otherDeductions;
            totalDeductions = e.totalDeductions();
            netSalary = e.netSalary();
        }

        void displaySalarySlip() {
            System.out.println("\n========== EMPLOYEE SALARY SLIP ==========");
            System.out.println("Employee ID       : " + employeeId);
            System.out.println("Employee Name     : " + employeeName);
            System.out.println("Designation       : " + designation);
            System.out.println("Department        : " + department);
            System.out.println("Payroll Month     : " + month);
            System.out.println("------------------------------------------");
            System.out.println("Basic Salary      : " + money(basicSalary));
            System.out.println("Allowances        : " + money(allowances));
            System.out.println("Gross Salary      : " + money(grossSalary));
            System.out.printf("Tax (%.2f%%)       : %s%n", taxRate, money(taxAmount));
            System.out.println("Other Deductions  : " + money(otherDeductions));
            System.out.println("Total Deductions  : " + money(totalDeductions));
            System.out.println("------------------------------------------");
            System.out.println("NET SALARY        : " + money(netSalary));
            System.out.println("==========================================");
        }
    }

    static String money(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "IN"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return "₹" + nf.format(amount);
    }

    @SuppressWarnings("unchecked")
    static void loadRecords() {
        File employeeFile = new File(EMPLOYEE_FILE);
        if (employeeFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(employeeFile))) {
                employees = (ArrayList<Employee>) in.readObject();
            } catch (IOException | ClassNotFoundException | ClassCastException ex) {
                System.out.println("Could not load employee records: " + ex.getMessage());
                employees = new ArrayList<>();
            }
        }
        File payrollFile = new File(PAYROLL_FILE);
        if (payrollFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(payrollFile))) {
                payrollRecords = (ArrayList<PayrollRecord>) in.readObject();
            } catch (IOException | ClassNotFoundException | ClassCastException ex) {
                System.out.println("Could not load payroll records: " + ex.getMessage());
                payrollRecords = new ArrayList<>();
            }
        }
    }

    static void saveEmployees() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(EMPLOYEE_FILE))) {
            out.writeObject(employees);
        } catch (IOException ex) {
            System.out.println("Error saving employee records: " + ex.getMessage());
        }
    }

    static void savePayroll() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PAYROLL_FILE))) {
            out.writeObject(payrollRecords);
        } catch (IOException ex) {
            System.out.println("Error saving payroll records: " + ex.getMessage());
        }
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException ex) { System.out.println("Enter a valid whole number."); }
        }
    }

    static double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double amount = Double.parseDouble(sc.nextLine().trim());
                if (Double.isFinite(amount) && amount >= 0) return amount;
                System.out.println("Amount must be a non-negative number.");
            } catch (NumberFormatException ex) {
                System.out.println("Enter a valid amount, for example 25000 or 25000.50.");
            }
        }
    }

    static double readTaxRate(String prompt) {
        while (true) {
            double rate = readAmount(prompt);
            if (rate <= 100) return rate;
            System.out.println("Tax rate cannot exceed 100%.");
        }
    }

    static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    static String readMonth() {
        while (true) {
            String value = readText("Enter payroll month (YYYY-MM, e.g. 2026-10): ");
            try { return YearMonth.parse(value).toString(); }
            catch (DateTimeParseException ex) { System.out.println("Invalid month. Use YYYY-MM, e.g. 2026-10."); }
        }
    }

    static Employee findEmployee(int id) {
        for (Employee e : employees) if (e.id == id) return e;
        return null;
    }

    static PayrollRecord findPayroll(int id, String month) {
        for (PayrollRecord p : payrollRecords)
            if (p.employeeId == id && p.month.equals(month)) return p;
        return null;
    }

    static boolean validSalary(double basic, double allowances, double deductions, double taxRate) {
        double gross = basic + allowances;
        double tax = gross * taxRate / 100.0;
        if (!Double.isFinite(gross) || !Double.isFinite(tax) || deductions + tax > gross) {
            System.out.println("Total deductions (including tax) cannot exceed gross salary.");
            return false;
        }
        return true;
    }

    static void addEmployee() {
        System.out.println("\n--- Add Employee ---");
        int id;
        while (true) {
            id = readInt("Employee ID: ");
            if (id <= 0) System.out.println("Employee ID must be positive.");
            else if (findEmployee(id) != null) System.out.println("That employee ID already exists.");
            else break;
        }
        String name = readText("Employee name: ");
        String designation = readText("Designation: ");
        String department = readText("Department: ");
        double basic = readAmount("Basic salary: ₹");
        double allowances = readAmount("Allowances: ₹");
        double deductions = readAmount("Other deductions: ₹");
        double tax = readTaxRate("Tax rate (0-100%): ");
        if (!validSalary(basic, allowances, deductions, tax)) {
            System.out.println("Employee was not added. Please enter valid salary details again.");
            return;
        }
        employees.add(new Employee(id, name, designation, department, basic, allowances, deductions, tax));
        saveEmployees();
        System.out.println("Employee added and saved successfully.");
    }

    static void viewEmployees() {
        System.out.println("\n--- Employee Details ---");
        if (employees.isEmpty()) { System.out.println("No employee records found."); return; }
        for (Employee e : employees) e.display();
    }

    static void searchEmployee() {
        Employee e = findEmployee(readInt("Enter employee ID to search: "));
        if (e == null) System.out.println("Employee not found.");
        else e.display();
    }

    static void updateSalary() {
        System.out.println("\n--- Update Salary Details ---");
        Employee e = findEmployee(readInt("Enter employee ID: "));
        if (e == null) { System.out.println("Employee not found."); return; }
        e.display();
        double basic = readAmount("New basic salary: ₹");
        double allowances = readAmount("New allowances: ₹");
        double deductions = readAmount("New other deductions: ₹");
        double tax = readTaxRate("New tax rate (0-100%): ");
        if (!validSalary(basic, allowances, deductions, tax)) {
            System.out.println("Salary details were not updated.");
            return;
        }
        e.basicSalary = basic;
        e.allowances = allowances;
        e.otherDeductions = deductions;
        e.taxRate = tax;
        saveEmployees();
        System.out.println("Salary details updated successfully.");
        System.out.println("Note: previously processed payroll records remain unchanged.");
    }

    static void processPayroll() {
        System.out.println("\n--- Process Monthly Payroll ---");
        if (employees.isEmpty()) { System.out.println("Add an employee first."); return; }
        int id = readInt("Enter employee ID: ");
        Employee e = findEmployee(id);
        if (e == null) { System.out.println("Employee not found."); return; }
        String month = readMonth();
        if (findPayroll(id, month) != null) {
            System.out.println("Payroll has already been processed for this employee and month.");
            return;
        }
        PayrollRecord record = new PayrollRecord(e, month);
        payrollRecords.add(record);
        savePayroll();
        System.out.println("Payroll processed successfully.");
        record.displaySalarySlip();
    }

    static void viewSalarySlip() {
        int id = readInt("Enter employee ID: ");
        String month = readMonth();
        PayrollRecord p = findPayroll(id, month);
        if (p == null) System.out.println("No payroll record found for that employee and month.");
        else p.displaySalarySlip();
    }

    static void viewPayrollHistory() {
        int id = readInt("Enter employee ID for payroll history: ");
        boolean found = false;
        for (PayrollRecord p : payrollRecords) {
            if (p.employeeId == id) { p.displaySalarySlip(); found = true; }
        }
        if (!found) System.out.println("No payroll history found for this employee.");
    }

    static void monthlyReport() {
        System.out.println("\n--- Monthly Salary Report ---");
        String month = readMonth();
        int count = 0;
        double gross = 0, tax = 0, deductions = 0, net = 0;
        for (PayrollRecord p : payrollRecords) {
            if (p.month.equals(month)) {
                System.out.println("ID: " + p.employeeId + " | Name: " + p.employeeName
                        + " | Department: " + p.department + " | Net salary: " + money(p.netSalary));
                count++;
                gross += p.grossSalary;
                tax += p.taxAmount;
                deductions += p.totalDeductions;
                net += p.netSalary;
            }
        }
        if (count == 0) { System.out.println("No payroll records found for " + month + "."); return; }
        System.out.println("\n========== MONTHLY SUMMARY ==========");
        System.out.println("Payroll month       : " + month);
        System.out.println("Employees processed : " + count);
        System.out.println("Total gross salary  : " + money(gross));
        System.out.println("Total tax           : " + money(tax));
        System.out.println("Total deductions    : " + money(deductions));
        System.out.println("Total net salary    : " + money(net));
        System.out.println("====================================");
    }

    static void departmentReport() {
        String department = readText("Enter department name: ");
        int count = 0;
        double gross = 0, net = 0;
        System.out.println("\n--- Department Report: " + department + " ---");
        for (Employee e : employees) {
            if (e.department.equalsIgnoreCase(department)) {
                System.out.println("ID: " + e.id + " | " + e.name + " | Net salary: " + money(e.netSalary()));
                gross += e.grossSalary();
                net += e.netSalary();
                count++;
            }
        }
        if (count == 0) System.out.println("No employees found in this department.");
        else {
            System.out.println("Employees: " + count);
            System.out.println("Combined gross salary: " + money(gross));
            System.out.println("Combined net salary  : " + money(net));
        }
    }

    public static void main(String[] args) {
        loadRecords();
        System.out.println("======================================");
        System.out.println("       EMPLOYEE PAYROLL SYSTEM");
        System.out.println("======================================");
        System.out.println("Employees loaded: " + employees.size());
        System.out.println("Payroll records loaded: " + payrollRecords.size());
        int choice;
        do {
            System.out.println("\n=============== MENU ===============");
            System.out.println("1. Add Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Search Employee");
            System.out.println("4. Update Salary Details");
            System.out.println("5. Process Monthly Payroll");
            System.out.println("6. View Salary Slip");
            System.out.println("7. View Payroll History");
            System.out.println("8. Generate Monthly Salary Report");
            System.out.println("9. Generate Department Report");
            System.out.println("10. Save and Exit");
            choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1: addEmployee(); break;
                case 2: viewEmployees(); break;
                case 3: searchEmployee(); break;
                case 4: updateSalary(); break;
                case 5: processPayroll(); break;
                case 6: viewSalarySlip(); break;
                case 7: viewPayrollHistory(); break;
                case 8: monthlyReport(); break;
                case 9: departmentReport(); break;
                case 10:
                    saveEmployees();
                    savePayroll();
                    System.out.println("Records saved. Exiting Payroll System.");
                    break;
                default: System.out.println("Invalid choice. Please select 1 to 10.");
            }
        } while (choice != 10);
        sc.close();
    }
}
