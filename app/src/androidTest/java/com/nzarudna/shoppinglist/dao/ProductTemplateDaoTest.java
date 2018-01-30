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
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Test Product List Dao methods
 */

@RunWith(AndroidJUnit4.class)
public class ProductTemplateDaoTest {

    private AppDatabase mDatabase;
    private ProductTemplateDao mSubjectDao;
    private CategoryDao mCategoryDao;
    private int mCategoryID_1;
    private int mCategoryID_2;

    @Before
    public void createDB() {

        Context context = InstrumentationRegistry.getContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mSubjectDao = mDatabase.productTemplateDao();

        mCategoryDao = mDatabase.categoryDao();
        mCategoryID_1 = TestUtils.insertCategory(mCategoryDao);
        mCategoryID_2 = TestUtils.insertCategory(mCategoryDao);
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

    @Test
    public void create() throws InterruptedException {

        ProductTemplate template = createTemplate();
        long listID = mSubjectDao.insert(template);

        assertThat(listID, not(0l));
    }

    @Test
    public void createAndRead() throws InterruptedException {

        ProductTemplate template = createTemplate();
        template.setName("new name");
        template.setCategoryID(mCategoryID_1);

        int templateID = (int) mSubjectDao.insert(template);
        template.setTemplateID(templateID);

        LiveData<ProductTemplate> templateLiveData = mSubjectDao.findByID(templateID);
        ProductTemplate insertedTemplate = TestUtils.getLiveDataValueSync(templateLiveData);

        assertThat(insertedTemplate, equalTo(template));
    }

    @Test(expected = SQLiteConstraintException.class)
    public void constrainedExceptionOnCreateWithNullName() throws InterruptedException {

        ProductTemplate template = createTemplate();
        template.setName(null);

        mSubjectDao.insert(template);
    }

    @Test
    public void findAllSortByName() throws InterruptedException {

        List<ProductTemplate> templates = createTemplates(3);
        templates.get(0).setName("#1");
        templates.get(0).setCategoryID(mCategoryID_2);

        templates.get(1).setName("#3");

        templates.get(2).setName("#2");
        templates.get(2).setCategoryID(mCategoryID_1);

        templates.get(3).setName("#4");
        templates.get(3).setCategoryID(mCategoryID_2);

        insertTemplates(templates);

        DataSource.Factory<Integer, CategoryTemplateItem> foundTemplatesDataSource =
                mSubjectDao.findAllSortByName();
        PagedList<CategoryTemplateItem> foundTemplates = TestUtils.getPagedListSync(foundTemplatesDataSource);

        Category category1 = mCategoryDao.findByIDSync(mCategoryID_1);
        Category category2 = mCategoryDao.findByIDSync(mCategoryID_2);
        List<CategoryTemplateItem> expectedTemplates = new ArrayList<>();
        expectedTemplates.add(new CategoryTemplateItem(category1));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(2)));
        expectedTemplates.add(new CategoryTemplateItem(category2));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(0)));
        expectedTemplates.add(new CategoryTemplateItem(templates.get(3)));

        TestUtils.assertEquals(expectedTemplates, foundTemplates);
    }

    @Test
    public void findAllSortByCategoryIDAndName() throws InterruptedException {

        List<ProductTemplate> templates = createTemplates(4);
        templates.get(0).setName("#1");

        templates.get(1).setCategoryID(mCategoryID_1);
        templates.get(1).setName("#4");

        templates.get(2).setCategoryID(mCategoryID_2);
        templates.get(2).setName("#3");

        templates.get(3).setCategoryID(mCategoryID_1);
        templates.get(3).setName("#2");

        insertTemplates(templates);

        List<ProductTemplate> expectedTemplates = new ArrayList<>();
        expectedTemplates.add(templates.get(0));
        expectedTemplates.add(templates.get(3));
        expectedTemplates.add(templates.get(1));
        expectedTemplates.add(templates.get(2));

        DataSource.Factory<Integer, ProductTemplate> foundTemplatesDataSource =
                mSubjectDao.findAllSortByCategoryIDAndName();
        PagedList<ProductTemplate> foundTemplates = TestUtils.getPagedListSync(foundTemplatesDataSource);

        TestUtils.assertEquals(expectedTemplates, foundTemplates);
    }

    private ProductTemplate createTemplate() throws InterruptedException {
        ProductTemplate template = new ProductTemplate();
        template.setName("Some name");

        return template;
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

        long templateID = mSubjectDao.insert(template);
        LiveData<ProductTemplate> createdTemplate = mSubjectDao.findByID((int) templateID);

        return TestUtils.getLiveDataValueSync(createdTemplate);
    }

    private void insertTemplates(List<ProductTemplate> templates) {
        for (ProductTemplate template : templates) {
            long insertedID = mSubjectDao.insert(template);
            template.setTemplateID((int) insertedID);
        }
    }
}
