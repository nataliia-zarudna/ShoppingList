package com.nzarudna.shoppinglist.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.TestUtils;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.user.UserDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nzarudna.shoppinglist.Constants.PRODUCT_ORDER_STEP;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Test Product List Dao methods
 */

@RunWith(AndroidJUnit4.class)
public class ProductDaoTest {

    private AppDatabase mDatabase;

    private ProductDao mSubjectDao;
    private CategoryDao mCategoryDao;
    private ProductListDao mProductListDao;
    private ProductTemplateDao mProductTemplateDao;

    private UUID mUserID_1;
    private UUID mProductsListID_1;
    private UUID mProductsListID_2;
    private UUID mLesserCategoryID;
    private UUID mGreaterCategoryID;
    private UUID mTemplateID_1;
    private UUID mTemplateID_2;

    @Before
    public void createDB() {

        mDatabase = TestUtils.buildInMemoryDB();
        mSubjectDao = mDatabase.productDao();
        mProductListDao = mDatabase.productListDao();

        UserDao userDao = mDatabase.userDao();
        mUserID_1 = TestUtils.insertUser(userDao);

        ProductListDao productListDao = mDatabase.productListDao();
        mProductsListID_1 = TestUtils.insertProductsList(productListDao, mUserID_1);
        mProductsListID_2 = TestUtils.insertProductsList(productListDao, mUserID_1);

        mCategoryDao = mDatabase.categoryDao();
        UUID categoryID_1 = TestUtils.insertCategory(mCategoryDao);
        UUID categoryID_2 = TestUtils.insertCategory(mCategoryDao);
        mLesserCategoryID = TestUtils.getLesserUUIDByString(categoryID_1, categoryID_2);
        mGreaterCategoryID = TestUtils.getGreaterUUIDByString(categoryID_1, categoryID_2);

        mProductTemplateDao = mDatabase.productTemplateDao();
        mTemplateID_1 = TestUtils.insertProductTemplate(mProductTemplateDao);
        mTemplateID_2 = TestUtils.insertProductTemplate(mProductTemplateDao);
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

    @Test
    public void create() throws InterruptedException {

        Product product = createProduct();
        mSubjectDao.insert(product);
    }

    @Test
    public void createAndRead() throws InterruptedException {

        Product product = new Product("new name");
        product.setListID(mProductsListID_1);
        product.setCategoryID(mLesserCategoryID);
        product.setComment("comments");
        product.setCount(5);
        //TODO: add units

        mSubjectDao.insert(product);

        LiveData<Product> productLiveData = mSubjectDao.findByID(product.getProductID());
        Product insertedProduct = TestUtils.getLiveDataValueSync(productLiveData);

        assertThat(insertedProduct, equalTo(product));
    }

    @Test
    public void createWithDefaultParams() throws InterruptedException {

        Product product = insertProduct();

        assertThat(product.getStatus(), is(Product.TO_BUY));
        assertThat(product.getCategoryID(), is(Category.DEFAULT_CATEGORY_ID));
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
        product.setListID(UUID.randomUUID());

        mSubjectDao.insert(product);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedException_OnCreateWithInvalid_CategoryID() throws InterruptedException {

        Product product = createProduct();
        product.setCategoryID(UUID.randomUUID());

        mSubjectDao.insert(product);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedException_OnCreateWithInvalid_UnitID() throws InterruptedException {

        Product product = createProduct();
        product.setUnitID(UUID.randomUUID());

        mSubjectDao.insert(product);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedException_OnCreateWithInvalid_TemplateID() throws InterruptedException {

        Product product = createProduct();
        product.setTemplateID(UUID.randomUUID());

        mSubjectDao.insert(product);
    }

    @Test
    public void setDefaultCategoryID() {

        Product product_1 = new Product("Some name");
        product_1.setListID(mProductsListID_1);
        product_1.setCategoryID(mLesserCategoryID);
        mSubjectDao.insert(product_1);

        Product product_2 = new Product("Some name");
        product_2.setListID(mProductsListID_1);
        product_2.setCategoryID(mGreaterCategoryID);
        mSubjectDao.insert(product_2);

        mSubjectDao.setDefaultCategoryID(mLesserCategoryID);

        Product foundProduct_1 = mSubjectDao.findByIDSync(product_1.getProductID());
        assertEquals(foundProduct_1.getCategoryID(), Category.DEFAULT_CATEGORY_ID);

        Product foundProduct_2 = mSubjectDao.findByIDSync(product_2.getProductID());
        assertEquals(foundProduct_2.getCategoryID(), product_2.getCategoryID());
    }

    @Test
    public void deleteByTemplateIDAndListID() {

        UUID templateID = TestUtils.insertProductTemplate(mProductTemplateDao);

        UUID listID_1 = TestUtils.insertProductsList(mProductListDao, mUserID_1);

        Product product_1 = new Product("Some name");
        product_1.setListID(listID_1);
        product_1.setTemplateID(templateID);
        mSubjectDao.insert(product_1);

        UUID productID_2 = TestUtils.insertProduct(mSubjectDao, listID_1);

        UUID listID_2 = TestUtils.insertProductsList(mProductListDao, mUserID_1);
        Product product_3 = new Product("Some name");
        product_3.setListID(listID_2);
        product_3.setTemplateID(templateID);
        mSubjectDao.insert(product_3);

        mSubjectDao.delete(templateID, listID_1);

        assertNull(mSubjectDao.findByIDSync(product_1.getProductID()));
        assertNotNull(mSubjectDao.findByIDSync(productID_2));
        assertNotNull(mSubjectDao.findByIDSync(product_3.getProductID()));
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

        List<CategoryProductItem> expectedProducts = new ArrayList<>();
        expectedProducts.add(new CategoryProductItem(products.get(0)));
        expectedProducts.add(new CategoryProductItem(products.get(3)));
        expectedProducts.add(new CategoryProductItem(products.get(1)));

        DataSource.Factory<Integer, CategoryProductItem> foundProductsDataSource =
                mSubjectDao.findByListIDSortByName(mProductsListID_1);
        PagedList<CategoryProductItem> foundProducts = TestUtils.getPagedListSync(foundProductsDataSource);

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

        List<CategoryProductItem> expectedProducts = new ArrayList<>();
        expectedProducts.add(new CategoryProductItem(products.get(2)));
        expectedProducts.add(new CategoryProductItem(products.get(4)));
        expectedProducts.add(new CategoryProductItem(products.get(3)));
        expectedProducts.add(new CategoryProductItem(products.get(1)));

        DataSource.Factory<Integer, CategoryProductItem> foundProductsDataSource =
                mSubjectDao.findByListIDSortByStatusAndName(mProductsListID_1);
        PagedList<CategoryProductItem> foundProducts = TestUtils.getPagedListSync(foundProductsDataSource);

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

        List<CategoryProductItem> expectedProducts = new ArrayList<>();
        expectedProducts.add(new CategoryProductItem(products.get(2)));
        expectedProducts.add(new CategoryProductItem(products.get(3)));
        expectedProducts.add(new CategoryProductItem(products.get(4)));
        expectedProducts.add(new CategoryProductItem(products.get(1)));

        DataSource.Factory<Integer, CategoryProductItem> foundProductsDataSource =
                mSubjectDao.findByListIDSortByProductOrder(mProductsListID_1);
        PagedList<CategoryProductItem> foundProducts = TestUtils.getPagedListSync(foundProductsDataSource);

        TestUtils.assertEquals(expectedProducts, foundProducts);
    }

    @Test
    public void findByListIDSortByName_withCategories() throws InterruptedException {

        List<Product> products = createProducts(5);
        products.get(0).setListID(mProductsListID_1);
        products.get(0).setName("#1");
        products.get(0).setCategoryID(mGreaterCategoryID);

        products.get(1).setListID(mProductsListID_1);
        products.get(1).setName("#3");

        products.get(2).setListID(mProductsListID_2);

        products.get(3).setListID(mProductsListID_1);
        products.get(3).setName("#4");
        products.get(3).setCategoryID(mLesserCategoryID);

        products.get(4).setListID(mProductsListID_1);
        products.get(4).setName("#2");
        products.get(4).setCategoryID(mGreaterCategoryID);

        insertProducts(products);

        DataSource.Factory<Integer, CategoryProductItem> factory =
                mSubjectDao.findByListIDSortByNameWithCategory(mProductsListID_1);
        PagedList<CategoryProductItem> pagedGroupedList = TestUtils.getPagedListSync(factory);

        // product order: cat 1, prod 3, cat 2, prod 0, prod 4, prod 1
        Category category1 = mCategoryDao.findByIDSync(mLesserCategoryID);
        Category category2 = mCategoryDao.findByIDSync(mGreaterCategoryID);
        Category defaultCategory = mCategoryDao.findByIDSync(Category.DEFAULT_CATEGORY_ID);

        List<CategoryProductItem> expectedGroupedList = new ArrayList<>();
        expectedGroupedList.add(new CategoryProductItem(category1));
        expectedGroupedList.add(new CategoryProductItem(products.get(3)));
        expectedGroupedList.add(new CategoryProductItem(category2));
        expectedGroupedList.add(new CategoryProductItem(products.get(0)));
        expectedGroupedList.add(new CategoryProductItem(products.get(4)));
        expectedGroupedList.add(new CategoryProductItem(defaultCategory));
        expectedGroupedList.add(new CategoryProductItem(products.get(1)));

        TestUtils.assertEquals(pagedGroupedList, expectedGroupedList);
    }

    @Test
    public void findByListIDSortByStatusAndName_withCategories() throws InterruptedException {

        List<Product> products = createProducts(5);
        products.get(0).setListID(mProductsListID_1);
        products.get(0).setName("#1");
        products.get(0).setStatus(Product.BOUGHT);
        products.get(0).setCategoryID(mGreaterCategoryID);

        products.get(1).setListID(mProductsListID_1);
        products.get(1).setName("#3");
        products.get(1).setStatus(Product.ABSENT);
        products.get(1).setCategoryID(mGreaterCategoryID);

        products.get(2).setListID(mProductsListID_2);

        products.get(3).setListID(mProductsListID_1);
        products.get(3).setName("#4");
        products.get(3).setStatus(Product.TO_BUY);
        products.get(3).setCategoryID(mLesserCategoryID);

        products.get(4).setListID(mProductsListID_1);
        products.get(4).setName("#2");
        products.get(4).setStatus(Product.BOUGHT);

        insertProducts(products);

        DataSource.Factory<Integer, CategoryProductItem> factory =
                mSubjectDao.findByListIDSortByStatusAndNameWithCategory(mProductsListID_1);
        PagedList<CategoryProductItem> pagedGroupedList = TestUtils.getPagedListSync(factory);

        // product order: cat 1, prod 3, cat 2, prod 1, prod 0, prod 4
        Category category1 = mCategoryDao.findByIDSync(mLesserCategoryID);
        Category category2 = mCategoryDao.findByIDSync(mGreaterCategoryID);
        Category defaultCategory = mCategoryDao.findByIDSync(Category.DEFAULT_CATEGORY_ID);

        List<CategoryProductItem> expectedGroupedList = new ArrayList<>();
        expectedGroupedList.add(new CategoryProductItem(category1));
        expectedGroupedList.add(new CategoryProductItem(products.get(3)));
        expectedGroupedList.add(new CategoryProductItem(category2));
        expectedGroupedList.add(new CategoryProductItem(products.get(1)));
        expectedGroupedList.add(new CategoryProductItem(products.get(0)));
        expectedGroupedList.add(new CategoryProductItem(defaultCategory));
        expectedGroupedList.add(new CategoryProductItem(products.get(4)));

        TestUtils.assertEquals(pagedGroupedList, expectedGroupedList);
    }

    @Test
    public void findByListIDSortByProductOrder_withCategories() throws InterruptedException {

        List<Product> products = createProducts(6);
        products.get(0).setListID(mProductsListID_1);
        products.get(0).setName("#1");
        products.get(0).setOrder(9);

        products.get(1).setListID(mProductsListID_1);
        products.get(1).setName("#3");
        products.get(1).setOrder(-5);
        products.get(1).setCategoryID(mGreaterCategoryID);

        products.get(2).setListID(mProductsListID_2);

        products.get(3).setListID(mProductsListID_1);
        products.get(3).setName("#4");
        products.get(3).setOrder(9.1);
        products.get(3).setCategoryID(mLesserCategoryID);

        products.get(4).setListID(mProductsListID_1);
        products.get(4).setName("#2");
        products.get(4).setOrder(0.8);
        products.get(4).setCategoryID(mGreaterCategoryID);

        products.get(5).setListID(mProductsListID_1);
        products.get(5).setName("#5");
        products.get(5).setOrder(1.8);

        insertProducts(products);

        DataSource.Factory<Integer, CategoryProductItem> factory =
                mSubjectDao.findByListIDSortByProductOrderWithCategory(mProductsListID_1);
        PagedList<CategoryProductItem> pagedGroupedList = TestUtils.getPagedListSync(factory);

        // product order: cat 1, prod 3, cat 2, prod 1, prod 4, prod 0
        Category category1 = mCategoryDao.findByIDSync(mLesserCategoryID);
        Category category2 = mCategoryDao.findByIDSync(mGreaterCategoryID);
        Category defaultCategory = mCategoryDao.findByIDSync(Category.DEFAULT_CATEGORY_ID);

        List<CategoryProductItem> expectedGroupedList = new ArrayList<>();
        expectedGroupedList.add(new CategoryProductItem(category1));
        expectedGroupedList.add(new CategoryProductItem(products.get(3)));
        expectedGroupedList.add(new CategoryProductItem(category2));
        expectedGroupedList.add(new CategoryProductItem(products.get(1)));
        expectedGroupedList.add(new CategoryProductItem(products.get(4)));
        expectedGroupedList.add(new CategoryProductItem(defaultCategory));
        expectedGroupedList.add(new CategoryProductItem(products.get(5)));
        expectedGroupedList.add(new CategoryProductItem(products.get(0)));

        TestUtils.assertEquals(pagedGroupedList, expectedGroupedList);
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

        assertEquals(Double.compare(maxOrder, 4.5), 0);
    }

    @Test
    public void getMinProductOrderByListID() throws InterruptedException {

        List<Product> products = createProducts(5);
        products.get(0).setListID(mProductsListID_2);

        products.get(1).setListID(mProductsListID_1);
        products.get(1).setOrder(4.5);

        products.get(2).setListID(mProductsListID_1);
        products.get(2).setOrder(0.2);

        products.get(3).setListID(mProductsListID_1);
        products.get(3).setOrder(1.2);

        products.get(4).setListID(mProductsListID_1);
        products.get(4).setOrder(4.05);

        insertProducts(products);

        double maxOrder = mSubjectDao.getMinProductOrderByListID(mProductsListID_1);

        assertEquals(Double.compare(maxOrder, 0.2), 0);
    }

    @Test
    public void setOrderToAllProductsInList_orderByName() {

        UUID listID = mProductsListID_1;

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 2");
        product1.setListID(listID);
        products.add(product1);

        Product product2 = new Product("Product 1");
        product2.setListID(listID);
        products.add(product2);

        Product product3 = new Product("Product 4");
        product3.setListID(listID);
        products.add(product3);

        Product product4 = new Product("Product 3");
        product4.setListID(listID);
        products.add(product4);

        insertProducts(products);

        mSubjectDao.updateProductOrdersByListIDSortByName(listID);

        List<Product> actualList = mSubjectDao.findByListIDSync(listID);

        product2.setOrder(0);
        product1.setOrder(1 *  + PRODUCT_ORDER_STEP);
        product4.setOrder(2 *  + PRODUCT_ORDER_STEP);
        product3.setOrder(3 *  + PRODUCT_ORDER_STEP);

        TestUtils.assertEquals(actualList, products);
    }

    @Test
    public void setOrderToAllProductsInList_orderByStatus() {

        UUID listID = mProductsListID_1;

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 2");
        product1.setListID(listID);
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product("Product 1");
        product2.setListID(listID);
        product2.setStatus(Product.ABSENT);
        products.add(product2);

        Product product3 = new Product("Product 4");
        product3.setListID(listID);
        product3.setStatus(Product.TO_BUY);
        products.add(product3);

        Product product4 = new Product("Product 3");
        product4.setListID(listID);
        product4.setStatus(Product.BOUGHT);
        products.add(product4);

        insertProducts(products);

        mSubjectDao.updateProductOrdersByListIDSortByStatusAndName(listID);

        List<Product> actualList = mSubjectDao.findByListIDSync(listID);

        product3.setOrder(0);
        product2.setOrder(1 *  + PRODUCT_ORDER_STEP);
        product1.setOrder(2 *  + PRODUCT_ORDER_STEP);
        product4.setOrder(3 *  + PRODUCT_ORDER_STEP);

        TestUtils.assertEquals(products, actualList);
    }

    @Test
    public void setOrderToAllProductsInList_orderByStatus_equalsStatus() {

        UUID listID = mProductsListID_1;

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 2");
        product1.setListID(listID);
        product1.setStatus(Product.TO_BUY);
        products.add(product1);

        Product product2 = new Product("Product 1");
        product2.setListID(listID);
        product2.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product("Product 4");
        product3.setListID(listID);
        product3.setStatus(Product.TO_BUY);
        products.add(product3);

        Product product4 = new Product("Product 3");
        product4.setListID(listID);
        product4.setStatus(Product.TO_BUY);
        products.add(product4);

        insertProducts(products);

        mSubjectDao.updateProductOrdersByListIDSortByStatusAndName(listID);

        List<Product> actualList = mSubjectDao.findByListIDSync(listID);

        product2.setOrder(0);
        product1.setOrder(1 *  + PRODUCT_ORDER_STEP);
        product4.setOrder(2 *  + PRODUCT_ORDER_STEP);
        product3.setOrder(3 *  + PRODUCT_ORDER_STEP); // 3 2 1 4

        TestUtils.assertEquals(products, actualList);
    }

    @Test
    public void clearTemplateIDs() throws InterruptedException {

        List<Product> products = createProducts(3);
        products.get(0).setTemplateID(mTemplateID_1);
        products.get(1).setTemplateID(mTemplateID_2);
        products.get(2).setTemplateID(mTemplateID_1);
        insertProducts(products);

        mSubjectDao.clearTemplateIDs(mTemplateID_1);

        Product product_0 = mSubjectDao.findByIDSync(products.get(0).getProductID());
        assertNull(product_0.getTemplateID());

        Product product_1 = mSubjectDao.findByIDSync(products.get(1).getProductID());
        assertEquals(product_1.getTemplateID(), mTemplateID_2);

        Product product_2 = mSubjectDao.findByIDSync(products.get(2).getProductID());
        assertNull(product_2.getTemplateID());
    }

    private Product createProduct() throws InterruptedException {
        Product product = new Product("Some name");
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

        mSubjectDao.insert(product);
        LiveData<Product> createdProduct = mSubjectDao.findByID(product.getProductID());

        return TestUtils.getLiveDataValueSync(createdProduct);
    }

    private void insertProducts(List<Product> products) {
        for (Product product : products) {
            mSubjectDao.insert(product);
        }
    }
}
