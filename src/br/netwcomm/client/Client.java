package br.netwcomm.client;

import br.netwcomm.support.InternalMessageHandler;
import br.netwcomm.support.NetworkComponent;

/**
 *
 * @author Thiago
 */
public abstract class Client implements Runnable, NetworkComponent
{
    protected boolean isRunning;
    protected String serverAddress;
    protected int serverPort;
    protected InternalMessageHandler msg;
    
    public Client(int listeningPort, boolean runStatus, InternalMessageHandler msgHand, String serverAddr)
    {
        this.serverPort = listeningPort;
        this.isRunning = runStatus;
        this.serverAddress = serverAddr;
        this.msg = msgHand;
    }
}