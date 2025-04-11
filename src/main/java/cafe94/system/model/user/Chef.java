package cafe94.system.model.user;

import java.util.List;

/**
 * Represents a Chef in the Cafe94 system.
 * <p>
 * Chefs are responsible for preparing orders marked as {@code PENDING_PREP}
 * and managing daily specials.
 * </p>
 */
public class Chef extends Staff {

    /**
     * Constructs a Chef with full detail (used when loading from file).
     *
     * @param id                the unique staff ID
     * @param firstName         the first name
     * @param lastName          the last name
     * @param password          the password
     * @param hoursToWork       weekly hours assigned
     * @param totalHoursWorked  total hours worked
     */
    public Chef(int id, String firstName, String lastName, String password,
                List<Double> hoursToWork, List<Double> totalHoursWorked) {
        super(id, firstName, lastName, StaffType.CHEF, password, hoursToWork, totalHoursWorked);
    }

    /**
     * Constructs a new Chef when creating from the UI (ID is auto-generated).
     *
     * @param firstName  the first name
     * @param lastName   the last name
     * @param password   the password
     */
    public Chef(String firstName, String lastName, String password) {
        super(firstName, lastName, StaffType.CHEF, password);
    }

    @Override
    public String toString() {
        return "Chef: " + getFullName();
    }
}
