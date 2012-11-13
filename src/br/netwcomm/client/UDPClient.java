package br.netwcomm.client;

import br.netwcomm.protocols.UDPHandShakeProtocol;
import br.netwcomm.stats.ChartStatsWorker;
import br.netwcomm.support.InternalMessageHandler;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Thiago
 */
public class UDPClient extends Client
{
    private DatagramSocket clientSocket = null;
    private int sampling;
    private int noTx;
    private int pkgsize;

    public UDPClient(int listeningPort, boolean runStatus, InternalMessageHandler msgHand, String serverAddr,
            int sampleInterval, int numberTransmissions, int packageSize) throws SocketException
    {
         super(listeningPort, runStatus, msgHand, serverAddr);
         
         this.sampling = sampleInterval;
         this.noTx = numberTransmissions;
         this.pkgsize = packageSize;
         
         this.clientSocket = new DatagramSocket();
    }
         
    @Override
    public void run()
    {
        long startTime;
        long endTime;
        long [] timeDiff = new long[this.noTx];
        byte [] data2send = new byte[1024];
        byte [] fromServer = new byte[this.pkgsize];
        DatagramPacket sendPacket = null;
        DatagramPacket receivePacket = null;
        
        UDPHandShakeProtocol udphand = new UDPHandShakeProtocol();
        
        try
        {    
            clientSocket = new DatagramSocket();
            InetAddress inServerAddress = InetAddress.getByName(serverAddress);
            //========================CLIENT LOGIC STARTS HERE=====================
            int counter = 0;
            String command2send = new String(Integer.toString(this.pkgsize));
            data2send = command2send.getBytes();
            
            while(isRunning)
            {
                if(counter < noTx)
                {
                    //Starts measurement of time
                    startTime = System.nanoTime();
                    //Sends command request to server
                    sendPacket = new DatagramPacket(data2send, data2send.length, inServerAddress, this.serverPort);
                    clientSocket.send(sendPacket);

                    //Receives data from server
                    receivePacket = new DatagramPacket(fromServer, fromServer.length);
                    clientSocket.receive(receivePacket);
                    //Ends measurement of time
                    endTime = System.nanoTime();
                    timeDiff[counter] = endTime - startTime;
                    
                    //Process response from server
                    msg.printMessage("Reception from Server [" + counter + "]:");
                    msg.printMessage("Data size: " + receivePacket.getLength() + " [bytes].");
                    msg.printMessage("Message Received: " + udphand.processIncomingResponse(receivePacket));
                    msg.printMessage("Time interval between send and reception (ns): " + Long.toString(timeDiff[counter]));
                
                    Thread.currentThread().sleep(this.sampling);
                    
                    counter++;
                }
                else
                {
                    this.isRunning = false;
                }
            }
            
            command2send = new String("End");
            data2send = command2send.getBytes();
            sendPacket = new DatagramPacket(data2send, data2send.length, inServerAddress, this.serverPort);
            clientSocket.send(sendPacket);
            
            //========================CLIENT LOGIC ENDS HERE=======================
            
            //==========================STATISTICS ANALYSIS========================
            ChartStatsWorker stats = new ChartStatsWorker(timeDiff, ChartStatsWorker.DEFAULT_NUMBER_OF_HISTOGRAM_DIVISIONS,
                    counter, this.pkgsize, this.sampling);
            Thread statsThread = new Thread(stats);
            statsThread.start();
            //=======================END OF STATISTICS ANALYSIS====================
            
            msg.printMessage("Trying to close Socket...");
        }
        catch (SocketException ex)
        {
            msg.printMessage("Socket Exception. Message: " + ex.getMessage());
        }
        catch(UnknownHostException uhex)
        {
            msg.printMessage("Unknown Host Exception. Message: " + uhex.getMessage());
        }
        catch(IOException ioex)
        {
            msg.printMessage("I/O Exception. Message: " + ioex.getMessage());
        }
        catch(Exception e)
        {
            msg.printMessage("An error occurred: " + e.getMessage());
        }
        finally
        {
            msg.printMessage("Client Stopped.");
        }
    }

    @Override
    public void startComponent(boolean start)
    {
        this.isRunning = start;
    }
}