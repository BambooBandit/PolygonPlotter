package com.mygdx.polyplot;

import java.util.Random;

public class Utils
{
    private static Utils utils;
    private Random random;

    private Utils()
    {
        random = new Random();
    }

    public static Utils get()
    {
        if(utils == null)
            utils = new Utils();
        return utils;
    }

    public static float randomFloat(int min, int max)
    {
        return min + get().random.nextFloat() * (max - min);
    }
}
