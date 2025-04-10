package cafe94.system.model.order;

/**
 * Represents the current status of an order in the Cafe94 system.
 */
public enum OrderStatus {
    PENDING_PREP("Waiting for Preparation"),
    READY_TO_SERVE("Ready to Serve"),
    READY_TO_PICKUP("Ready for Pickup"),
    READY_TO_DELIVER("Ready for Delivery"),
    PENDING_APPROVAL("Waiting for Approval"),
    DELIVERED("Delivered");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}

