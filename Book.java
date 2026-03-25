public class Book {

    int id;
    String title;
    String author;
    int quantity;

    public Book(int id, String title, String author, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    public void display() {
        System.out.println(id + "," + title + "," + author + "," + quantity);
    }

    public String toCSV() {
        return id + "," + title + "," + author + "," + quantity;
    }
}