package com.nzarudna.shoppinglist;

import java.util.Date;

/**
 * Created by Nataliia on 23.01.2018.
 */

public class DateUtils {

    public static long getTimeInSeconds(Date date) {
        return date.getTime() / 60;
    }
}
