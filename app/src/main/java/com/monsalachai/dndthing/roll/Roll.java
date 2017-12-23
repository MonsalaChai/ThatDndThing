package com.monsalachai.dndthing.roll;

import android.util.Log;
import java.util.Random;

/**
 * Created by mesalu on 12/17/2017.
 */

public class Roll {
    final static Random rng = new Random();
    public static RollResult makeRoll(int c, int d, int x)
    {
        RollResult result = new RollResult(x);
        for (int i = 0; i < c; i++) {
            int nextInt = rng.nextInt(d) + 1;
            Log.i("Roll", String.format("The d%d rolled %d", d, nextInt));
            result.addRoll(d, nextInt);
        }
        return result;
    }

    public static RollResult makeRoll(int c, int d, RollResult initial) {
        for (int i = 0; i < c; i++) {
            int nextInt = rng.nextInt(d) + 1;
            Log.i("Roll", String.format("The d%d rolled %d", d, nextInt));
            initial.addRoll(d, nextInt);
        }
        return initial;
    }
}
