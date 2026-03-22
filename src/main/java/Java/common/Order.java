package Java.common;

public class Order {
    public enum Side { BUY, SELL }

    private final Side side;
    private final String title;
    private final double price;
    private final String username;

    public Order(Side side, String title, double price, String username) {
        this.side     = side;
        this.title    = title;
        this.price    = price;
        this.username = username;
    }

    public Side   getSide()     { return side; }
    public String getTitle()    { return title; }
    public double getPrice()    { return price; }
    public String getUsername() { return username; }

    /** Returns "B" or "S" shorthand. */
    public String getSideCode() {
        return side == Side.BUY ? "B" : "S";
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s  $%.2f", getSideCode(), title, price);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Order)) return false;
        Order o = (Order) obj;
        return side == o.side
                && title.equalsIgnoreCase(o.title)
                && Double.compare(price, o.price) == 0
                && username.equalsIgnoreCase(o.username);
    }

    @Override
    public int hashCode() {
        int result = side.hashCode();
        result = 31 * result + title.toLowerCase().hashCode();
        result = 31 * result + Double.hashCode(price);
        result = 31 * result + username.toLowerCase().hashCode();
        return result;
    }
}
