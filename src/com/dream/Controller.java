package com.dream;

import com.dream.Data.DataList;
import com.github.dreamrec.ApplicationException;
import com.github.dreamrec.DataSaveManager;
import com.github.dreamrec.ExtFileFilter;

import javax.swing.*;
import java.io.File;


/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 10.05.14
 * Time: 12:40
 * To change this template use File | Settings | File Templates.
 */
public class Controller {
    private MainView mainWindow;
    private ApparatModel model;

    public Controller( ApparatModel model) {
        this.model = model;
    }

    public void setView(MainView view) {
        mainWindow = view;
    }

    public void readFromFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./"));
            fileChooser.setFileFilter(new ExtFileFilter("drm", "*.drm Dream records"));
            int fileChooserState = fileChooser.showOpenDialog(mainWindow);
            if (fileChooserState == JFileChooser.APPROVE_OPTION) {
                File selectedFile =  fileChooser.getSelectedFile();
                new FileIOManager().readFromFile(selectedFile, model);
                mainWindow.setTitle(selectedFile.getName());
                mainWindow.setStart(model.getStartTime(), 1000 / model.getFrequency());
            }
        } catch (ApplicationException e) {
            mainWindow.showMessage(e.getMessage());
        }
        mainWindow.syncView();
    }


}
