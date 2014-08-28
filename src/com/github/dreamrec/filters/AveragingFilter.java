package com.github.dreamrec.filters;

/**
 *
 */
public class AveragingFilter extends AbstractFilter<Integer> {

    public AveragingFilter(Filter<Integer> inputData, int divider) {
        super(inputData);
        this.divider = divider;
    }

    @Override
    protected Integer doFilter(int index) {
        if(index == 0) return 0;
        int sum = 0;
        int incomingDataIndex = index*divider - 1;
        for (int i = 0; i < divider; i++) {
            sum += Math.abs(inputData.get(incomingDataIndex - i));
        }
        return sum/divider;
    }
}
