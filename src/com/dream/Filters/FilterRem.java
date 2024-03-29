package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */

public class FilterRem extends Filter {
    int stopTimeMscec = 80;
    int derivativeMin = 400;
    int derivativeMax = 2000;
    int frequency = 50;

    int stopPoints =  (stopTimeMscec * frequency) / 1000;

    public FilterRem (DataStream inputData) {
        super(inputData);
    }

    @Override
    protected int getData(int index) {

        if (index <= stopPoints) {
            return 0;
        }
        if ( index >= (inputData.size() - stopPoints)) {
            return 0;
        }
        int derivative = inputData.get(index) - inputData.get(index - 1);
        if(Math.abs(derivative) < derivativeMin) {
            return 0;
        }
        if(Math.abs(derivative) > derivativeMax) {
            return 0;
        }

        for(int i = 1; i <= stopPoints; i++ ) {
            int derivative_i = inputData.get(index - i) - inputData.get(index - i -1);
            if(Math.abs(derivative_i) > derivativeMin ) {
                return 0;
            }
        }

        int delta = 0;
        int sign = signum(derivative);
        int index_i = index + 1;
        while((Math.abs(derivative) > derivativeMin) && isEqualSign(derivative, sign) && ( index_i < size() ))  {
            delta += derivative;
            derivative = inputData.get(index_i) - inputData.get(index_i - 1);
            index_i++;
        }

        for(int i = 1; i <= stopPoints; i++ ) {
            int derivative_i = inputData.get(index_i + i) - inputData.get(index_i + i - 1);
            if(Math.abs(derivative_i) > derivativeMin ) {
                return 0;
            }
        }

        return delta;
    }

    private int signum(int a){
        if(a >= 0) {
            return 1;
        }

        return -1;
    }

    protected boolean isEqualSign(int a, int b) {
        if( ( a > 0 )  && (b > 0) ){
            return true;
        }

        if( ( a < 0 )  && (b < 0) ){
            return true;
        }

        return false;
    }
}

