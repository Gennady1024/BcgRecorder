package com.github.dreamrec.filters;

/**
 *
 */
public class AccelerometerZNormalizeFilter extends AbstractFilter<Integer> {

    private final int DATA_Z_NULL =  4500;

    public AccelerometerZNormalizeFilter(Filter<Integer> inputData) {
        super(inputData);
    }

    @Override
    protected Integer doFilter(int index) {
        return -(inputData.get(index) + DATA_Z_NULL);
    }
}
