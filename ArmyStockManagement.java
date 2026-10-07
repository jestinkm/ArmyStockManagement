import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * ============================================================
 *        SECURE ARMY STOCK MANAGEMENT SYSTEM
 * ============================================================
 *
 * Features:
 * 1. Login & authentication
 * 2. Role-based access
 * 3. Weapon management
 * 4. Ammunition management
 * 5. Ration management
 * 6. Stock In
 * 7. Stock Out / Issue
 * 8. Stock Transfer between Army Units
 * 9. Low Stock Alert
 * 10. Expiry Alert
 * 11. Search inventory
 * 12. Inventory dashboard
 * 13. Transaction history
 * 14. Audit logs
 * 15. Reports
 * 16. Input validation
 *
 * Compile:
 * javac ArmyStockManagement.java
 *
 * Run:
 * java ArmyStockManagement
 */
public class ArmyStockManagement {

    // ============================================================
    // CONSTANTS
    // ============================================================

    static Scanner sc = new Scanner(System.in);

    static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    static Map<String, Item> inventory = new LinkedHashMap<>();
    static List<Transaction> transactions = new ArrayList<>();
    static List<AuditLog> auditLogs = new ArrayList<>();
    static List<Transfer> transfers = new ArrayList<>();
    static List<User> users = new ArrayList<>();

    static String currentUser;

    // ============================================================
    // MAIN
    // ============================================================

    public static void main(String[] args) {

        initializeUsers();
        initializeInventory();

        printBanner();

        if (!login()) {
            System.out.println("\nToo many failed login attempts.");
            System.out.println("System locked.");
            return;
        }

        addAudit("LOGIN", "User logged into the system");

        while (true) {

            displayDashboard();

            System.out.print("\nEnter your choice: ");
            int choice = readInt();

            switch (choice) {

                case 1:
                    viewInventory();
                    break;

                case 2:
                    addNewItem();
                    break;

                case 3:
                    stockIn();
                    break;

                case 4:
                    stockOut();
                    break;

                case 5:
                    transferStock();
                    break;

                case 6:
                    searchInventory();
                    break;

                case 7:
                    lowStockAlert();
                    break;

                case 8:
                    expiryAlert();
                    break;

                case 9:
                    transactionHistory();
                    break;

                case 10:
                    viewAuditLogs();
                    break;

                case 11:
                    generateReport();
                    break;

                case 12:
                    viewTransfers();
                    break;

                case 13:
                    itemDetails();
                    break;

                case 14:
                    logout();
                    return;

                default:
                    System.out.println("\nInvalid choice.");
            }

            pause();
        }
    }

    // ============================================================
    // INITIAL DATA
    // ============================================================

    static void initializeUsers() {

        users.add(new User(
                "admin",
                "admin123",
                "ADMIN",
                "General Headquarters"
        ));

        users.add(new User(
                "officer",
                "officer123",
                "OFFICER",
                "Chennai Army Base"
        ));

        users.add(new User(
                "sergeant",
                "sergeant123",
                "SERGEANT",
                "Salem Army Base"
        ));

        users.add(new User(
                "storekeeper",
                "store123",
                "STOREKEEPER",
                "Coimbatore Army Base"
        ));
    }

