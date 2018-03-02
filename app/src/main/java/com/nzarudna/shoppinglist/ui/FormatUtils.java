package com.nzarudna.shoppinglist.ui;

import java.text.DecimalFormat;

/**
 * Created by nsirobaba on 3/2/18.
 */

public class FormatUtils {

    public static String format(double value) {
        DecimalFormat format = new DecimalFormat("#.###");
        return format.format(value);
    }

}
