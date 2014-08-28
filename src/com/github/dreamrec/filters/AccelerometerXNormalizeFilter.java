package com.github.dreamrec.filters;

/**
 *
 */
public class AccelerometerXNormalizeFilter extends AbstractFilter<Integer> {

    private final int DATA_X_NULL = -1088;

    public AccelerometerXNormalizeFilter(Filter<Integer> inputData) {
        super(inputData);
    }

    @Override
    protected Integer doFilter(int index) {
        return (inputData.get(index) - DATA_X_NULL);
    }
}
