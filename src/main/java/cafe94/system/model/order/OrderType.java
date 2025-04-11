package cafe94.system.model.order;


/**
 * Enum representing the type of orders in Cafe94.
 * Used for UI, reporting, or user-friendly display purposes.
 */
public enum OrderType {
    EAT_IN("Eat-In"),
    TAKEAWAY("Takeaway"),
    DELIVERY("Delivery");

    private final String displayName;

    OrderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the next appropriate order status based on the order type.
     * This can be used when marking an order as completed by the chef.
     *
     * @return the next status for this order type
     */
    public OrderStatus getNextStatus() {
        return switch (this) {
            case EAT_IN -> OrderStatus.READY_TO_SERVE;
            case TAKEAWAY -> OrderStatus.READY_TO_PICKUP;
            case DELIVERY -> OrderStatus.READY_TO_DELIVER;
        };
    }
}
