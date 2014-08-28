package com.github.dreamrec.filters;

/**
 *  Определяем величину пропорциональную движению головы
 *  (дельта между текущим и предыдущим значением сигналов акселерометра).
 *  Суммируем амплитуды движений по трем осям.
 *  За ноль принят шумовой уровень.
 */
public class AccelerometerDynamicFilter extends AbstractFilter<Integer> {

    protected final Filter<Integer> yData;
    protected final Filter<Integer> zData;

    private int data_X_previous = 0;
    private int data_Y_previous = 0;
    private int data_Z_previous = 0;


    public AccelerometerDynamicFilter(Filter xData, Filter yData, Filter zData) {
        super(xData);
        this.yData = yData;
        this.zData = zData;
    }

    @Override
    protected Integer doFilter(int index) {

        int data_Z = zData.get(index);
        int data_X =  inputData.get(index);
        int data_Y = yData.get(index);
        int dX, dY, dZ;


        dX =  data_X - data_X_previous; data_X_previous = data_X; if (dX < 0)dX = -dX;
        dY =  data_Y - data_Y_previous; data_Y_previous = data_Y; if (dY < 0)dY = -dY;
        dZ =  data_Z - data_Z_previous; data_Z_previous = data_Z; if (dZ < 0)dZ = -dZ;

        return (dX + dY + dZ);
    }
}
