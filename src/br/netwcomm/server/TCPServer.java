package br.netwcomm.server;

import br.netwcomm.protocols.TCPHandShakeProtocol;
import br.netwcomm.support.InternalMessageHandler;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Thiago
 */
public class TCPServer extends Server
{
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private DataOutputStream os = null;
    private BufferedReader in = null;
    
    public TCPServer(int listeningPort, boolean runStatus, InternalMessageHandler msgHand, int timeOut) throws IOException
    {
        super(listeningPort, runStatus, msgHand, timeOut);
        
        msg.printMessage("Starting Server...");
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(timeout);
    }
    
    @Override
    public void startComponent(boolean start)
    {
        this.isRunning = start;
    }

    @Override
    public void run()
    {
        msg.printMessage("Waiting for incoming connection request...");
        String fromClient;
        TCPHandShakeProtocol protocol = new TCPHandShakeProtocol();
        
        try
        {
            clientSocket = serverSocket.accept();
            msg.printMessage("Connected to " + clientSocket.getRemoteSocketAddress().toString());
            os = new DataOutputStream(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //===========================SERVER LOGIC BEGINS HERE================================
            while(isRunning)
            {
                fromClient = in.readLine();
                if(fromClient != null)
                {
                    if(fromClient.equals("End"))
                    {
                        this.isRunning = false;
                    }
                    else
                    {
                        msg.printMessage("Command received from Client: " + fromClient);
                        os.write(protocol.processIncomingRequest(fromClient));
                        msg.printMessage("Response sent to Client.");
                    }
                }
            }
            //============================SERVER LOGIC ENDS HERE=================================
        
            msg.printMessage("Trying to close connections...");
        
            in.close();
            os.close();
            clientSocket.close();
            serverSocket.close();
        }
        catch(IOException ioex)
        {
            String cause = ioex.getMessage();
            msg.printMessage(cause);
                
            if (cause.equals("Accept timed out"))
            {
                try
                {
                    serverSocket.close();
                } catch (IOException ex)
                {
                        msg.printMessage(ex.getMessage());
                }
            }
        }
        catch(Exception e)
        {
            msg.printMessage("Exception: " + e.getMessage());
        }
        finally
        {
            msg.printMessage("Server Stopped.");
        }
    }
}