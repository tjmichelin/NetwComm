package br.netwcomm.protocols;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 *
 * @author Thiago
 */
public class UDPHandShakeProtocol
{   
    public byte[] processIncomingRequest(String inCommand)
    {
        int size = Integer.parseInt(inCommand);
        
        byte [] result = new byte[size];
        Random randomgenerator = new Random();
        
        randomgenerator.nextBytes(result);
        
        return result;
    }
    
    public String processIncomingResponse(DatagramPacket response)
    {
        StringBuffer received = new StringBuffer();
        byte [] incoming = response.getData();
        received.append("s*");
        
        for(int i = 0; i < incoming.length; i++)
        {
            received.append(Byte.toString(incoming[i]));
            received.append("*");
        }
        received.append("e");
        
        return received.toString();
    }
}