package com.monsalachai.dndthing.entry;


import android.support.annotation.NonNull;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by mesalu on 12/17/17.
 * This class aids in establishing what is rollable and storing the results of a roll.
 * It can also handle compound roll mechanics. Ex: "1d6 + 2 and 2d4 + 3"
 */

public class RollEntry extends Entry {
    public interface OnRollListener {
        void onRoll();
    }


    // Todo: tweak  this class to support parsing from json and performing
    // todo: the actual RNG-roll itself.
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
    public class RollResult implements Iterable<DieCast> {

        private Vector<DieCast> mDieCasts;
        RollResult() {
            mDieCasts = new Vector<>();
        }

        @Override
        @NonNull
        public Iterator<DieCast> iterator()
        {
            return mDieCasts.iterator();
        }
    }

}
