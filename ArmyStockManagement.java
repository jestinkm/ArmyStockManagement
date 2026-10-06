import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ==========================================
// 1. BASE ITEM CLASS (Encapsulation)
// ==========================================
abstract class Item {
    private String itemId;
    private String name;
    private int quantity;
    private int clearanceLevel; // 1 (Private) to 5 (General/Commander)

    public Item(String itemId, String name, int quantity, int clearanceLevel) {
        this.itemId = itemId;
        this.name = name;
        this.quantity = quantity;
        this.clearanceLevel = clearanceLevel;
    }

    public void updateQuantity(int amount) {
        this.quantity += amount;
        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getClearanceLevel() { return clearanceLevel; }

    // Polymorphic method
    public String getDetails() {
        return "ID: " + itemId + " | Name: " + name + " | Qty: " + quantity + " | Clearance Req: Level " + clearanceLevel;
    }
}

// ==========================================
// 2. SUBCLASSES (Inheritance & Polymorphism)
// ==========================================
class Weapon extends Item {
    private String caliber;

    public Weapon(String itemId, String name, int quantity, String caliber, int clearanceLevel) {
        super(itemId, name, quantity, clearanceLevel);
        this.caliber = caliber;
    }

    @Override
    public String getDetails() {
        return "[WEAPON] " + super.getDetails() + " | Caliber: " + caliber;
    }
}

class Ammunition extends Item {
    private int roundsPerBox;

    public Ammunition(String itemId, String name, int quantity, int roundsPerBox, int clearanceLevel) {
        super(itemId, name, quantity, clearanceLevel);
        this.roundsPerBox = roundsPerBox;
    }

    @Override
    public String getDetails() {
        return "[AMMUNITION] " + super.getDetails() + " | Rounds/Box: " + roundsPerBox;
    }
}

class Ration extends Item {
    private String expiryDate;

    public Ration(String itemId, String name, int quantity, String expiryDate, int clearanceLevel) {
        super(itemId, name, quantity, clearanceLevel);
        this.expiryDate = expiryDate;
    }

    @Override
    public String getDetails() {
        return "[RATION/SUPPLY] " + super.getDetails() + " | Expiry: " + expiryDate;
    }
}

// ==========================================
// 3. USER CLASS
// ==========================================
class User {
    private String badgeId;
    private String name;
    private String rank;
    private int clearanceLevel;

    public User(String badgeId, String name, String rank, int clearanceLevel) {
        this.badgeId = badgeId;
        this.name = name;
        this.rank = rank;
        this.clearanceLevel = clearanceLevel;
    }

    public String getName() { return name; }
    public String getRank() { return rank; }
    public int getClearanceLevel() { return clearanceLevel; }
}

// ==========================================
// 4. INVENTORY MANAGER CLASS
// ==========================================
class InventoryManager {
    private Map<String, Item> inventory = new HashMap<>();
    private List<String> transactionLogs = new ArrayList<>();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addItem(Item item, User user) {
        if (user.getClearanceLevel() < item.getClearanceLevel()) {
            System.out.println("ACCESS DENIED: " + user.getRank() + " " + user.getName() + " lacks clearance for item '" + item.getName() + "'.");
            return;
        }

        inventory.put(item.getItemId(), item);
        String logEntry = "[" + dtf.format(LocalDateTime.now()) + "] ADDED: " + item.getName() + " (Qty: " + item.getQuantity() + ") by " + user.getRank() + " " + user.getName();
        transactionLogs.add(logEntry);
        System.out.println("Successfully added/updated item: " + item.getName());
    }

    public void modifyStock(String itemId, int amount, User user) {
        if (!inventory.containsKey(itemId)) {
            System.out.println("Error: Item ID not found in armory inventory.");
            return;
        }

        Item item = inventory.get(itemId);
        if (user.getClearanceLevel() < item.getClearanceLevel()) {
            System.out.println("ACCESS DENIED: Insufficient clearance level to modify " + item.getName() + ".");
            return;
        }

        item.updateQuantity(amount);
        String action = amount > 0 ? "RESTOCKED" : "ISSUED/DISPATCHED";
        String logEntry = "[" + dtf.format(LocalDateTime.now()) + "] " + action + ": " + Math.abs(amount) + " units of " + item.getName() + " by " + user.getRank() + " " + user.getName();
        transactionLogs.add(logEntry);
        System.out.println("Stock updated successfully. New quantity for " + item.getName() + ": " + item.getQuantity());
    }

    public void viewInventory(User user) {
        System.out.println("\n--- ARMY ARMORY INVENTORY ---");
        if (inventory.isEmpty()) {
            System.out.println("Inventory is currently empty.");
            return;
        }

        for (Item item : inventory.values()) {
            if (user.getClearanceLevel() >= item.getClearanceLevel()) {
                System.out.println(item.getDetails());
            } else {
                System.out.println("[CLASSIFIED] ID: " + item.getItemId() + " | Name: " + item.getName() + " | [RESTRICTED CLEARANCE REQUIRED]");
            }
        }
    }

    public void viewLogs(User user) {
        System.out.println("\n--- SECURE TRANSACTION LOGS ---");
        if (user.getClearanceLevel() < 4) {
            System.out.println("ACCESS DENIED: Audit logs require General/Commander clearance (Level 4+).");
            return;
        }
        if (transactionLogs.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }
        for (String log : transactionLogs) {
            System.out.println(log);
        }
    }
}

// ==========================================
// 5. MAIN PROGRAM & CLI INTERFACE
// ==========================================
public class ArmyStockManagement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InventoryManager manager = new InventoryManager();

