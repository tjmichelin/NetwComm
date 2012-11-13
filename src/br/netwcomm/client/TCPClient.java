package br.netwcomm.client;

import br.netwcomm.protocols.TCPHandShakeProtocol;
import br.netwcomm.stats.ChartStatsWorker;
import br.netwcomm.support.InternalMessageHandler;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Thiago
 */
public class TCPClient extends Client
{
    private Socket clientSocket = null;
    private PrintWriter os = null;
    private DataInputStream is = null;
    private int timeInverval;
    private int notx;
    private int pkgSize;
    
    public TCPClient(int listeningPort, boolean runStatus, InternalMessageHandler msgHand, String serverAddr,
            int sampleInterval, int numberTransmissions, int packageSize)
    {
        super(listeningPort, runStatus, msgHand, serverAddr);
        
        this.timeInverval = sampleInterval;
        this.notx = numberTransmissions;
        this.pkgSize = packageSize;
        
        msg.printMessage("Starting Client...");
    }
    
    @Override
    public void run() 
    {
        long startTime;
        long endTime;
        long [] timeDiff = new long[this.notx];
        byte [] in_buffer = new byte[pkgSize];
        int in_bytes;
        TCPHandShakeProtocol handshake = new TCPHandShakeProtocol();
        
        try
        {
            clientSocket = new Socket(serverAddress, serverPort);
            os = new PrintWriter(clientSocket.getOutputStream(), true);
            is = new DataInputStream(clientSocket.getInputStream());
            
            //======================CLIENT LOGIC STARTS HERE======================       
            int counter = 0;
            
            while(isRunning)
            {
                if(counter < this.notx)
                {
                    //Start of time measurement
                    startTime = System.nanoTime();
                    //Send command to Server
                    os.println(Integer.toString(pkgSize));

                    //Waiting and pre-processing Server's response
                    in_bytes = is.read(in_buffer);
                    //End of time measurement
                    endTime = System.nanoTime();
                    timeDiff[counter] = endTime - startTime;
                    msg.printMessage("Reception from Server [" + counter + "]:");
                    msg.printMessage("Data size: " + in_bytes + " [bytes].");
                    msg.printMessage("Message Received: " + handshake.processIncomingResponse(in_buffer));
                    msg.printMessage("Time interval between send and reception (ns): " + Long.toString(timeDiff[counter]));
                    
                    Thread.currentThread().sleep(this.timeInverval);
                    
                    counter++;
                }
                else
                {
                    this.isRunning = false;
                }
            }
            
            //Send to server end of communication signal
            os.println("End");
            //=======================CLIENT LOGIC ENDS HERE=======================
            
            //=======================SHOWING GRAPH================================
            
            ChartStatsWorker stats = new ChartStatsWorker(timeDiff, 15, counter, pkgSize, this.timeInverval);
            Thread statsThread = new Thread(stats);
            statsThread.start();
            
            //=======================END SHOWING GRAPH============================
            
            msg.printMessage("Trying to close connections...");
            os.close();
            is.close();
            clientSocket.close();
        }
        catch(UnknownHostException uhex)
        {
            msg.printMessage("Unknown Host Exception. Error: " + uhex.getMessage());
        }
        catch(IOException ioex)
        {
            msg.printMessage("Input/Output Exception. Error: " + ioex.getMessage());
        }
        catch(Exception e)
        {
            msg.printMessage("Error: " + e.getMessage());
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