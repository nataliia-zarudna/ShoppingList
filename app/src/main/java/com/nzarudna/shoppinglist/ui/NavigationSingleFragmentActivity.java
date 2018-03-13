package com.nzarudna.shoppinglist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.MenuItem;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ui.archivedproductlists.ArchivedListsActivity;
import com.nzarudna.shoppinglist.ui.categories.CategoriesActivity;
import com.nzarudna.shoppinglist.ui.productlists.ProductListsActivity;
import com.nzarudna.shoppinglist.ui.templates.TemplatesActivity;
import com.nzarudna.shoppinglist.ui.units.UnitsActivity;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class NavigationSingleFragmentActivity extends SingleFragmentActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

                Class activityClass = null;
                switch (item.getItemId()) {
                    case R.id.shopping_lists_item:
                        activityClass = ProductListsActivity.class;
                        break;
                    case R.id.templates_item:
                        activityClass = TemplatesActivity.class;
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
