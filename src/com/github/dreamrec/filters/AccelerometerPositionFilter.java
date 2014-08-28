package com.github.dreamrec.filters;

/**
 *  Вычисляет угол поворота головы. Модуль с акселерометром расположен на макушке.
 *  Ноль - когда лежим на спине, лищо вверх.
 *  Величина меняется от -180 градусов до +180.
 *
 *  to do: сделать возможность  располагать модуль с акселерометром также на лбу.
 */
public class AccelerometerPositionFilter extends AbstractFilter<Integer> {

    protected int divider = 1;
    protected final Filter<Integer> yData;
    protected final Filter<Integer> zData;
    private final int DATA_SIN_90 = 16000;
    private       int X_data_mod  = DATA_SIN_90, Y_data_mod = DATA_SIN_90, Z_data_mod = DATA_SIN_90;
    private final int data_Sin_45 = DATA_SIN_90 *3363/4756; // sin(45) = sqrt(2)/2 ~= 3363/4756

    private final int Sin_90 = 1800/4;  // if (F(X,Y) = 4) arc_F(X,Y) = 180 Grad
    private int X_mod = Sin_90, Y_mod = Sin_90, Z_mod = 90;

    private int data_X_Max = 0, data_X_Min = 0;
    private int data_Y_Max = 0, data_Y_Min = 0;
    private int data_Z_Max = 0, data_Z_Min = 0;

    public AccelerometerPositionFilter(Filter xData, Filter yData, Filter zData) {
        super(xData);
        this.yData = yData;
        this.zData = zData;
    }


    @Override
    protected Integer doFilter(int index) {
        int XY_angle;
        int data_Z = zData.get(index);
        int data_X = inputData.get(index);
        int data_Y = yData.get(index);

        if (data_Z > data_Sin_45){   // Если человек не лежит
            Z_mod *= -1; return Z_mod;
        }

        double Z = (double)data_Z / Z_data_mod;

        double ZZ = Z*Z;
        double sec_Z = 1 + ZZ*0.43 + ZZ*ZZ*0.77;

        double double_X = ((double)data_X / X_data_mod)* sec_Z;
        double double_Y = ((double)data_Y / Y_data_mod)* sec_Z;
        int X = (int)(double_X * X_mod);
        int Y = (int)(double_Y * Y_mod);

        XY_angle = angle(X, Y);

        return XY_angle;
    }

    protected int angle(int X, int Y) {
        int XY_angle = 0;
        // XY_angle =  1 + sin(x) - cos(x); if (X >= 0 && Y >=0)
        // XY_angle =  3 - sin(x) - cos(x); if (X >= 0 && Y < 0)
        // XY_angle = -1 + sin(x) + cos(x); if (X <  0 && Y >=0)
        // XY_angle = -3 - sin(x) + cos(x); if (X <  0 && Y < 0)

        if (X >= 0 && Y >=0) { XY_angle =     Sin_90 + X - Y; }
        else if (X >= 0 && Y < 0) { XY_angle =  3* Sin_90 - X - Y; }

        else if (X <  0 && Y >=0) { XY_angle =    -Sin_90 + X + Y; }
        else if (X <  0 && Y < 0) { XY_angle = -3* Sin_90 - X + Y; }

        return XY_angle/10;
    }


    protected void data_Max_Min(int X, int Y, int Z) {

        if (data_X_Max < X) data_X_Max = X;  if (data_X_Min > X) data_X_Min = X;
        if (data_Y_Max < Y) data_Y_Max = Y;  if (data_Y_Min > Y) data_Y_Min = Y;
        if (data_Z_Max < Z) data_Z_Max = Z;  if (data_Z_Min > Z) data_Z_Min = Z;
    }
}
