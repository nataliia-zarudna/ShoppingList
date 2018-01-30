package com.nzarudna.shoppinglist.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.TestUtils;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.user.UserDao;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.nzarudna.shoppinglist.model.product.list.ProductListRepository.SORT_LISTS_BY_CREATED_AT;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test Product List Dao methods
 */

@RunWith(AndroidJUnit4.class)
public class ProductListDaoTest {

    private AppDatabase mDatabase;
    private ProductListDao mSubjectDao;
    private ProductDao mProductDao;
    private int mUserID_1;
    private int mUserID_2;

    @Before
    public void createDB() {

        mDatabase = TestUtils.buildInMemoryDB();
        mSubjectDao = mDatabase.productListDao();
        mProductDao = mDatabase.productDao();

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

        ProductList listToInsert = createProductListWithNotNullParams();
        long listID = mSubjectDao.insert(listToInsert);

        assertThat(listID, not(0l));
    }

    @Test
    public void createAndRead() throws InterruptedException {

        ProductList list = new ProductList();
        list.setName("new name");
        list.setAssignedID(mUserID_1);
        list.setModifiedAt(new Date());
        list.setCreatedBy(mUserID_1);
        list.setModifiedBy(mUserID_2);
        list.setSorting(SORT_LISTS_BY_CREATED_AT);
        list.setIsGroupedView(true);

        int listID = (int) mSubjectDao.insert(list);
        list.setListID(listID);

        LiveData<ProductList> listLiveData = mSubjectDao.findByID(listID);
        ProductList insertedList = TestUtils.getLiveDataValueSync(listLiveData);

        assertThat(insertedList, equalTo(list));
    }

    @Test
    public void readSync_Nonexistent() throws InterruptedException {

        ProductList listLiveData = mSubjectDao.findByIDSync(99);

        assertNull(listLiveData);
    }

    @Test
    public void readAsync_Nonexistent() throws InterruptedException {

        LiveData<ProductList> listLiveData = mSubjectDao.findByID(99);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        listLiveData.observeForever(new Observer<ProductList>() {
            @Override
            public void onChanged(@Nullable ProductList productList) {

                assertNull(productList);

                countDownLatch.countDown();
            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void createAndReadSync() throws InterruptedException {

        ProductList list = new ProductList();
        list.setName("new name");
        list.setAssignedID(mUserID_1);
        list.setModifiedAt(new Date());
        list.setCreatedBy(mUserID_1);
        list.setModifiedBy(mUserID_2);
        list.setSorting(SORT_LISTS_BY_CREATED_AT);
        list.setIsGroupedView(true);

        int listID = (int) mSubjectDao.insert(list);
        list.setListID(listID);

        ProductList insertedList = mSubjectDao.findByIDSync(listID);

        assertThat(insertedList, equalTo(list));
    }

    @Test
    public void createWithActiveStatus() throws InterruptedException {

        ProductList list = insertList();

        assertThat(list.getStatus(), is(ProductList.STATUS_ACTIVE));
    }

    @Test
    public void createWithCurrentCreatedAtTime() throws InterruptedException {

        ProductList list = insertList();

        TestUtils.assertDateEqualsToSeconds(new Date(), list.getCreatedAt());
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedExceptionOnCreateWithNullName() throws InterruptedException {

        ProductList list = createProductListWithNotNullParams();
        list.setName(null);

        mSubjectDao.insert(list);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedExceptionOnCreateWithNullCreatedBy() throws InterruptedException {

        ProductList list = createProductListWithNotNullParams();
        list.setCreatedBy(0);

        mSubjectDao.insert(list);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedExceptionOnCreateWithNullCreatedAt() throws InterruptedException {

        ProductList list = createProductListWithNotNullParams();
        list.setCreatedAt(null);

        mSubjectDao.insert(list);
    }

    @Test
    public void update() throws InterruptedException {

        ProductList list = insertList();

        list.setName("New name");
        list.setStatus(ProductList.STATUS_ARCHIVED);

        mSubjectDao.update(list);

        ProductList updatedList = mSubjectDao.findByIDSync(list.getListID());

        assertThat(updatedList, equalTo(list));
    }

    @Test
    public void delete() throws InterruptedException {

        ProductList list = insertList();

        mSubjectDao.delete(list);

        ProductList foundList = mSubjectDao.findByIDSync(list.getListID());

        assertEquals(foundList, null);
    }

    @Test
    public void deleteByID() throws InterruptedException {

        ProductList listToDelete = insertList();
        ProductList otherList = insertList();

        mSubjectDao.deleteByID(listToDelete.getListID());

        ProductList deletedList = mSubjectDao.findByIDSync(listToDelete.getListID());
        assertNull(deletedList);

        ProductList foundOtherList = mSubjectDao.findByIDSync(otherList.getListID());
        assertEquals(foundOtherList, otherList);
    }

    @Test
    public void findActiveSortByName() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mUserID_1);
        lists.get(0).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(0).setName("#1");

        lists.get(1).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(1).setName("#3");

        lists.get(2).setStatus(ProductList.STATUS_ARCHIVED);

        lists.get(3).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(3).setName("#2");

        TestUtils.insertProductsLists(mSubjectDao, lists);

        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.TO_BUY);
        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.TO_BUY);
        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.ABSENT);
        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.ABSENT);

        TestUtils.insertProduct(mProductDao, lists.get(3).getListID(), Product.TO_BUY);
        TestUtils.insertProduct(mProductDao, lists.get(3).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(3).getListID(), Product.ABSENT);


        List<ProductListWithStatistics> activeLists = new ArrayList<>();
        activeLists.add(new ProductListWithStatistics(
                lists.get(0).getListID(), lists.get(0).getName(), 0, 0,0)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(3).getListID(), lists.get(3).getName(), 1, 1,1)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(1).getListID(), lists.get(1).getName(), 2, 2,0)
        );

