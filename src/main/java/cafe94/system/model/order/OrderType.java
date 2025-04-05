package cafe94.system.model.order;

/**
 * Represents the type of order in Cafe94.
 */
public enum OrderType {
    EAT_IN,
    TAKEAWAY,
    DELIVERY;

    /**
     * Convert order type to a user-friendly string.
     */
    public static String getDisplayType(OrderType type) {
        return switch (type) {
            case EAT_IN -> "Eat-In";
            case TAKEAWAY -> "Takeaway";
            case DELIVERY -> "Delivery";
        };
    }

    /**
     * Returns the next appropriate order status based on order type.
     */
    public OrderStatus getNextStatus() {
        return switch (this) {
            case EAT_IN -> OrderStatus.READY_TO_SERVE;
            case TAKEAWAY -> OrderStatus.READY_TO_PICKUP;
            case DELIVERY -> OrderStatus.READY_TO_DELIVER;
        };
    }
}
