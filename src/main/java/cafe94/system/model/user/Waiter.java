package cafe94.system.model.user;

public class Waiter extends Staff {

    public Waiter(int id, String firstName, String lastName, String password,
                  double hoursToWork, double totalHoursWorked) {
        super(id, firstName, lastName, StaffType.WAITER, password, hoursToWork, totalHoursWorked);
    }

    public Waiter(String firstName, String lastName, String password) {
        super(firstName, lastName, StaffType.WAITER, password);
    }

    @Override
    public String toString() {
        return "Waiter: " + getFullName();
    }
}
