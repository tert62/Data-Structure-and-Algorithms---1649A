public class Action {

    String type; // CREATE_ORDER, PROCESS_ORDER, CREATE_BOOK
    Order order;
    Book book;

    public Action(String type, Order order, Book book) {
        this.type = type;
        this.order = order;
        this.book = book;
    }
}