package com.github.dreamrec.filters;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 */
public abstract class FrequencyDividingPreFilter {
    private Queue<Integer> filteredData = new LinkedList<Integer>();
    private int divider;
    private int counter;

    public FrequencyDividingPreFilter(int divider) {
        this.divider = divider;
    }

    public void add(Integer value) {
        counter++;
        filteredData.offer(value);
        if (counter == divider) {
            int sum = 0;
            for (int i = 0; i < divider; i++) {
                sum += filteredData.poll();
            }
            notifyListeners(sum / divider);
            counter = 0;
        }
    }

    public abstract void notifyListeners(int filteredValue);
}
