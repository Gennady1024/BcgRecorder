package com.github.dreamrec.filters;
import com.github.dreamrec.filters.AbstractFilter;
import com.github.dreamrec.filters.Filter;

/**
 * Created by IntelliJ IDEA.
 * User: GENA
 * Date: 06.12.13
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class SlowSleepFilter extends AbstractFilter<Integer> {
    
    protected final Filter<Integer> accelerometerPosition;
    protected final Filter<Integer> accelerometerDynamic;
    protected final Filter<Integer> channel_2;

    public SlowSleepFilter(Filter accelerometerPosition, Filter accelerometerDynamic, Filter channel_1, Filter channel_2) {
        super(channel_1);
        this.accelerometerPosition = accelerometerPosition;
        this.accelerometerDynamic = accelerometerDynamic;
        this.channel_2 = channel_2;
    }

    @Override
    protected Integer doFilter(int index) {
        if (index < 10) {
            return 0;
        }

          return
        //         inputData.get(index - 10) +
        accelerometerPosition.get(index-5)
        + accelerometerDynamic.get(index)*10
                  ;
        //        +channel_2.get(index-3);
    }
}
