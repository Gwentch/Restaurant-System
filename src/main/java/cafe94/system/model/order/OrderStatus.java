package cafe94.system.model.order;

/**
 * Represents the current status of an order in the Cafe94 system.
 */
public enum OrderStatus {
    PENDING_PREP,       // Order has been placed and needs preparation by a chef
    READY_TO_SERVE,     // Eat-in order is ready to be served by a waiter
    READY_TO_PICKUP,    // Takeaway order is ready for customer collection
    READY_TO_DELIVER,   // Delivery order is ready for the driver to deliver
    PENDING_APPROVAL,   // Delivery order request pending waiter approval
    DELIVERED;          // Delivery has been completed


    @Override
    public String toString() {
        return switch (this) {
            case PENDING_APPROVAL -> "Waiting for Approval";
            case PENDING_PREP -> "Waiting for Preparation";
            case READY_TO_SERVE -> "Ready to Serve";
            case READY_TO_PICKUP -> "Ready for Pickup";
            case READY_TO_DELIVER -> "Ready for Delivery";
            case DELIVERED -> "Delivered";
        };
    }
}

