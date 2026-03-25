public class User {

    String username;
    String role; // ADMIN, STAFF, VIEWER

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }
}