package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */
// public class FilterHiPass extends Filter<Integer> {
public class FilterHiPass extends FilterBuffered {
    private int bufferSize;
    private int indexBefore = -10;
    private int sumBefore = 0;

    public FilterHiPass(DataStream inputData, int bufferSize) {
        super(inputData);
        this.bufferSize = bufferSize;
    }

   // @Override
    protected int getData(int index) {
        if (index < bufferSize) {
            return 0;
        }
        if (index >= size()- bufferSize) {
            return 0;
        }
        int sum = 0;
        if(index == (indexBefore +1)) {
           sum = sumBefore + inputData.get(index + bufferSize) - inputData.get(index - bufferSize);
           sumBefore = sum;
           indexBefore = index;
        }
        else {
            for (int i = (index - bufferSize); i < (index + bufferSize); i++) {
                sum += inputData.get(i);
            }
        }
        return inputData.get(index) - sum/(2*bufferSize);
    }

    protected Integer getData_old(int index) {
        if (index < bufferSize) {
            return 0;
        }
        if (index >= size()-bufferSize) {
            return 0;
        }
        int sum = inputData.get(index);
        for (int i = 1; i <bufferSize; i++) {
            sum += (inputData.get(index - i) + inputData.get(index + i));
        }
        sum += inputData.get(index - bufferSize);
        return inputData.get(index) - sum/(2*bufferSize);
    }

}