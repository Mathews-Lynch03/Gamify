package server;

import java.io.*;
import java.net.*;

public class GamifyServer
{
    protected int maxConnections;
    protected int listenPort;

    public GamifyServer(int aListenPort, int maxConnections)
    {
        listenPort = aListenPort;
        this.maxConnections = maxConnections;
    }

    public static void main(String[] args)
    {
        GamifyServer server = new GamifyServer(7777, 5);
        server.setUpHandlers();
        server.acceptConnections();
    }

    public void setUpHandlers()
    {
        for (int i = 0; i < maxConnections; i++)
        {
            GamifyHandler currentHandler = new GamifyHandler();
            Thread t = new Thread(currentHandler);
            t.start();
        }
    }

    public void acceptConnections()
    {
        try
        {
            ServerSocket server = new ServerSocket(listenPort, 5);
            Socket incomingConnection = null;

            while (true)
            {
                incomingConnection = server.accept();
                handleConnection(incomingConnection);
            }
        } catch (BindException e)
        {
            System.out.println("Unable to bind to port " + listenPort);
        } catch (IOException e)
        {
            System.out.println("Unable to instantiate a ServerSocket on port: " + listenPort);
        }
    }

    protected void handleConnection(Socket connectionToHandle)
    {
        GamifyHandler.processRequest(connectionToHandle);
    }
}
