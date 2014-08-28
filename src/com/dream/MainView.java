package com.dream;

import com.dream.Data.DataStream;
import com.dream.Filters.*;
import com.dream.Graph.GraphsViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Main Window of our program...
 */
public class MainView extends JFrame {
    private String title = "Dream Recorder";
    private GraphsViewer graphsViewer;
    private  JMenuBar menu = new JMenuBar();
    private  ApparatModel model;
    private Controller controller;

    public MainView(ApparatModel apparatModel, Controller controller) {
        model = apparatModel;
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        formMenu();

        // Key Listener to change MovementLimit in model
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                graphsViewer.dispatchEvent(e); // send KeyEvent to graphsViewer
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP) {
                    model.movementLimitUp();
                    graphsViewer.syncView();
                }

                if (key == KeyEvent.VK_DOWN) {
                    model.movementLimitDown();
                    graphsViewer.syncView();
                }
            }
        });

        graphsViewer = new GraphsViewer();
        graphsViewer.setPreferredSize(getWorkspaceDimention());
        add(graphsViewer, BorderLayout.CENTER);

        formBcgViewer();

        pack();
        setFocusable(true);
        setVisible(true);
    }

    private void formBcgViewer() {
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);
        graphsViewer.addGraphPanel(4, true);

        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(4, false);

        DataStream channel_1 = model.getCh1DataStream();
        DataStream channel_2 = model.getCh2DataStream();
        DataStream filteredDataBcg1 = new FilterBandPass(channel_1, 500, 0);
        DataStream filteredDataBcg2 = new FilterBandPass(channel_2, 500, 0);
        DataStream filteredData_Card_Bcg1 = new FilterBandPass(channel_1, 5, 0);
        DataStream filteredData_Card_Bcg2 = new FilterBandPass(channel_2, 5, 0);

        graphsViewer.addGraph(0, filteredDataBcg1);
        graphsViewer.addGraph(1, filteredDataBcg2);

        graphsViewer.addGraph(2, filteredData_Card_Bcg1);
        graphsViewer.addGraph(3, filteredData_Card_Bcg2);

        graphsViewer.addCompressedGraph(0, new CompressorAveragingAbs(filteredDataBcg1, graphsViewer.getCompression()));
        graphsViewer.addCompressedGraph(1, new CompressorAveragingAbs(filteredDataBcg2, graphsViewer.getCompression()));

    }

    private void formGalaViewer() {
        graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(2, true);

        graphsViewer.addCompressedGraphPanel(2, false);
        graphsViewer.addCompressedGraphPanel(2, false);

        DataStream channel_1 = model.getCh1DataStream();
        DataStream rem = model.getSleepStream();
        graphsViewer.addGraph(0, new FilterOffset(channel_1, graphsViewer));
        graphsViewer.addGraph(1, new FilterDerivative(channel_1));
        graphsViewer.addGraph(2, new Filter2Derivative(channel_1));

        DataStream compressedDreamGraph = new CompressorAveraging(new FilterDerivativeAbs(model.getEyeData()), graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(0, compressedDreamGraph);
        DataStream compressedRem =  new CompressorMaximizing(new FilterAbs(rem), graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(1, compressedRem);

    }

    private void formGenaViewer() {
        graphsViewer.addGraphPanel(8, true);
        graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(4, true);

        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(2, true);

        DataStream channel_1 = model.getCh1DataStream();
        DataStream channel_2 = model.getCh2DataStream();
        graphsViewer.addGraph(0, new FilterOffset(channel_1, graphsViewer));
        graphsViewer.addGraph(0, new FilterOffset(channel_2, graphsViewer));

        DataStream filteredData1 = new FilterResonance(channel_1, 4, 4);
        DataStream filteredData2 = new FilterBandPass_Alfa(channel_2);
        DataStream filteredDataAlfa1 = new FilterHiPass(filteredData1, 2);
        DataStream filteredDataAlfa2 = new FilterHiPass(filteredData2, 2);
        graphsViewer.addGraph(1, filteredDataAlfa1);
        graphsViewer.addGraph(2, filteredDataAlfa2);

        DataStream filteredDataDelta1 = new FilterBandPass_Delta(channel_1);
        DataStream filteredDataDelta2 = new FilterBandPass_Delta(channel_2);
        graphsViewer.addGraph(3, filteredDataDelta1 );
        graphsViewer.addGraph(3,  filteredDataDelta2);

        DataStream compressedPositionGraph = new CompressorAveraging(model.getAccPositionStream(), graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(3, compressedPositionGraph);

        graphsViewer.addCompressedGraph(0, new CompressorAveragingDeltaAbs(filteredDataDelta1, graphsViewer.getCompression(), 60));

        graphsViewer.addCompressedGraph(1, new CompressorMaximizingDelta(filteredDataAlfa2, graphsViewer.getCompression()));
        graphsViewer.addCompressedGraph(2, new CompressorAveragingDeltaAbs(filteredDataDelta2, graphsViewer.getCompression(), 80));

        DataStream rem =  model.getSleepStream();
        DataStream compressedRem =  new CompressorMaximizing(new FilterAbs(rem), graphsViewer.getCompression());
//        graphsViewer.addCompressedGraph(0, compressedRem);
        graphsViewer.addCompressedGraph(1, compressedRem);
//        graphsViewer.addCompressedGraph(2, compressedRem);
    }

    private void formStasViewer() {
        graphsViewer.addGraphPanel(8, true);
        graphsViewer.addGraphPanel(8, true);
        graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(2, true);
        graphsViewer.addGraphPanel(4, true);

        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(4, false);
        graphsViewer.addCompressedGraphPanel(2, true);

        DataStream channel_1 = model.getCh1DataStream();
        DataStream channel_2 = model.getCh2DataStream();
        graphsViewer.addGraph(0, new FilterOffset(channel_1, graphsViewer));
        graphsViewer.addGraph(0, new FilterOffset(channel_2, graphsViewer));

        graphsViewer.addGraph(1, new FilterBandPass_03_25(channel_1));
        graphsViewer.addGraph(1, new FilterBandPass_03_25(channel_2));

        DataStream filteredData1 = new FilterBandPass_Alfa(channel_1);
        DataStream filteredData2 = new FilterBandPass_Alfa(channel_2);
        DataStream filteredDataAlfa1 = new FilterHiPass(filteredData1, 2);
        DataStream filteredDataAlfa2 = new FilterHiPass(filteredData2, 2);
        graphsViewer.addGraph(2, filteredDataAlfa1);
        graphsViewer.addGraph(3, filteredDataAlfa2);

        DataStream filteredDataDelta1 = new FilterBandPass_Delta(channel_1);
        DataStream filteredDataDelta2 = new FilterBandPass_Delta(channel_2);
        graphsViewer.addGraph(4, filteredDataDelta1 );
        graphsViewer.addGraph(4,  filteredDataDelta2);

        DataStream compressedPositionGraph = new CompressorAveraging(model.getAccPositionStream(), graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(3, compressedPositionGraph);

        graphsViewer.addCompressedGraph(0, new CompressorAveragingDeltaAbs(filteredDataDelta1, graphsViewer.getCompression(), 60));

        graphsViewer.addCompressedGraph(1, new CompressorMaximizingDelta(filteredDataAlfa2, graphsViewer.getCompression()));
        graphsViewer.addCompressedGraph(2, new CompressorAveragingDeltaAbs(filteredDataDelta2, graphsViewer.getCompression(), 80));

        DataStream rem =  model.getSleepStream();
        DataStream compressedRem =  new CompressorMaximizing(new FilterAbs(rem), graphsViewer.getCompression());
        graphsViewer.addCompressedGraph(1, compressedRem);
    }

    public void syncView() {
        graphsViewer.syncView();
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    public void setStart(long starTime, int period_msec) {
        graphsViewer.setStart(starTime, period_msec);
    }

    private Dimension getWorkspaceDimention() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = dimension.width - 20;
        int height = dimension.height - 150;
        return new Dimension(width, height);
    }


    private void formMenu() {

        JMenu fileMenu = new JMenu("File");
        menu.add(fileMenu);
        JMenuItem open = new JMenuItem("Open");
        fileMenu.add(open);

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.readFromFile();
            }
        });


        JMenu recordMenu = new JMenu("Record");
        menu.add(recordMenu);
        JMenuItem start = new JMenuItem("Start");
        JMenuItem stop = new JMenuItem("Stop");
        recordMenu.add(start);
        recordMenu.add(stop);

        add(menu, BorderLayout.NORTH);
    }
}
