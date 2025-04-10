package cafe94.system.model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Staff model for Cafe94
 */
public class Staff extends User {

    private static int nextStaffIdCounter = 1;

    public enum StaffType {
        MANAGER, WAITER, DRIVER, CHEF
    }

    private StaffType type;
    private List<Double> hoursToWork;
    private List<Double> totalHoursWorked;

    // Constructor for manager creating new staff
    public Staff(String firstName, String lastName, StaffType type, String password) {
        super(nextStaffIdCounter++, firstName, lastName, password);
        this.type = type;
        this.hoursToWork = new ArrayList<>();
        this.totalHoursWorked = new ArrayList<>();
    }

    // Constructor when loading from file
    public Staff(int id, String firstName, String lastName, StaffType type, String password) {
        super(id, firstName, lastName, password);
        this.type = type;
        this.hoursToWork = new ArrayList<>();
        this.totalHoursWorked = new ArrayList<>();
        updateStaffIdCounter(id);
    }

    // Overloaded constructor to load hours
    public Staff(int id, String firstName, String lastName, StaffType type, String password,
                 List<Double> hoursToWork, List<Double> totalHoursWorked) {
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

    public List<Double> getHoursToWork() {
        return hoursToWork;
    }

    public void setHoursToWork(List<Double> hours) {
        this.hoursToWork = new ArrayList<>(hours);
    }

    public List<Double> getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void setTotalHoursWorked(List<Double> hours) {
        this.totalHoursWorked = hours;
    }

    /**
     * Adds a single day's planned working hours to the list.
     * Only use this if you're incrementally updating the hours list.
     */
    public void addHourToWork(double hours) {
        this.hoursToWork.add(hours);
    }

    public void addWorkedHour(double hours) {
        this.totalHoursWorked.add(hours);
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
        return String.format("%d;%s;%s;%s;%s;%s;%s",
                getId(),
                getFirstName(),
                getLastName(),
                getPassword(),
                type.name().toLowerCase(),
                listToString(hoursToWork),
                listToString(totalHoursWorked));
    }


    private String listToString(List<Double> list) {
        return list.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public static List<Double> parseHoursList(String str) {
        List<Double> hours = new ArrayList<>();
        if (str == null || str.isBlank()) return hours;

        for (String part : str.split(",")) {
            try {
                hours.add(Double.parseDouble(part.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return hours;
    }


}