        DataSource.Factory<Integer, ProductListWithStatistics> foundLists =
                mSubjectDao.findWithStaticticsByStatusSortByName(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByCreatedAtDesc() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mUserID_1);
        lists.get(0).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(0).setCreatedAt(new Date(3000));

        lists.get(1).setStatus(ProductList.STATUS_ARCHIVED);

        lists.get(2).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(2).setCreatedAt(new Date(1000));

        lists.get(3).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(3).setCreatedAt(new Date(2000));

        TestUtils.insertProductsLists(mSubjectDao, lists);

        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.BOUGHT);

        TestUtils.insertProduct(mProductDao, lists.get(3).getListID(), Product.TO_BUY);
        TestUtils.insertProduct(mProductDao, lists.get(3).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(3).getListID(), Product.ABSENT);


        List<ProductListWithStatistics> activeLists = new ArrayList<>();
        activeLists.add(new ProductListWithStatistics(
                lists.get(0).getListID(), lists.get(0).getName(), 0, 0,3)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(3).getListID(), lists.get(3).getName(), 1, 1,1)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(2).getListID(), lists.get(2).getName(), 0, 0,0)
        );

        DataSource.Factory<Integer, ProductListWithStatistics> foundLists =
                mSubjectDao.findWithStaticticsByStatusSortByCreatedAtDesc(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByCreatedByAndName() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mUserID_1);
        lists.get(0).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(0).setName("#1");
        lists.get(0).setCreatedBy(mUserID_2);

        lists.get(1).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(1).setName("#3");
        lists.get(1).setCreatedBy(mUserID_1);

        lists.get(2).setStatus(ProductList.STATUS_ARCHIVED);
        lists.get(2).setCreatedBy(mUserID_1);

        lists.get(3).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(3).setName("#2");
        lists.get(3).setCreatedBy(mUserID_2);

        TestUtils.insertProductsLists(mSubjectDao, lists);

        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.TO_BUY);
        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.BOUGHT);

        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.ABSENT);

        TestUtils.insertProduct(mProductDao, lists.get(3).getListID(), Product.TO_BUY);
        TestUtils.insertProduct(mProductDao, lists.get(3).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(3).getListID(), Product.ABSENT);


        List<ProductListWithStatistics> activeLists = new ArrayList<>();
        activeLists.add(new ProductListWithStatistics(
                lists.get(1).getListID(), lists.get(1).getName(), 0, 1,1)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(0).getListID(), lists.get(0).getName(), 1, 0,2)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(3).getListID(), lists.get(3).getName(), 1, 1,1)
        );

        DataSource.Factory<Integer, ProductListWithStatistics> foundLists =
                mSubjectDao.findWithStaticticsByStatusSortByCreatedByAndName(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByModifiedAtDesc() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mUserID_1);
        lists.get(0).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(0).setModifiedAt(new Date(3000));

        lists.get(1).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(1).setModifiedAt(new Date(1000));

        lists.get(2).setStatus(ProductList.STATUS_ARCHIVED);

        lists.get(3).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(3).setModifiedAt(new Date(2000));

        TestUtils.insertProductsLists(mSubjectDao, lists);

        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.TO_BUY);
        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.BOUGHT);

        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.ABSENT);
        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.ABSENT);
        TestUtils.insertProduct(mProductDao, lists.get(1).getListID(), Product.TO_BUY);


        List<ProductListWithStatistics> activeLists = new ArrayList<>();
        activeLists.add(new ProductListWithStatistics(
                lists.get(0).getListID(), lists.get(0).getName(), 1, 0,2)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(3).getListID(), lists.get(3).getName(), 0, 0,0)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(1).getListID(), lists.get(1).getName(), 1, 2,2)
        );

        DataSource.Factory<Integer, ProductListWithStatistics> foundLists =
                mSubjectDao.findWithStaticticsByStatusSortByModifiedAtDesc(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByAssignedAndName() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mUserID_1);
        lists.get(0).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(0).setName("#1");
        lists.get(0).setAssignedID(mUserID_2);

        lists.get(1).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(1).setName("#3");
        lists.get(1).setAssignedID(mUserID_1);

        lists.get(2).setStatus(ProductList.STATUS_ARCHIVED);
        lists.get(2).setAssignedID(mUserID_1);

        lists.get(3).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(3).setName("#2");
        lists.get(3).setAssignedID(mUserID_2);

        TestUtils.insertProductsLists(mSubjectDao, lists);

        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.BOUGHT);
        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.TO_BUY);
        TestUtils.insertProduct(mProductDao, lists.get(0).getListID(), Product.BOUGHT);

        List<ProductListWithStatistics> activeLists = new ArrayList<>();
        activeLists.add(new ProductListWithStatistics(
                lists.get(1).getListID(), lists.get(1).getName(), 0, 0,0)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(0).getListID(), lists.get(0).getName(), 1, 0,2)
        );
        activeLists.add(new ProductListWithStatistics(
                lists.get(3).getListID(), lists.get(3).getName(), 0, 0,0)
        );

        DataSource.Factory<Integer, ProductListWithStatistics> foundLists =
                mSubjectDao.findWithStaticticsByStatusSortByAssignedAndName(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findAllSortByModifiedAtDesc() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mUserID_1);
        lists.get(0).setStatus(ProductList.STATUS_ARCHIVED);
        lists.get(0).setModifiedAt(new Date(3000));

        lists.get(1).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(1).setModifiedAt(new Date(1000));

        lists.get(2).setStatus(ProductList.STATUS_ARCHIVED);
        lists.get(2).setModifiedAt(new Date(4000));

        lists.get(3).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(3).setModifiedAt(new Date(2000));

        TestUtils.insertProductsLists(mSubjectDao, lists);

        List<ProductList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(2));
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));
        activeLists.add(lists.get(1));

        DataSource.Factory<Integer, ProductList> foundLists =
                mSubjectDao.findAllSortByModifiedAtDesc();
        PagedList<ProductList> foundProductList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductList);
    }

    @Test
    public void findArchivedSortByModifiedAtDesc() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mUserID_1);
        lists.get(0).setStatus(ProductList.STATUS_ARCHIVED);
        lists.get(0).setModifiedAt(new Date(3000));

        lists.get(1).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(1).setModifiedAt(new Date(1000));

        lists.get(2).setStatus(ProductList.STATUS_ARCHIVED);
        lists.get(2).setModifiedAt(new Date(4000));

        lists.get(3).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(3).setModifiedAt(new Date(2000));

        TestUtils.insertProductsLists(mSubjectDao, lists);

        List<ProductList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(2));
        activeLists.add(lists.get(0));

        DataSource.Factory<Integer, ProductList> foundLists =
                mSubjectDao.findByStatusSortByModifiedAtDesc(ProductList.STATUS_ARCHIVED);
        PagedList<ProductList> foundProductList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductList);
    }

    private ProductList createProductListWithNotNullParams() {
        ProductList list = new ProductList();
        list.setName("Some name");
        list.setCreatedBy(mUserID_1);

        return list;
    }

    private ProductList insertList() {
        ProductList listToInsert = createProductListWithNotNullParams();
        int listID = (int) mSubjectDao.insert(listToInsert);
        listToInsert.setListID(listID);

        return listToInsert;
    }
}
