package cafe94.system.model.user;

import java.util.List;

/**
 * Represents a delivery driver in the Cafe94 system.
 * Inherits common staff attributes and behavior from {@link Staff}.
 */
public class Driver extends Staff {

    /**
     * Constructs a Driver when loading from file with all fields.
     *
     * @param id                the unique staff ID
     * @param firstName         the driver's first name
     * @param lastName          the driver's last name
     * @param password          the driver's password
     * @param hoursToWork       list of planned working hours
     * @param totalHoursWorked  list of completed working hours
     */
    public Driver(int id, String firstName, String lastName, String password,
                  List<Double> hoursToWork, List<Double> totalHoursWorked) {
        super(id, firstName, lastName, StaffType.DRIVER, password, hoursToWork, totalHoursWorked);
    }

    /**
     * Constructs a new Driver for use in staff creation.
     *
     * @param firstName the driver's first name
     * @param lastName  the driver's last name
     * @param password  the driver's password
     */
    public Driver(String firstName, String lastName, String password) {
        super(firstName, lastName, StaffType.DRIVER, password);
    }

    @Override
    public String toString() {
        return "Driver: " + getFullName();
    }
}
