package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.model.Category;
import com.nzarudna.shoppinglist.model.Product;
import com.nzarudna.shoppinglist.model.ProductTemplate;
import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.User;
import com.nzarudna.shoppinglist.model.dao.CategoryDao;
import com.nzarudna.shoppinglist.model.dao.ProductDao;
import com.nzarudna.shoppinglist.model.dao.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;
import com.nzarudna.shoppinglist.model.dao.UserDao;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class with utils test methods
 */

public class TestUtils {

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

    public static int insertUser(UserDao userDao) {
        User user = new User();
        user.setName("new user");
        return (int) userDao.insert(user);
    }

    public static int insertProductsList(ProductsListDao productsListDao, int createdBy) {

        ProductsList list = new ProductsList();
        list.setName("Some name");
        list.setCreatedBy(createdBy);

        return (int) productsListDao.insert(list);
    }

    public static List<ProductsList> createProductsLists(int count, int createdBy) {

        List<ProductsList> lists = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ProductsList productsList = new ProductsList();
            productsList.setName("Some name");
            productsList.setCreatedBy(createdBy);
            lists.add(productsList);
        }
        return lists;
    }

    public static void insertProductsLists(ProductsListDao productsListDao, List<ProductsList> listsToInsert) {
        for (ProductsList list : listsToInsert) {
            long insertedID = productsListDao.insert(list);
            list.setListID(insertedID);
        }
    }

    public static int insertCategory(CategoryDao categoryDao) {

        Category category = new Category();
        category.setName("Some name");

        return (int) categoryDao.insert(category);
    }

    public static int insertProduct(ProductDao productDao, int listID) {

        Product product = new Product();
        product.setName("Some name");
        product.setListID(listID);

        return (int) productDao.insert(product);
    }

    public static int insertProductTemplate(ProductTemplateDao productTemplateDao) {

        ProductTemplate template = new ProductTemplate();
        template.setName("Some name");

        return (int) productTemplateDao.insert(template);
    }
}
