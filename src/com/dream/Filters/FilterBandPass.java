package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */
public class FilterBandPass extends Filter {
    private int bufferHalf_Hi =  500;
    private int bufferHalf_Lo =   0;
    private int bufferHalf_Max = Math.max(bufferHalf_Hi, bufferHalf_Lo);


    public FilterBandPass(DataStream inputData, int bufferHalf_Hi, int bufferHalf_Lo) {
        super(inputData);
        this.bufferHalf_Hi = bufferHalf_Hi;
        this.bufferHalf_Lo = bufferHalf_Lo;
    }

    @Override
    protected int getData(int index) {

        if (index < bufferHalf_Max || (index >= size()- bufferHalf_Max)) {
            return 0;
        }

        int sum_1 = inputData.get(index);
        for (int i = 1; i <= bufferHalf_Hi; i++) {
            sum_1 += (inputData.get(index - i)) + inputData.get(index + i);
        }

        int sum_2 = inputData.get(index);
        for (int i = 1; i <= bufferHalf_Lo; i++) {
            sum_2 += (inputData.get(index - i)) + inputData.get(index + i);
        }

        return sum_2/(2* bufferHalf_Lo + 1) - sum_1/(2* bufferHalf_Hi + 1);
    }
}

