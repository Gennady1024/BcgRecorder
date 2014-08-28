package com.github.dreamrec.filters;

/**
 * Created by IntelliJ IDEA.
 * User: galafit
 * Date: 26/04/14
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
public class AccelerometerDynamicGraphFilter extends AbstractFilter<Integer> {
    private final int NOISE_LEVEL_MIN = 500;// минимально возможный уровень шума когда человек находится в глубоком сне и не шевелится
    private final int  INT_SCALE = 50;


    public AccelerometerDynamicGraphFilter (Filter accDynamicData) {
        super(accDynamicData);

    }

    @Override
    protected Integer doFilter(int index) {
        int out;
        int dXYZ = (INT_SCALE*(inputData.get(index) - NOISE_LEVEL_MIN) )/ NOISE_LEVEL_MIN;

        if (dXYZ <= 0) out = dXYZ / 2;
        else
        if (dXYZ >= 100) out = dXYZ / 10 + 100;
        else
            out = dXYZ;

        return out;
    }

}
