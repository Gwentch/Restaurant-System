package cafe94.system.model.user;

import java.util.List;

public class Manager extends Staff {

    public Manager(int id, String firstName, String lastName, String password,
                   List<Double> hoursToWork, List<Double> totalHoursWorked) {
        super(id, firstName, lastName, StaffType.MANAGER, password, hoursToWork, totalHoursWorked);
    }

    public Manager(String firstName, String lastName, String password) {
        super(firstName, lastName, StaffType.MANAGER, password);
    }

    @Override
    public String toString() {
        return "Manager: " + getFullName();
    }
}
