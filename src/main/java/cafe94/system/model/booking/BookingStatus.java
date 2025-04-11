package cafe94.system.model.booking;

/**
 * Represents the status of a booking in the Cafe94 system.
 * <p>
 * Each status includes a display-friendly name for use in the UI.
 * </p>
 *
 * <ul>
 *   <li>{@link #PENDING} – Booking has been submitted but not yet approved</li>
 *   <li>{@link #APPROVED} – Booking has been confirmed by a staff member</li>
 *   <li>{@link #CANCELLED} – Booking was cancelled by the customer or staff</li>
 * </ul>
 */
public enum BookingStatus {
    PENDING("Pending Approval"),
    APPROVED("Approved"),
    CANCELLED("Cancelled");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly string representation of this status.
     *
     * @return the display name of the booking status
     */
    @Override
    public String toString() {
        return displayName;
    }
}
