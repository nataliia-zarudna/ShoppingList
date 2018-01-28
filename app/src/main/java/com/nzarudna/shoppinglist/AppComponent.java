package com.nzarudna.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.product.ProductTemplateRepository;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;
import com.nzarudna.shoppinglist.persistence.RoomDaoModule;
import com.nzarudna.shoppinglist.ui.editshoppinglist.EditProductListViewModel;
import com.nzarudna.shoppinglist.ui.lists.ProductListsViewModel;
import com.nzarudna.shoppinglist.ui.shoppinglist.ShoppingListViewModel;

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

    ShoppingListRepository getShoppingListRepository();

    ProductTemplateRepository getProductTemplateRepository();

    void inject(ProductListsViewModel viewModel);

    void inject(EditProductListViewModel viewModel);

    void inject(ShoppingList shoppingList);

    void inject(ShoppingListViewModel viewModel);
}
