package com.nzarudna.shoppinglist.ui;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import java.text.DecimalFormat;

/**
 * Created by nsirobaba on 3/2/18.
 */

public class FormatUtils {

    public static String format(double value) {
        DecimalFormat format = new DecimalFormat("#.###");
        return format.format(value);
    }

    public static Spannable getCrossedText(String text) {
        Spannable formattedText = new SpannableString(text);
        formattedText.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return formattedText;
    }

}
