package com.crostec.bdfrecorder;

import com.crostec.ads.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import javax.swing.*;

public class Controller {

    private boolean isRecording;
    private SettingsWindow settingsWindow;
    private Ads ads;
    private BdfWriter bdfWriter;
    private static  final Log log = LogFactory.getLog(Controller.class);

    public Controller(Ads ads) {
        this.ads = ads;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void startRecording(BdfHeaderData bdfHeaderData) {
        new AdsConfigUtil().saveAdsConfiguration(bdfHeaderData.getAdsConfiguration());
        log.info(bdfHeaderData.getAdsConfiguration().toString());
        isRecording = true;
        if (bdfWriter != null) {
            ads.removeAdsDataListener(bdfWriter);
        }
        bdfWriter = new BdfWriter(bdfHeaderData);
        ads.addAdsDataListener(bdfWriter);
        try {
            ads.startRecording(bdfHeaderData.getAdsConfiguration());
        } catch (AdsException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }

    }

    public void stopRecording() {
        if (!isRecording) return;
        bdfWriter.stopRecording();
        ads.stopRecording();
        isRecording = false;
    }

    public void closeApplication(BdfHeaderData bdfHeaderData) {
        new AdsConfigUtil().saveAdsConfiguration(bdfHeaderData.getAdsConfiguration());
        stopRecording();
        System.exit(0);
    }

    public void setSettingsWindow(SettingsWindow settingsWindow) {
        this.settingsWindow = settingsWindow;
    }
}
