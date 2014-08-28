package com.github.dreamrec.filters;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HiPassPreFilter {

    private List<Integer> rawData = new ArrayList<Integer>();
    private int bufferSize;
    private static final Log log = LogFactory.getLog(HiPassPreFilter.class);
    private double cutOffFrequency;

    /*
    *   bufferSize = inputFrequency/cutOffFrequency
    */

    public HiPassPreFilter(int inputFrequency, double cutOffFrequency) {
        this.cutOffFrequency = cutOffFrequency;
            bufferSize = (int) (inputFrequency / cutOffFrequency);
    }

    public double getCutOffFrequency() {
        return cutOffFrequency;
    }

    public int getFilteredValue(int value) {
        int filteredValue = value;
        if (bufferSize != 0){
            rawData.add(value);
            if (rawData.size() == bufferSize + 1) {
                rawData.remove(0);
            } else if (rawData.size() > bufferSize + 1) {
                throw new IllegalStateException("bufferSize exceeds maximum value: " + rawData.size());
            }
            long rawDataBufferSum = 0;

            for (int i = 0; i < rawData.size(); i++) {
                rawDataBufferSum += rawData.get(i) * i;
            }

            int rawDataSize = rawData.size();
            if (rawDataSize > 1) {
                filteredValue = value - (int) (rawDataBufferSum * 2 / (rawDataSize * (rawDataSize - 1)));
            }
        }

        /*if (filteredValue > Short.MAX_VALUE) {
            log.info("Incoming value exceeds Short.MAX_VALUE: " + filteredValue + ". Incoming value = " +  value);
            filteredValue = Short.MAX_VALUE;
        }
        if (filteredValue < Short.MIN_VALUE) {
            log.info("Incoming value less than Short.MIN_VALUE: " + filteredValue + ". Incoming value = " +  value);
            filteredValue = Short.MIN_VALUE;
        }*/

        return (short) filteredValue;
    }
}
