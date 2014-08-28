package com.github.dreamrec;

import com.github.dreamrec.filters.Filter;

import java.util.ArrayList;

/**
 *
 */
public class DataList<T> extends ArrayList<T> implements Filter<T> {
    public int divider() {
        return 1;
    }
}
