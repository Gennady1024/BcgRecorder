package com.github.dreamrec.filters;


import com.github.dreamrec.filters.AbstractFilter;
import com.github.dreamrec.filters.Filter;

public class HiPassFilter extends AbstractFilter<Integer> {
    private int bufferSize;

    public HiPassFilter(Filter<Integer> inputData, int bufferSize) {
        super(inputData);
        this.bufferSize = bufferSize;
    }

    @Override
    protected Integer doFilter(int index) {
        if (index < bufferSize) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < bufferSize; i++) {
             sum+=inputData.get(index - i);
        }
        return inputData.get(index) - sum/bufferSize;
    }
}
