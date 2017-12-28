package com.monsalachai.dndthing.roll;

/**
 * Created by mesalu on 12/27/2017.
 * Represents attributes of a common die.
 */

public class Die {
    private int die;
    private int coefficient;

    public Die(int c, int d)
    {
        die = d;
        coefficient = c;
    }
    public String toString()
    {
        return String.format("%dd%d", coefficient, die);
    }

    public int getDie() { return die; }
    public int getCoefficient() { return coefficient; }
}
