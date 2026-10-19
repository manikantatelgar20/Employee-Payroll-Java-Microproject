import java.io.*;
import java.util.ArrayList;

public class FileStorage {

    @SuppressWarnings("unchecked")
    public static ArrayList<Employee> loadEmployees() {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("employees.dat"))) {

            return (ArrayList<Employee>) in.readObject();

        } catch (FileNotFoundException e) {
            return new ArrayList<>();

        } catch (IOException | ClassNotFoundException
                 | ClassCastException e) {
            System.out.println("Error loading employees: "
                    + e.getMessage());
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<PayrollRecord> loadPayroll() {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("payroll.dat"))) {

            return (ArrayList<PayrollRecord>) in.readObject();

        } catch (FileNotFoundException e) {
            return new ArrayList<>();

        } catch (IOException | ClassNotFoundException
                 | ClassCastException e) {
            System.out.println("Error loading payroll: "
                    + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveEmployees(
            ArrayList<Employee> employees) {

        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("employees.dat"))) {

            out.writeObject(employees);

        } catch (IOException e) {
            System.out.println("Error saving employees: "
                    + e.getMessage());
        }
    }

    public static void savePayroll(
            ArrayList<PayrollRecord> records) {

        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("payroll.dat"))) {

            out.writeObject(records);

        } catch (IOException e) {
            System.out.println("Error saving payroll: "
                    + e.getMessage());
        }
    }
}