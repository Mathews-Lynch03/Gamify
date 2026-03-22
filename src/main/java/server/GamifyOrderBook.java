package server;

import java.io.*;
import java.util.*;

public class GamifyOrderBook
{
    protected ArrayList<GamifyOrder> orders;
    protected HashMap<String, PrintWriter> connectedUsers;

    public GamifyOrderBook()
    {
        orders = new ArrayList<>();
        connectedUsers = new HashMap<>();
    }

    public synchronized void registerUser(String userName, PrintWriter output)
    {
        connectedUsers.put(userName, output);
    }

    public synchronized void removeUser(String userName)
    {
        connectedUsers.remove(userName);
    }

    public synchronized String placeOrder(GamifyOrder newOrder)
    {
        GamifyOrder match = findMatch(newOrder);

        if (match != null)
        {
            orders.remove(match);
            double execPrice = match.getPrice();
            String execPriceStr = String.format("%.2f", execPrice);

            PrintWriter otherOutput = connectedUsers.get(match.getUserName());
            if (otherOutput != null)
            {
                otherOutput.println("MATCH:" + match.getSide() + "," + match.getTitle() + "," + execPriceStr + "," + newOrder.getUserName());
                otherOutput.flush();
            }

            return "MATCH:" + newOrder.getSide() + "," + newOrder.getTitle() + "," + execPriceStr + "," + match.getUserName();
        }

        orders.add(newOrder);
        return null;
    }

    private GamifyOrder findMatch(GamifyOrder newOrder)
    {
        GamifyOrder bestMatch = null;

        for (int i = 0; i < orders.size(); i++)
        {
            GamifyOrder existing = orders.get(i);

            if (!existing.getTitle().equalsIgnoreCase(newOrder.getTitle()))
            {
                continue;
            }

            if (newOrder.getSide().equals("B") && existing.getSide().equals("S"))
            {
                if (existing.getPrice() <= newOrder.getPrice())
                {
                    if (bestMatch == null || existing.getPrice() < bestMatch.getPrice())
                    {
                        bestMatch = existing;
                    }
                }
            }
            else if (newOrder.getSide().equals("S") && existing.getSide().equals("B"))
            {
                if (existing.getPrice() >= newOrder.getPrice())
                {
                    if (bestMatch == null || existing.getPrice() > bestMatch.getPrice())
                    {
                        bestMatch = existing;
                    }
                }
            }
        }

        return bestMatch;
    }

    public synchronized String cancelOrder(String side, String title, double price, String userName)
    {
        for (int i = 0; i < orders.size(); i++)
        {
            GamifyOrder o = orders.get(i);
            if (o.getSide().equals(side) && o.getTitle().equalsIgnoreCase(title) && o.getPrice() == price && o.getUserName().equals(userName))
            {
                orders.remove(i);
                return "CANCELLED";
            }
        }
        return "NOT_FOUND";
    }

    public synchronized ArrayList<String> getOrderBookDisplay()
    {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(String.valueOf(orders.size()));
        for (int i = 0; i < orders.size(); i++)
        {
            GamifyOrder o = orders.get(i);
            lines.add(o.getSide() + "," + o.getTitle() + "," + String.format("%.2f", o.getPrice()));
        }
        return lines;
    }
}
