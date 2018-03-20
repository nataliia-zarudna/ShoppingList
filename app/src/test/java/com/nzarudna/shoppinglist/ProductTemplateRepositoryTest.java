package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by nsirobaba on 1/29/18.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProductTemplateRepositoryTest {

    @Mock
    private ProductTemplateDao mProductTemplateDao;
    @Mock
    private ProductDao mProductDao;

    private ProductTemplateRepository mSubject;

    @Before
    public void setUp() {

        mSubject = new ProductTemplateRepository(mProductTemplateDao, mProductDao);
    }

    @Test
    public void createTemplate() throws InterruptedException {

        final ProductTemplate template = new ProductTemplate();
        template.setName("some name");

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.createTemplate(template, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate resultTemplate) {
                assertEquals(resultTemplate, template);
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductTemplateDao).insert(template);
    }

    @Test
    public void create_nullNameError() throws InterruptedException {

        final ProductTemplate newProductTemplate = new ProductTemplate();

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.createTemplate(newProductTemplate, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate template) {

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

        final ProductTemplate newProductTemplate = new ProductTemplate();
        newProductTemplate.setName("   ");

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.createTemplate(newProductTemplate, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate template) {

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

        String name = "some name";
        final ProductTemplate newProductTemplate = new ProductTemplate();
        newProductTemplate.setName(name);
        when(mProductTemplateDao.isTemplatesWithSameNameExists(name)).thenReturn(true);

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.createTemplate(newProductTemplate, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate template) {

            }

            @Override
            public void onAsyncError(Exception e) {
                assertTrue(e instanceof UniqueNameConstraintException);

                countDown.countDown();
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
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
        mSubject.createTemplateFromProduct(product, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate resultTemplate) {
                assertTrue(AssertUtils.matchesExceptID(resultTemplate, expectedTemplate));
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductTemplateDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedTemplate)));
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
        mSubject.createTemplateFromProduct(product, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate resultTemplate) {
                assertTrue(AssertUtils.matchesExceptID(resultTemplate, expectedTemplate));
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductTemplateDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedTemplate)));
    }

    @Test
    public void updateTemplate_andClearLinksToProducts() throws InterruptedException {

        UUID templateID = UUID.randomUUID();

        final ProductTemplate template = new ProductTemplate();
        template.setName("Some name");
        template.setTemplateID(templateID);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.updateTemplate(template, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate resultTemplate) {
                assertEquals(resultTemplate, template);
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductTemplateDao).update(template);
        verify(mProductDao).clearTemplateIDs(templateID);
    }

    @Test
    public void update_nullNameError() throws InterruptedException {

        final ProductTemplate newProductTemplate = new ProductTemplate();

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.updateTemplate(newProductTemplate, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate template) {

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

        final ProductTemplate newProductTemplate = new ProductTemplate();
        newProductTemplate.setName("   ");

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.updateTemplate(newProductTemplate, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate template) {

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

        String name = "some name";
        final ProductTemplate newProductTemplate = new ProductTemplate();
        newProductTemplate.setName(name);
        when(mProductTemplateDao.isTemplatesWithSameNameExists(name)).thenReturn(true);

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.updateTemplate(newProductTemplate, new AsyncResultListener<ProductTemplate>() {
            @Override
            public void onAsyncSuccess(ProductTemplate template) {

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
    public void removeTemplate() {

        ProductTemplate template = new ProductTemplate();
        mSubject.remove(template);

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
