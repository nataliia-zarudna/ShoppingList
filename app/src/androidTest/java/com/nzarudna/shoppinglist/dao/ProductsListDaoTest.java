package com.nzarudna.shoppinglist.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.TestUtils;
import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;
import com.nzarudna.shoppinglist.model.dao.UserDao;
import com.nzarudna.shoppinglist.model.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test Product List Dao methods
 */

@RunWith(AndroidJUnit4.class)
public class ProductsListDaoTest {

    private AppDatabase mDatabase;
    private ProductsListDao mSubjectDao;
    private int mUserID_1;
    private int mUserID_2;

    @Before
    public void createDB() {

        Context context = InstrumentationRegistry.getContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mSubjectDao = mDatabase.productsListDao();

        UserDao userDao = mDatabase.userDao();
        mUserID_1 = TestUtils.insertUser(userDao);
        mUserID_2 = TestUtils.insertUser(userDao);
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

    @Test
    public void create() throws InterruptedException {

        ProductsList listToInsert = createProductListWithNotNullParams();
        long listID = insertList(listToInsert);

        assertThat(listID, not(0l));
    }

    @Test
    public void createAndRead() throws InterruptedException {

        ProductsList list = new ProductsList();
        list.setName("new name");
        list.setAssignedID(mUserID_1);
        list.setModifiedAt(new Date());
        list.setCreatedBy(mUserID_1);
        list.setModifiedBy(mUserID_2);

        long listID = insertList(list);
        list.setListID(listID);

        LiveData<ProductsList> listLiveData = mSubjectDao.findByID(listID);
        ProductsList insertedList = TestUtils.findByIDSync(listLiveData);

        assertThat(insertedList, equalTo(list));
    }

    @Test
    public void createAndReadSync() throws InterruptedException {

        ProductsList list = new ProductsList();
        list.setName("new name");
        list.setAssignedID(mUserID_1);
        list.setModifiedAt(new Date());
        list.setCreatedBy(mUserID_1);
        list.setModifiedBy(mUserID_2);

        long listID = insertList(list);
        list.setListID(listID);

        ProductsList insertedList = mSubjectDao.findByIDSync(listID);

        assertThat(insertedList, equalTo(list));
    }

    @Test
    public void createWithActiveStatus() throws InterruptedException {

        ProductsList list = insertList();

        assertThat(list.getStatus(), is(ProductsList.STATUS_ACTIVE));
    }

    @Test
    public void createWithCurrentCreatedAtTime() throws InterruptedException {

        ProductsList list = insertList();

        TestUtils.assertDateEqualsToSeconds(new Date(), list.getCreatedAt());
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedExceptionOnCreateWithNullName() throws InterruptedException {

        ProductsList list = createProductListWithNotNullParams();
        list.setName(null);

        mSubjectDao.insert(list);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedExceptionOnCreateWithNullCreatedBy() throws InterruptedException {

        ProductsList list = createProductListWithNotNullParams();
        list.setCreatedBy(0);

        mSubjectDao.insert(list);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedExceptionOnCreateWithNullCreatedAt() throws InterruptedException {

        ProductsList list = createProductListWithNotNullParams();
        list.setCreatedAt(null);

        mSubjectDao.insert(list);
    }

    @Test
    public void update() throws InterruptedException {

        ProductsList list = insertList();

        list.setName("New name");
        list.setStatus(ProductsList.STATUS_ARCHIVED);

        mSubjectDao.update(list);

        ProductsList updatedList = findListByID(list.getListID());

        assertThat(updatedList, equalTo(list));
    }

    @Test
    public void delete() throws InterruptedException {

        ProductsList list = insertList();

        mSubjectDao.delete(list);

        ProductsList foundList = findListByID(list.getListID());

        assertEquals(foundList, null);
    }

    @Test
    public void findActiveSortByName() throws InterruptedException {

        List<ProductsList> lists = createProductsLists(4);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setName("#1");

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setName("#3");

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setName("#2");

        insertLists(lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));
        activeLists.add(lists.get(1));

        DataSource.Factory<Integer, ProductsList> foundLists =
                mSubjectDao.findByStatusSortByName(ProductsList.STATUS_ACTIVE);
        PagedList<ProductsList> foundProductsList = TestUtils.findSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByCreatedAtDesc() throws InterruptedException {

        List<ProductsList> lists = createProductsLists(4);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setCreatedAt(new Date(3000));

        lists.get(1).setStatus(ProductsList.STATUS_ARCHIVED);

        lists.get(2).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(2).setCreatedAt(new Date(1000));

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setCreatedAt(new Date(2000));

        insertLists(lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));
        activeLists.add(lists.get(2));

        DataSource.Factory<Integer, ProductsList> foundLists =
                mSubjectDao.findByStatusSortByCreatedAtDesc(ProductsList.STATUS_ACTIVE);
        PagedList<ProductsList> foundProductsList = TestUtils.findSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByCreatedByAndName() throws InterruptedException {

        List<ProductsList> lists = createProductsLists(4);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setName("#1");
        lists.get(0).setCreatedBy(mUserID_2);

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setName("#3");
        lists.get(1).setCreatedBy(mUserID_1);

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);
        lists.get(2).setCreatedBy(mUserID_1);

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setName("#2");
        lists.get(3).setCreatedBy(mUserID_2);

        insertLists(lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(1));
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));

        DataSource.Factory<Integer, ProductsList> foundLists =
                mSubjectDao.findByStatusSortByCreatedByAndName(ProductsList.STATUS_ACTIVE);
        PagedList<ProductsList> foundProductsList = TestUtils.findSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByModifiedAtDesc() throws InterruptedException {

        List<ProductsList> lists = createProductsLists(4);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setModifiedAt(new Date(3000));

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setModifiedAt(new Date(1000));

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setModifiedAt(new Date(2000));

        insertLists(lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));
        activeLists.add(lists.get(1));

        DataSource.Factory<Integer, ProductsList> foundLists =
                mSubjectDao.findByStatusSortByModifiedAtDesc(ProductsList.STATUS_ACTIVE);
        PagedList<ProductsList> foundProductsList = TestUtils.findSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findArchivedSortByModifiedAtDesc() throws InterruptedException {

        List<ProductsList> lists = createProductsLists(4);
        lists.get(0).setStatus(ProductsList.STATUS_ARCHIVED);
        lists.get(0).setModifiedAt(new Date(3000));

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setModifiedAt(new Date(1000));

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);
        lists.get(2).setModifiedAt(new Date(4000));

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setModifiedAt(new Date(2000));

        insertLists(lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(2));
        activeLists.add(lists.get(0));

        DataSource.Factory<Integer, ProductsList> foundLists =
                mSubjectDao.findByStatusSortByModifiedAtDesc(ProductsList.STATUS_ARCHIVED);
        PagedList<ProductsList> foundProductsList = TestUtils.findSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByAssignedAndName() throws InterruptedException {

        List<ProductsList> lists = createProductsLists(4);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setName("#1");
        lists.get(0).setAssignedID(mUserID_2);

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setName("#3");
        lists.get(1).setAssignedID(mUserID_1);

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);
        lists.get(2).setAssignedID(mUserID_1);

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setName("#2");
        lists.get(3).setAssignedID(mUserID_2);

        insertLists(lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(1));
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));

