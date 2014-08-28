package com.github.dreamrec.filters;

import com.github.dreamrec.Model;
import com.github.dreamrec.filters.AbstractFilter;
import com.github.dreamrec.filters.Filter;

/**
 * Фильтр усредняет данные Акселерометра
 * Вход:  Положение головы и дрожание головы быстрые
 * Выход: Положение головы и дрожание головы усредненные.
 */
public class SlowAccelerometerFilter extends AbstractFilter<Integer> {

    protected final Filter<Integer> accelerometerDynamicFilter;

    public SlowAccelerometerFilter(Filter<Integer> accelerometerPositionFilter,  Filter<Integer> accelerometerDynamicFilter){
        super(accelerometerPositionFilter);
        this.accelerometerDynamicFilter = accelerometerDynamicFilter;
        divider = Model.DIVIDER;
    }

    @Override
    protected Integer doFilter(int index) {

        if(index == 0) return 0;
        int sum = 0;
        int incomingDataIndex = index*divider - 1;
        for (int i = 0; i < divider; i++) {
           // sum += (inputData.get(incomingDataIndex - i)) + accelerometerDynamicFilter.get(incomingDataIndex - i);
            sum += inputData.get(incomingDataIndex - i);
        }
        return sum/divider;
    }
}
