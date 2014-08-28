package com.github.dreamrec;

import com.crostec.ads.*;
import com.dream.ApparatModel;
import com.github.dreamrec.filters.FrequencyDividingPreFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class  Controller {
    public static final int DRM_FREQUENCY = 50;
    public static final int ACC_FREQUENCY = 10;
    private Timer repaintTimer;
    private Model model;
    private MainWindow mainWindow;
    private ApplicationProperties applicationProperties;
    private static final Log log = LogFactory.getLog(Controller.class);
    public static int CURSOR_SCROLL_STEP = 1; //in points
    private boolean isAutoScroll = false;
    private boolean isRecording = false;
    private Ads ads = new Ads();
    private BdfWriter bdfWriter;
    private IncomingDataBuffer incomingDataBuffer;
    private FrequencyDividingPreFilter channel0FrequencyDividingPreFilter;
    private FrequencyDividingPreFilter channel1FrequencyDividingPreFilter;
    private FrequencyDividingPreFilter accelerometer0DividingPreFilter;
    private FrequencyDividingPreFilter accelerometer1DividingPreFilter;
    private FrequencyDividingPreFilter accelerometer2DividingPreFilter;
    private AdsConfiguration adsConfiguration = new AdsConfigUtil().readConfiguration();

    private int maxDiv;
    private int nrOfChannel0Samples; //number of channel0 samples in data frame
    private int nrOfChannel1Samples;
    private int nrOfAccelerometerSamples;
    private int sps;
    private int accelerometerOffset;

    public Controller(final Model model, ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.model = model;
        sps = adsConfiguration.getSps().getValue();
        maxDiv = adsConfiguration.getDeviceType().getMaxDiv().getValue();
        int channel0Divider = adsConfiguration.getAdsChannels().get(0).getDivider().getValue();
        nrOfChannel0Samples = maxDiv / channel0Divider;
        int channel1Divider = adsConfiguration.getAdsChannels().get(1).getDivider().getValue();
        nrOfChannel1Samples = maxDiv / channel1Divider;
        int accelerometerDivider = adsConfiguration.getAccelerometerDivider().getValue();
        nrOfAccelerometerSamples = maxDiv / accelerometerDivider;
        accelerometerOffset = AdsUtils.getDecodedFrameSize(adsConfiguration) - 2 - 3 * nrOfAccelerometerSamples;


        repaintTimer = new Timer(applicationProperties.getRepaintDelay(), new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateModel();
                mainWindow.repaint();
            }
        });

        channel0FrequencyDividingPreFilter = new FrequencyDividingPreFilter(sps / (DRM_FREQUENCY * channel0Divider)) {
            @Override
            public void notifyListeners(int value) {
                model.addEyeData(value);
            }
        };
        channel1FrequencyDividingPreFilter = new FrequencyDividingPreFilter(sps / (DRM_FREQUENCY * channel1Divider)) {
            @Override
            public void notifyListeners(int value) {
                model.addCh2Data(value);
            }
        };
        accelerometer0DividingPreFilter = new FrequencyDividingPreFilter(sps / (ACC_FREQUENCY * accelerometerDivider)) {
            @Override
            public void notifyListeners(int value) {
                model.addAcc1Data(value);
            }
        };
        accelerometer1DividingPreFilter = new FrequencyDividingPreFilter(sps / (ACC_FREQUENCY * accelerometerDivider)) {
            @Override
            public void notifyListeners(int value) {
                model.addAcc2Data(value);
            }
        };
        accelerometer2DividingPreFilter = new FrequencyDividingPreFilter(sps / (ACC_FREQUENCY  * accelerometerDivider)) {
            @Override
            public void notifyListeners(int value) {
                model.addAcc3Data(value);
            }
        };
    }



    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public boolean isRecording() {
        return isRecording;
    }

    protected void updateModel() {
        while (incomingDataBuffer.available()) {
            int[] frame = incomingDataBuffer.poll();
            for (int i = 0; i < nrOfChannel0Samples; i++) {
                channel0FrequencyDividingPreFilter.add(frame[i]);
            }
            for (int i = nrOfChannel0Samples; i < nrOfChannel0Samples + nrOfChannel1Samples; i++) {
                channel1FrequencyDividingPreFilter.add(frame[i]);
            }
            for (int i = 0; i < nrOfAccelerometerSamples; i++) {
                accelerometer0DividingPreFilter.add(frame[accelerometerOffset + i]);
            }
            for (int i = 0; i < nrOfAccelerometerSamples; i++) {
                accelerometer1DividingPreFilter.add(frame[accelerometerOffset + nrOfAccelerometerSamples + i]);
            }
            for (int i = 0; i < nrOfAccelerometerSamples; i++) {
                accelerometer2DividingPreFilter.add(frame[accelerometerOffset + 2 * nrOfAccelerometerSamples + i]);
            }
        }
        if (isAutoScroll) {
            model.setFastGraphIndexMaximum();
        }
    }

    public void startRecording() {
        isRecording = true;
        BdfHeaderData bdfHeaderData = new BdfHeaderData(adsConfiguration);
        if (bdfWriter != null) {
            ads.removeAdsDataListener(bdfWriter);
        }
        bdfHeaderData.setFileNameToSave(new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis())) + ".bdf");
        bdfWriter = new BdfWriter(bdfHeaderData);
        ads.addAdsDataListener(bdfWriter);
        model.clear();
        model.setFrequency(DRM_FREQUENCY);
        model.setStartTime(System.currentTimeMillis());  //todo remove
        repaintTimer.start();
        incomingDataBuffer = new IncomingDataBuffer();
        ads.addAdsDataListener(incomingDataBuffer);
        try {
            ads.startRecording(bdfHeaderData.getAdsConfiguration());
        } catch (AdsException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }

        isAutoScroll = true;
    }

    public void stopRecording() {
        if (!isRecording) return;
        isRecording = false;
        ads.stopRecording();
        bdfWriter.stopRecording();
        repaintTimer.stop();
        isAutoScroll = false;
        saveToFile();
    }

    private void saveToFile() {
        String fileName = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date(System.currentTimeMillis())) + ".drm";
        try {
            new DataSaveManager().saveToFile(new File(fileName), model);
        } catch (ApplicationException e) {
            String msg = "error saving to file " + fileName;
            log.error(msg, e);
            mainWindow.showMessage(msg);
        }
    }

    public void changeXSize(int xSize) {
        model.setXSize(xSize);
        applicationProperties.setXSize(xSize);
        mainWindow.repaint();
    }

    public void scrollCursorForward() {
        model.moveFastGraph(model.getFastGraphIndex() + CURSOR_SCROLL_STEP * Model.DIVIDER);
        if (model.isFastGraphIndexMaximum()) {
            isAutoScroll = true;
        }
        mainWindow.repaint();
    }

    public void scrollCursorBackward() {
        model.moveFastGraph(model.getFastGraphIndex() - CURSOR_SCROLL_STEP * Model.DIVIDER);
        isAutoScroll = false;
        mainWindow.repaint();
    }

    public void moveCursor(int newPosition) {
        model.moveCursor(newPosition);
        mainWindow.repaint();
    }

    public void scrollSlowGraph(int scrollPosition) {
        model.moveSlowGraph(scrollPosition);
        isAutoScroll = false;
        mainWindow.repaint();
    }

    public void closeApplication() {
        stopRecording();
        System.exit(0);
    }

    public void readBdfFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(mainWindow);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            log.info("Opening: " + file.getName() + ".");
            BdfReader bdfReader = new BdfReader();
            incomingDataBuffer = new IncomingDataBuffer();
            model.clear();
            bdfReader.addDataListener(incomingDataBuffer);
            bdfReader.read(file);
            updateModel();
            mainWindow.repaint();
        } else {
            log.info("Open command cancelled by user.");
        }
    }

    public void readFromFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./"));
            fileChooser.setFileFilter(new ExtFileFilter("drm", "*.drm Dream records"));
            int fileChooserState = fileChooser.showOpenDialog(mainWindow);
            if (fileChooserState == JFileChooser.APPROVE_OPTION) {
                new DataSaveManager().readFromFile(fileChooser.getSelectedFile(), model);
            }
        } catch (ApplicationException e) {
            mainWindow.showMessage(e.getMessage());
        }
        mainWindow.repaint();
    }
}
