package com.monsalachai.dndthing.roll;

/**
 * Created by mesalu on 12/17/2017.
 */

public class DieCast {
    private int mDieSize;
    private int mRollValue;

    public DieCast(int size, int cast)
    {
        mDieSize = size;
        mRollValue = cast;
    }

    public int getDieCast() { return mRollValue; }
    public int getDieSize() { return mDieSize; }
}
