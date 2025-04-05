package cafe94.system.model.user;

public class Driver extends Staff {

    public Driver(int id, String firstName, String lastName, String password,
                  double hoursToWork, double totalHoursWorked) {
        super(id, firstName, lastName, StaffType.DRIVER, password, hoursToWork, totalHoursWorked);
    }

    public Driver(String firstName, String lastName, String password) {
        super(firstName, lastName, StaffType.DRIVER, password);
    }

    @Override
    public String toString() {
        return "Driver: " + getFullName();
    }
}
