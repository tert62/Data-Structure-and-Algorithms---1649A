public class Order {

    int id;
    String customer;
    Book book;
    String status;

    public Order(int id, String customer, Book book, String status) {
        this.id = id;
        this.customer = customer;
        this.book = book;
        this.status = status;
    }

    public void display() {
        System.out.println(id + " | " + customer + " | " + book.title + " | " + status);
    }

    public String toCSV() {
        return id + "," + customer + "," + book.id + "," + status;
    }
}