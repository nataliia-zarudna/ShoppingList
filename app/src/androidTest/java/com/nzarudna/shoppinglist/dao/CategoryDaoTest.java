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
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.category.CategoryStatisticsItem;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;

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

        mAppDatabase = TestUtils.buildInMemoryDB();
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

    @Test
    public void findAllWithStatistics() throws InterruptedException {

        List<Category> categories = createCategories(3);

        int userID = TestUtils.insertUser(mAppDatabase.userDao());
        int listID = TestUtils.insertProductsList(mAppDatabase.productListDao(), userID);

        Product product = new Product();
        product.setName("Some product");
        product.setListID(listID);
        product.setCategoryID(categories.get(1).getCategoryID());
        mAppDatabase.productDao().insert(product);

        ProductTemplate template = new ProductTemplate();
        template.setName("Some template");
        template.setCategoryID(categories.get(2).getCategoryID());

        DataSource.Factory<Integer, CategoryStatisticsItem> factory =
                mSubjectDao.findAllWithStatistics();
        PagedList<CategoryStatisticsItem> foundList = TestUtils.getPagedListSync(factory);

        List<CategoryStatisticsItem> expectedList = new ArrayList<>();
        expectedList.add(new CategoryStatisticsItem(categories.get(0), false));
        expectedList.add(new CategoryStatisticsItem(categories.get(1), false));
        expectedList.add(new CategoryStatisticsItem(categories.get(2), false));

        TestUtils.assertEquals(foundList, expectedList);
    }

    @After
    public void closeDB() {
        mAppDatabase.close();
    }

    private Category createCategory(String name) throws InterruptedException {
        Category category = new Category();
        category.setName(name);

        int categoryID = (int) mSubjectDao.insert(category);
        category.setCategoryID(categoryID);

        return category;
    }

    private List<Category> createCategories(int count) throws InterruptedException {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = "Category #" + i;
            categories.add(createCategory(name));
        }
        return categories;
    }
}
