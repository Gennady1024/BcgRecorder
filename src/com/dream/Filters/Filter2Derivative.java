package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */

public class Filter2Derivative extends Filter {

    public Filter2Derivative(DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {
        if (index < 2) {
            return 0;
        }
        return (inputData.get(index) - inputData.get(index - 1)) -  (inputData.get(index-1) - inputData.get(index - 2));
    }
}