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
import com.nzarudna.shoppinglist.persistence.ProductListDao;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.persistence.CategoryDao;
import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.UserDao;
import com.nzarudna.shoppinglist.persistence.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
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
public class ProductDaoTest {

    private AppDatabase mDatabase;
    private ProductDao mSubjectDao;
    private int mUserID_1;
    private int mProductsListID_1;
    private int mProductsListID_2;
    private int mCategoryID_1;
    private int mCategoryID_2;

    @Before
    public void createDB() {

        Context context = InstrumentationRegistry.getContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mSubjectDao = mDatabase.productDao();

        UserDao userDao = mDatabase.userDao();
        mUserID_1 = TestUtils.insertUser(userDao);

        ProductListDao productListDao = mDatabase.productsListDao();
        mProductsListID_1 = TestUtils.insertProductsList(productListDao, mUserID_1);
        mProductsListID_2 = TestUtils.insertProductsList(productListDao, mUserID_1);

        CategoryDao categoryDao = mDatabase.categoryDao();
        mCategoryID_1 = TestUtils.insertCategory(categoryDao);
        mCategoryID_2 = TestUtils.insertCategory(categoryDao);
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

    @Test
    public void create() throws InterruptedException {

        Product product = createProduct();
        long listID = mSubjectDao.insert(product);

        assertThat(listID, not(0l));
    }

    @Test
    public void createAndRead() throws InterruptedException {

        Product product = new Product();
        product.setName("new name");
        product.setListID(mProductsListID_1);
        product.setCategoryID(mCategoryID_1);
        product.setComment("comments");
        product.setCount(5);
        //TODO: add units

        int productID = (int) mSubjectDao.insert(product);
        product.setProductID(productID);

        LiveData<Product> productLiveData = mSubjectDao.findByID(productID);
        Product insertedProduct = TestUtils.getLiveDataValueSync(productLiveData);

        assertThat(insertedProduct, equalTo(product));
    }

    @Test
    public void createWithToBuyStatus() throws InterruptedException {

        Product product = insertProduct();

        assertThat(product.getStatus(), is(Product.TO_BUY));
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedException_OnCreateWith_NullName() throws InterruptedException {

        Product product = createProduct();
        product.setName(null);

        mSubjectDao.insert(product);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedException_OnCreateWithInvalid_ListID() throws InterruptedException {

        Product product = createProduct();
        product.setListID(99);

        mSubjectDao.insert(product);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedException_OnCreateWithInvalid_CategoryID() throws InterruptedException {

        Product product = createProduct();
        product.setCategoryID(99);

        mSubjectDao.insert(product);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedException_OnCreateWithInvalid_UnitID() throws InterruptedException {

        Product product = createProduct();
        product.setUnitID(99);

        mSubjectDao.insert(product);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedException_OnCreateWithInvalid_TemplateID() throws InterruptedException {

        Product product = createProduct();
        product.setTemplateID(99);

        mSubjectDao.insert(product);
    }

    @Test
    public void findByListIDSync() throws InterruptedException {

        List<Product> products = createProducts(4);
        products.get(0).setListID(mProductsListID_1);
        products.get(1).setListID(mProductsListID_1);
        products.get(2).setListID(mProductsListID_2);
        products.get(3).setListID(mProductsListID_1);

        insertProducts(products);

        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(products.get(0));
        expectedProducts.add(products.get(1));
        expectedProducts.add(products.get(3));

        List<Product> foundProducts = mSubjectDao.findByListIDSync(mProductsListID_1);

        TestUtils.assertEquals(expectedProducts, foundProducts);
    }

    @Test
    public void findByListIDSortByName() throws InterruptedException {

        List<Product> products = createProducts(4);
        products.get(0).setListID(mProductsListID_1);
        products.get(0).setName("#1");

        products.get(1).setListID(mProductsListID_1);
        products.get(1).setName("#3");

        products.get(2).setListID(mProductsListID_2);

        products.get(3).setListID(mProductsListID_1);
        products.get(3).setName("#2");

        insertProducts(products);

        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(products.get(0));
        expectedProducts.add(products.get(3));
        expectedProducts.add(products.get(1));

        DataSource.Factory<Integer, Product> foundProductsDataSource =
                mSubjectDao.findByListIDSortByName(mProductsListID_1);
        PagedList<Product> foundProducts = TestUtils.getPagedListSync(foundProductsDataSource);

        TestUtils.assertEquals(expectedProducts, foundProducts);
    }

    @Test
    public void findByListIDSortByCategoryIDAndName() throws InterruptedException {

        List<Product> products = createProducts(5);
        products.get(0).setListID(mProductsListID_1);
        products.get(0).setName("#1");

        products.get(1).setListID(mProductsListID_1);
        products.get(1).setCategoryID(mCategoryID_1);
        products.get(1).setName("#4");

        products.get(2).setListID(mProductsListID_2);

        products.get(3).setListID(mProductsListID_1);
        products.get(3).setCategoryID(mCategoryID_2);
        products.get(3).setName("#3");

        products.get(4).setListID(mProductsListID_1);
        products.get(4).setCategoryID(mCategoryID_1);
        products.get(4).setName("#2");

        insertProducts(products);

        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(products.get(0));
        expectedProducts.add(products.get(4));
        expectedProducts.add(products.get(1));
        expectedProducts.add(products.get(3));

        DataSource.Factory<Integer, Product> foundProductsDataSource =
                mSubjectDao.findByListIDSortByCategoryIDAndName(mProductsListID_1);
        PagedList<Product> foundProducts = TestUtils.getPagedListSync(foundProductsDataSource);

        TestUtils.assertEquals(expectedProducts, foundProducts);
    }

    @Test
    public void findByListIDSortByStatusAndName() throws InterruptedException {

        List<Product> products = createProducts(5);
        products.get(0).setListID(mProductsListID_2);

        products.get(1).setListID(mProductsListID_1);
        products.get(1).setStatus(Product.BOUGHT);
        products.get(1).setName("#3");

        products.get(2).setListID(mProductsListID_1);
        products.get(2).setStatus(Product.TO_BUY);
        products.get(2).setName("#4");

        products.get(3).setListID(mProductsListID_1);
        products.get(3).setStatus(Product.BOUGHT);
        products.get(3).setName("#1");

        products.get(4).setListID(mProductsListID_1);
        products.get(4).setStatus(Product.ABSENT);
        products.get(4).setName("#2");

        insertProducts(products);

        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(products.get(2));
        expectedProducts.add(products.get(4));
        expectedProducts.add(products.get(3));
        expectedProducts.add(products.get(1));

        DataSource.Factory<Integer, Product> foundProductsDataSource =
                mSubjectDao.findByListIDSortByStatusAndName(mProductsListID_1);
        PagedList<Product> foundProducts = TestUtils.getPagedListSync(foundProductsDataSource);

        TestUtils.assertEquals(expectedProducts, foundProducts);
    }

    @Test
    public void findByListIDSortByProductOrder() throws InterruptedException {

        List<Product> products = createProducts(5);
        products.get(0).setListID(mProductsListID_2);

        products.get(1).setListID(mProductsListID_1);
        products.get(1).setOrder(4.5);

        products.get(2).setListID(mProductsListID_1);
        products.get(2).setOrder(0);

        products.get(3).setListID(mProductsListID_1);
        products.get(3).setOrder(1.2);

        products.get(4).setListID(mProductsListID_1);
        products.get(4).setOrder(4.05);

        insertProducts(products);

        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(products.get(2));
        expectedProducts.add(products.get(3));
        expectedProducts.add(products.get(4));
        expectedProducts.add(products.get(1));

        DataSource.Factory<Integer, Product> foundProductsDataSource =
                mSubjectDao.findByListIDSortByProductOrder(mProductsListID_1);
        PagedList<Product> foundProducts = TestUtils.getPagedListSync(foundProductsDataSource);

        TestUtils.assertEquals(expectedProducts, foundProducts);
    }

    @Test
    public void getMaxProductOrderByListID() throws InterruptedException {

        List<Product> products = createProducts(5);
        products.get(0).setListID(mProductsListID_2);

        products.get(1).setListID(mProductsListID_1);
        products.get(1).setOrder(4.5);

        products.get(2).setListID(mProductsListID_1);
        products.get(2).setOrder(0);

        products.get(3).setListID(mProductsListID_1);
        products.get(3).setOrder(1.2);

        products.get(4).setListID(mProductsListID_1);
        products.get(4).setOrder(4.05);

        insertProducts(products);

        double maxOrder = mSubjectDao.getMaxProductOrderByListID(mProductsListID_1);

        //assertTrue(Math.abs(maxOrder - 4.5) < 0.0001);
        assertEquals(Double.compare(maxOrder, 4.5), 0);
    }

    private Product createProduct() throws InterruptedException {
        Product product = new Product();
        product.setName("Some name");
        product.setListID(mProductsListID_1);

        return product;
    }

    public List<Product> createProducts(int count) throws InterruptedException {

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            products.add(createProduct());
        }
        return products;
    }

    private Product insertProduct() throws InterruptedException {
        Product product = createProduct();

        long productID = mSubjectDao.insert(product);
        LiveData<Product> createdProduct = mSubjectDao.findByID((int) productID);

        return TestUtils.getLiveDataValueSync(createdProduct);
    }

    private void insertProducts(List<Product> products) {
        for (Product product : products) {
            long insertedID = mSubjectDao.insert(product);
            product.setProductID((int) insertedID);
        }
    }
}
