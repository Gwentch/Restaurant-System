package cafe94.system.model.user;

import java.util.List;

/**
 * Represents a manager in the Cafe94 system.
 * Inherits common staff attributes and functionality from {@link Staff}.
 */
public class Manager extends Staff {

    /**
     * Constructs a Manager when loading from file with full attributes.
     *
     * @param id                the unique staff ID
     * @param firstName         the manager's first name
     * @param lastName          the manager's last name
     * @param password          the manager's password
     * @param hoursToWork       list of hours scheduled to work
     * @param totalHoursWorked  list of actual hours worked
     */
    public Manager(int id, String firstName, String lastName, String password,
                   List<Double> hoursToWork, List<Double> totalHoursWorked) {
        super(id, firstName, lastName, StaffType.MANAGER, password, hoursToWork, totalHoursWorked);
    }

    /**
     * Constructs a new Manager instance for creation in-app.
     *
     * @param firstName the manager's first name
     * @param lastName  the manager's last name
     * @param password  the manager's password
     */
    public Manager(String firstName, String lastName, String password) {
        super(firstName, lastName, StaffType.MANAGER, password);
    }

    @Override
    public String toString() {
        return "Manager: " + getFullName();
    }
}
