package cafe94.system.model.booking;

/**
 * Enum representing the status of a booking.
 */
public enum BookingStatus {
    PENDING("Pending Approval"),
    APPROVED("Approved"),
    CANCELLED("Cancelled");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
