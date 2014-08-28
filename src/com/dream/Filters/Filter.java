package com.dream.Filters;

import com.dream.Data.DataStream;

/**
 *
 */
public abstract class Filter implements DataStream {
    protected final DataStream inputData;

    public Filter(DataStream inputData) {
        this.inputData = inputData;
    }

    protected abstract int getData(int index);


    public int size() {
        return inputData.size();
    }


    public int get(int index) {
        checkIndexBounds(index);
        return getData(index);
    }

    private void checkIndexBounds(int index){
        if(index > size() || index < 0 ){
            throw  new IndexOutOfBoundsException("index:  "+index+", available:  "+size());
        }
    }
}
