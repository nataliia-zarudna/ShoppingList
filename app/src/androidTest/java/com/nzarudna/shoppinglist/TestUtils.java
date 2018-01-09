package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.model.Category;
import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.User;
import com.nzarudna.shoppinglist.model.dao.CategoryDao;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;
import com.nzarudna.shoppinglist.model.dao.UserDao;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Class with utils test methods
 */

public class TestUtils {

    public static <T> T findByIDSync(LiveData<T> listLiveData) throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);
        final T[] insertedList = (T[]) new Object[1];
        listLiveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(@Nullable T productsList) {

                insertedList[0] = productsList;
                latch.countDown();
            }
        });

        latch.await(3000, TimeUnit.MILLISECONDS);

        return insertedList[0];
    }

    public static <T> PagedList<T> findSync(DataSource.Factory<Integer, T> dataSourceFactory) throws InterruptedException {

        final PagedList<T>[] foundProductsList = new PagedList[1];
        final CountDownLatch latch = new CountDownLatch(1);

        LiveData<PagedList<T>> livePagedProductsList = new LivePagedListBuilder<>(dataSourceFactory, 4).build();
        livePagedProductsList.observeForever(new Observer<PagedList<T>>() {
            @Override
            public void onChanged(@Nullable PagedList<T> list) {

                foundProductsList[0] = list;
                latch.countDown();
            }
        });
        latch.await(3000, TimeUnit.MILLISECONDS);

        return foundProductsList[0];
    }

    public static void assertPagedListEqualsToList(List expected, PagedList actual) {

        try {

            assertEquals(expected.size(), actual.size());

            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i), actual.get(i));
            }
        } catch (AssertionError e) {
            throw new AssertionError("List is not equals to paged list: " +
                    "\nexpected: " + expected + ",\nactual: " + actual, e);
        }
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

    public static int insertCategory(CategoryDao categoryDao) {

        Category category = new Category();
        category.setName("Some name");

        return (int) categoryDao.insert(category);
    }
}
