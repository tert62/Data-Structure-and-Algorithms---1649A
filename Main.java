import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        BookStoreSystem system = new BookStoreSystem();
        Scanner sc = new Scanner(System.in);

        system.login();

        system.loadBooks();
        system.loadOrders();

        int choice;

        do {
            System.out.println("\n===== BOOKSTORE (" + system.currentUser.role + ") =====");

            System.out.println("1. View Books");
            System.out.println("2. Sort Books");

            // ADMIN ONLY
            if (system.isAdmin()) {
                System.out.println("3. Create Book");
            }

            // ADMIN + STAFF
            if (system.isAdmin() || system.isStaff()) {
                System.out.println("4. Create Order");
                System.out.println("5. Process Order");
            }

            // ADMIN ONLY
            if (system.isAdmin()) {
                System.out.println("6. Undo");
            }

            // ALL
            System.out.println("7. Search Order");
            System.out.println("8. Exit");

            System.out.print("Choose: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    system.viewBooks();
                    break;

                case 2:
                    system.sortBooks();
                    break;

                case 3:
                    if (system.isAdmin())
                        system.createBook();
                    break;

                case 4:
                    if (system.isAdmin() || system.isStaff())
                        system.createOrder();
                    break;

                case 5:
                    if (system.isAdmin() || system.isStaff())
                        system.processOrder();
                    break;

                case 6:
                    if (system.isAdmin())
                        system.undo();
                    break;

                case 7:
                    system.searchOrder();
                    break;

            }

        } while (choice != 8);

        System.out.println("Program closed");
    }
}