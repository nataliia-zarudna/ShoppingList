package com.nzarudna.shoppinglist;

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

        ProductTemplate template = new ProductTemplate();
        mSubject.createTemplate(template);

        verify(mProductTemplateDao).insert(template);
    }

    @Test
    public void createTemplateFromProduct() {

        String name = "Some product";
        int categoryID = 8;
        int unitID = 7;

        Product product = new Product();
        product.setName(name);
        product.setCategoryID(categoryID);
        product.setUnitID(unitID);

        mSubject.createTemplateFromProduct(product);

        ProductTemplate expectedTemplate = new ProductTemplate();
        expectedTemplate.setName(name);
        expectedTemplate.setCategoryID(categoryID);
        expectedTemplate.setUnitID(unitID);

        verify(mProductTemplateDao).insert(expectedTemplate);
    }

    @Test
    public void createTemplateFromProduct_withoutFKs() {

        String name = "Some product";

        Product product = new Product();
        product.setName(name);

        mSubject.createTemplateFromProduct(product);

        ProductTemplate expectedTemplate = new ProductTemplate();
        expectedTemplate.setName(name);
        expectedTemplate.setCategoryID(null);
        expectedTemplate.setUnitID(null);

        verify(mProductTemplateDao).insert(expectedTemplate);
    }

    @Test
    public void updateTemplate_andClearLinksToProducts() {

        int templateID = 5;

        ProductTemplate template = new ProductTemplate();
        template.setName("Some name");
        template.setTemplateID(templateID);

        mSubject.updateTemplate(template);

        verify(mProductTemplateDao).update(template);
        verify(mProductDao).clearTemplateIDs(templateID);
    }

    @Test
    public void removeTemplate() {

        ProductTemplate template = new ProductTemplate();
        mSubject.removeTemplate(template);

        verify(mProductTemplateDao).delete(template);
    }
}
