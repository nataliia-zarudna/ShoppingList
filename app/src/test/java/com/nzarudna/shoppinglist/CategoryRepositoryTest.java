package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

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
        Category category = new Category("Some name");
        mSubject.create(category);

        verify(mCategoryDao).insert(category);
    }

    @Test
    public void update() {
        Category category = new Category("Some name");
        mSubject.update(category);

        verify(mCategoryDao).update(category);
    }

    @Test
    public void remove() {
        Category category = new Category("Some name");
        mSubject.remove(category);

        verify(mCategoryDao).delete(category);
    }

    @Test
    public void remove_setDefaultCategoryToFKs() {

        Category category = new Category("Some name");
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
}