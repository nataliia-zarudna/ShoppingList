package com.nzarudna.shoppinglist.ui.openinvitation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;
import com.nzarudna.shoppinglist.utils.UserUtils;

public class OpenInvitationActivity extends SingleFragmentActivity {

    private static final String TAG = OpenInvitationActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleDeepLink();
    }

    private void handleDeepLink() {
        UserUtils.handleDeepLink(getIntent(), new UserUtils.ParseDynamicLinkListener() {
            @Override
            public void onParseDynamicLinkSuccess(String invitorFirebaseToken, String invitorName) {
                OpenInvitationFragment invitationFragment
                        = (OpenInvitationFragment) OpenInvitationActivity.this.mFragment;
                invitationFragment.setInvitationData(invitorFirebaseToken, invitorName);
            }

            @Override
            public void onParseDynamicLinkError() {

            }
        });
    }

    @Override
    protected Fragment getFragment() {
        return OpenInvitationFragment.newInstance();
    }
}
