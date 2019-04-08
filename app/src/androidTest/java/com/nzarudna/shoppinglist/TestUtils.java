package com.nzarudna.shoppinglist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.room.Room;
import android.content.Context;
import androidx.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitDao;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserDao;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Class with utils test methods
 */

public class TestUtils {

    public static AppDatabase buildInMemoryDB() {

        Context context = InstrumentationRegistry.getContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        return appDatabase;
    }

    public static <T> T getLiveDataValueSync(LiveData<T> liveData) throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);
        final T[] valueArr = (T[]) new Object[1];
        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(@Nullable T value) {

                valueArr[0] = value;
                latch.countDown();
            }
        });

        latch.await(3000, TimeUnit.MILLISECONDS);

        return valueArr[0];
    }

    public static <T> PagedList<T> getPagedListSync(DataSource.Factory<Integer, T> dataSourceFactory) throws InterruptedException {

        final PagedList<T>[] foundList = new PagedList[1];
        final CountDownLatch latch = new CountDownLatch(1);

        LiveData<PagedList<T>> livePagedList = new LivePagedListBuilder<>(dataSourceFactory, 4).build();
        livePagedList.observeForever(new Observer<PagedList<T>>() {
            @Override
            public void onChanged(@Nullable PagedList<T> list) {

                foundList[0] = list;
                latch.countDown();
            }
        });
        latch.await(3000, TimeUnit.MILLISECONDS);

        return foundList[0];
    }

    public static void assertEquals(List expected, List actual) {

        try {

            Assert.assertEquals(expected.size(), actual.size());

            for (int i = 0; i < expected.size(); i++) {
                Assert.assertEquals(expected.get(i), actual.get(i));
            }
        } catch (AssertionError e) {
            throw new AssertionError("List is not equals to paged list: " +
                    "\nexpected: " + expected + ",\nactual: " + actual, e);
        }
    }

    public static void assertDateEqualsToSeconds(Date expected, Date actual) {
        boolean diffWithE = expected.compareTo(actual) < 60; // seconds

        assertTrue(diffWithE);
    }

    public static UUID insertUser(UserDao userDao) {
        User user = new User();
        user.setName("new user");
        userDao.insert(user);
        return user.getUserID();
    }

    public static UUID insertProductsList(ProductListDao productListDao, UUID createdBy) {

        ProductList list = new ProductList("Some name", createdBy);
        productListDao.insert(list);
        return list.getListID();
    }

    public static List<ProductList> createProductsLists(int count, UUID createdBy) {

        List<ProductList> lists = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ProductList productList = new ProductList("Some name", createdBy);
            lists.add(productList);
        }
        return lists;
    }

    public static void insertProductsLists(ProductListDao productListDao, List<ProductList> listsToInsert) {
        for (ProductList list : listsToInsert) {
            productListDao.insert(list);
        }
    }

    public static UUID insertCategory(CategoryDao categoryDao) {

        Category category = new Category();
        categoryDao.insert(category);
        return category.getCategoryID();
    }

    public static void insertDefaultCategory(CategoryDao categoryDao) {

        Category defaultCategory = new Category();
        defaultCategory.setCategoryID(Category.DEFAULT_CATEGORY_ID);
        categoryDao.insert(defaultCategory);
    }

    public static UUID insertProduct(ProductDao productDao, UUID listID) {
        return insertProduct(productDao, listID, 0);
    }

    public static UUID insertProduct(ProductDao productDao, UUID listID, @Product.ProductStatus int status) {

        Product product = new Product();
        product.setListID(listID);
        product.setStatus(status);

        productDao.insert(product);
        return product.getProductID();
    }

    public static UUID insertProductTemplate(ProductTemplateDao productTemplateDao) {

        ProductTemplate template = new ProductTemplate();
        productTemplateDao.insert(template);
        return template.getTemplateID();
    }

    public static UUID insertUnit(UnitDao unitDao) {

        Unit unit = new Unit();
        unitDao.insert(unit);
        return unit.getUnitID();
    }

    public static UUID getLesserUUIDByString(UUID id1, UUID id2) {
        return id1.toString().compareTo(id2.toString()) < 0 ? id1 : id2;
    }

    public static UUID getGreaterUUIDByString(UUID id1, UUID id2) {
        return id1.toString().compareTo(id2.toString()) > 0 ? id1 : id2;
    }
}
