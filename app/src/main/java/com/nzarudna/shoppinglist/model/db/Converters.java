package com.nzarudna.shoppinglist.model.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Room type converters
 */

public class Converters {

    @TypeConverter
    public static Date timestampToDate(Long time) {
        return time != null ? new Date(time) : null;
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date != null ? date.getTime() : null;
    }
}
