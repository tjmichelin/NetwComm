package br.netwcomm.server;

import br.netwcomm.protocols.UDPHandShakeProtocol;
import br.netwcomm.support.InternalMessageHandler;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author Thiago
 */
public class UDPServer extends Server
{
    private DatagramSocket serverSocket = null;
    
    public UDPServer(int listeningPort, boolean runStatus, InternalMessageHandler msgHand, int timeOut) throws SocketException
    {
        super(listeningPort, runStatus, msgHand, timeOut);
        serverSocket = new DatagramSocket(listeningPort);
    }
    
    @Override
    public void startComponent(boolean start)
    {
        this.isRunning = start;
    }

    @Override
    public void run()
    {
        byte [] receiveData = new byte[1024];
        byte [] sendData = null;
        DatagramPacket receivePacket = null;
        DatagramPacket sendPacket = null;
        
        try
        {
            UDPHandShakeProtocol udphs = new UDPHandShakeProtocol();
            
            while(isRunning)
            {
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String command = new String(receivePacket.getData()).trim();
                msg.printMessage("Command received from Client: " + command);
                
                if(command.equals("End"))
                {
                    this.isRunning = false;
                }
                else
                {
                    InetAddress incomingAddr = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();

                    sendData = udphs.processIncomingRequest(command);
                    sendPacket = new DatagramPacket(sendData, sendData.length, incomingAddr, clientPort);
                    serverSocket.send(sendPacket);
                }
            }
            
            msg.printMessage("Trying to shutdown server...");
            serverSocket.close();
        }
        catch(IOException ioex)
        {
            msg.printMessage("Exception captured: " + ioex.getMessage());
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        finally
        {
            msg.printMessage("Server closed.");
        }
    }
}