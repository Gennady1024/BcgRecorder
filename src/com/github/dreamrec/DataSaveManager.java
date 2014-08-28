package com.github.dreamrec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * Saves measurement data to file and reads from it
 */
public class DataSaveManager {


    private static final Log log = LogFactory.getLog(DataSaveManager.class);

    public void saveToFile(File file, Model model) throws ApplicationException {
        DataOutputStream outStream = null;
//        try {
        try {
            outStream = new DataOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
           log.error(e);
            throw new ApplicationException("Error while saving file " + file.getName());
        }
        try {
            saveStateToStream(outStream, model);
        } catch (IOException e) {
           log.error(e);
            throw new ApplicationException("Error while saving file " + file.getName());
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    private void saveStateToStream(DataOutputStream outStream, Model model) throws IOException {
        outStream.writeLong(model.getStartTime());
        double frequency = model.getFrequency();
        int accDivider = (int) (frequency/model.ACC_MAX_FREQUENCY);
        outStream.writeDouble(frequency);
        for (int i = 0; i < model.getEyeDataList().size(); i++) {
            outStream.writeInt(model.getEyeDataList().get(i));
            outStream.writeInt(model.getCh2DataList().get(i));
            if((i % accDivider) == 0) {
                outStream.writeInt(model.getAcc1DataList().get(i));
                outStream.writeInt(model.getAcc2DataList().get(i));
                outStream.writeInt(model.getAcc3DataList().get(i));
            }
        }
    }


    public void readFromFile(File file, Model model) throws ApplicationException {
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(new FileInputStream(file));
            loadStateFromInStream(dataInputStream, model);
        } catch (Exception e) {
            log.error(e);
            throw new ApplicationException("Error while reading from file " + file.getName());
        } finally {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    private void loadStateFromInStream(DataInputStream inputStream, Model model) throws IOException {
        try {
            long startTime = inputStream.readLong();
            double frequency = inputStream.readDouble();
            int accDivider = (int) (frequency/model.ACC_MAX_FREQUENCY);
            model.clear();
            model.setFrequency(frequency);
            model.setStartTime(startTime);
            int i = 0;
            while (true) {
                model.addEyeData(inputStream.readInt());
                model.addCh2Data(inputStream.readInt());
                if((i % accDivider) == 0) {
                    model.addAcc1Data(inputStream.readInt());
                    model.addAcc2Data(inputStream.readInt());
                    model.addAcc3Data(inputStream.readInt());
                }
                i++;
            }
        } catch (EOFException e) {
            log.info("End of file");
        }
    }
}

