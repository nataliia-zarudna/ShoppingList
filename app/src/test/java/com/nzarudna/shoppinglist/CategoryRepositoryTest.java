package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by nsirobaba on 2/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryRepositoryTest {

    private CategoryRepository mSubject;

    @Mock
    private CategoryDao mCategoryDao;
    @Mock
    private ProductDao mProductDao;
    @Mock
    private ProductTemplateDao mProductTemplateDao;

    @Before
    public void setUp() {
        mSubject = new CategoryRepository(mCategoryDao, mProductDao, mProductTemplateDao);
    }

    @Test
    public void create() {
        Category category = new Category();
        category.setName("some name");
        mSubject.create(category,  null);

        verify(mCategoryDao).insert(category);
    }

    @Test
    public void create_trimName() {
        String name = " some name   ";

        Category category = new Category();
        category.setName(name);
        mSubject.create(category,  null);

        category.setName(name.trim());

        verify(mCategoryDao).insert(category);
    }

    @Test
    public void create_callListenerCallback() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Category newCategory = new Category();
        newCategory.setName("some name");
        mSubject.create(newCategory, new AsyncResultListener<Category>() {
            @Override
            public void onAsyncSuccess(Category category) {

                assertEquals(newCategory, category);
                countDown.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);

        verify(mCategoryDao).insert(newCategory);
    }

    @Test
    public void create_nullNameError() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Category newCategory = new Category();
        mSubject.create(newCategory, new AsyncResultListener<Category>() {
            @Override
            public void onAsyncSuccess(Category category) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void create_emptyNameError() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Category newCategory = new Category();
        newCategory.setName("  ");
        mSubject.create(newCategory, new AsyncResultListener<Category>() {
            @Override
            public void onAsyncSuccess(Category category) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void create_duplicateNameError() throws InterruptedException {

        String categoryName = "some name";
        when(mCategoryDao.findBySimilarName(categoryName)).thenReturn(UUID.randomUUID());

        final CountDownLatch countDown = new CountDownLatch(1);


        final Category newCategory = new Category();
        newCategory.setName(categoryName);
        mSubject.create(newCategory, new AsyncResultListener<Category>() {
            @Override
            public void onAsyncSuccess(Category category) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof UniqueNameConstraintException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void update() {
        Category category = new Category();
        category.setName("Some name");
        mSubject.update(category, null);

        verify(mCategoryDao).update(category);
    }

    @Test
    public void update_trimName() {
        String name = " some name   ";

        Category category = new Category();
        category.setName(name);
        mSubject.update(category,  null);

        category.setName(name.trim());

        verify(mCategoryDao).update(category);
    }

    @Test
    public void update_callListenerCallback() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Category newCategory = new Category();
        newCategory.setName("some name");
        mSubject.update(newCategory, new AsyncResultListener<Category>() {
            @Override
            public void onAsyncSuccess(Category category) {

                assertEquals(newCategory, category);
                countDown.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);

        verify(mCategoryDao).update(newCategory);
    }

    @Test
    public void update_nullNameError() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Category newCategory = new Category();
        mSubject.update(newCategory, new AsyncResultListener<Category>() {
            @Override
            public void onAsyncSuccess(Category category) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void update_emptyNameError() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Category newCategory = new Category();
        newCategory.setName("   ");
        mSubject.update(newCategory, new AsyncResultListener<Category>() {
            @Override
            public void onAsyncSuccess(Category category) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void update_duplicateNameError() throws InterruptedException {

        String categoryName = "some name";
        when(mCategoryDao.findBySimilarName(categoryName)).thenReturn(UUID.randomUUID());

        final CountDownLatch countDown = new CountDownLatch(1);

        final Category newCategory = new Category();
        newCategory.setName(categoryName);
        mSubject.update(newCategory, new AsyncResultListener<Category>() {
            @Override
            public void onAsyncSuccess(Category category) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof UniqueNameConstraintException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void remove() {
        Category category = new Category();
        mSubject.remove(category);

        verify(mCategoryDao).delete(category);
    }

    @Test
    public void remove_setDefaultCategoryToFKs() {

        Category category = new Category();
        mSubject.remove(category);

        verify(mProductDao).setDefaultCategoryID(category.getCategoryID());
        verify(mProductTemplateDao).setDefaultCategoryID(category.getCategoryID());
    }

    @Test
    public void findAll() {
        mSubject.getAllCategories();

        verify(mCategoryDao).findAll();
    }

    @Test
    public void findAllWithStatistics() {
        mSubject.getCategoriesWithStatistics();

        verify(mCategoryDao).findAllWithStatistics();
    }

    @Test
    public void findAllLiveData() {
        mSubject.getAvailableCategories();

        verify(mCategoryDao).findAllLiveData();
    }
}
