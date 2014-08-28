package com.github.dreamrec;

import com.github.dreamrec.filters.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Model {
    private int xSize; //data points per screen.
    public static final int ACC_MAX_FREQUENCY = Controller.ACC_FREQUENCY;
    public static final int DIVIDER = 120; //frequency divider for slow graphics
    private DataList<Integer> eyeDataList = new DataList<Integer>();   //list with prefiltered incoming data of eye movements
    private DataList<Integer> chanel2DataList = new DataList<Integer>();   //list with prefiltered incoming chanel2 data
    private DataList<Integer> acc1DataList = new DataList<Integer>();   //list with accelerometer 1 chanel data
    private DataList<Integer> acc2DataList = new DataList<Integer>();   //list with accelerometer 1 chanel data
    private DataList<Integer> acc3DataList = new DataList<Integer>();   //list with accelerometer 1 chanel data

    private double frequency = 50; //frequency Hz of the incoming data (for fast graphics)
    private long startTime; //time when data recording was started
    private int fastGraphIndex; //index for the first point on a screen for fast graphics
    private int slowGraphIndex; //index for the first point on a screen for slow graphics

    public DataList<Integer> getEyeDataList() {
        return eyeDataList;
    }

    public DataList<Integer> getAcc1DataList_() {
        return acc1DataList;
    }

    public DataList<Integer> getAcc2DataList_() {
        return acc2DataList;
    }

    public DataList<Integer> getAcc3DataList_() {
        return acc3DataList;
    }

     public DataList<Integer> getCh2DataList() {
        return chanel2DataList;
    }

    private int getAccDivider() {
        return (int) (getFrequency()/ ACC_MAX_FREQUENCY);
    }

    public  Filter<Integer> getAcc1DataList() {
        return new DataStreamAdapter<Integer>() {
            @Override
            protected Integer getData(int index) {
                return acc1DataList.get(index/getAccDivider());
            }
        };
    }

    public  Filter<Integer> getAcc2DataList() {
        return new DataStreamAdapter<Integer>() {
            @Override
            protected Integer getData(int index) {
                return acc2DataList.get(index/getAccDivider());
            }
        };
    }

    public  Filter<Integer> getAcc3DataList() {
        return new DataStreamAdapter<Integer>() {
            @Override
            protected Integer getData(int index) {
                return acc3DataList.get(index/getAccDivider());
            }
        };
    }


    abstract class DataStreamAdapter<Integer> implements Filter<Integer> {
        protected abstract Integer getData(int index);

        public final Integer get(int index) {
            checkIndexBounds(index);
            return getData(index);
        }

        public int divider() {
            return 1;
        }


        private void checkIndexBounds(int index){
            if(index > size() || index < 0 ){
                throw  new IndexOutOfBoundsException("index:  "+index+", available:  "+size());
            }
        }

        @Override
        public int size() {
            return getDataSize();
        }
    }

    public void addEyeData(int data) {
        eyeDataList.add(data);
    }

    public void addCh2Data(int data) {
        chanel2DataList.add(data);
    }

    public void addAcc1Data(int data) {
        acc1DataList.add(data);
    }

    public void addAcc2Data(int data) {
        acc2DataList.add(data);
    }

    public void addAcc3Data(int data) {
        acc3DataList.add(data);
    }

    public double getFrequency() {
        return frequency;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getFastGraphIndex() {
        return fastGraphIndex;
    }

    public int getSlowGraphIndex() {
        return slowGraphIndex;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getXSize() {
        return xSize;
    }

    public void setXSize(int xSize) {
        this.xSize = xSize;
        checkCursorScreenBounds();
    }

    public int getDataSize() {
        return eyeDataList.size();
    }

    public void clear() {
        eyeDataList.clear();
        frequency = 0;
        startTime = 0;
    }

    public int getCursorWidth() {
        return xSize / DIVIDER;
    }

    public int getCursorPosition() {
        return fastGraphIndex / DIVIDER - slowGraphIndex;
    }

    public void moveFastGraph(int newFastGraphIndex) {
        newFastGraphIndex = checkGraphIndexBounds(newFastGraphIndex, getDataSize());
        fastGraphIndex = newFastGraphIndex;
        checkCursorScreenBounds();
    }

    public void moveSlowGraph(int newSlowGraphIndex) {
        newSlowGraphIndex = checkGraphIndexBounds(newSlowGraphIndex, getSlowDataSize());
        slowGraphIndex = newSlowGraphIndex;
        if(getCursorPosition()<0){
            moveCursor(0);
        }
        int cursorMaxPosition = xSize - getCursorWidth() -1;
        if(getCursorPosition() > cursorMaxPosition){
            moveCursor(cursorMaxPosition);
        }
    }



    //correct graph index if it points to invalid data. Should be > 0 and < (graphSize - xSize)
    private int checkGraphIndexBounds(int newIndex, int dataSize) {
        int maxValue = getIndexMax(dataSize);
        newIndex = newIndex < 0 ? 0 : newIndex;
        newIndex = newIndex > maxValue ? maxValue : newIndex;
        return newIndex;
    }

    public int getIndexMax(int dataSize) {
        int maxValue = dataSize - xSize - 1;
        maxValue = maxValue < 0 ? 0 : maxValue;
        return maxValue;
    }

    public int getSlowDataSize(){
        return getDataSize()/DIVIDER;
    }

    public boolean isFastGraphIndexMaximum() {
        return fastGraphIndex == getIndexMax(getDataSize());
    }

    public void setFastGraphIndexMaximum() {
         moveFastGraph(getIndexMax(getDataSize()));
    }

    public void moveCursor(int newCursorPosition) {
        newCursorPosition = checkCursorIndexBounds(newCursorPosition, getSlowDataSize());
        // move cursor to new position, even if this new position is out of the screen
        fastGraphIndex = (slowGraphIndex + newCursorPosition) * DIVIDER;
        checkCursorScreenBounds();
    }

    private void checkCursorScreenBounds() {
        //adjust slowGraphIndex to place cursor at the beginning of the screen
        if (getCursorPosition() < 0) {
            slowGraphIndex += getCursorPosition();
        } else
            //adjust slowGraphIndex to place cursor at the end of the screen
            if (getCursorPosition() > xSize - getCursorWidth() - 1) {
                slowGraphIndex += getCursorPosition() - xSize + getCursorWidth();
            }
    }

    //correct cursor positions if it points to invalid data index: < 0 and > graphSize
    private int checkCursorIndexBounds(int newCursorPosition, int dataSize) {
        int minValue = -slowGraphIndex;
        int maxValue = dataSize - slowGraphIndex - getCursorWidth() - 1;
        maxValue = maxValue < minValue ? minValue : maxValue;
        newCursorPosition = newCursorPosition < minValue ? minValue : newCursorPosition;
        newCursorPosition = newCursorPosition > maxValue ? maxValue : newCursorPosition;
        return newCursorPosition;
    }
}



