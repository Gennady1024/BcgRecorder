package com.github.dreamrec.filters;


import com.github.dreamrec.filters.AbstractFilter;
import com.github.dreamrec.filters.Filter;

public class TestFilter extends AbstractFilter<Integer> {

    public TestFilter(Filter<Integer> inputData) {
        super(inputData);
    }

    @Override
    protected Integer doFilter(int index) {
        if (index == 0) {
            return 0;
        }
        return Math.abs(inputData.get(index) - inputData.get(index - 1));
    }
}