        DataSource.Factory<Integer, ProductsList> foundLists =
                mSubjectDao.findByStatusSortByAssignedAndName(ProductsList.STATUS_ACTIVE);
        PagedList<ProductsList> foundProductsList = TestUtils.findSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    private ProductsList createProductListWithNotNullParams() {
        ProductsList list = new ProductsList();
        list.setName("Some name");
        list.setCreatedBy(mUserID_1);

        return list;
    }

    private List<ProductsList> createProductsLists(int count) {

        List<ProductsList> lists = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ProductsList productsList = createProductListWithNotNullParams();
            lists.add(productsList);
        }
        return lists;
    }

    private ProductsList insertList() {
        ProductsList listToInsert = createProductListWithNotNullParams();
        long listID = insertList(listToInsert);
        listToInsert.setListID(listID);

        return listToInsert;
    }

    private long insertList(ProductsList listToInsert) {
        return mSubjectDao.insert(listToInsert);
    }

    private void insertLists(List<ProductsList> listsToInsert) {
        for (ProductsList list : listsToInsert) {
            long insertedID = insertList(list);
            list.setListID(insertedID);
        }
    }

    private ProductsList findListByID(long listID) throws InterruptedException {
        LiveData<ProductsList> listLiveData = mSubjectDao.findByID(listID);
        return TestUtils.findByIDSync(listLiveData);
    }
}
