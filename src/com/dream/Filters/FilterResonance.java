package com.dream.Filters;

import com.dream.Functions;
import com.dream.Data.DataStream;

/**
 *
 */
public class FilterResonance extends Filter {
    private int period = 2;
    private int bufferHalf;

    public FilterResonance(DataStream inputData, int period, int n) {
        super(inputData);
        this.period = period;
        this.bufferHalf = period * n;
    }

    @Override
    protected int getData(int index) {

        if (index < bufferHalf || (index >= size()- bufferHalf)) {
            return 0;
        }

        int sum = 0;
        for (int i = -bufferHalf; i < bufferHalf; i++) {
            sum += inputData.get(index + i)* Functions.getTriangle(i, period);
        }
       return sum/(bufferHalf * Functions.SCALE);
    }
}
