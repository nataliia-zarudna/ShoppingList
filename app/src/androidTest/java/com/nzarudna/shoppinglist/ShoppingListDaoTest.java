package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.model.ShoppingList;
import com.nzarudna.shoppinglist.model.db.AppDatabase;
import com.nzarudna.shoppinglist.model.db.ShoppingListDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created by nsirobaba on 12/26/17.
 */

@RunWith(AndroidJUnit4.class)
public class ShoppingListDaoTest {

    private AppDatabase mDatabase;
    private ShoppingListDao mSubjectDao;

    //private Observer<List<ShoppingList>>

    @Before
    public void createDB() {

        Context context = InstrumentationRegistry.getContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mSubjectDao = mDatabase.shoppingListDao();
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

    @Test
    public void createAndRead() throws InterruptedException {

        ShoppingList listToInsert = TestUtils.getShoppingList();

        long listID = createList(listToInsert);
        ShoppingList insertedList = findByIDSync((int) listID);

        assertThat(insertedList.getName(), equalTo(listToInsert.getName()));
    }

    @Test
    public void rename() throws InterruptedException {

        ShoppingList listToUpdate = TestUtils.getShoppingList();
        long listID = createList(listToUpdate);
        listToUpdate.setListID((int) listID);

        listToUpdate.setName("New name");
        mSubjectDao.update(listToUpdate);

        ShoppingList updatedList = findByIDSync(listToUpdate.getListID());

        assertThat(updatedList.getName(), equalTo(listToUpdate.getName()));
    }

    private long createList(ShoppingList listToInsert) {
        return mSubjectDao.insert(listToInsert);
    }

    private ShoppingList findByIDSync(int listID) throws InterruptedException {
        LiveData<ShoppingList> listLiveData = mSubjectDao.findByID((int) listID);

        final CountDownLatch latch = new CountDownLatch(1);
        final ShoppingList[] insertedList = new ShoppingList[1];
        listLiveData.observeForever(new Observer<ShoppingList>() {
            @Override
            public void onChanged(@Nullable ShoppingList shoppingList) {

                insertedList[0] = shoppingList;
                latch.countDown();
            }
        });

        latch.await(3000, TimeUnit.MILLISECONDS);

        return insertedList[0];
    }

}
