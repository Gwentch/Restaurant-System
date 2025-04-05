package cafe94.system.model.user;

public class Chef extends Staff {

    public Chef(int id, String firstName, String lastName, String password,
                double hoursToWork, double totalHoursWorked) {
        super(id, firstName, lastName, StaffType.CHEF, password, hoursToWork, totalHoursWorked);
    }

    public Chef(String firstName, String lastName, String password) {
        super(firstName, lastName, StaffType.CHEF, password);
    }

    @Override
    public String toString() {
        return "Chef: " + getFullName();
    }
}
