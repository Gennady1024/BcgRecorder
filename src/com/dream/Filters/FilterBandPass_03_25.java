package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */
 public class FilterBandPass_03_25 extends Filter {
    private int bufferSize = 75;

    public FilterBandPass_03_25(DataStream inputData) {
        super(inputData);
    }

    // @Override
    protected int getData(int index) {
        if (index < bufferSize || (index >= size()- bufferSize)) {
            return 0;
        }

        int sum = 0;
        for (int i = (index - bufferSize); i < (index + bufferSize); i++) {
             sum += inputData.get(i);
        }
        return (inputData.get(index - 1) + inputData.get(index) - sum/bufferSize)/2;
    }
}
