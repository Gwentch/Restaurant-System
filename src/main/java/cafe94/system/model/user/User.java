package cafe94.system.model.user;

/**
 * Abstract base class representing a general user in the Cafe94 system.
 * This class is extended by {@link Customer} and {@link Staff}.
 */
public abstract class User {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String password;

    /**
     * Constructs a User with the specified details.
     *
     * @param id        the unique ID of the user
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param password  the user's password
     */
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

    /**
     * Returns the user's role, such as "Customer" or a staff type.
     */
    public abstract String getRole(); // Customer or Staff

    /**
     * Returns the full name of the user.
     */
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    /**
     * Returns a string representing the user's data for file saving.
     */
    public abstract String toFileString();

    @Override
    public String toString() {
        return "[ID: " + id + "] " + getFullName();
    }
}