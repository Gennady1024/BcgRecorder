// public class FilterResonance_1Hz {
package com.dream.Filters;

import com.dream.Functions;
import com.dream.Data.DataStream;

/**
 *
 */
public class FilterBandPass_1Hz extends Filter {
    private int bufferHalf = 110;


    private int period_1 = 80;
    private int bufferHalf_1 = period_1;

    private int period_2 = 50;
    private int bufferHalf_2 = period_2;

    private int period_3 = 35;
    private int bufferHalf_3 = period_3;

    public FilterBandPass_1Hz(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {

        if (index < bufferHalf || (index >= size()- bufferHalf)) {
            return 0;
        }

        int sum = 0;
        for (int i = -bufferHalf; i < bufferHalf; i++) {
            sum += inputData.get(index + i)* Functions.getTriangle(i, period_1);
        }

        int sum_1 = 0;
        for (int i = -bufferHalf_1; i < bufferHalf_1; i++) {
            sum_1 += inputData.get(index + i)* Functions.getTriangle(i, period_1);
        }

        int sum_2 = 0;
        for (int i = -bufferHalf_2; i < bufferHalf_2; i++) {
            sum_2 += inputData.get(index + i)* Functions.getTriangle(i, period_2);
        }

        int sum_3 = 0;
        for (int i = -bufferHalf_3; i < bufferHalf_3; i++) {
            sum_3 += inputData.get(index + i)* Functions.getTriangle(i, period_3);
        }

        int y =
                 (sum_1/(2 * bufferHalf_1 * Functions.SCALE)) * 8/10
              +  (sum_2/(2 * bufferHalf_2 * Functions.SCALE)) * 16/10
              +  (sum_3/(2 * bufferHalf_3 * Functions.SCALE)) * 5/10
            ;

        return y;
    }
}

