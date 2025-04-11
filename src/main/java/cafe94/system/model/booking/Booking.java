package cafe94.system.model.booking;


/**
 * Represents a booking made by a customer to eat in the restaurant.
 * Includes information about the number of guests, date, time, duration, and booking status.
 */
public class Booking {

    private static int nextBookingID = 1;

    private final int bookingID;
    private final int customerID;
    private int guestNum;
    private String date;
    private String time;
    private int durationHours;
    private BookingStatus status;

    /**
     * Creates a new booking with default status of PENDING.
     *
     * @param customerID       ID of the customer making the booking
     * @param guestNum   number of guests
     * @param date             date of booking in YYYY-MM-DD
     * @param time             time of booking in HH:MM
     * @param durationHours  duration in hours (defaults to 1 but can be more)
     */
    public Booking(int customerID, int guestNum, String date, String time, int durationHours) {
        this.bookingID = nextBookingID++;
        this.customerID = customerID;
        this.guestNum = guestNum;
        this.date = date;
        this.time = time;
        this.durationHours = durationHours;
        this.status = BookingStatus.PENDING;
    }

    /**
     * Constructor used when loading from file.
     */
    public Booking(int bookingID, int customerID, int guestNum, String date, String time, int durationHours, BookingStatus status) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.guestNum = guestNum;
        this.date = date;
        this.time = time;
        this.durationHours = durationHours;
        this.status = status;
        nextBookingID = Math.max(nextBookingID, bookingID + 1);
    }

    public int getBookingID() {
        return bookingID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getGuestNum() {
        return guestNum;
    }

    public void setGuestNum(int guestNum) {
        this.guestNum = guestNum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    /**
     * Returns the next available booking ID.
     * Used for creating a new booking before saving.
     */
    public static int getNextBookingID() {
        return nextBookingID;
    }

    /**
     * Sets the booking ID counter (used when loading from file to avoid duplicates).
     */
    public static void setNextBookingID(int nextId) {
        nextBookingID = nextId;
    }


    /**
     * Returns the booking data as a file string.
     * Format: bookingID;customerID;numberOfGuests;date;time;duration;status
     */
    public String toFileString() {
        return String.format("%d;%d;%d;%s;%s;%d;%s",
                bookingID,
                customerID,
                guestNum,
                date,
                time,
                durationHours,
                status.name()
        );
    }

    /**
     * Parses a booking from a line in the text file.
     */
    public static Booking parseFromFile(String line) {
        try {
            String[] parts = line.split(";", -1);
            if (parts.length != 7) return null;

            int bookingID = Integer.parseInt(parts[0]);
            int customerID = Integer.parseInt(parts[1]);
            int guestNum = Integer.parseInt(parts[2]);
            String date = parts[3];
            String time = parts[4];
            int duration = Integer.parseInt(parts[5]);
            BookingStatus status = BookingStatus.valueOf(parts[6]);

            return new Booking(bookingID, customerID, guestNum, date, time, duration, status);
        } catch (Exception e) {
            System.err.println("Failed to parse booking: " + line);
            e.printStackTrace();
            return null;
        }
    }
}
