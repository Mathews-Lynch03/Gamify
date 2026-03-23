package client;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GamifyClient {
    public static void main(String[] args)
    {
        try
        {
            // Establish channel of communication
            Socket dataSocket = new Socket("localhost", GamifyServiceDetails.LISTENING_PORT);

            // Build output stream for sending to server
            OutputStream out = dataSocket.getOutputStream();
            PrintWriter output = new PrintWriter(new OutputStreamWriter(out));

            InputStream in = dataSocket.getInputStream();
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(in));

            GamifyListener listener = new GamifyListener(serverInput);
            Thread listenerThread = new Thread(listener, "GamifyListener");
            listenerThread.setDaemon(true);
            listenerThread.start();

            //Set up  scanner for user input
            Scanner keyboard = new Scanner(System.in);
            String message = "";

            System.out.println("Welcome to the Gamify Game Market (Stream-based Client).");
            System.out.println("Please register your username before placing orders.");

            while (!message.equals(GamifyServiceDetails.END_SESSION))
            {
                displayMenu();
                int choice = getNumber(keyboard);
                String response = "";

                if (choice >= 0 && choice < 6)
                {
                    switch (choice)
                    {
                        case 0:
                            message = GamifyServiceDetails.END_SESSION;

                            // Send message
                            output.println(message);
                            output.flush();

                            // Give listener thread time to print the ENDED response
                            Thread.sleep(300);
                            listener.stop();
                            break;

                        case 1:
                            message = generateRegisterUser(keyboard);

                            // Send message
                            output.println(message);
                            output.flush();
                            break;

                        case 2:
                            message = generateOrder(keyboard, GamifyServiceDetails.BUY_SIDE);

                            // Send message
                            output.println(message);
                            output.flush();
                            break;

                        case 3:
                            message = generateOrder(keyboard, GamifyServiceDetails.SELL_SIDE);

                            // Send message
                            output.println(message);
                            output.flush();
                            break;

                        case 4:
                            message = generateCancel(keyboard);

                            // Send message
                            output.println(message);
                            output.flush();
                            break;

                        case 5:
                            message = GamifyServiceDetails.VIEW;

                            // Send message
                            output.println(message);
                            output.flush();
                            break;
                    }

                    // Allow the listener thread to print the server response
                    // before the menu is displayed again
                    Thread.sleep(400);
                }
                else
                {
                    System.out.println("Please select an option from the menu.");
                }
            }

            System.out.println("Thank you for using the (Stream-based) Gamify system.");
            dataSocket.close();
        }
        catch (Exception e)
        {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void displayMenu()
    {
        System.out.println();
        System.out.println("0) Exit");
        System.out.println("1) Register username");
        System.out.println("2) Place a buy order");
        System.out.println("3) Place a sell order");
        System.out.println("4) Cancel an order");
        System.out.println("5) View the order book");
    }

    // A method to retrieve a number from the user at all times.
    // This method deals with repeatedly requesting a number from the user
    // until they enter numeric data instead of text.
    public static int getNumber(Scanner keyboard)
    {
        boolean numberEntered = false;
        int number = 0;
        while (!numberEntered)
        {
            try
            {
                number = keyboard.nextInt();
                numberEntered = true;
            }
            catch (InputMismatchException e)
            {
                System.out.println("Please enter a number.");
                keyboard.nextLine();
            }
        }
        keyboard.nextLine();
        return number;
    }

    // A method to retrieve a price from the user.
    // Repeatedly requests input until the user enters a valid positive number.
    public static double getPrice(Scanner keyboard)
    {
        boolean priceEntered = false;
        double price = 0;
        while (!priceEntered)
        {
            try
            {
                price = keyboard.nextDouble();
                if (price < 0)
                {
                    System.out.println("Price cannot be negative. Please try again.");
                }
                else
                {
                    priceEntered = true;
                }
            }
            catch (InputMismatchException e)
            {
                System.out.println("Please enter a valid price (e.g. 25.00).");
                keyboard.nextLine();
            }
        }
        keyboard.nextLine();
        return price;
    }


    // Methods to provide command functionality on the client side

    public static String generateRegisterUser(Scanner keyboard)
    {
        // Set up command text
        StringBuffer message = new StringBuffer(GamifyServiceDetails.REGISTER_USER);
        message.append(GamifyServiceDetails.COMMAND_SEPARATOR);
        // Get the username to register
        System.out.println("Please enter your username: ");
        String userName = keyboard.nextLine();
        // Add username to end of command string
        message.append(userName);

        // Return final message
        return message.toString();
    }

    public static String generateOrder(Scanner keyboard, String side)
    {
        // Set up command text
        StringBuffer message = new StringBuffer(GamifyServiceDetails.ORDER);
        message.append(GamifyServiceDetails.COMMAND_SEPARATOR);
        message.append(side);
        message.append(GamifyServiceDetails.FIELD_SEPARATOR);
        // Get the game title
        System.out.println("Please enter the game title: ");
        String title = keyboard.nextLine();
        message.append(title);
        message.append(GamifyServiceDetails.FIELD_SEPARATOR);
        // Get the price
        System.out.println("Please enter the price: ");
        double price = getPrice(keyboard);
        message.append(String.format("%.2f", price));

        // Return final message
        return message.toString();
    }

    public static String generateCancel(Scanner keyboard)
    {
        // Set up command text
        StringBuffer message = new StringBuffer(GamifyServiceDetails.CANCEL);
        message.append(GamifyServiceDetails.COMMAND_SEPARATOR);
        // Get the side to cancel
        System.out.println("Is this a buy or sell order? (Enter B or S): ");
        String side = keyboard.nextLine().trim().toUpperCase();
        while (!side.equals(GamifyServiceDetails.BUY_SIDE) && !side.equals(GamifyServiceDetails.SELL_SIDE))
        {
            System.out.println("Please enter B for buy or S for sell: ");
            side = keyboard.nextLine().trim().toUpperCase();
        }
        message.append(side);
        message.append(GamifyServiceDetails.FIELD_SEPARATOR);
        // Get the game title
        System.out.println("Please enter the game title: ");
        String title = keyboard.nextLine();
        message.append(title);
        message.append(GamifyServiceDetails.FIELD_SEPARATOR);
        // Get the price
        System.out.println("Please enter the price of the order to cancel: ");
        double price = getPrice(keyboard);
        message.append(String.format("%.2f", price));

        // Return final message
        return message.toString();
    }
}
