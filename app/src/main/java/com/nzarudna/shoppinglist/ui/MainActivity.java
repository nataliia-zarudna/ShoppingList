package com.nzarudna.shoppinglist.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ui.users.UsersViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BottomNavigationView mBottomMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBottomMenu = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(mBottomMenu, Navigation.findNavController(this, R.id.nav_host_fragment));

        handleDeepLink();
    }

    private void handleDeepLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {

                    if (pendingDynamicLinkData != null) {
                        UsersViewModel usersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
                        usersViewModel.createUserFromInvitationLink(pendingDynamicLinkData);

                        mBottomMenu.setSelectedItemId(R.id.users_item);

                        //TODO add UI with info about added user and ability to share shopping with him
                        Toast.makeText(this, R.string.user_is_added_msg, Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(this, e -> Log.w(TAG, "getDynamicLink:onFailure", e));
    }
}
