package com.dream.Filters;

import com.dream.Data.DataStream;
import com.dream.Graph.GraphsViewer;
import com.dream.MainView;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 07.08.14
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
public class FilterOffset extends Filter {
    private GraphsViewer graphsViewer;
    private int offset = 640;

    public FilterOffset(DataStream inputData, GraphsViewer graphsViewer) {
        super(inputData);
        this.graphsViewer = graphsViewer;
    }

    // @Override
    protected int getData(int index) {
        if (index >= size()- 1920) {
            return 0;
        }

        int offsetLevel = graphsViewer.getStartIndex() + offset;
        return inputData.get(index) - inputData.get(offsetLevel);
    }
}