    static void initializeInventory() {

        inventory.put(
                "W001",
                new Weapon(
                        "W001",
                        "Assault Rifle",
                        "Weapon",
                        50,
                        20,
                        "Chennai Army Base",
                        "SERGEANT",
                        "AR-15"
                )
        );

        inventory.put(
                "W002",
                new Weapon(
                        "W002",
                        "Combat Helmet",
                        "Protection",
                        80,
                        25,
                        "Salem Army Base",
                        "SERGEANT",
                        "MICH-2000"
                )
        );

        inventory.put(
                "W003",
                new Weapon(
                        "W003",
                        "Body Armor",
                        "Protection",
                        45,
                        15,
                        "Chennai Army Base",
                        "OFFICER",
                        "LEVEL-IV"
                )
        );

        inventory.put(
                "A001",
                new Ammunition(
                        "A001",
                        "5.56mm Ammunition",
                        "Ammunition",
                        5000,
                        1000,
                        "Chennai Army Base",
                        "OFFICER",
                        "5.56mm"
                )
        );

        inventory.put(
                "A002",
                new Ammunition(
                        "A002",
                        "9mm Ammunition",
                        "Ammunition",
                        2500,
                        500,
                        "Salem Army Base",
                        "SERGEANT",
                        "9mm"
                )
        );

        inventory.put(
                "R001",
                new Ration(
                        "R001",
                        "MRE Food Pack",
                        "Ration",
                        300,
                        100,
                        "Salem Army Base",
                        "STOREKEEPER",
                        LocalDate.now().plusDays(45)
                )
        );

        inventory.put(
                "R002",
                new Ration(
                        "R002",
                        "Drinking Water",
                        "Ration",
                        1000,
                        200,
                        "Coimbatore Army Base",
                        "STOREKEEPER",
                        LocalDate.now().plusDays(120)
                )
        );

        inventory.put(
                "M001",
                new GeneralItem(
                        "M001",
                        "Medical Kit",
                        "Medical",
                        75,
                        20,
                        "Chennai Army Base",
                        "SERGEANT"
                )
        );
    }

    // ============================================================
    // BANNER
    // ============================================================

    static void printBanner() {

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("           SECURE ARMY STOCK MANAGEMENT SYSTEM");
        System.out.println("==============================================================");
        System.out.println("             Military Inventory & Logistics");
        System.out.println("==============================================================");
    }

    // ============================================================
    // LOGIN
    // ============================================================

    static boolean login() {

        for (int attempt = 1; attempt <= 3; attempt++) {

            System.out.println("\n---------------- LOGIN ----------------");

            System.out.print("Username: ");
            String username = sc.nextLine();

            System.out.print("Password: ");
            String password = sc.nextLine();

            for (User user : users) {

                if (user.username.equals(username)
                        && user.password.equals(password)) {

                    currentUser = username;

                    System.out.println(
                            "\nLogin successful."
                    );

                    System.out.println(
                            "Welcome, " + username
                    );

                    System.out.println(
                            "Role: " + user.role
                    );

                    System.out.println(
                            "Unit: " + user.unit
                    );

                    return true;
                }
            }

            System.out.println(
                    "Invalid username or password."
            );

            System.out.println(
                    "Attempts remaining: " + (3 - attempt)
            );
        }

        return false;
    }

    // ============================================================
    // DASHBOARD
    // ============================================================

    static void displayDashboard() {

        int totalItems = inventory.size();
        int totalQuantity = 0;
        int lowStock = 0;
        int expiring = 0;

        for (Item item : inventory.values()) {

            totalQuantity += item.quantity;

            if (item.quantity <= item.minimumStock) {
                lowStock++;
            }

            if (item instanceof Ration) {

                Ration ration = (Ration) item;

                long days =
                        ChronoUnit.DAYS.between(
                                LocalDate.now(),
                                ration.expiryDate
                        );

                if (days <= 30) {
                    expiring++;
                }
            }
        }

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("                        DASHBOARD");
        System.out.println("==============================================================");

        System.out.println(
                "Logged User      : " + currentUser
        );

        System.out.println(
                "Date             : " +
                        LocalDateTime.now().format(DATE_TIME_FORMAT)
        );

        System.out.println("--------------------------------------------------------------");

        System.out.printf(
                "Total Item Types : %-10d%n",
                totalItems
        );

        System.out.printf(
                "Total Stock      : %-10d%n",
                totalQuantity
        );

        System.out.printf(
                "Low Stock Items  : %-10d%n",
                lowStock
        );

        System.out.printf(
                "Expiring Items   : %-10d%n",
                expiring
        );

        System.out.printf(
                "Transactions      : %-10d%n",
                transactions.size()
        );

        System.out.printf(
                "Transfers         : %-10d%n",
                transfers.size()
        );

        System.out.println("--------------------------------------------------------------");

        System.out.println("[1]  View Inventory");
        System.out.println("[2]  Add New Item");
        System.out.println("[3]  Stock In");
        System.out.println("[4]  Stock Out / Issue");
        System.out.println("[5]  Transfer Stock");
        System.out.println("[6]  Search Inventory");
        System.out.println("[7]  Low Stock Alert");
        System.out.println("[8]  Expiry Alert");
        System.out.println("[9]  Transaction History");
        System.out.println("[10] Audit Logs");
        System.out.println("[11] Generate Report");
        System.out.println("[12] Transfer History");
        System.out.println("[13] Item Details");
        System.out.println("[14] Logout");

        System.out.println("==============================================================");
    }

