package com.dream;

import com.dream.Data.DataList;
import com.dream.Data.DataStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 10.05.14
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class ApparatModel {

    private static final int ACC_FREQUENCY = 10;
    private int frequency = 50;

    private DataList chanel_1_data = new DataList();   //list with prefiltered incoming data of eye movements
    private DataList chanel_2_data = new DataList();   //list with prefiltered incoming chanel2 data
    private DataList acc_1_data = new DataList();   //list with accelerometer 1 chanel data
    private DataList acc_2_data = new DataList();   //list with accelerometer 2 chanel data
    private DataList acc_3_data = new DataList();   //list with accelerometer 3 chanel data

    private DataList sleep_patterns = new DataList();
    private SaccadeDetector saccadeDetector = new SaccadeDetector(chanel_1_data);


    private long startTime; //time when data recording was started

    private int movementLimit = 2000;
    private final double MOVEMENT_LIMIT_CHANGE = 1.05;

    private final int FALLING_ASLEEP_TIME = 30; // seconds
    private int sleepTimer = 0;

    private final int SIN_90 = 1800 / 4;  // if (F(X,Y) = 4) arc_F(X,Y) = 180 Grad
    private final int ACC_X_NULL = -1088;
    private final int ACC_Y_NULL = 1630;
    private final int ACC_Z_NULL = 4500;
    int Z_mod = 90;

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void movementLimitUp() {
        movementLimit *= MOVEMENT_LIMIT_CHANGE;
        sleepTimer = 0;
        for (int i = 0; i < getDataSize(); i++) {
        }
    }

    public void movementLimitDown() {
        movementLimit /= MOVEMENT_LIMIT_CHANGE;
        sleepTimer = 0;
        for (int i = 0; i < getDataSize(); i++) {
        }
    }

    private boolean isStand(int index) {
        if (getAccPosition(index) == DataStream.STAND) {
            return true;
        }
        return false;
    }

    public DataStream getEyeData() {
        return new DataStreamAdapter() {
            @Override
            protected int getData(int index) {
                int buffer = 5;
                if (index < buffer) {
                    return 0;
                }
                int sum = 0;
                for (int i = 0; i < buffer; i++) {
                    sum += chanel_1_data.get(index - i);
                }
                return sum / buffer;
            }
        };
    }


    private boolean isSleep(int index) {
//        if(index < 18000) {   //выкидиваем первые полчаса от начала записи
//            return false;
//        }
        if (isStand(index)) {
            sleepTimer = FALLING_ASLEEP_TIME * frequency;
        }
        if (isMoved(index)) {
            sleepTimer = Math.max(sleepTimer, FALLING_ASLEEP_TIME * frequency);
        }

        boolean isSleep = true;

        if ((sleepTimer > 0)) {
            isSleep = false;
            sleepTimer--;
        }

        return isSleep;
    }



    /**
     * Определяем величину пропорциональную движению головы
     * (дельта между текущим и предыдущим значением сигналов акселерометра).
     * Суммируем амплитуды движений по трем осям.
     * За ноль принят шумовой уровень.
     */

    private int getAccMovement(int index) {
        int step = 2;
        int dX, dY, dZ;
        int maxX = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        if (index > step) {
            for (int i = 0; i <= step; i++) {
                maxX = Math.max(maxX, getNormalizedDataAcc1(index - i));
                minX = Math.min(minX, getNormalizedDataAcc1(index - i));
                maxY = Math.max(maxY, getNormalizedDataAcc2(index - i));
                minY = Math.min(minY, getNormalizedDataAcc2(index - i));
                maxZ = Math.max(maxZ, getNormalizedDataAcc3(index - i));
                minZ = Math.min(minZ, getNormalizedDataAcc3(index - i));
            }
            dX = maxX - minX;
            dY = maxY - minY;
            dZ = maxZ - minZ;
        } else {
            dX = 0;
            dY = 0;
            dZ = 0;
        }

        int dXYZ = Math.abs(dX) + Math.abs(dY) + Math.abs(dZ);
        return dXYZ;
    }


    public DataStream getSleepStream() {
        return sleep_patterns;
    }

    public DataStream getAccMovementStream() {
        return new DataStreamAdapter() {
            @Override
            protected int getData(int index) {
                return getAccMovement(index);
            }
        };
    }


    public DataStream getAccPositionStream() {
        return new DataStreamAdapter() {
            @Override
            protected int getData(int index) {
                return getAccPosition(index);
            }
        };
    }

    public DataStream getNotSleepEventsStream() {
        return new DataStreamAdapter() {
            @Override
            protected int getData(int index) {
                return sleep_patterns.get(index);
            }
        };
    }


    private int getAccPosition(int index) {
        final int DATA_SIN_90 = 16000;

        final int DATA_SIN_45 = DATA_SIN_90 * 3363 / 4756; // sin(45) = sqrt(2)/2 ~= 3363/4756

        final int X_data_mod = DATA_SIN_90, Y_data_mod = DATA_SIN_90, Z_data_mod = DATA_SIN_90;
        final int X_mod = SIN_90, Y_mod = SIN_90;


        int XY_angle;
        int data_X = getNormalizedDataAcc1(index);
        int data_Y = getNormalizedDataAcc2(index);
        int data_Z = getNormalizedDataAcc3(index);

        if (data_Z > DATA_SIN_45) {   // Если человек не лежит
            return DataStream.STAND;
        }

        double Z = (double) data_Z / Z_data_mod;

        double ZZ = Z * Z;
        double sec_Z = 1 + ZZ * 0.43 + ZZ * ZZ * 0.77;

        double double_X = ((double) data_X / X_data_mod) * sec_Z;
        double double_Y = ((double) data_Y / Y_data_mod) * sec_Z;
        int X = (int) (double_X * X_mod);
        int Y = (int) (double_Y * Y_mod);

        XY_angle = angle(X, Y);

        return XY_angle;
    }

    private int angle(int X, int Y) {
        int XY_angle = 0;
        // XY_angle =  1 + sin(x) - cos(x); if (X >= 0 && Y >=0)
        // XY_angle =  3 - sin(x) - cos(x); if (X >= 0 && Y < 0)
        // XY_angle = -1 + sin(x) + cos(x); if (X <  0 && Y >=0)
        // XY_angle = -3 - sin(x) + cos(x); if (X <  0 && Y < 0)

        if (X >= 0 && Y >= 0) {
            XY_angle = SIN_90 + X - Y;
        } else if (X >= 0 && Y < 0) {
            XY_angle = 3 * SIN_90 - X - Y;
        } else if (X < 0 && Y >= 0) {
            XY_angle = -SIN_90 + X + Y;
        } else if (X < 0 && Y < 0) {
            XY_angle = -3 * SIN_90 - X + Y;
        }

        return XY_angle / 10;
    }


    private boolean isMoved(int index) {
        if (getAccMovement(index) > movementLimit) {
            return true;
        }
        return false;
    }


    public int getAccDivider() {
        return frequency / ACC_FREQUENCY;
    }

    private int getNormalizedDataAcc1(int index) {
        int accIndex = index / getAccDivider();
        return (acc_1_data.get(accIndex) - ACC_X_NULL);
    }


    private int getNormalizedDataAcc2(int index) {
        int accIndex = index / getAccDivider();
        return -(acc_2_data.get(accIndex) - ACC_Y_NULL);
    }


    private int getNormalizedDataAcc3(int index) {
        int accIndex = index / getAccDivider();
        return -(acc_3_data.get(accIndex) + ACC_Z_NULL);
    }


    public int getDataSize() {
        int accDivider = getAccDivider();
        int size = chanel_1_data.size();
        size = Math.min(size, chanel_2_data.size());
        size = Math.min(size, acc_1_data.size() * accDivider);
        size = Math.min(size, acc_2_data.size() * accDivider);
        size = Math.min(size, acc_3_data.size() * accDivider);

        return size;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void clear() {
        startTime = 0;
        if(getDataSize() > 0) {
            chanel_1_data.clear();
            chanel_2_data.clear();
            acc_1_data.clear();
            acc_2_data.clear();
            acc_3_data.clear();

            sleep_patterns.clear();
        }
    }


    public DataStream getCh1DataStream() {
        return chanel_1_data;
    }

    public DataStream getCh2DataStream() {
        return chanel_2_data;
    }

    public DataStream getAcc1DataStream() {
        return acc_1_data;
    }

    public DataStream getAcc2DataStream() {
        return acc_2_data;
    }

    public DataStream getAcc3DataStream() {
        return acc_3_data;
    }


    private void addData(int data, DataList dataStore) {
        int size = getDataSize();
        dataStore.add(data);
        int sizeNew = getDataSize();
        if (sizeNew > size) {
            sleep_patterns.add(0);
            if (saccadeDetector.isSaccadeDetected(sizeNew - 1)) {
                int saccadeIndex = saccadeDetector.getSaccadeBeginIndex();
                sleep_patterns.set(saccadeIndex, saccadeDetector.getSaccadeValue());
            }

      /*     if (getAccPosition(sizeNew - 1) == DataStream.STAND) {  // person is standing
                sleep_patterns.add(DataStream.STAND);
            } else if (isMoved(sizeNew - 1)) { // person is moving
                sleep_patterns.add(DataStream.MOVE);
            }
            else {
               sleep_patterns.add(0);
               if (saccadeDetector.isSaccadeDetected(sizeNew - 1)) {
                   int saccadeIndex = saccadeDetector.getSaccadeBeginIndex();
                   sleep_patterns.set(saccadeIndex, saccadeDetector.getSaccadeValue());
               }
            } */
        }
    }

    public void addCh1Data(int data) {
        addData(data, chanel_1_data);
    }

    public void addCh2Data(int data) {
        addData(data, chanel_2_data);
    }

    public void addAcc1Data(int data) {
        addData(data, acc_1_data);
    }


    public void addAcc2Data(int data) {
        addData(data, acc_2_data);

    }

    public void addAcc3Data(int data) {
        addData(data, acc_3_data);
    }


    abstract class DataStreamAdapter implements DataStream {
        protected abstract int getData(int index);

        public final int get(int index) {
            checkIndexBounds(index);
            return getData(index);
        }


        private void checkIndexBounds(int index) {
            if (index > size() || index < 0) {
                throw new IndexOutOfBoundsException("index:  " + index + ", available:  " + size());
            }
        }

        @Override
        public int size() {
            return getDataSize();
        }
    }


    /**
     * Saccade (step):
     * 1) MAX_LEVEL > abs(derivation) > SACCADE_LEVEL
     * 2) derivation don't change sign
     * 3) saccade duration > 40 msec (SACCADE_WIDTH_MIN_MSEC)
     * 4) before and after saccade eyes are in rest (when abs(derivation) < NOISE_LEVEL) > 100 msec (REST_PERIOD_MIN_MSEC)
     */
    class SaccadeDetector {
        int NOISE_LEVEL = 250;
        int SACCADE_LEVEL = 500;
        int MAX_LEVEL = 2500;
        int SACCADE_WIDTH_MIN_MSEC = 40;
        int REST_PERIOD_MIN_MSEC = 100;

        private DataStream inputData;
        private int saccadeBeginIndex = 0;
        private int saccadeEndIndex = 0;
        private int saccadeValue = 0;  // eye way during saccade
        private int saccadeSign = 0;

        private boolean isUnderDetection = false;

        SaccadeDetector(DataStream inputData) {
            this.inputData = inputData;
        }

        private void resetSaccade() {
            saccadeBeginIndex = 0;
            saccadeEndIndex = 0;
            saccadeValue = 0;
            saccadeSign = 0;
            isUnderDetection = false;
        }

        private int getDerivative(int index) {
            if (index < 1) {
                return 0;
            }
            return inputData.get(index) - inputData.get(index - 1);
        }

        private boolean hasRestPeriodBefore(int index) {
            int restPeriodPoints = (REST_PERIOD_MIN_MSEC * frequency) / 1000;
            if ((index - 1) <= restPeriodPoints) {
                return false;
            }
            for (int i = 1; i <= restPeriodPoints; i++) {
                if (Math.abs(getDerivative(index - i)) > NOISE_LEVEL) {
                    return false;
                }
            }
            return true;
        }

        private boolean isSccadeDerivative(int index) {
            int derivative = getDerivative(index);
            if ((Math.abs(derivative) > SACCADE_LEVEL) && (Math.abs(derivative) < MAX_LEVEL) && isEqualSign(derivative, saccadeSign)) {
              /*  DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                int  point_distance_msec = 1000/frequency;
                long time = startTime + index * point_distance_msec;
                String timeStamp = dateFormat.format(new Date(time));
                System.out.println("Sacade level: "+derivative+ "   time: "+timeStamp);  */
                return true;
            }
            return false;
        }

        private boolean isRestDerivative(int index) {
            if (Math.abs(getDerivative(index)) < NOISE_LEVEL) {
                return true;
            }
            return false;
        }


        public boolean isSaccadeDetected_(int index) {
              boolean result = false;
              if (isSccadeDerivative(index)) {
                  saccadeBeginIndex = index;
                  saccadeEndIndex = index;
                  saccadeValue = 500;
                  result = true;
              }
            return result;
        }


        public boolean isSaccadeDetected(int index) {
            if (!isUnderDetection) {    // saccade beginning
                if (isSccadeDerivative(index) && hasRestPeriodBefore(index)) {
                    resetSaccade();
                    int derivative = getDerivative(index);
                    isUnderDetection = true;
                    saccadeBeginIndex = index;
                    saccadeSign = getSign(derivative);
                    saccadeValue += derivative;
                }
            } else {
                if (saccadeEndIndex == 0) {  // saccade in process
                    if (isSccadeDerivative(index)) {
                        saccadeValue += getDerivative(index);
                    } else {
                        int saccadeMinWidthPoints = (SACCADE_WIDTH_MIN_MSEC * frequency) / 1000;
                        if ((index - saccadeBeginIndex) >= saccadeMinWidthPoints) {  // check saccade width
                            saccadeEndIndex = index;
                        } else {
                            resetSaccade();
                        }

                    }
                } else {   // saccade finished
                    if (!isRestDerivative(index)) {
                        resetSaccade(); // check reset period after saccade
                    } else {
                        int restPeriodPoints = (REST_PERIOD_MIN_MSEC * frequency) / 1000;
                        if ((index - saccadeEndIndex) == restPeriodPoints) {
                            isUnderDetection = false;
                            saccadeSign = 0;
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public int getSaccadeBeginIndex() {
            return saccadeBeginIndex;
        }

        public int getSaccadeEndIndex() {
            return saccadeEndIndex;
        }

        public int getSaccadeValue() {
            return saccadeValue/8;
        }


        private int getSign(int a) {
            if (a >= 0) {
                return 1;
            }
            return -1;
        }

        protected boolean isEqualSign(int a, int b) {
            if ((a >= 0) && (b >= 0)) {
                return true;
            }

            if ((a <= 0) && (b <= 0)) {
                return true;
            }

            return false;
        }


    }

}

