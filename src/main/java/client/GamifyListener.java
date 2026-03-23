package client;

import java.io.BufferedReader;
import java.io.IOException;

public class GamifyListener implements Runnable{
    private BufferedReader input;
    private volatile boolean running = true;

    public GamifyListener(BufferedReader input)
    {
        this.input = input;
    }

    public void run()
    {
        try
        {
            String response = "";
            while (running && (response = input.readLine()) != null)
            {
                handleResponse(response);
            }
        }
        catch (IOException e)
        {
            if (running)
            {
                System.out.println("Connection to server lost: " + e.getMessage());
            }
        }
    }

    public void handleResponse(String response) throws IOException
    {
        // MATCH notification - can arrive at any time from another user's trade
        if (response.startsWith(GamifyServiceDetails.MATCH + GamifyServiceDetails.COMMAND_SEPARATOR))
        {
            displayMatch(response);
        }

        // Simple one-line responses
        else if (response.equals(GamifyServiceDetails.CONNECTED))
        {
            System.out.println("Successfully registered with the server.");
        }
        else if (response.equals(GamifyServiceDetails.CANCELLED))
        {
            System.out.println("Your order has been cancelled successfully.");
        }
        else if (response.equals(GamifyServiceDetails.NOT_FOUND))
        {
            System.out.println("That order was not found in the order book.");
        }
        else if (response.equals(GamifyServiceDetails.SESSION_TERMINATED))
        {
            System.out.println("Session ended by server.");
            running = false;
        }

        // Order book - server sends the count first, then one line per order
        else
        {
            displayOrderBook(response);
        }
    }

    // Methods to display server responses

    public void displayMatch(String response)
    {
        // Format: MATCH:(B|S),<title>,<price>,<counterparty>
        String body  = response.substring(GamifyServiceDetails.MATCH.length() + 1);
        String[] parts = body.split(GamifyServiceDetails.FIELD_SEPARATOR, 4);

        if (parts.length == 4)
        {
            String side         = parts[0].equals(GamifyServiceDetails.BUY_SIDE) ? "BUY" : "SELL";
            String title        = parts[1];
            String price        = parts[2];
            String counterparty = parts[3];

            System.out.println("*** ORDER MATCHED ***");
            System.out.println("Side: "         + side);
            System.out.println("Title: "        + title);
            System.out.println("Price: $"       + price);
            System.out.println("Counterparty: " + counterparty);
            System.out.println("*********************");
        }
        else
        {
            System.out.println("Match received: " + response);
        }
    }

    public void displayOrderBook(String firstLine) throws IOException
    {
        // The server sends the order count as the first line,
        // followed by one CSV line per order: e.g. "B,Minecraft,25.00"
        try
        {
            int count = Integer.parseInt(firstLine.trim());
            System.out.println("Order Book (" + count + " order(s))");

            if (count == 0)
            {
                System.out.println("The order book is currently empty.");
            }
            else
            {
                System.out.println("Side | Title                  | Price");
                System.out.println("---------------------------------------");
                for (int i = 0; i < count; i++)
                {
                    String orderLine = input.readLine();
                    if (orderLine == null) break;

                    String[] parts = orderLine.split(GamifyServiceDetails.FIELD_SEPARATOR, 3);
                    if (parts.length == 3)
                    {
                        String side  = parts[0].equals(GamifyServiceDetails.BUY_SIDE) ? "BUY " : "SELL";
                        String title = parts[1];
                        String price = parts[2];
                        System.out.printf("%-4s | %-22s | $%s%n", side, title, price);
                    }
                }
            }
            System.out.println("-----------------------------------");
        }
        catch (NumberFormatException e)
        {
            System.out.println("Server: " + firstLine);
        }
    }

    public void stop()
    {
        running = false;
    }
}
