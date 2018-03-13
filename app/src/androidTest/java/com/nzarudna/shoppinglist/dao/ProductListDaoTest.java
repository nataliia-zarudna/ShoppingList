package com.nzarudna.shoppinglist.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.TestUtils;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.model.user.UserDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.nzarudna.shoppinglist.model.product.list.ProductListRepository.SORT_LISTS_BY_CREATED_AT;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Test Product List Dao methods
 */

@RunWith(AndroidJUnit4.class)
public class ProductListDaoTest {

    private AppDatabase mDatabase;
    private ProductListDao mSubjectDao;
    private ProductDao mProductDao;
    private UUID mLesserUserID;
    private UUID mGreaterUserID;

    @Before
    public void createDB() {

        mDatabase = TestUtils.buildInMemoryDB();
        mSubjectDao = mDatabase.productListDao();
        mProductDao = mDatabase.productDao();

        UserDao userDao = mDatabase.userDao();
        UUID userID_1 = TestUtils.insertUser(userDao);
        UUID userID_2 = TestUtils.insertUser(userDao);
        mLesserUserID = TestUtils.getLesserUUIDByString(userID_1, userID_2);
        mGreaterUserID = TestUtils.getGreaterUUIDByString(userID_1, userID_2);
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

    @Test
    public void create() throws InterruptedException {

        ProductList listToInsert = createProductListWithNotNullParams();
        mSubjectDao.insert(listToInsert);
    }

    @Test
    public void createAndRead() throws InterruptedException {

        ProductList list = new ProductList("new name", mLesserUserID);
        list.setModifiedAt(new Date());
        list.setCreatedBy(mLesserUserID);
        list.setModifiedBy(mGreaterUserID);
        list.setSorting(SORT_LISTS_BY_CREATED_AT);
        list.setIsGroupedView(true);

        mSubjectDao.insert(list);

        LiveData<ProductList> listLiveData = mSubjectDao.findByID(list.getListID());
        ProductList insertedList = TestUtils.getLiveDataValueSync(listLiveData);

        assertThat(insertedList, equalTo(list));
    }

    @Test
    public void readSync_Nonexistent() throws InterruptedException {

        ProductList listLiveData = mSubjectDao.findByIDSync(UUID.randomUUID());

        assertNull(listLiveData);
    }

    @Test
    public void readAsync_Nonexistent() throws InterruptedException {

        LiveData<ProductList> listLiveData = mSubjectDao.findByID(UUID.randomUUID());

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

        ProductList list = new ProductList("new name", mLesserUserID);
        list.setAssignedID(mLesserUserID);
        list.setModifiedAt(new Date());
        list.setModifiedBy(mGreaterUserID);
        list.setSorting(SORT_LISTS_BY_CREATED_AT);
        list.setIsGroupedView(true);

        mSubjectDao.insert(list);

        ProductList insertedList = mSubjectDao.findByIDSync(list.getListID());

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
        list.setCreatedBy(UUID.randomUUID());

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
    public void remove_testOnDeleteCascade() {

        ProductList list = insertList();
        UUID productID = TestUtils.insertProduct(mProductDao, list.getListID());

        mSubjectDao.delete(list);

        Product foundProduct = mProductDao.findByIDSync(productID);
        assertNull(foundProduct);
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

        List<ProductList> lists = TestUtils.createProductsLists(4, mLesserUserID);
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
                mSubjectDao.findWithStatisticsByStatusSortByName(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByCreatedAtDesc() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mLesserUserID);
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
                mSubjectDao.findWithStatisticsByStatusSortByCreatedAtDesc(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByCreatedByAndName() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mLesserUserID);
        lists.get(0).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(0).setName("#1");
        lists.get(0).setCreatedBy(mGreaterUserID);

        lists.get(1).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(1).setName("#3");
        lists.get(1).setCreatedBy(mLesserUserID);

        lists.get(2).setStatus(ProductList.STATUS_ARCHIVED);
        lists.get(2).setCreatedBy(mLesserUserID);

        lists.get(3).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(3).setName("#2");
        lists.get(3).setCreatedBy(mGreaterUserID);

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
                mSubjectDao.findWithStatisticsByStatusSortByCreatedByAndName(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByModifiedAtDesc() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mLesserUserID);
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
                mSubjectDao.findWithStatisticsByStatusSortByModifiedAtDesc(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByAssignedAndName() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mLesserUserID);
        lists.get(0).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(0).setName("#1");
        lists.get(0).setAssignedID(mGreaterUserID);

        lists.get(1).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(1).setName("#3");
        lists.get(1).setAssignedID(mLesserUserID);

        lists.get(2).setStatus(ProductList.STATUS_ARCHIVED);
        lists.get(2).setAssignedID(mLesserUserID);

        lists.get(3).setStatus(ProductList.STATUS_ACTIVE);
        lists.get(3).setName("#2");
        lists.get(3).setAssignedID(mGreaterUserID);

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
                mSubjectDao.findWithStatisticsByStatusSortByAssignedAndName(ProductList.STATUS_ACTIVE);
        PagedList<ProductListWithStatistics> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findAllSortByModifiedAtDesc() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mLesserUserID);
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

        LiveData<List<ProductList>> foundLists =
                mSubjectDao.findAllSortByModifiedAtDesc();
        List<ProductList> foundProductList = TestUtils.getLiveDataValueSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductList);
    }

    @Test
    public void findArchivedSortByModifiedAtDesc() throws InterruptedException {

        List<ProductList> lists = TestUtils.createProductsLists(4, mLesserUserID);
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
        return new ProductList("Some name", mLesserUserID);
    }

    private ProductList insertList() {
        ProductList listToInsert = createProductListWithNotNullParams();
        mSubjectDao.insert(listToInsert);
        return listToInsert;
    }
}
