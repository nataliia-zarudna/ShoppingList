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
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItemWithListStatistics;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Test Product List Dao methods
 */

@RunWith(AndroidJUnit4.class)
public class ProductTemplateDaoTest {

    private AppDatabase mDatabase;
    private ProductTemplateDao mSubjectDao;
    private CategoryDao mCategoryDao;
    private ProductListDao mProductListDao;
    private ProductDao mProductDao;

    private UUID mLesserCategoryID;
    private UUID mGreaterCategoryID;
    private UUID mUser_1;

    @Before
    public void createDB() {

        mDatabase = TestUtils.buildInMemoryDB();
        mSubjectDao = mDatabase.productTemplateDao();
        mProductListDao = mDatabase.productListDao();
        mProductDao = mDatabase.productDao();

        mUser_1 = TestUtils.insertUser(mDatabase.userDao());

        mCategoryDao = mDatabase.categoryDao();
        UUID categoryID_1 = TestUtils.insertCategory(mCategoryDao);
        UUID categoryID_2 = TestUtils.insertCategory(mCategoryDao);
        mLesserCategoryID = TestUtils.getLesserUUIDByString(categoryID_1, categoryID_2);
        mGreaterCategoryID = TestUtils.getGreaterUUIDByString(categoryID_1, categoryID_2);
    }

    @Test
    public void create() throws InterruptedException {

        ProductTemplate template = createTemplate();
        mSubjectDao.insert(template);
    }

    @Test
    public void createAndRead() throws InterruptedException {

        ProductTemplate template = createTemplate();
        template.setName("new name");
        template.setCategoryID(mLesserCategoryID);

        mSubjectDao.insert(template);

        LiveData<ProductTemplate> templateLiveData = mSubjectDao.findByID(template.getTemplateID());
        ProductTemplate insertedTemplate = TestUtils.getLiveDataValueSync(templateLiveData);

        assertThat(insertedTemplate, equalTo(template));
    }

