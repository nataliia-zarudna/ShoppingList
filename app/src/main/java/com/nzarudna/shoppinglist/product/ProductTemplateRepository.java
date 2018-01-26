package com.nzarudna.shoppinglist.product;

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
