package com.nzarudna.shoppinglist.model.persistence.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;
import java.util.UUID;

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

    @TypeConverter
    public static UUID stringToUUID(String uuidStr) {
        return uuidStr != null ? UUID.fromString(uuidStr) : null;
    }

    @TypeConverter
    public static String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
}
