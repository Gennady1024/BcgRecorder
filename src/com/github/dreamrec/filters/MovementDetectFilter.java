package com.github.dreamrec.filters;

/**
 *   Detect if the patient moves or does not move
 *   1 - patient lies and moves;
 *   0 - patient lies and do not moves;
 */
public class MovementDetectFilter extends AbstractFilter<Integer> {
    private final int NOISE_LEVEL_MIN = 500; // минимально возможный уровень шума когда человек находится в глубоком сне и не шевелится
    private final int NOISE_LEVEL = 3*NOISE_LEVEL_MIN;
    

    public MovementDetectFilter(Filter accDynamicData) {
        super(accDynamicData);
    }



    @Override
    protected Integer doFilter(int index) {
        
        
        if (inputData.get(index) > NOISE_LEVEL) {
            return 1;   // patient lies and moves;
        }

        return 0;   // patient lies and do not moves;

    }
    
}
