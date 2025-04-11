package cafe94.system.model.user;

import java.util.List;

/**
 * Represents a Waiter in the Cafe94 system.
 * <p>
 * A Waiter is a type of Staff responsible for assisting customers,
 * placing eat-in orders, and approving delivery bookings.
 */
public class Waiter extends Staff {

    /**
     * Constructs a Waiter instance when loading from file.
     *
     * @param id                the waiter's ID
     * @param firstName         first name
     * @param lastName          last name
     * @param password          login password
     * @param hoursToWork       list of planned work hours
     * @param totalHoursWorked  list of actual worked hours
     */
    public Waiter(int id, String firstName, String lastName, String password,
                  List<Double> hoursToWork, List<Double> totalHoursWorked) {
        super(id, firstName, lastName, StaffType.WAITER, password, hoursToWork, totalHoursWorked);
    }

    /**
     * Constructs a new Waiter instance during staff registration.
     *
     * @param firstName first name
     * @param lastName  last name
     * @param password  login password
     */
    public Waiter(String firstName, String lastName, String password) {
        super(firstName, lastName, StaffType.WAITER, password);
    }

    @Override
    public String toString() {
        return "Waiter: " + getFullName();
    }
}
