package com.github.dreamrec.filters;

/**
 *   Detect if the patient lies or does not lie.
 *   0 - lie
 *   1 - dont lie
 *
 */
public class PositionDetectFilter extends AbstractFilter<Integer> {

    public PositionDetectFilter (Filter inputData) {
        super(inputData);
    }

    @Override
    protected Integer doFilter(int index) {
        int DATA_90 = 16000;  // Максимальная абсолютная величина данных
        int DATA_45 = DATA_90 *3363/4756; // sin(45) = sqrt(2)/2 ~= 3363/4756

        if (inputData.get(index) > DATA_45){
            return 1; // patient dont lies
        }

        return 0; // patient lies
    }

}