    @Test
    public void create_withDefaultParams() throws InterruptedException {

        ProductTemplate template = createTemplate();

        assertThat(template.getCategoryID(), equalTo(Category.DEFAULT_CATEGORY_ID));
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedExceptionOnCreateWithNullName() throws InterruptedException {

        ProductTemplate template = createTemplate();
        template.setName(null);

        mSubjectDao.insert(template);
    }

    @Test
    public void setDefaultCategoryID() {

        ProductTemplate template_1 = new ProductTemplate();
        template_1.setName("Some name");
        template_1.setCategoryID(mLesserCategoryID);
        mSubjectDao.insert(template_1);

        ProductTemplate template_2 = new ProductTemplate();
        template_2.setName("Some name");
        template_2.setCategoryID(mGreaterCategoryID);
        mSubjectDao.insert(template_2);

        mSubjectDao.setDefaultCategoryID(mLesserCategoryID);

        ProductTemplate foundTemplate_1 = mSubjectDao.findByIDSync(template_1.getTemplateID());
        assertEquals(foundTemplate_1.getCategoryID(), Category.DEFAULT_CATEGORY_ID);

        ProductTemplate foundTemplate_2 = mSubjectDao.findByIDSync(template_2.getTemplateID());
        assertEquals(foundTemplate_2.getCategoryID(), template_2.getCategoryID());
    }

    @Test
    public void remove_testOnDeleteCascade() throws InterruptedException {

        ProductTemplate template = insertTemplate();

        UUID listID = TestUtils.insertProductsList(mProductListDao, mUser_1);
        Product product = new Product();
        product.setName("Some name");
        product.setListID(listID);
        product.setTemplateID(template.getTemplateID());
        mProductDao.insert(product);

        mSubjectDao.delete(template);

        Product foundProduct = mProductDao.findByIDSync(product.getProductID());
        assertNotNull(foundProduct);
        assertNull(foundProduct.getTemplateID());
    }

    @Test
    public void findAllSortByName() throws InterruptedException {

        List<ProductTemplate> templates = createTemplates(4);
        templates.get(0).setName("#1");
        templates.get(0).setCategoryID(mGreaterCategoryID);

        templates.get(1).setName("#3");

        templates.get(2).setName("#2");
        templates.get(2).setCategoryID(mLesserCategoryID);

        templates.get(3).setName("#4");
        templates.get(3).setCategoryID(mGreaterCategoryID);

        insertTemplates(templates);

        DataSource.Factory<Integer, CategoryTemplateItem> foundTemplatesDataSource =
                mSubjectDao.findAllSortByName();
        PagedList<CategoryTemplateItem> foundTemplates = TestUtils.getPagedListSync(foundTemplatesDataSource);

        List<CategoryTemplateItem> expectedTemplates = new ArrayList<>();
        expectedTemplates.add(new CategoryTemplateItem(templates.get(0)));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(2)));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(1)));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(3)));

        TestUtils.assertEquals(foundTemplates, expectedTemplates);
    }

    @Test
    public void findAllSortByName_groupedByCategory() throws InterruptedException {

        List<ProductTemplate> templates = createTemplates(4);
        templates.get(0).setName("#1");
        templates.get(0).setCategoryID(mGreaterCategoryID);

        templates.get(1).setName("#3");

        templates.get(2).setName("#2");
        templates.get(2).setCategoryID(mLesserCategoryID);

        templates.get(3).setName("#4");
        templates.get(3).setCategoryID(mGreaterCategoryID);

        insertTemplates(templates);

        DataSource.Factory<Integer, CategoryTemplateItem> foundTemplatesDataSource =
                mSubjectDao.findAllSortByNameWithCategory();
        PagedList<CategoryTemplateItem> foundTemplates = TestUtils.getPagedListSync(foundTemplatesDataSource);

        Category category1 = mCategoryDao.findByIDSync(mLesserCategoryID);
        Category category2 = mCategoryDao.findByIDSync(mGreaterCategoryID);
        Category defaultCategory = mCategoryDao.findByIDSync(Category.DEFAULT_CATEGORY_ID);

        List<CategoryTemplateItem> expectedTemplates = new ArrayList<>();
        expectedTemplates.add(new CategoryTemplateItem(category1));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(2)));
        expectedTemplates.add(new CategoryTemplateItem(category2));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(0)));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(3)));
        expectedTemplates.add(new CategoryTemplateItem(defaultCategory));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(1)));

        TestUtils.assertEquals(foundTemplates, expectedTemplates);
    }

    @Test
    public void findAllSortByName_withListStatistics() throws InterruptedException {

        List<ProductTemplate> templates = createTemplates(4);
        templates.get(0).setName("#1");
        templates.get(0).setCategoryID(mGreaterCategoryID);

        templates.get(1).setName("#3");

        templates.get(2).setName("#2");
        templates.get(2).setCategoryID(mLesserCategoryID);

        templates.get(3).setName("#4");
        templates.get(3).setCategoryID(mGreaterCategoryID);

        insertTemplates(templates);

        UUID listID = TestUtils.insertProductsList(mProductListDao, mUser_1);
        for (int i = 0; i < 2; i++) {
            Product product = new Product();
            product.setName("Some prod");
            product.setListID(listID);
            product.setTemplateID(templates.get(i).getTemplateID());
            mProductDao.insert(product);
        }

        DataSource.Factory<Integer, CategoryTemplateItemWithListStatistics> foundTemplatesDataSource =
                mSubjectDao.findAllSortByNameWithListStatistics(listID);
        PagedList<CategoryTemplateItemWithListStatistics> foundTemplates = TestUtils.getPagedListSync(foundTemplatesDataSource);

        List<CategoryTemplateItem> expectedTemplates = new ArrayList<>();
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(templates.get(0), true));
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(templates.get(2), false));
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(templates.get(1), true));
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(templates.get(3), false));

        TestUtils.assertEquals(foundTemplates, expectedTemplates);
    }

    @Test
    public void findAllSortByName_withListStatistics_groupedByCategory() throws InterruptedException {

        List<ProductTemplate> templates = createTemplates(4);
        templates.get(0).setName("#1");
        templates.get(0).setCategoryID(mGreaterCategoryID);

        templates.get(1).setName("#3");

        templates.get(2).setName("#2");
        templates.get(2).setCategoryID(mLesserCategoryID);

        templates.get(3).setName("#4");
        templates.get(3).setCategoryID(mGreaterCategoryID);

        insertTemplates(templates);

        UUID listID = TestUtils.insertProductsList(mProductListDao, mUser_1);
        for (int i = 0; i < 2; i++) {
            Product product = new Product();
            product.setName("Some prod");
            product.setListID(listID);
            product.setTemplateID(templates.get(i + 1).getTemplateID());
            mProductDao.insert(product);
        }

        DataSource.Factory<Integer, CategoryTemplateItemWithListStatistics> foundTemplatesDataSource =
                mSubjectDao.findAllSortByNameWithCategoryAndListStatistics(listID);
        PagedList<CategoryTemplateItemWithListStatistics> foundTemplates = TestUtils.getPagedListSync(foundTemplatesDataSource);

        Category category1 = mCategoryDao.findByIDSync(mLesserCategoryID);
        Category category2 = mCategoryDao.findByIDSync(mGreaterCategoryID);
        Category defaultCategory = mCategoryDao.findByIDSync(Category.DEFAULT_CATEGORY_ID);

        List<CategoryTemplateItem> expectedTemplates = new ArrayList<>();
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(category1));
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(templates.get(2), true));
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(category2));
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(templates.get(0), false));
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(templates.get(3), false));
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(defaultCategory));
        expectedTemplates.add(new CategoryTemplateItemWithListStatistics(templates.get(1), true));

        TestUtils.assertEquals(foundTemplates, expectedTemplates);
    }

    @Test
    public void findAllByNameLike_withNoListUsed() throws InterruptedException {

        List<ProductTemplate> templates = createTemplates(6);
        templates.get(0).setName("abc"); // -
        templates.get(1).setName("aBCde"); // +
        templates.get(2).setName("bcDef"); // +
        templates.get(3).setName("abcD"); // +
        templates.get(4).setName("Bce"); // -
        templates.get(5).setName("BCD"); // +

        insertTemplates(templates);

        LiveData<List<ProductTemplate>> resultListLiveData =
                mSubjectDao.findAllByNameLike("BcD", UUID.randomUUID());
        List<ProductTemplate> resultList = TestUtils.getLiveDataValueSync(resultListLiveData);

        List<ProductTemplate> expectedTemplates = new ArrayList<>();
        expectedTemplates.add(templates.get(3));
        expectedTemplates.add(templates.get(1));
        expectedTemplates.add(templates.get(5));
        expectedTemplates.add(templates.get(2));

        TestUtils.assertEquals(expectedTemplates, resultList);
    }

    @Test
    public void findAllByNameLike_withInListUsed() throws InterruptedException {

        List<ProductTemplate> templates = createTemplates(4);
        templates.get(0).setName("abc"); // -
        templates.get(1).setName("abcd"); // -
        templates.get(2).setName("aBCde"); // +
        templates.get(3).setName("abcdef"); // +

        insertTemplates(templates);

        UUID productListID_1 = TestUtils.insertProductsList(mProductListDao, mUser_1);
        Product product_1 = new Product();
        product_1.setName("Some name");
        product_1.setListID(productListID_1);
        product_1.setTemplateID(templates.get(1).getTemplateID());
        mProductDao.insert(product_1);

        UUID productListID_2 = TestUtils.insertProductsList(mProductListDao, mUser_1);
        Product product_2 = new Product();
        product_2.setName("Some name");
        product_2.setListID(productListID_2);
        product_2.setTemplateID(templates.get(3).getTemplateID());
        mProductDao.insert(product_2);

        LiveData<List<ProductTemplate>> resultListLiveData =
                mSubjectDao.findAllByNameLike("bcd", UUID.randomUUID());
        List<ProductTemplate> resultList = TestUtils.getLiveDataValueSync(resultListLiveData);

        List<ProductTemplate> expectedTemplates = new ArrayList<>();
        expectedTemplates.add(templates.get(2));
        expectedTemplates.add(templates.get(3));

        TestUtils.assertEquals(expectedTemplates, resultList);
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

    private ProductTemplate createTemplate() throws InterruptedException {
        return new ProductTemplate();
    }

    public List<ProductTemplate> createTemplates(int count) throws InterruptedException {

        List<ProductTemplate> templates = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            templates.add(createTemplate());
        }
        return templates;
    }

    private ProductTemplate insertTemplate() throws InterruptedException {
        ProductTemplate template = createTemplate();

        mSubjectDao.insert(template);
        LiveData<ProductTemplate> createdTemplate = mSubjectDao.findByID(template.getTemplateID());

        return TestUtils.getLiveDataValueSync(createdTemplate);
    }

    private void insertTemplates(List<ProductTemplate> templates) {
        for (ProductTemplate template : templates) {
            mSubjectDao.insert(template);
        }
    }
}
