package com.github.dreamrec.filters;

/**
 *
 */
public class AccelerometerYNormalizeFilter extends AbstractFilter<Integer> {

    private final int DATA_Y_NULL =  1630;

    public AccelerometerYNormalizeFilter(Filter<Integer> inputData) {
        super(inputData);
    }

    @Override
    protected Integer doFilter(int index) {
        return -(inputData.get(index) - DATA_Y_NULL);
    }

}
