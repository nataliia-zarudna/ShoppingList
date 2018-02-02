package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

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
    public void createTemplate() {

        ProductTemplate template = new ProductTemplate("Some name");
        mSubject.createTemplate(template);

        verify(mProductTemplateDao).insert(template);
    }

    @Test
    public void createTemplateFromProduct() {

        String name = "Some product";
        UUID categoryID = UUID.randomUUID();
        UUID unitID = UUID.randomUUID();

        Product product = new Product(name);
        product.setCategoryID(categoryID);
        product.setUnitID(unitID);

        mSubject.createTemplateFromProduct(product);

        final ProductTemplate expectedTemplate = new ProductTemplate(name);
        expectedTemplate.setCategoryID(categoryID);
        expectedTemplate.setUnitID(unitID);

        verify(mProductTemplateDao).insert(argThat(new ArgumentMatcher<ProductTemplate>() {
            @Override
            public boolean matches(ProductTemplate argument) {
                return equalsWithoutPK(expectedTemplate, argument);
            }
        }));
    }

    @Test
    public void createTemplateFromProduct_withoutFKs() {

        String name = "Some product";
        Product product = new Product(name);

        mSubject.createTemplateFromProduct(product);

        final ProductTemplate expectedTemplate = new ProductTemplate(name);
        expectedTemplate.setUnitID(null);

        verify(mProductTemplateDao).insert(argThat(new ArgumentMatcher<ProductTemplate>() {
            @Override
            public boolean matches(ProductTemplate argument) {
                return equalsWithoutPK(expectedTemplate, argument);
            }
        }));
    }

    @Test
    public void updateTemplate_andClearLinksToProducts() {

        UUID templateID = UUID.randomUUID();

        ProductTemplate template = new ProductTemplate("Some name");
        template.setTemplateID(templateID);

        mSubject.updateTemplate(template);

        verify(mProductTemplateDao).update(template);
        verify(mProductDao).clearTemplateIDs(templateID);
    }

    @Test
    public void removeTemplate() {

        ProductTemplate template = new ProductTemplate("Some name");
        mSubject.removeTemplate(template);

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

    private boolean equalsWithoutPK(ProductTemplate template1, ProductTemplate template2) {
        if (!template1.getName().equals(template2.getName())) {
            return false;
        }
        if (!template1.getCategoryID().equals(template2.getCategoryID())) {
            return false;
        }
        if (template1.getTemplateID() != null
                ? template1.getTemplateID().equals(template2.getTemplateID()) : template2.getTemplateID() == null) {
            return false;
        }

        return true;
    }
}
