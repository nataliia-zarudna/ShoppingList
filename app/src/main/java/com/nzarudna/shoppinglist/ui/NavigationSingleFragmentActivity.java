package com.nzarudna.shoppinglist.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ui.archivedproductlists.ArchivedListsActivity;
import com.nzarudna.shoppinglist.ui.categories.CategoriesActivity;
import com.nzarudna.shoppinglist.ui.productlists.ProductListsActivity;
import com.nzarudna.shoppinglist.ui.templates.TemplatesActivity;
import com.nzarudna.shoppinglist.ui.units.UnitsActivity;
import com.nzarudna.shoppinglist.ui.users.UsersActivity;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class NavigationSingleFragmentActivity extends SingleFragmentActivity {

    private static final String TAG = "NavSingleFragmentActivi";
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        handleFirebase();
//
//        buildLink();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();


                if (item.getItemId() == R.id.templates_item) {
//                    Intent intent = new Intent(Intent.ACTION_SEND);
////                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                    intent.putExtra("sms", "123");
//                    intent.putExtra("smsto", "+38099999999");
//                    intent.setType("text/plain");
//                    startActivityForResult(intent, 1);


                    Intent sendIntent = new Intent(Intent.ACTION_SEND, Uri.parse("sms:+380989606919"));
//                    sendIntent.setAction(I);
                    sendIntent.setData(Uri.parse("sms:+380989606919"));
                    sendIntent.putExtra("sms_body", "ABC");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);



                    return true;
                }


                Class activityClass = null;
                switch (item.getItemId()) {
                    case R.id.shopping_lists_item:
                        activityClass = ProductListsActivity.class;
                        break;
                    case R.id.templates_item:
                        activityClass = TemplatesActivity.class;
                        break;
                    case R.id.users_item:
                        activityClass = UsersActivity.class;
                        break;
                    case R.id.categories_item:
                        activityClass = CategoriesActivity.class;
                        break;
                    case R.id.units_item:
                        activityClass = UnitsActivity.class;
                        break;
                    case R.id.archive_item:
                        activityClass = ArchivedListsActivity.class;
                        break;
                }
                if (activityClass != null) {
                    Intent intent = new Intent(NavigationSingleFragmentActivity.this, activityClass);
                    startActivity(intent);
                }

                return true;
            }
        });
    }

    private void handleFirebase() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
