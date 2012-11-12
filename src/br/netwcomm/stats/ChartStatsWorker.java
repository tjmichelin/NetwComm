package br.netwcomm.stats;

import br.netwcomm.gui.StatsGui;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

/**
 *
 * @author Thiago
 */
public class ChartStatsWorker implements Runnable
{
    private long [] timeDiffData = null;
    private int numberOfGraphIntervals;
    int nSamples;
    int packageSize;
    int sampleTimeDelay;
    
    public ChartStatsWorker(long [] timeDiff, int graphIntervals, int samples, int pkgSize,
            int sampleDelay)
    {
        this.timeDiffData = timeDiff;
        this.numberOfGraphIntervals = graphIntervals;
        
        this.nSamples = samples;
        this.packageSize = pkgSize;
        this.sampleTimeDelay = sampleDelay;
    }
    
    private double[] forceTypeCast(long [] dataseries)
    {
        double [] castedDataSeries = new double[dataseries.length];
        
        for(int i = 0; i < dataseries.length; i++)
        {
            castedDataSeries[i] = (double)dataseries[i]/(1e-6);
        }
        
        return castedDataSeries;
    }
    
    private double maxTime(long [] dataseries)
    {
        long max = -1;
        
        for(int i = 0; i < dataseries.length; i++)
        {
            if(max < dataseries[i])
            {
                max = dataseries[i];
            }
        }
        
        return (double) max/(1e-6);
    }
    
    private double minTime(long [] dataseries)
    {
        long min = Long.MAX_VALUE;
        
        for(int i = 0; i < dataseries.length; i++)
        {
            if(min > dataseries[i])
            {
                min = dataseries[i];
            }
        }
        
        return (double) min/(1e-6);
    }
    
    @Override
    public void run()
    {
        double [] timeDiffStats = forceTypeCast(this.timeDiffData);
        
        LogStatsWorker logworker = new LogStatsWorker(timeDiffData);
        Thread log = new Thread(logworker);
        log.start();
        
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        dataset.addSeries("Time Difference", timeDiffStats, numberOfGraphIntervals,
                minTime(this.timeDiffData), maxTime(this.timeDiffData));
        
        String plotTitle = "Transmission Statistics";
        String xaxis = "Time (ms)";
        String yaxis = "Number of samples";
        
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        boolean show = true;
        boolean toolTips = false;
        boolean urls = false;
        
        JFreeChart chart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, dataset, orientation, show, toolTips, urls);
        ChartPanel cpanel = new ChartPanel(chart);
        
        StatsGui statistics = new StatsGui(cpanel);
        statistics.setVisible(true);
    }
}