        // Pre-populate base inventory items
        manager.addItem(new Weapon("W001", "M4 Carbine", 150, "5.56mm", 3), new User("B003", "General Vance", "General", 5));
        manager.addItem(new Ammunition("A001", "5.56mm NATO Rounds", 5000, 30, 2), new User("B003", "General Vance", "General", 5));
        manager.addItem(new Ration("R001", "MRE Packets", 1200, "2028-12-31", 1), new User("B003", "General Vance", "General", 5));

        // Setup active user profiles
        Map<String, User> users = new HashMap<>();
        users.put("1", new User("B001", "Private John", "Private", 1));
        users.put("2", new User("B002", "Sergeant Miller", "Sergeant", 3));
        users.put("3", new User("B003", "General Vance", "General", 5));

        System.out.println("========================================");
        System.out.println("  WELCOME TO THE ARMY STOCK MANAGEMENT  ");
        System.out.println("========================================");
        
        System.out.println("Select Active User Session:");
        System.out.println("1. Private John (Clearance Level 1 - Infantry)");
        System.out.println("2. Sergeant Miller (Clearance Level 3 - NCO/Armorer)");
        System.out.println("3. General Vance (Clearance Level 5 - High Command)");
        
        System.out.print("Enter user choice (1-3): ");
        String choice = scanner.nextLine().trim();
        User currentUser = users.getOrDefault(choice, users.get("1"));
        System.out.println("\nSession initialized as: " + currentUser.getRank() + " " + currentUser.getName() + " (Clearance Level " + currentUser.getClearanceLevel() + ")");

        while (true) {
            System.out.println("\n--- COMMAND MENU ---");
            System.out.println("1. View Inventory");
            System.out.println("2. Add New Item");
            System.out.println("3. Restock Item");
            System.out.println("4. Issue / Dispatch Item");
            System.out.println("5. View Audit Logs (Restricted)");
            System.out.println("6. Switch User Session");
            System.out.println("7. Exit System");

            System.out.print("Enter your command (1-7): ");
            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1":
                    manager.viewInventory(currentUser);
                    break;

                case "2":
                    try {
                        System.out.println("\n--- Add New Stock Item ---");
                        System.out.print("Enter item type (1: Weapon, 2: Ammunition, 3: Ration): ");
                        String itemType = scanner.nextLine().trim();
                        System.out.print("Enter Item ID (e.g., W002): ");
                        String itemId = scanner.nextLine().trim();
                        System.out.print("Enter Item Name: ");
                        String name = scanner.nextLine().trim();
                        System.out.print("Enter Initial Quantity: ");
                        int quantity = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Enter Clearance Level Required (1-5): ");
                        int clearance = Integer.parseInt(scanner.nextLine().trim());

                        Item newItem = null;
                        if (itemType.equals("1")) {
                            System.out.print("Enter Weapon Caliber: ");
                            String caliber = scanner.nextLine().trim();
                            newItem = new Weapon(itemId, name, quantity, caliber, clearance);
                        } else if (itemType.equals("2")) {
                            System.out.print("Enter Rounds per Box: ");
                            int rounds = Integer.parseInt(scanner.nextLine().trim());
                            newItem = new Ammunition(itemId, name, quantity, rounds, clearance);
                        } else if (itemType.equals("3")) {
                            System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
                            String expiry = scanner.nextLine().trim();
                            newItem = new Ration(itemId, name, quantity, expiry, clearance);
                        } else {
                            System.out.println("Invalid item type selection.");
                            break;
                        }

                        manager.addItem(newItem, currentUser);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid numerical input. Please try again.");
                    }
                    break;

                case "3":
                    try {
                        System.out.print("Enter Item ID to restock: ");
                        String restockId = scanner.nextLine().trim();
                        System.out.print("Enter quantity to add: ");
                        int amount = Integer.parseInt(scanner.nextLine().trim());
                        if (amount > 0) {
                            manager.modifyStock(restockId, amount, currentUser);
                        } else {
                            System.out.println("Restock amount must be positive.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number entered.");
                    }
                    break;

                case "4":
                    try {
                        System.out.print("Enter Item ID to issue/dispatch: ");
                        String issueId = scanner.nextLine().trim();
                        System.out.print("Enter quantity to issue: ");
                        int amount = Integer.parseInt(scanner.nextLine().trim());
                        if (amount > 0) {
                            manager.modifyStock(issueId, -amount, currentUser);
                        } else {
                            System.out.println("Issue amount must be positive.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number entered.");
                    }
                    break;

                case "5":
                    manager.viewLogs(currentUser);
                    break;

                case "6":
                    System.out.println("\nSwitching User Session...");
                    System.out.println("1. Private John (Level 1)");
                    System.out.println("2. Sergeant Miller (Level 3)");
                    System.out.println("3. General Vance (Level 5)");
                    System.out.print("Enter user choice (1-3): ");
                    String subChoice = scanner.nextLine().trim();
                    if (users.containsKey(subChoice)) {
                        currentUser = users.get(subChoice);
                        System.out.println("Switched session to: " + currentUser.getRank() + " " + currentUser.getName() + " (Clearance Level " + currentUser.getClearanceLevel() + ")");
                    } else {
                        System.out.println("Invalid selection.");
                    }
                    break;

                case "7":
                    System.out.println("Shutting down system. Stay safe, soldier.");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option. Please choose between 1 and 7.");
            }
        }
    }
}
