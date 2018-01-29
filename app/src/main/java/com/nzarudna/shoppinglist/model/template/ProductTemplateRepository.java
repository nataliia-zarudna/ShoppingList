package com.nzarudna.shoppinglist.model.template;

import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository class for work with product templates
 */
@Singleton
public class ProductTemplateRepository {

    @Inject
    public ProductTemplateRepository() {}

    public void createTemplate(ProductTemplate template) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void createTemplateFromProduct(Product product) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void updateTemplate(ProductTemplate template) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
