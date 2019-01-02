package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.BaseRepository;
import com.nzarudna.shoppinglist.model.exception.EmptyNameException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by nsirobaba on 1/29/18.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProductTemplateRepositoryTest extends BaseRepositoryTest<ProductTemplate> {

    @Mock
    private ProductTemplateDao mProductTemplateDao;
    @Mock
    private ProductDao mProductDao;

    private ProductTemplateRepository mSubject;
    private AppExecutors mAppExecutors;

    @Before
    public void setUp() {

        mAppExecutors = new TestAppExecutors();
        mSubject = new ProductTemplateRepository(mProductTemplateDao, mProductDao, mAppExecutors);
    }

    @Override
    protected BaseRepository<ProductTemplate> getRepositorySubject() {
        return mSubject;
    }

    @Test
    public void createTemplate() throws InterruptedException {

        final ProductTemplate template = new ProductTemplate();
        template.setName("some name");

        verifyCreate(template);

        verify(mProductTemplateDao).insert(template);
    }

    @Test
    public void create_nullNameError() throws InterruptedException {

        final ProductTemplate newProductTemplate = new ProductTemplate();

        verifyCreateWithException(newProductTemplate, EmptyNameException.class);
    }

    @Test
    public void create_emptyNameError() throws InterruptedException {

        final ProductTemplate newProductTemplate = new ProductTemplate();
        newProductTemplate.setName("   ");

        verifyCreateWithException(newProductTemplate, EmptyNameException.class);
    }

    @Test
    public void create_duplicateNameError() throws InterruptedException {

        String name = "some name";
        final ProductTemplate newProductTemplate = new ProductTemplate();
        newProductTemplate.setName(name);
        when(mProductTemplateDao.isTemplatesWithSameNameExists(name)).thenReturn(true);

        verifyCreateWithException(newProductTemplate, UniqueNameConstraintException.class);
    }

    @Test
    public void createTemplateFromProduct() throws InterruptedException {

        String name = "Some product";
        UUID categoryID = UUID.randomUUID();
        UUID unitID = UUID.randomUUID();

        Product product = new Product();
        product.setName(name);
        product.setCategoryID(categoryID);
        product.setUnitID(unitID);

        final ProductTemplate expectedTemplate = new ProductTemplate();
        expectedTemplate.setName(name);
        expectedTemplate.setCategoryID(categoryID);
        expectedTemplate.setUnitID(unitID);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        AsyncResultListener<ProductTemplate> asyncListener = Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {

            ProductTemplate resultTemplate = invocation.getArgument(0);
            assertTrue(AssertUtils.matchesExceptID(resultTemplate, expectedTemplate));

            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess(any(ProductTemplate.class));

        mSubject.createFromProductAsync(product, asyncListener);
        await(countDownLatch);

        verify(mProductTemplateDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedTemplate)));
        verify(asyncListener).onAsyncSuccess(any(ProductTemplate.class));
    }

    @Test
    public void createTemplateFromProduct_withoutFKs() throws InterruptedException {

        String name = "Some product";
        Product product = new Product();
        product.setName(name);

        final ProductTemplate expectedTemplate = new ProductTemplate();
        expectedTemplate.setName(name);
        expectedTemplate.setUnitID(null);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        AsyncResultListener<ProductTemplate> asyncListener = (AsyncResultListener<ProductTemplate>) Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {

            ProductTemplate resultTemplate = invocation.getArgument(0);
            assertTrue(AssertUtils.matchesExceptID(resultTemplate, expectedTemplate));

            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess(any(ProductTemplate.class));

        mSubject.createFromProductAsync(product, asyncListener);

        await(countDownLatch);

        verify(mProductTemplateDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedTemplate)));
        verify(asyncListener).onAsyncSuccess(any(ProductTemplate.class));
    }

    @Test
    public void updateTemplate_andClearLinksToProducts() throws InterruptedException {

        UUID templateID = UUID.randomUUID();

        final ProductTemplate template = new ProductTemplate();
        template.setName("Some name");
        template.setTemplateID(templateID);

        verifyUpdate(template);

        verify(mProductTemplateDao).update(template);
        verify(mProductDao).clearTemplateIDs(templateID);
    }

    @Test
    public void update_nullNameError() throws InterruptedException {

        final ProductTemplate newProductTemplate = new ProductTemplate();

        verifyUpdateWithException(newProductTemplate, EmptyNameException.class);
    }

    @Test
    public void update_emptyNameError() throws InterruptedException {

        final ProductTemplate newProductTemplate = new ProductTemplate();
        newProductTemplate.setName("   ");

        verifyUpdateWithException(newProductTemplate, EmptyNameException.class);
    }

    @Test
    public void update_duplicateNameError() throws InterruptedException {

        String name = "some name";
        final ProductTemplate newProductTemplate = new ProductTemplate();
        newProductTemplate.setName(name);
        when(mProductTemplateDao.isTemplatesWithSameNameExists(name)).thenReturn(true);

        verifyUpdateWithException(newProductTemplate, UniqueNameConstraintException.class);
    }

    @Test
    public void removeTemplate() throws InterruptedException {
        ProductTemplate template = new ProductTemplate();

        verifyRemove(template);

        verify(mProductTemplateDao).delete(template);
    }

    @Test
    public void findAll() {
        mSubject.getTemplates(false);

        verify(mProductTemplateDao).findAllSortByName();
    }

    @Test
    public void findAll_groupedByCategory() {
        mSubject.getTemplates(true);

        verify(mProductTemplateDao).findAllSortByNameWithCategory();
    }

    @Test
    public void findAll_withListStatisctics() {
        UUID listID = UUID.randomUUID();
        mSubject.getTemplates(false, listID);

        verify(mProductTemplateDao).findAllSortByNameWithListStatistics(listID);
    }

    @Test
    public void findAll_withListStatisctics_groupedByCategory() {
        UUID listID = UUID.randomUUID();
        mSubject.getTemplates(true, listID);

        verify(mProductTemplateDao).findAllSortByNameWithCategoryAndListStatistics(listID);
    }

    @Test
    public void findAll_byNameLike() {
        String name = "Some name";
        UUID listID = UUID.randomUUID();
        mSubject.getTemplatesByNameLike(name, listID);

        verify(mProductTemplateDao).findAllByNameLike(name, listID);
    }
}
