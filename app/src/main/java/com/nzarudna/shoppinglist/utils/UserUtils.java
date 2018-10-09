package com.nzarudna.shoppinglist.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.concurrent.Executor;

public class UserUtils {

    private static String TAG = UserUtils.class.getSimpleName();

    private static final String INVITATION_LINK = "https://familyshopping.ua/invite";
    private static final String DYNAMIC_LINK_DOMAIN = "familyshopping.page.link";

    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_INVITOR_NAME = "invitor_name";

    public static void buildInvitationLink(Executor executor,
                                           String invitorFirebaseToken,
                                           String invitorName,
                                           BuildDynamicLinkListener listener) {

        Uri invitationUri = new Uri.Builder()
                .appendEncodedPath(INVITATION_LINK)
                .appendQueryParameter(PARAM_TOKEN, invitorFirebaseToken)
                .appendQueryParameter(PARAM_INVITOR_NAME, invitorName)
                .build();

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(invitationUri)
                .setDynamicLinkDomain(DYNAMIC_LINK_DOMAIN)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(executor, task -> {
                    if (task.isSuccessful()) {
                        // Short link created
                        Uri shortLink = task.getResult().getShortLink();

                        listener.onBuildDynamicLinkSuccess(shortLink);

                        Log.d(TAG, "shortLink " + shortLink);
                    } else {
                        listener.onBuildDynamicLinkError();
                    }
                });
    }

    public static void handleDeepLink(Intent intent, ParseDynamicLinkListener listener) {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(pendingDynamicLinkData -> {

                    Uri deepLink = pendingDynamicLinkData.getLink();
                    if (deepLink != null) {

                        String invitorFirebaseToken = deepLink.getQueryParameter(PARAM_TOKEN);
                        String invitorName = deepLink.getQueryParameter(PARAM_INVITOR_NAME);

                        listener.onParseDynamicLinkSuccess(invitorFirebaseToken, invitorName);
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get firebase deep link", e);
                    listener.onParseDynamicLinkError();
                });
    }

    public interface BuildDynamicLinkListener {
        void onBuildDynamicLinkSuccess(Uri shortLink);

        void onBuildDynamicLinkError();
    }

    public interface ParseDynamicLinkListener {
        void onParseDynamicLinkSuccess(String invitorFirebaseToken, String invitorName);

        void onParseDynamicLinkError();
    }
}
