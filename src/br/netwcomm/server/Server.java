package br.netwcomm.server;

import br.netwcomm.support.InternalMessageHandler;
import br.netwcomm.support.NetworkComponent;

/**
 *
 * @author Thiago
 */
public abstract class Server implements NetworkComponent, Runnable
{
    protected int port;
    protected boolean isRunning;
    protected InternalMessageHandler msg;
    protected int timeout;
    
    public Server(int listeningPort, boolean runStatus, InternalMessageHandler msgHand, int timeOut)
    {
        this.port = listeningPort;
        this.isRunning = runStatus;
        this.msg = msgHand;
        this.timeout = timeOut;
    }
}