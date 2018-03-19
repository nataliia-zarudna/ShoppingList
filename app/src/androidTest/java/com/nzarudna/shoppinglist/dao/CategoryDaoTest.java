package com.nzarudna.shoppinglist.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
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
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

        mSubjectDao.insert(category);
    }

    @Test
    public void createAndRead() throws InterruptedException {

        Category category = new Category();
        category.setName("New name");

        mSubjectDao.insert(category);

        LiveData<Category> categoryLiveData = mSubjectDao.findByID(category.getCategoryID());
        Category foundCategory = TestUtils.getLiveDataValueSync(categoryLiveData);

        assertEquals(category, foundCategory);
    }

    @Test
    public void remove() throws InterruptedException {

        Category category = createCategory();

        mSubjectDao.delete(category);

        Category foundCategory = mSubjectDao.findByIDSync(category.getCategoryID());

        assertNull(foundCategory);
    }

    @Test
    public void remove_testOnDeleteCascade() throws InterruptedException {

        Category category = createCategory();

        UUID userID = TestUtils.insertUser(mAppDatabase.userDao());
        UUID listID = TestUtils.insertProductsList(mAppDatabase.productListDao(), userID);

        Product product = new Product();
        product.setName("Some name");
        product.setListID(listID);
        product.setCategoryID(category.getCategoryID());
        mAppDatabase.productDao().insert(product);

        ProductTemplate template = new ProductTemplate();
        template.setName("Some template");
        template.setCategoryID(category.getCategoryID());
        mAppDatabase.productTemplateDao().insert(template);

        mSubjectDao.delete(category);

        Product foundProduct = mAppDatabase.productDao().findByIDSync(product.getProductID());
        assertNotNull(foundProduct);
        assertNull(foundProduct.getCategoryID());

        ProductTemplate foundTemplate = mAppDatabase.productTemplateDao().findByIDSync(template.getTemplateID());
        assertNotNull(foundTemplate);
        assertNull(foundTemplate.getCategoryID());
    }

    @Test
    public void findAll() throws InterruptedException {

        List<Category> createdCategories = createCategories(3);

        DataSource.Factory<Integer, Category> foundCategoriesDataSource = mSubjectDao.findAll();
        PagedList<Category> actualCategories = TestUtils.getPagedListSync(foundCategoriesDataSource);

        TestUtils.assertEquals(createdCategories, actualCategories);
    }

    @Test
    public void findAllLiveData() throws InterruptedException {

        List<Category> createdCategories = createCategories(3);

        LiveData<List<Category>> foundCategoriesLiveData = mSubjectDao.findAllLiveData();
        List<Category> actualCategories = TestUtils.getLiveDataValueSync(foundCategoriesLiveData);

        TestUtils.assertEquals(createdCategories, actualCategories);
    }

    @Test
    public void findBySimilarName() throws InterruptedException {

        Category category_1 = createCategory("category 1");
        Category category_2 = createCategory("CaTeGoRy");
        Category category_3 = createCategory("something");

        UUID foundCategoryID = mSubjectDao.findBySimilarName("category");

        assertEquals(category_2.getCategoryID(), foundCategoryID);
    }

    @Test
    public void findBySimilarName_emptyResult() throws InterruptedException {

        Category category_1 = createCategory("category 1");
        Category category_2 = createCategory("CaTeGoRy123");
        Category category_3 = createCategory("something");

        UUID foundCategoryID = mSubjectDao.findBySimilarName("category");

        assertNull(foundCategoryID);
    }

    @Test
    public void findAllWithStatistics() throws InterruptedException {

        List<Category> categories = createCategories(3);

        UUID userID = TestUtils.insertUser(mAppDatabase.userDao());
        UUID listID = TestUtils.insertProductsList(mAppDatabase.productListDao(), userID);

        Product product = new Product();
        product.setName("Some product");
        product.setListID(listID);
        product.setCategoryID(categories.get(1).getCategoryID());
        mAppDatabase.productDao().insert(product);

        ProductTemplate template = new ProductTemplate();
        template.setName("Some template");
        template.setCategoryID(categories.get(2).getCategoryID());
        mAppDatabase.productTemplateDao().insert(template);

        DataSource.Factory<Integer, CategoryStatisticsItem> factory =
                mSubjectDao.findAllWithStatistics();
        PagedList<CategoryStatisticsItem> foundList = TestUtils.getPagedListSync(factory);

        List<CategoryStatisticsItem> expectedList = new ArrayList<>();
        expectedList.add(new CategoryStatisticsItem(categories.get(0), false));
        expectedList.add(new CategoryStatisticsItem(categories.get(1), true));
        expectedList.add(new CategoryStatisticsItem(categories.get(2), true));

        TestUtils.assertEquals(foundList, expectedList);
    }

    @After
    public void closeDB() {
        mAppDatabase.close();
    }

    private Category createCategory() throws InterruptedException {
        return createCategory("Some name");
    }

    private Category createCategory(String name) throws InterruptedException {
        Category category = new Category();
        category.setName(name);
        mSubjectDao.insert(category);
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
