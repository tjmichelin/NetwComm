package br.netwcomm.stats;

import br.netwcomm.gui.StatsGui;
import java.awt.Color;
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
            castedDataSeries[i] = (double)dataseries[i]/(1e6);
        }
        
        return castedDataSeries;
    }
    
    private double dMaxTime(long [] dataseries)
    {       
        return (double) lMaxTime(dataseries) /(1e6);
    }
    
    private long lMaxTime(long [] dataseries)
    {
        long max = -1;
        
        for(int i = 0; i < dataseries.length; i++)
        {
            if(max < dataseries[i])
            {
                max = dataseries[i];
            }
        }
        
        return max;
    }
    
    private double dMinTime(long [] dataseries)
    {
        return (double) lMinTime(dataseries) /(1e6);
    }
    
    private long lMinTime(long [] dataseries)
    {
        long min = Long.MAX_VALUE;
        
        for(int i = 0; i < dataseries.length; i++)
        {
            if(min > dataseries[i])
            {
                min = dataseries[i];
            }
        }
        
        return min;
    }
    
    @Override
    public void run()
    {
        double [] timeDiffStats = forceTypeCast(this.timeDiffData);
        long lmin, lmax;
        double dmin, dmax;
        
        LogStatsWorker logworker = new LogStatsWorker(timeDiffData);
        Thread log = new Thread(logworker);
        log.start();
        
        dmin = this.dMinTime(timeDiffData);
        dmax = this.dMaxTime(timeDiffData);
        
        lmin = this.lMinTime(timeDiffData);
        lmax = this.lMaxTime(timeDiffData);
        
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        dataset.addSeries("Time Difference", timeDiffStats, numberOfGraphIntervals,
                dmin, dmax);
        
        String plotTitle = "Transmission Statistics";
        String xaxis = "Time (ms)";
        String yaxis = "Number of samples";
        
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        boolean show = true;
        boolean toolTips = false;
        boolean urls = false;
        
        JFreeChart chart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, dataset, orientation, show, toolTips, urls);
        chart.setAntiAlias(true);
        chart.setBackgroundPaint(Color.WHITE);
        ChartPanel cpanel = new ChartPanel(chart);
        
        StatsGui statistics = new StatsGui(cpanel, lmax, lmin, nSamples, packageSize);
        statistics.setVisible(true);
    }
}