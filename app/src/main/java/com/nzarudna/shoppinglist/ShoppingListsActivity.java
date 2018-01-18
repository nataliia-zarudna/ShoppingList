package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductsListDao;
import com.nzarudna.shoppinglist.persistence.UserDao;
import com.nzarudna.shoppinglist.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ProductsList;
import com.nzarudna.shoppinglist.user.User;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ShoppingListsActivity extends SingleFragmentActivity {

    private static final String LOG = "ShoppingListsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, "db1").build();
        final User userInit = new User();
        userInit.setName("abc");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        new AsyncTask<User, Void, Long>() {


            @Override
            protected Long doInBackground(User... user) {

                long userID = database.userDao().insert(user[0]);

                userInit.setUserID((int) userID);

                return userID;
            }

            @Override
            protected void onPostExecute(Long prodID) {
                super.onPostExecute(prodID);

                Log.d(LOG, "onPostExecute, prodID " + prodID);

                LiveData<User> productLiveData = database.userDao().findByID((int) prodID.longValue());
                productLiveData.observe(ShoppingListsActivity.this, new Observer<User>() {
                    @Override
                    public void onChanged(@Nullable User user) {

                        Log.d(LOG, "onChanged, user " + user);
                    }
                });

                countDownLatch.countDown();

                Log.d(LOG, "onPostExecute, productLiveData " + productLiveData);

            }
        }.execute(userInit);

        try {
            countDownLatch.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                Log.d(LOG, "update");

                userInit.setName("new name");
                database.userDao().update(userInit);

                return null;
            }
        }.execute();
    }
}
