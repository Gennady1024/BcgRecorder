package com.dream.Filters;

import com.dream.Functions;
import com.dream.Data.DataStream;

/**
 *
 */
public class FilterBandPass_Alfa extends FilterBuffered {
    private int period = 4;
    private int bufferHalf = period * 4;
    public FilterBandPass_Alfa(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {

        if (index < bufferHalf || (index >= size()- bufferHalf)) {
            return 0;
        }

        int sum = inputData.get(index);
        for (int i = -bufferHalf; i < bufferHalf; i++) {
            sum += inputData.get(index + i)* Functions.getTriangle(i, period);
        }
//        return sum/(2 * bufferHalf * Functions.SCALE);
        return sum/(bufferHalf * Functions.SCALE);
    }
}