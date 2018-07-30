package com.nzarudna.shoppinglist;

import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.inject.Inject;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = AppFirebaseMessagingService.class.getSimpleName();

    @Inject
    UserRepository mUserRepository;

    public AppFirebaseMessagingService() {
        super();

        ShoppingListApplication.getAppComponent().inject(this);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New firebase token " + token);

        User selfUser = mUserRepository.getSelfUser();
        selfUser.setToken(token);

        //TODO send new token to other users
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        try {
//            String token = getAccessToken();
//            return;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        NotificationCompat.Builder notificationBuilder
//                = new NotificationCompat.Builder(this)
//
//
//        NotificationManager notificationManager
//                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, remoteMessage.getNotification());
    }

//    private String getAccessToken() throws IOException {
//
//        InputStream inputStream =
//                getApplicationContext().getResources()
//                        .openRawResource(R.raw.service_account);
//        GoogleCredential credential = GoogleCredential.fromStream(inputStream)
//                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
//        credential.refreshToken();
//        return credential.getAccessToken();
//    }
}
