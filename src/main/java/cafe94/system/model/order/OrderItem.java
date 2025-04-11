package cafe94.system.model.order;

import cafe94.system.model.menu.MenuItem;

/**
 * Represents a single item within an order, combining a menu item and its ordered quantity.
 * <p>
 * Used in all order types (Eat-In, Takeaway, Delivery) to calculate individual subtotals.
 * </p>
 */
public class OrderItem {
    private final MenuItem menuItem;
    private final int quantity;

    /**
     * Constructs an OrderItem with the specified menu item and quantity.
     *
     * @param menuItem the menu item being ordered
     * @param quantity the number of units ordered
     */
    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }
}