    // ============================================================
    // VIEW INVENTORY
    // ============================================================

    static void viewInventory() {

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("                       INVENTORY");
        System.out.println("==============================================================");

        if (inventory.isEmpty()) {

            System.out.println("Inventory is empty.");
            return;
        }

        System.out.printf(
                "%-6s %-22s %-15s %-10s %-10s %-20s%n",
                "ID",
                "NAME",
                "CATEGORY",
                "STOCK",
                "MIN",
                "UNIT"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (Item item : inventory.values()) {

            System.out.printf(
                    "%-6s %-22s %-15s %-10d %-10d %-20s%n",
                    item.id,
                    item.name,
                    item.category,
                    item.quantity,
                    item.minimumStock,
                    item.unit
            );
        }
    }

    // ============================================================
    // ADD NEW ITEM
    // ============================================================

    static void addNewItem() {

        if (!hasPermission("ADMIN", "OFFICER", "STOREKEEPER")) {

            accessDenied();
            return;
        }

        System.out.println("\n---------------- ADD NEW ITEM ----------------");

        System.out.print("Item ID: ");
        String id = sc.nextLine().toUpperCase();

        if (inventory.containsKey(id)) {

            System.out.println("Item ID already exists.");
            return;
        }

        System.out.print("Item Name: ");
        String name = sc.nextLine();

        System.out.print("Category: ");
        String category = sc.nextLine();

        System.out.print("Initial Quantity: ");
        int quantity = readPositiveInt();

        System.out.print("Minimum Stock Level: ");
        int minimum = readPositiveInt();

        System.out.print("Army Unit/Base: ");
        String unit = sc.nextLine();

        System.out.print("Required Clearance: ");
        String clearance = sc.nextLine().toUpperCase();

        Item item;

        if (category.equalsIgnoreCase("Weapon")) {

            System.out.print("Weapon Model: ");
            String model = sc.nextLine();

            item = new Weapon(
                    id,
                    name,
                    category,
                    quantity,
                    minimum,
                    unit,
                    clearance,
                    model
            );

        } else if (category.equalsIgnoreCase("Ammunition")) {

            System.out.print("Caliber: ");
            String caliber = sc.nextLine();

            item = new Ammunition(
                    id,
                    name,
                    category,
                    quantity,
                    minimum,
                    unit,
                    clearance,
                    caliber
            );

        } else if (category.equalsIgnoreCase("Ration")) {

            System.out.print(
                    "Expiry date (dd-MM-yyyy): "
            );

            LocalDate expiry = readDate();

            item = new Ration(
                    id,
                    name,
                    category,
                    quantity,
                    minimum,
                    unit,
                    clearance,
                    expiry
            );

        } else {

            item = new GeneralItem(
                    id,
                    name,
                    category,
                    quantity,
                    minimum,
                    unit,
                    clearance
            );
        }

        inventory.put(id, item);

        addAudit(
                "ADD ITEM",
                "Added " + name + " (" + id + ")"
        );

        System.out.println(
                "\nItem added successfully."
        );
    }

    // ============================================================
    // STOCK IN
    // ============================================================

    static void stockIn() {

        if (!hasPermission("ADMIN", "OFFICER", "STOREKEEPER")) {

            accessDenied();
            return;
        }

        System.out.println("\n---------------- STOCK IN ----------------");

        System.out.print("Enter Item ID: ");
        String id = sc.nextLine().toUpperCase();

        Item item = inventory.get(id);

        if (item == null) {

            System.out.println("Item not found.");
            return;
        }

        System.out.println(
                "Item: " + item.name
        );

        System.out.println(
                "Current Stock: " + item.quantity
        );

        System.out.print("Quantity to add: ");
        int quantity = readPositiveInt();

        item.quantity += quantity;

        transactions.add(
                new Transaction(
                        "STOCK-IN",
                        id,
                        item.name,
                        quantity,
                        currentUser,
                        item.unit
                )
        );

        addAudit(
                "STOCK IN",
                quantity + " units added to " + item.name
        );

        System.out.println(
                "\nStock updated successfully."
        );

        System.out.println(
                "New Stock: " + item.quantity
        );
    }

    // ============================================================
    // STOCK OUT
    // ============================================================

    static void stockOut() {

        if (!hasPermission(
                "ADMIN",
                "OFFICER",
                "SERGEANT",
                "STOREKEEPER")) {

            accessDenied();
            return;
        }

        System.out.println("\n---------------- STOCK OUT ----------------");

        System.out.print("Enter Item ID: ");
        String id = sc.nextLine().toUpperCase();

        Item item = inventory.get(id);

        if (item == null) {

            System.out.println("Item not found.");
            return;
        }

        System.out.println(
                "Item: " + item.name
        );

        System.out.println(
                "Available Stock: " + item.quantity
        );

        if (!checkClearance(item.requiredClearance)) {

            System.out.println(
                    "You do not have sufficient clearance."
            );

            return;
        }

        System.out.print("Quantity to issue: ");
        int quantity = readPositiveInt();

        if (quantity > item.quantity) {

            System.out.println(
                    "Insufficient stock."
            );

            return;
        }

        item.quantity -= quantity;

        transactions.add(
                new Transaction(
                        "STOCK-OUT",
                        id,
                        item.name,
                        quantity,
                        currentUser,
                        item.unit
                )
        );

        addAudit(
                "STOCK OUT",
                quantity + " units issued from " + item.name
        );

        System.out.println(
                "\nStock issued successfully."
        );

        System.out.println(
                "Remaining Stock: " + item.quantity
        );

        if (item.quantity <= item.minimumStock) {

            System.out.println(
                    "\nWARNING: LOW STOCK ALERT!"
            );
        }
    }

    // ============================================================
    // TRANSFER STOCK
    // ============================================================

    static void transferStock() {

        if (!hasPermission("ADMIN", "OFFICER")) {

            accessDenied();
            return;
        }

        System.out.println(
                "\n---------------- STOCK TRANSFER ----------------"
        );

        System.out.print("Item ID: ");
        String id = sc.nextLine().toUpperCase();

        Item item = inventory.get(id);

        if (item == null) {

            System.out.println("Item not found.");
            return;
        }

        System.out.println(
                "Item: " + item.name
        );

        System.out.println(
                "Current Unit: " + item.unit
        );

        System.out.println(
                "Available Stock: " + item.quantity
        );

        System.out.print("Destination Unit: ");
        String destination = sc.nextLine();

        if (destination.equalsIgnoreCase(item.unit)) {

            System.out.println(
                    "Destination cannot be the same unit."
            );

            return;
        }

        System.out.print("Transfer Quantity: ");
        int quantity = readPositiveInt();

        if (quantity > item.quantity) {

            System.out.println(
                    "Insufficient stock for transfer."
            );

            return;
        }

        String source = item.unit;

        item.quantity -= quantity;

        Transfer transfer = new Transfer(
                generateTransferId(),
                id,
                item.name,
                source,
                destination,
                quantity,
                currentUser
        );

        transfers.add(transfer);

        transactions.add(
                new Transaction(
                        "TRANSFER",
                        id,
                        item.name,
                        quantity,
                        currentUser,
                        source + " -> " + destination
                )
        );

        addAudit(
                "STOCK TRANSFER",
                quantity + " units of "
                        + item.name
                        + " transferred from "
                        + source
                        + " to "
                        + destination
        );

        System.out.println(
                "\nTransfer successful."
        );

        System.out.println(
                "Transfer ID: " + transfer.transferId
        );

        System.out.println(
                "Remaining Stock: " + item.quantity
        );
    }

    // ============================================================
    // SEARCH
    // ============================================================

    static void searchInventory() {

        System.out.println(
                "\n---------------- SEARCH INVENTORY ----------------"
        );

        System.out.print(
                "Enter ID / Name / Category / Unit: "
        );

        String keyword =
                sc.nextLine().toLowerCase();

        boolean found = false;

        for (Item item : inventory.values()) {

            if (item.id.toLowerCase().contains(keyword)
                    || item.name.toLowerCase().contains(keyword)
                    || item.category.toLowerCase().contains(keyword)
                    || item.unit.toLowerCase().contains(keyword)) {

                displayItem(item);

                found = true;
            }
        }

        if (!found) {

            System.out.println(
                    "No matching item found."
            );
        }
    }

    // ============================================================
    // LOW STOCK ALERT
    // ============================================================

    static void lowStockAlert() {

        System.out.println(
                "\n=============================================================="
        );

        System.out.println(
                "                    LOW STOCK ALERT"
        );

        System.out.println(
                "=============================================================="
        );

        boolean found = false;

        for (Item item : inventory.values()) {

            if (item.quantity <= item.minimumStock) {

                System.out.println(
                        "\nID       : " + item.id
                );

                System.out.println(
                        "Item     : " + item.name
                );

                System.out.println(
                        "Current  : " + item.quantity
                );

                System.out.println(
                        "Minimum  : " + item.minimumStock
                );

                System.out.println(
                        "Unit     : " + item.unit
                );

                if (item.quantity == 0) {

                    System.out.println(
                            "Status   : OUT OF STOCK"
                    );

                } else {

                    System.out.println(
                            "Status   : CRITICAL"
                    );
                }

                found = true;
            }
        }

        if (!found) {

            System.out.println(
                    "\nNo low-stock items."
            );
        }
    }

    // ============================================================
    // EXPIRY ALERT
    // ============================================================

    static void expiryAlert() {

        System.out.println(
                "\n=============================================================="
        );

        System.out.println(
                "                    EXPIRY ALERT"
        );

        System.out.println(
                "=============================================================="
        );

        boolean found = false;

        for (Item item : inventory.values()) {

            if (item instanceof Ration) {

                Ration ration = (Ration) item;

                long days =
                        ChronoUnit.DAYS.between(
                                LocalDate.now(),
                                ration.expiryDate
                        );

                if (days <= 30) {

                    System.out.println(
                            "\nID      : " + ration.id
                    );

                    System.out.println(
                            "Item    : " + ration.name
                    );

                    System.out.println(
                            "Expiry  : " +
                                    ration.expiryDate.format(
                                            DATE_FORMAT
                                    )
                    );

                    if (days < 0) {

                        System.out.println(
                                "Status  : EXPIRED"
                        );

                    } else {

                        System.out.println(
                                "Days Left: " + days
                        );

                        System.out.println(
                                "Status  : EXPIRING SOON"
                        );
                    }

                    found = true;
                }
            }
        }

        if (!found) {

            System.out.println(
                    "\nNo items are expiring within 30 days."
            );
        }
    }

    // ============================================================
    // TRANSACTION HISTORY
    // ============================================================

    static void transactionHistory() {

        System.out.println(
                "\n=============================================================="
        );

        System.out.println(
                "                   TRANSACTION HISTORY"
        );

        System.out.println(
                "=============================================================="
        );

        if (transactions.isEmpty()) {

            System.out.println(
                    "No transactions available."
            );

            return;
        }

        for (Transaction transaction : transactions) {

            System.out.println(
                    "\nTransaction ID : " +
                            transaction.transactionId
            );

            System.out.println(
                    "Type           : " +
                            transaction.type
            );

            System.out.println(
                    "Item           : " +
                            transaction.itemName
            );

            System.out.println(
                    "Quantity       : " +
                            transaction.quantity
            );

            System.out.println(
                    "User           : " +
                            transaction.user
            );

            System.out.println(
                    "Unit           : " +
                            transaction.unit
            );

            System.out.println(
                    "Time           : " +
                            transaction.time
            );

            System.out.println(
                    "--------------------------------------------------"
            );
        }
    }

    // ============================================================
    // AUDIT LOG
    // ============================================================

    static void addAudit(
            String action,
            String description) {

        auditLogs.add(
                new AuditLog(
                        currentUser,
                        action,
                        description
                )
        );
    }

    static void viewAuditLogs() {

        System.out.println(
                "\n=============================================================="
        );

        System.out.println(
                "                         AUDIT LOGS"
        );

        System.out.println(
                "=============================================================="
        );

        if (auditLogs.isEmpty()) {

            System.out.println(
                    "No audit logs available."
            );

            return;
        }

        for (AuditLog log : auditLogs) {

            System.out.println(
                    "\nTime        : " + log.time
            );

            System.out.println(
                    "User        : " + log.user
            );

            System.out.println(
                    "Action      : " + log.action
            );

            System.out.println(
                    "Description : " + log.description
            );

            System.out.println(
                    "--------------------------------------------------"
            );
        }
    }

    // ============================================================
    // REPORT
    // ============================================================

    static void generateReport() {

        System.out.println(
                "\n=============================================================="
        );

        System.out.println(
                "                  STOCK MANAGEMENT REPORT"
        );

        System.out.println(
                "=============================================================="
        );

        int totalStock = 0;

        int weaponCount = 0;
        int ammunitionCount = 0;
        int rationCount = 0;
        int otherCount = 0;

        int lowStock = 0;

        for (Item item : inventory.values()) {

            totalStock += item.quantity;

            if (item instanceof Weapon) {

                weaponCount++;

            } else if (item instanceof Ammunition) {

                ammunitionCount++;

            } else if (item instanceof Ration) {

                rationCount++;

            } else {

                otherCount++;
            }

            if (item.quantity <= item.minimumStock) {

                lowStock++;
            }
        }

        System.out.println(
                "Generated By      : " + currentUser
        );

        System.out.println(
                "Generated On      : " +
                        LocalDateTime.now()
                                .format(DATE_TIME_FORMAT)
        );

        System.out.println(
                "\nTotal Item Types  : " + inventory.size()
        );

        System.out.println(
                "Total Stock       : " + totalStock
        );

        System.out.println(
                "Weapons           : " + weaponCount
        );

        System.out.println(
                "Ammunition        : " + ammunitionCount
        );

        System.out.println(
                "Ration Items      : " + rationCount
        );

        System.out.println(
                "Other Items       : " + otherCount
        );

        System.out.println(
                "Low Stock Items   : " + lowStock
        );

        System.out.println(
                "Transactions      : " + transactions.size()
        );

        System.out.println(
                "Transfers         : " + transfers.size()
        );

        System.out.println(
                "Audit Logs        : " + auditLogs.size()
        );

        System.out.println(
                "\n--------------------------------------------------------------"
        );

        System.out.println(
                "                  CATEGORY SUMMARY"
        );

        System.out.println(
                "--------------------------------------------------------------"
        );

        Map<String, Integer> categoryStock =
                new HashMap<>();

        for (Item item : inventory.values()) {

            categoryStock.put(
                    item.category,
                    categoryStock.getOrDefault(
                            item.category,
                            0
                    ) + item.quantity
            );
        }

        for (Map.Entry<String, Integer> entry
                : categoryStock.entrySet()) {

            System.out.printf(
                    "%-20s : %d%n",
                    entry.getKey(),
                    entry.getValue()
            );
        }
    }

    // ============================================================
    // TRANSFER HISTORY
    // ============================================================

    static void viewTransfers() {

        System.out.println(
                "\n=============================================================="
        );

        System.out.println(
                "                    TRANSFER HISTORY"
        );

        System.out.println(
                "=============================================================="
        );

        if (transfers.isEmpty()) {

            System.out.println(
                    "No transfers available."
            );

            return;
        }

        for (Transfer transfer : transfers) {

            System.out.println(
                    "\nTransfer ID : " +
                            transfer.transferId
            );

            System.out.println(
                    "Item        : " +
                            transfer.itemName
            );

            System.out.println(
                    "From        : " +
                            transfer.source
            );

            System.out.println(
                    "To          : " +
                            transfer.destination
            );

            System.out.println(
                    "Quantity    : " +
                            transfer.quantity
            );

            System.out.println(
                    "Approved By : " +
                            transfer.user
            );

            System.out.println(
                    "Time        : " +
                            transfer.time
            );

            System.out.println(
                    "--------------------------------------------------"
            );
        }
    }

    // ============================================================
    // ITEM DETAILS
    // ============================================================

    static void itemDetails() {

        System.out.println(
                "\n---------------- ITEM DETAILS ----------------"
        );

        System.out.print("Enter Item ID: ");

        String id =
                sc.nextLine().toUpperCase();

        Item item = inventory.get(id);

        if (item == null) {

            System.out.println(
                    "Item not found."
            );

            return;
        }

        displayItem(item);
    }

    static void displayItem(Item item) {

        System.out.println(
                "\n================================================"
        );

        System.out.println(
                "Item ID          : " + item.id
        );

        System.out.println(
                "Name             : " + item.name
        );

        System.out.println(
                "Category         : " + item.category
        );

        System.out.println(
                "Quantity         : " + item.quantity
        );

        System.out.println(
                "Minimum Stock    : " + item.minimumStock
        );

        System.out.println(
                "Army Unit        : " + item.unit
        );

        System.out.println(
                "Required Clearance: "
                        + item.requiredClearance
        );

        if (item instanceof Weapon) {

            Weapon weapon = (Weapon) item;

            System.out.println(
                    "Weapon Model     : "
                            + weapon.model
            );

        } else if (item instanceof Ammunition) {

            Ammunition ammunition =
                    (Ammunition) item;

            System.out.println(
                    "Caliber          : "
                            + ammunition.caliber
            );

        } else if (item instanceof Ration) {

            Ration ration =
                    (Ration) item;

            System.out.println(
                    "Expiry Date      : "
                            + ration.expiryDate
                                    .format(DATE_FORMAT)
            );
        }

        System.out.println(
                "================================================"
        );
    }

    // ============================================================
    // PERMISSION
    // ============================================================

    static boolean hasPermission(
            String... allowedRoles) {

        User current = getCurrentUser();

        if (current == null) {
            return false;
        }

        for (String role : allowedRoles) {

            if (current.role.equalsIgnoreCase(role)) {
                return true;
            }
        }

        return false;
    }

    static boolean checkClearance(
            String requiredClearance) {

        User user = getCurrentUser();

        if (user == null) {
            return false;
        }

        Map<String, Integer> level =
                new HashMap<>();

        level.put("STOREKEEPER", 1);
        level.put("SERGEANT", 2);
        level.put("OFFICER", 3);
        level.put("ADMIN", 4);

        int userLevel =
                level.getOrDefault(
                        user.role.toUpperCase(),
                        0
                );

        int requiredLevel =
                level.getOrDefault(
                        requiredClearance.toUpperCase(),
                        0
                );

        return userLevel >= requiredLevel;
    }

    static User getCurrentUser() {

        for (User user : users) {

            if (user.username.equals(currentUser)) {

                return user;
            }
        }

        return null;
    }

    static void accessDenied() {

        System.out.println(
                "\nACCESS DENIED."
        );

        System.out.println(
                "Your role does not have permission "
                        + "to perform this operation."
        );
    }

    // ============================================================
    // LOGOUT
    // ============================================================

    static void logout() {

        addAudit(
                "LOGOUT",
                "User logged out"
        );

        System.out.println(
                "\nUser logged out successfully."
        );

        System.out.println(
                "Thank you for using Army Stock Management System."
        );
    }

    // ============================================================
    // ID GENERATORS
    // ============================================================

    static String generateTransactionId() {

        return "TXN-" +
                System.currentTimeMillis();
    }

    static String generateTransferId() {

        return "TRF-" +
                System.currentTimeMillis();
    }

    // ============================================================
    // INPUT METHODS
    // ============================================================

    static int readInt() {

        while (true) {

            try {

                String input =
                        sc.nextLine();

                return Integer.parseInt(
                        input.trim()
                );

            } catch (Exception e) {

                System.out.print(
                        "Enter a valid number: "
                );
            }
        }
    }

    static int readPositiveInt() {

        while (true) {

            int value = readInt();

            if (value > 0) {

                return value;
            }

            System.out.print(
                    "Enter a value greater than 0: "
            );
        }
    }

    static LocalDate readDate() {

        while (true) {

            try {

                String input =
                        sc.nextLine();

                return LocalDate.parse(
                        input,
                        DATE_FORMAT
                );

            } catch (Exception e) {

                System.out.print(
                        "Invalid date. Use dd-MM-yyyy: "
                );
            }
        }
    }

    static void pause() {

        System.out.println(
                "\nPress ENTER to continue..."
        );

        sc.nextLine();
    }

    // ============================================================
    // BASE ITEM CLASS
    // ============================================================

    static abstract class Item {

        protected String id;
        protected String name;
        protected String category;
        protected int quantity;
        protected int minimumStock;
        protected String unit;
        protected String requiredClearance;

        public Item(
                String id,
                String name,
                String category,
                int quantity,
                int minimumStock,
                String unit,
                String requiredClearance) {

            this.id = id;
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.minimumStock = minimumStock;
            this.unit = unit;
            this.requiredClearance = requiredClearance;
        }

        public abstract String getItemType();
    }

    // ============================================================
    // WEAPON
    // ============================================================

    static class Weapon extends Item {

        String model;

        public Weapon(
                String id,
                String name,
                String category,
                int quantity,
                int minimumStock,
                String unit,
                String requiredClearance,
                String model) {

            super(
                    id,
                    name,
                    category,
                    quantity,
                    minimumStock,
                    unit,
                    requiredClearance
            );

            this.model = model;
        }

        @Override
        public String getItemType() {

            return "Weapon";
        }
    }

    // ============================================================
    // AMMUNITION
    // ============================================================

    static class Ammunition extends Item {

        String caliber;

        public Ammunition(
                String id,
                String name,
                String category,
                int quantity,
                int minimumStock,
                String unit,
                String requiredClearance,
                String caliber) {

            super(
                    id,
                    name,
                    category,
                    quantity,
                    minimumStock,
                    unit,
                    requiredClearance
            );

            this.caliber = caliber;
        }

        @Override
        public String getItemType() {

            return "Ammunition";
        }
    }

    // ============================================================
    // RATION
    // ============================================================

    static class Ration extends Item {

        LocalDate expiryDate;

        public Ration(
                String id,
                String name,
                String category,
                int quantity,
                int minimumStock,
                String unit,
                String requiredClearance,
                LocalDate expiryDate) {

            super(
                    id,
                    name,
                    category,
                    quantity,
                    minimumStock,
                    unit,
                    requiredClearance
            );

            this.expiryDate = expiryDate;
        }

        @Override
        public String getItemType() {

            return "Ration";
        }
    }

    // ============================================================
    // GENERAL ITEM
    // ============================================================

    static class GeneralItem extends Item {

        public GeneralItem(
                String id,
                String name,
                String category,
                int quantity,
                int minimumStock,
                String unit,
                String requiredClearance) {

            super(
                    id,
                    name,
                    category,
                    quantity,
                    minimumStock,
                    unit,
                    requiredClearance
            );
        }

        @Override
        public String getItemType() {

            return "General";
        }
    }

    // ============================================================
    // USER
    // ============================================================

    static class User {

        String username;
        String password;
        String role;
        String unit;

        public User(
                String username,
                String password,
                String role,
                String unit) {

            this.username = username;
            this.password = password;
            this.role = role;
            this.unit = unit;
        }
    }

    // ============================================================
    // TRANSACTION
    // ============================================================

    static class Transaction {

        String transactionId;
        String type;
        String itemId;
        String itemName;
        int quantity;
        String user;
        String unit;
        String time;

        public Transaction(
                String type,
                String itemId,
                String itemName,
                int quantity,
                String user,
                String unit) {

            this.transactionId =
                    generateTransactionId();

            this.type = type;
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.user = user;
            this.unit = unit;

            this.time =
                    LocalDateTime.now()
                            .format(DATE_TIME_FORMAT);
        }
    }

    // ============================================================
    // TRANSFER
    // ============================================================

    static class Transfer {

        String transferId;
        String itemId;
        String itemName;
        String source;
        String destination;
        int quantity;
        String user;
        String time;

        public Transfer(
                String transferId,
                String itemId,
                String itemName,
                String source,
                String destination,
                int quantity,
                String user) {

            this.transferId = transferId;
            this.itemId = itemId;
            this.itemName = itemName;
            this.source = source;
            this.destination = destination;
            this.quantity = quantity;
            this.user = user;

            this.time =
                    LocalDateTime.now()
                            .format(DATE_TIME_FORMAT);
        }
    }

    // ============================================================
    // AUDIT LOG
    // ============================================================

    static class AuditLog {

        String user;
        String action;
        String description;
        String time;

        public AuditLog(
                String user,
                String action,
                String description) {

            this.user = user;
            this.action = action;
            this.description = description;

            this.time =
                    LocalDateTime.now()
                            .format(DATE_TIME_FORMAT);
        }
    }
}
