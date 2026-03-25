import java.util.*;
import java.io.*;

public class BookStoreSystem {

    Scanner sc = new Scanner(System.in);

    LinkedList<Book> books = new LinkedList<>();
    LinkedList<Order> orders = new LinkedList<>();

    LinkedList<Order> queue = new LinkedList<>();
    Stack<Action> history = new Stack<>();

    User currentUser;

    // ===== LOGIN =====
    public void login() {

        System.out.print("Enter username: ");
        String name = sc.nextLine().trim().toLowerCase(); 

        if (name.equals("admin")) {
            currentUser = new User("admin", "ADMIN");
        } 
        else if (name.equals("staff")) {
            currentUser = new User("staff", "STAFF");
        } 
        else {
            currentUser = new User(name, "VIEWER");
        }

        System.out.println("Logged in as: " + currentUser.role);
    }

    public boolean isAdmin() {
        return currentUser.role.equals("ADMIN");
    }

    public boolean isStaff() {
        return currentUser.role.equals("STAFF");
    }

    // ===== LOAD BOOKS =====
    public void loadBooks() {
        try {
            File file = new File("books.csv");
            if (!file.exists()) return;

            Scanner f = new Scanner(file);
            boolean skip = true;

            while (f.hasNextLine()) {

                String line = f.nextLine().trim();
                if (line.isEmpty()) continue;

                if (skip) { skip = false; continue; }

                String[] d = line.split(",");

                books.add(new Book(
                        Integer.parseInt(d[0]),
                        d[1],
                        d[2],
                        Integer.parseInt(d[3])
                ));
            }

            f.close();
        } catch (Exception e) {
            System.out.println("Error loading books");
        }
    }

    // ===== LOAD ORDERS =====
    public void loadOrders() {
        try {
            File file = new File("orders.csv");
            if (!file.exists()) return;

            Scanner f = new Scanner(file);
            boolean skip = true;

            while (f.hasNextLine()) {

                String line = f.nextLine().trim();
                if (line.isEmpty()) continue;

                if (skip) { skip = false; continue; }

                String[] d = line.split(",");

                int id = Integer.parseInt(d[0]);
                String cus = d[1].trim().toLowerCase(); // 🔥 normalize
                int bookId = Integer.parseInt(d[2]);
                String status = d[3];

                Book found = null;
                for (Book b : books)
                    if (b.id == bookId) found = b;

                if (found != null) {
                    Order o = new Order(id, cus, found, status);
                    orders.add(o);

                    if (status.equalsIgnoreCase("PENDING"))
                        queue.add(o);
                }
            }

            f.close();
        } catch (Exception e) {
            System.out.println("Error loading orders");
        }
    }

    // ===== SAVE =====
    public void saveBooks() {
        try {
            PrintWriter pw = new PrintWriter("books.csv");
            pw.println("id,title,author,quantity");

            for (Book b : books)
                pw.println(b.toCSV());

            pw.close();
        } catch (Exception e) {}
    }

    public void saveOrders() {
        try {
            PrintWriter pw = new PrintWriter("orders.csv");
            pw.println("id,customer,bookId,status");

            for (Order o : orders)
                pw.println(o.toCSV());

            pw.close();
        } catch (Exception e) {}
    }

    // ===== VIEW =====
    public void viewBooks() {
        for (Book b : books)
            b.display();
        pause();
    }

    // ===== SORT =====
    public void sortBooks() {
        SortBooks.quickSort(books, 0, books.size() - 1);
        System.out.println("Sorted!");
        saveBooks();
        pause();
    }

    // ===== CREATE BOOK (ADMIN + STAFF) =====
    public void createBook() {

        if (!(isAdmin() || isStaff())) {
            System.out.println("Permission denied");
            pause();
            return;
        }

        System.out.print("ID: ");
        int id = sc.nextInt(); sc.nextLine();

        System.out.print("Title: ");
        String title = sc.nextLine();

        System.out.print("Author: ");
        String author = sc.nextLine();

        System.out.print("Quantity: ");
        int qty = sc.nextInt();

        Book b = new Book(id, title, author, qty);
        books.add(b);

        history.push(new Action("CREATE_BOOK", null, b));

        saveBooks();
        System.out.println("Book added");
        pause();
    }

    // ===== CREATE ORDER (ALL USERS) =====
    public void createOrder() {

        System.out.print("Order ID: ");
        int id = sc.nextInt(); sc.nextLine();

        String cus = currentUser.username; 

        System.out.print("Book ID: ");
        int bookId = sc.nextInt();

        Book found = null;
        for (Book b : books)
            if (b.id == bookId) found = b;

        if (found == null || found.quantity <= 0) {
            System.out.println("Invalid book");
            pause();
            return;
        }

        Order o = new Order(id, cus, found, "PENDING");

        orders.add(o);
        queue.add(o);

        history.push(new Action("CREATE_ORDER", o, null));

        saveOrders();
        System.out.println("Order created");
        pause();
    }

    // ===== PROCESS ORDER (ADMIN + STAFF) =====
    public void processOrder() {

        if (!(isAdmin() || isStaff())) {
            System.out.println("Permission denied");
            pause();
            return;
        }

        if (queue.isEmpty()) {
            System.out.println("No orders");
            pause();
            return;
        }

        Order o = queue.removeFirst();

        o.status = "DONE";
        o.book.quantity--;

        history.push(new Action("PROCESS_ORDER", o, null));

        saveOrders();
        saveBooks();

        System.out.println("Processed:");
        o.display();
        pause();
    }

    // ===== UNDO (ADMIN + STAFF) =====
    public void undo() {

        if (!(isAdmin() || isStaff())) {
            System.out.println("Permission denied");
            pause();
            return;
        }

        if (history.isEmpty()) {
            System.out.println("Nothing to undo");
            pause();
            return;
        }

        Action a = history.pop();

        if (a.type.equals("PROCESS_ORDER")) {
            Order o = a.order;
            o.status = "PENDING";
            o.book.quantity++;
            queue.addFirst(o);
        }
        else if (a.type.equals("CREATE_ORDER")) {
            orders.remove(a.order);
            queue.remove(a.order);
        }
        else if (a.type.equals("CREATE_BOOK")) {
            books.remove(a.book);
        }

        saveOrders();
        saveBooks();

        System.out.println("Undo successful");
        pause();
    }

    // ===== SEARCH ORDER (🔥 VIEWER ONLY OWN) =====
    public void searchOrder() {

        System.out.print("Enter Order ID: ");
        int id = sc.nextInt();

        ArrayList<Order> list = new ArrayList<>(orders);
        list.sort((a, b) -> a.id - b.id);

        Order found = SearchOrder.binarySearch(list, id);

        if (found == null) {
            System.out.println("Not found");
        } 
        else {

            // 🔐 VIEWER chỉ xem order của mình
            if (!isAdmin() && !isStaff()) {

                if (!found.customer.equals(currentUser.username)) {
                    System.out.println("Access denied (not your order)");
                    pause();
                    return;
                }
            }

            found.display();
        }

        pause();
    }

    // ===== PAUSE =====
    public void pause() {
        System.out.println("\nPress Enter...");
        sc.nextLine();
    }
}