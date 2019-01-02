package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.BaseRepository;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.model.exception.EmptyNameException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by nsirobaba on 2/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryRepositoryTest extends BaseRepositoryTest<Category> {

    private CategoryRepository mSubject;
    private AppExecutors mAppExecutors;

    @Mock
    private CategoryDao mCategoryDao;
    @Mock
    private ProductDao mProductDao;
    @Mock
    private ProductTemplateDao mProductTemplateDao;

    @Before
    public void setUp() {
        mAppExecutors = new TestAppExecutors();
        mSubject = new CategoryRepository(mCategoryDao, mProductDao, mProductTemplateDao, mAppExecutors);
    }

    @Override
    protected BaseRepository<Category> getRepositorySubject() {
        return mSubject;
    }

    @Test
    public void create() throws InterruptedException {
        Category category = new Category();
        category.setName("some name");

        verifyCreate(category);

        verify(mCategoryDao).insert(category);
    }

    @Test
    public void create_trimName() throws InterruptedException, CloneNotSupportedException {
        String name = " some name   ";

        Category category = new Category();
        category.setName(name);

        Category resultCategory = category.clone();
        resultCategory.setName(name.trim());

        verifyCreate(category, resultCategory);

        verify(mCategoryDao).insert(category);
    }

    @Test
    public void create_nullNameError() throws InterruptedException {
        final Category newCategory = new Category();
        verifyCreateWithException(newCategory, EmptyNameException.class);
    }

    @Test
    public void create_emptyNameError() throws InterruptedException {
        final Category newCategory = new Category();
        newCategory.setName("  ");

        verifyCreateWithException(newCategory, EmptyNameException.class);
    }

    @Test
    public void create_duplicateNameError() throws InterruptedException {

        String categoryName = "some name";
        when(mCategoryDao.findBySimilarName(categoryName)).thenReturn(UUID.randomUUID());

        final Category newCategory = new Category();
        newCategory.setName(categoryName);

        verifyCreateWithException(newCategory, UniqueNameConstraintException.class);
    }

    @Test
    public void update() throws InterruptedException {
        Category category = new Category();
        category.setName("Some name");

        verifyUpdate(category);

        verify(mCategoryDao).update(category);
    }

    @Test
    public void update_trimName() throws InterruptedException, CloneNotSupportedException {
        String name = " some name   ";

        Category category = new Category();
        category.setName(name);

        Category savedCategory = category.clone();
        savedCategory.setName(name.trim());

        verifyUpdate(category, savedCategory);

        verify(mCategoryDao).update(category);
    }

    @Test
    public void update_nullNameError() throws InterruptedException {
        final Category newCategory = new Category();
        verifyUpdateWithException(newCategory, EmptyNameException.class);
    }

    @Test
    public void update_emptyNameError() throws InterruptedException {
        final Category newCategory = new Category();
        newCategory.setName("   ");
        verifyUpdateWithException(newCategory, EmptyNameException.class);
    }

    @Test
    public void update_duplicateNameError() throws InterruptedException {

        String categoryName = "some name";
        when(mCategoryDao.findBySimilarName(categoryName)).thenReturn(UUID.randomUUID());

        final Category newCategory = new Category();
        newCategory.setName(categoryName);

        verifyUpdateWithException(newCategory, UniqueNameConstraintException.class);
    }

    @Test
    public void remove() throws InterruptedException {
        Category category = new Category();

        verifyRemove(category);

        verify(mCategoryDao).delete(category);
    }

    @Test
    public void remove_setDefaultCategoryToFKs() throws InterruptedException {

        Category category = new Category();

        verifyRemove(category);

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
