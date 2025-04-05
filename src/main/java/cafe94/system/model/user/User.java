package cafe94.system.model.user;

public abstract class User {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String password;

    public User(int id, String firstName, String lastName, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    // --- Getters & Setters ---
    public int getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPassword() {
        return password; }

    public void setPassword(String password) {
        this.password = password; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public abstract String getRole(); // Customer or Staff

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public String toString() {
        return "[ID: " + id + "] " + getFullName();
    }
}