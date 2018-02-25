package com.nzarudna.shoppinglist.dependency;

import android.content.Context;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.persistence.RoomDaoModule;
import com.nzarudna.shoppinglist.ui.productlist.CategoryProductItemViewModel;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;
import com.nzarudna.shoppinglist.ui.productlists.ProductListItemViewModel;
import com.nzarudna.shoppinglist.ui.productlists.ProductListsViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component with application dependencies
 */
@Component(modules = {RoomDaoModule.class, AppModule.class})
@Singleton
public interface AppComponent {

    Context getContext();

    SharedPreferences getSharedPreferences();

    ProductListRepository getProductListRepository();

    ProductTemplateRepository getProductTemplateRepository();

    void inject(ProductListsViewModel viewModel);

    void inject(CategoryProductItemViewModel viewModel);

    void inject(ShoppingList shoppingList);

    void inject(ProductListViewModel viewModel);

    void inject(ProductListItemViewModel viewModel);
}
