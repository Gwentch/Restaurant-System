package cafe94.system.model.menu;

/**
 * Represents a menu item in Cafe94.
 */
public class MenuItem {

    private final String name;
    private final double price;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " (£" + String.format("%.2f", price) + ")";
    }
}
