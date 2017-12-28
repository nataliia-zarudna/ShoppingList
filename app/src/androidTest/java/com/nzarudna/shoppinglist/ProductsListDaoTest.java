package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.db.AppDatabase;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by nsirobaba on 12/26/17.
 */

@RunWith(AndroidJUnit4.class)
public class ProductsListDaoTest {

    private AppDatabase mDatabase;
    private ProductsListDao mSubjectDao;

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
    public void create() throws InterruptedException {

        ProductsList listToInsert = new ProductsList();

        long listID = createList(listToInsert);

        assertNotEquals(listID, 0l);
    }

    @Test
    public void createAndRead() throws InterruptedException {

        ProductsList listToInsert = new ProductsList();
        listToInsert.setName("Shopping List #1");

        long listID = createList(listToInsert);
        LiveData<ProductsList> listLiveData = mSubjectDao.findByID((int) listID);
        ProductsList insertedList = TestUtils.findByIDSync(listLiveData);

        assertThat(insertedList.getName(), equalTo(listToInsert.getName()));
    }

    @Test
    public void rename() throws InterruptedException {

        ProductsList listToUpdate = new ProductsList();
        long listID = createList(listToUpdate);
        listToUpdate.setListID((int) listID);

        listToUpdate.setName("New name");
        mSubjectDao.update(listToUpdate);

        LiveData<ProductsList> listLiveData = mSubjectDao.findByID(listToUpdate.getListID());
        ProductsList updatedList = TestUtils.findByIDSync(listLiveData);

        assertThat(updatedList.getName(), equalTo(listToUpdate.getName()));
    }

    @Test
    public void findActive() throws InterruptedException {

        List<ProductsList> lists = TestUtils.getProductsLists(3);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setStatus(ProductsList.STATUS_ARCHIVED);
        lists.get(2).setStatus(ProductsList.STATUS_ACTIVE);

        insertLists(lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(2));

        PagedList<ProductsList> foundProductsList = TestUtils.findSync(mSubjectDao.findByStatus(ProductsList.STATUS_ACTIVE));

        TestUtils.assertPagedListEqualsToList(activeLists, foundProductsList);
    }

    private long createList(ProductsList listToInsert) {
        return mSubjectDao.insert(listToInsert);
    }

    private void insertLists(List<ProductsList> listsToInsert) {
        for (ProductsList list : listsToInsert) {
            int insertedID = (int) createList(list);
            list.setListID(insertedID);
        }
    }
}
