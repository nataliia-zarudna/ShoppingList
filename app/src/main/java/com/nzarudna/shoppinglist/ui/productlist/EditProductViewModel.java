package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 2/26/18.
 */

public class EditProductViewModel extends ViewModel implements Observable {

    @Inject
    ProductTemplateRepository mTemplateRepository;

    @Bindable
    private Product mProduct;

    private PropertyChangeRegistry mRegistry = new PropertyChangeRegistry();

    public void setProduct(Product product) {
        this.mProduct = product;

        mRegistry.notifyChange(this, BR._all);
    }

    public LiveData<Product> getProduct(UUID productID) {
        return null;
    }

    public LiveData<List<ProductTemplate>> getNameAutocompleteList(String filterValue) {
        return mTemplateRepository.getTemplatesByNameLike(filterValue, mProduct.getListID());
    }

    public String getProductName() {
        return mProduct != null ? mProduct.getName() : "";
    }

    public void saveProduct() {

    }

    public String getDialogTitle() {
        return getProductName();
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.remove(onPropertyChangedCallback);
    }
}
