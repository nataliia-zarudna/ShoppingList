package com.nzarudna.shoppinglist.utils;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

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
                                           DynamicLinkListener dynamicLinkListener) {

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

                        dynamicLinkListener.onBuildDynamicLinkSuccess(shortLink);

                        Log.d(TAG, "shortLink " + shortLink);
                    } else {
                        dynamicLinkListener.onBuildDynamicLinkError();
                    }
                });
    }

    public interface DynamicLinkListener {
        void onBuildDynamicLinkSuccess(Uri shortLink);

        void onBuildDynamicLinkError();
    }
}
