package com.nzarudna.shoppinglist.notification;

/**
 * Created by Nataliia on 09.01.2018.
 */

public class NotificationManager {

    private static NotificationManager sInstance;

    public static NotificationManager getInstance() {
        if (sInstance == null) {
            sInstance = new NotificationManager();
        }
        return sInstance;
    }

    private NotificationManager() {}

    public void sendNotification() {

    }
}
