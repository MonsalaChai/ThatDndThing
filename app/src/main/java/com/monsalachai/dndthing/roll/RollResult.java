package com.monsalachai.dndthing.roll;

import android.util.Log;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by mesalu on 12/17/2017.
 */

public class RollResult implements Iterable<DieCast> {
    private Vector<DieCast> mCasts;
    private int mInitial;

    public RollResult(){
        mCasts = new Vector<>();
        mInitial = 0;
    }

    public RollResult(int initial) {
        mCasts = new Vector<>();
        mInitial = initial;
        Log.i("RR", "Initialized Roll Result with initial value:  " + initial);
    }

    public Iterator<DieCast> iterator(){
        return mCasts.iterator();
    }

    public void addRoll(int size, int roll)
    {
        Log.i("RR", "Adding diecast to result: d" + size + "-> " + roll);
        mCasts.add(new DieCast(size, roll));
    }

    public int getResult()
    {
        Log.i("RR", "Using mInitial with value: " + mInitial);
        int sum = mInitial;
        for (DieCast dc : mCasts)
            sum += dc.getDieCast();
        return sum;
    }

    public int getResult(int idx)
    {
        return mCasts.get(idx).getDieCast();
    }

    public int getInitial() { return mInitial; }
}
