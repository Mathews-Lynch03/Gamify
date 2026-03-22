package server;

public class GamifyOrder
{
    protected String side;
    protected String title;
    protected double price;
    protected String userName;

    public GamifyOrder(String side, String title, double price, String userName)
    {
        this.side = side;
        this.title = title;
        this.price = price;
        this.userName = userName;
    }

    public String getSide()
    {
        return side;
    }

    public String getTitle()
    {
        return title;
    }

    public double getPrice()
    {
        return price;
    }

    public String getUserName()
    {
        return userName;
    }
}
