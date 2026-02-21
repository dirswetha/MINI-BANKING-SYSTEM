import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

// ── Account class ──────────────────────────────────────────
class BankAccount {

    private int id;
    private String holder;
    private double balance;

    static final double MIN = 1000.0;

    BankAccount(int id, String holder, double balance) {
        this.id      = id;
        this.holder  = holder;
        this.balance = balance;
    }

    int    getId()      { return id;      }
    String getHolder()  { return holder;  }
    double getBalance() { return balance; }

    void deposit(double amt) {
        balance = balance + amt;
        System.out.println("  Deposited Rs." + amt + "  |  New Balance: Rs." + balance);
    }

    boolean withdraw(double amt) {
        if ((balance - amt) < MIN) {
            System.out.println("  Cannot withdraw. Min balance Rs." + MIN + " required.");
            System.out.println("  Max you can take: Rs." + (balance - MIN));
            return false;
        }
        balance = balance - amt;
        System.out.println("  Withdrawn Rs." + amt + "  |  New Balance: Rs." + balance);
        return true;
    }

    String toLine() {
        return id + "," + holder + "," + balance;
    }

    static BankAccount fromLine(String line) {
        String[] p = line.split(",");
        return new BankAccount(Integer.parseInt(p[0].trim()),
                               p[1].trim(),
                               Double.parseDouble(p[2].trim()));
    }

    void show() {
        System.out.println("  --------------------------------");
        System.out.println("  Account Number : " + id);
        System.out.println("  Account Holder : " + holder);
        System.out.println("  Balance        : Rs." + balance);
        System.out.println("  --------------------------------");
    }
}

// ── Main class ─────────────────────────────────────────────
public class BankApp {

    static final String DATA = "bank_data.txt";
    static ArrayList<BankAccount> list = new ArrayList<BankAccount>();
    static int nextId = 1001;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();

        System.out.println("\n  ===================================");
        System.out.println("       Welcome to Smart Bank System   ");
        System.out.println("  ===================================");

        int choice = 0;

        while (choice != 6) {

            System.out.println("\n  --- Main Menu ---");
            System.out.println("  1. Withdraw");
            System.out.println("  2. Deposit");
            System.out.println("  3. Display All Accounts");
            System.out.println("  4. Create Account");
            System.out.println("  5. Search Account");
            System.out.println("  6. Exit");
            System.out.print("  Enter your choice (1-6): ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Enter a number 1-6.");
                continue;
            }

            switch (choice) {
                case 1: withdraw();         break;
                case 2: deposit();          break;
                case 3: showAll();          break;
                case 4: createAccount();    break;
                case 5: search();           break;
                case 6: System.out.println("\n  Thank you for using Smart Bank System. Goodbye!"); break;
                default: System.out.println("  [!] Invalid choice. Please enter a number between 1 and 6.");
            }
        }

        sc.close();
    }

    static void createAccount() {
        System.out.println("\n  === Create New Account ===");

        System.out.print("  Enter your full name: ");
        String name = sc.nextLine().trim();

        if (name.length() == 0) {
            System.out.println("  [!] Name cannot be empty.");
            return;
        }

        System.out.print("  Enter opening deposit (min Rs.1000): Rs.");
        double amount = 0;

        try {
            amount = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid amount.");
            return;
        }

        if (amount < 1000) {
            System.out.println("  [!] Must deposit at least Rs.1000.");
            return;
        }

        BankAccount acc = new BankAccount(nextId, name, amount);
        list.add(acc);
        nextId = nextId + 1;
        saveData();

        System.out.println("\n  [OK] Account created!");
        acc.show();
    }

    static void deposit() {
        System.out.println("\n  === Deposit Money ===");

        BankAccount acc = findAccount();
        if (acc == null) return;

        System.out.print("  Enter amount to deposit: Rs.");
        double amt = readAmount();
        if (amt <= 0) return;

        acc.deposit(amt);
        saveData();
    }

    static void withdraw() {
        System.out.println("\n  === Withdraw Money ===");

        BankAccount acc = findAccount();
        if (acc == null) return;

        System.out.print("  Enter amount to withdraw: Rs.");
        double amt = readAmount();
        if (amt <= 0) return;

        boolean ok = acc.withdraw(amt);
        if (ok) saveData();
    }

    static void search() {
        System.out.println("\n  === Search Account ===");

        BankAccount acc = findAccount();
        if (acc == null) return;

        System.out.println("  [OK] Account found:");
        acc.show();
    }

    static void showAll() {
        System.out.println("\n  === All Accounts ===");

        if (list.size() == 0) {
            System.out.println("  No accounts yet.");
            return;
        }

        System.out.println("  Total: " + list.size());
        int i = 0;
        while (i < list.size()) {
            list.get(i).show();
            i = i + 1;
        }
    }

    static BankAccount findAccount() {
        System.out.print("  Enter Account Number: ");
        int num = 0;

        try {
            num = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid number.");
            return null;
        }

        int i = 0;
        while (i < list.size()) {
            if (list.get(i).getId() == num) {
                System.out.println("  Found: " + list.get(i).getHolder());
                return list.get(i);
            }
            i = i + 1;
        }

        System.out.println("  [!] Account " + num + " not found.");
        return null;
    }

    static double readAmount() {
        try {
            double amt = Double.parseDouble(sc.nextLine().trim());
            if (amt <= 0) {
                System.out.println("  [!] Amount must be greater than zero.");
                return -1;
            }
            return amt;
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid amount.");
            return -1;
        }
    }

    static void loadData() {
        File f = new File(DATA);
        if (f.exists() == false) {
            System.out.println("  [Info] No saved data. Starting fresh.");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATA));
            String line = reader.readLine();

            while (line != null) {
                line = line.trim();
                if (line.length() > 0 && line.startsWith("#") == false) {
                    BankAccount acc = BankAccount.fromLine(line);
                    list.add(acc);
                    if (acc.getId() >= nextId) {
                        nextId = acc.getId() + 1;
                    }
                }
                line = reader.readLine();
            }

            reader.close();
            System.out.println("  [Info] " + list.size() + " account(s) loaded.");

        } catch (IOException e) {
            System.out.println("  [Error] Could not load file.");
        }
    }

    static void saveData() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATA, false));B
            writer.write("# bank_data.txt");
            writer.newLine();

            int i = 0;
            while (i < list.size()) {
                writer.write(list.get(i).toLine());
                writer.newLine();
                i = i + 1;
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("  [Error] Could not save file.");
        }
    }
}
