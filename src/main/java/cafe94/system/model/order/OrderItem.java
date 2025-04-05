package cafe94.system.model.order;

import cafe94.system.model.menu.MenuItem;

public class OrderItem {
    public MenuItem menuItem;
    public int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem; }

    public int getQuantity() {
        return quantity; }

    public String getItemName() {
        return menuItem.getName();
    }

    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return menuItem.getName() + " x" + quantity + " (£" + String.format("%.2f", getSubtotal()) + ")";
    }

}