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
import com.nzarudna.shoppinglist.product.Category;
import com.nzarudna.shoppinglist.persistence.CategoryDao;
import com.nzarudna.shoppinglist.persistence.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests for Category Dao
 */
@RunWith(AndroidJUnit4.class)
public class CategoryDaoTest {

    private AppDatabase mAppDatabase;
    private CategoryDao mSubjectDao;

    @Before
    public void createDB() {

        Context context = InstrumentationRegistry.getContext();
        mAppDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mSubjectDao = mAppDatabase.categoryDao();
    }

    @Test
    public void create() {

        Category category = new Category();
        category.setName("New name");

        long categoryID = mSubjectDao.insert(category);

        assertThat(categoryID, not(0l));
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedExceptionOnCreateWithNullName() {

        Category category = new Category();
        category.setName(null);

        mSubjectDao.insert(category);
    }

    @Test
    public void createAndRead() throws InterruptedException {

        Category category = new Category();
        category.setName("New name");

        long categoryID = mSubjectDao.insert(category);
        category.setCategoryID((int) categoryID);

        LiveData<Category> categoryLiveData = mSubjectDao.findByID((int) categoryID);
        Category foundCategory = TestUtils.getLiveDataValueSync(categoryLiveData);

        assertEquals(category, foundCategory);
    }

    @Test
    public void findAll() throws InterruptedException {

        List<Category> createdCategories = createCategories(3);

        DataSource.Factory<Integer, Category> foundCategoriesDataSource = mSubjectDao.findAll();
        PagedList<Category> actualCategories = TestUtils.getPagedListSync(foundCategoriesDataSource);

        TestUtils.assertEquals(createdCategories, actualCategories);
    }

    @After
    public void closeDB() {
        mAppDatabase.close();
    }

    private Category createCategory() throws InterruptedException {
        Category category = new Category();
        category.setName("New name");

        long categoryID = mSubjectDao.insert(category);
        LiveData<Category> categoryLiveData = mSubjectDao.findByID((int) categoryID);

        return TestUtils.getLiveDataValueSync(categoryLiveData);
    }

    private List<Category> createCategories(int count) throws InterruptedException {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            categories.add(createCategory());
        }
        return categories;
    }
}
