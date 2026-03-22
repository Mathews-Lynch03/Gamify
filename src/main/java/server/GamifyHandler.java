package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class GamifyHandler implements Runnable
{
    protected Socket clientLink;
    protected static List pool = new LinkedList();
    protected static GamifyOrderBook orderBook = new GamifyOrderBook();

    public GamifyHandler()
    {
    }

    public static void processRequest(Socket incomingClient)
    {
        synchronized (pool)
        {
            pool.add(pool.size(), incomingClient);
            pool.notifyAll();
        }
    }

    public void run()
    {
        while (true)
        {
            synchronized (pool)
            {
                while (pool.isEmpty())
                {
                    try
                    {
                        pool.wait();
                    } catch (InterruptedException e)
                    {
                        return;
                    }
                }
                clientLink = (Socket) pool.remove(0);
            }
            handleConnection();
        }
    }

    public void handleConnection()
    {
        try
        {
            PrintWriter output = new PrintWriter(clientLink.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(clientLink.getInputStream()));

            String userName = null;
            String message = null;
            boolean running = true;

            while (running && (message = input.readLine()) != null)
            {
                if (message.startsWith("USER:"))
                {
                    userName = message.substring(5);
                    orderBook.registerUser(userName, output);
                    output.println("CONNECTED");
                    output.flush();
                }
                else if (message.startsWith("ORDER:"))
                {
                    String orderData = message.substring(6);
                    String[] parts = orderData.split(",");
                    String side = parts[0];
                    String title = parts[1];
                    double price = Double.parseDouble(parts[2]);

                    GamifyOrder newOrder = new GamifyOrder(side, title, price, userName);
                    String result = orderBook.placeOrder(newOrder);

                    if (result != null)
                    {
                        output.println(result);
                        output.flush();
                    }
                    else
                    {
                        ArrayList<String> display = orderBook.getOrderBookDisplay();
                        for (int i = 0; i < display.size(); i++)
                        {
                            output.println(display.get(i));
                        }
                        output.flush();
                    }
                }
                else if (message.startsWith("CANCEL:"))
                {
                    String cancelData = message.substring(7);
                    String[] parts = cancelData.split(",");
                    String side = parts[0];
                    String title = parts[1];
                    double price = Double.parseDouble(parts[2]);

                    String result = orderBook.cancelOrder(side, title, price, userName);
                    output.println(result);
                    output.flush();
                }
                else if (message.equals("VIEW"))
                {
                    ArrayList<String> display = orderBook.getOrderBookDisplay();
                    for (int i = 0; i < display.size(); i++)
                    {
                        output.println(display.get(i));
                    }
                    output.flush();
                }
                else if (message.equals("END"))
                {
                    output.println("ENDED");
                    output.flush();
                    running = false;
                }
            }

            if (userName != null)
            {
                orderBook.removeUser(userName);
            }
            output.close();
            input.close();
            clientLink.close();

        } catch (IOException e)
        {
            System.out.println("Error handling a client: " + e);
        }
    }
}
