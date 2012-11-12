package br.netwcomm.stats;

import br.netwcomm.gui.StatsGui;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thiago
 */
public class LogStatsWorker implements Runnable
{
    private long [] timeseries = null;
    
    public LogStatsWorker(long [] dataseries)
    {
        this.timeseries = dataseries;
    }
    
    @Override
    public void run() 
    {
        this.saveData2File("TimeSeries_" + Long.toString(System.currentTimeMillis()) + ".txt");
    }
    
     private void saveData2File(String fileName)
    {
        String savingPath = new File(".").getAbsolutePath();
        BufferedWriter writer = null;
        
        try
        {
            writer = new BufferedWriter(new FileWriter(savingPath + "\\" + fileName));
            
            for(int i = 0; i < this.timeseries.length; i++)
            {
                writer.write(Integer.toString(i) + ";" + Long.toString(this.timeseries[i]));
                writer.newLine();
            }
            
            writer.close();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(StatsGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}