package GUI;

import Game.Province;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultKeyedValues2DDataset;

import javax.swing.*;
import java.awt.*;

public class PopulationChartMaker {

    public static JPanel createChartPanel(Province province) {
        JFreeChart chart = createChart(createDataset(province));
        ChartPanel panel = new ChartPanel(chart);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(panel, BorderLayout.CENTER);

        return wrapperPanel;
    }

    public static JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Population Chart Demo 1",
                "Age Group",     // domain axis label
                "Population", // range axis label
                dataset,         // data
                PlotOrientation.HORIZONTAL,
                true,            // include legend
                true,            // tooltips
                false            // urls
        );

        return chart;
    }

    public static CategoryDataset createDataset(Province province) {
        DefaultKeyedValues2DDataset data = new DefaultKeyedValues2DDataset();
        Integer[] popsMale = province.getPopsMale();
        Integer[] popsFemale = province.getPopsMale();
        for(int i = 0;i<16;i++){
            data.addValue(-popsMale[i], "Male", ""+(i+i*4)+"-"+ (i+4 + i*4));
            data.addValue(popsFemale[i], "Female", ""+(i+i*4)+"-"+ (i+4 + i*4));
        }
        data.addValue(-popsMale[16], "Male", "85+");
        data.addValue(popsFemale[16], "Female", "85+");
        return data;
    }
}
