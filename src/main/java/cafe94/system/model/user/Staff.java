package cafe94.system.model.user;

/**
 * Staff model for Cafe94
 */
public class Staff extends User {

    private static int nextStaffIdCounter = 1;

    public enum StaffType {
        MANAGER, WAITER, DRIVER, CHEF
    }

    private static final double DEFAULT_HOURS_TO_WORK = 40.0;
    private static final double DEFAULT_TOTAL_HOURS_WORKED = 0.0;

    private StaffType type;
    private double hoursToWork;       // expected hours per week
    private double totalHoursWorked;  // accumulated hours worked

    // Constructor for manager creating new staff
    public Staff(String firstName, String lastName, StaffType type, String password) {
        super(nextStaffIdCounter++, firstName, lastName, password);
        this.type = type;
        this.hoursToWork = DEFAULT_HOURS_TO_WORK;
        this.totalHoursWorked = DEFAULT_TOTAL_HOURS_WORKED;
    }

    // Constructor when loading from file
    public Staff(int id, String firstName, String lastName, StaffType type, String password) {
        super(id, firstName, lastName, password);
        this.type = type;
        this.hoursToWork = DEFAULT_HOURS_TO_WORK;
        this.totalHoursWorked = DEFAULT_TOTAL_HOURS_WORKED;
        updateStaffIdCounter(id);
    }

    // Overloaded constructor to load hours
    public Staff(int id, String firstName, String lastName, StaffType type, String password,
                 double hoursToWork, double totalHoursWorked) {
        super(id, firstName, lastName, password);
        this.type = type;
        this.hoursToWork = hoursToWork;
        this.totalHoursWorked = totalHoursWorked;
        updateStaffIdCounter(id);
    }

    // Static counter update
    public static void updateStaffIdCounter(int id) {
        if (id >= nextStaffIdCounter) {
            nextStaffIdCounter = id + 1;
        }
    }

    // Getters & Setters
    public StaffType getType() {
        return type;
    }

    public void setType(StaffType type) {
        this.type = type;
    }

    public double getHoursToWork() {
        return hoursToWork;
    }

    public void setHoursToWork(double hoursToWork) {
        this.hoursToWork = hoursToWork;
    }

    public double getTotalHoursWorked() {
        return totalHoursWorked;
    }


    public void setTotalHoursWorked(double hours) {
        this.totalHoursWorked = hours;
    }


    @Override
    public String getRole() {
        return type.name();
    }

    @Override
    public String toString() {
        return "[ID: " + id + "] " + getFullName() + " (" + type.name() + ")";
    }

    @Override
    public String toFileString() {
        return String.format("%d;%s;%s;%s;%s;%s", getId(), getFirstName(), getLastName(), getPassword(), type, getHoursToWork());
    }

}
