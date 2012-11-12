package br.netwcomm.protocols;

import java.util.Random;

/**
 *
 * @author Thiago
 */
public class TCPHandShakeProtocol
{    
    public byte[] processIncomingRequest(String input)
    {        
        int number = Integer.parseInt(input.trim());
        byte result [] = new byte[number];
        Random randomGenerator = new Random();
        
        randomGenerator.nextBytes(result);
        
        return result;
    }
    
    public String processIncomingResponse(byte [] input)
    {
        StringBuffer received = new StringBuffer();
        
        received.append("s*");
        for(int i = 0; i < input.length; i++)
        {
            received.append(Byte.toString(input[i]));
            received.append("*");
        }
        received.append("e");
        
        return received.toString();
    }
}