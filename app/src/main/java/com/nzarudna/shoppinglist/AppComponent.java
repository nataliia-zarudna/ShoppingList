package com.nzarudna.shoppinglist;

import android.content.Context;

import com.nzarudna.shoppinglist.product.ShoppingListRepository;
import com.nzarudna.shoppinglist.persistence.RoomDaoModule;
import com.nzarudna.shoppinglist.ui.lists.ProductListsViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component with application dependencies
 */
@Component(modules = {RoomDaoModule.class, AppModule.class})
@Singleton
public interface AppComponent {

    Context getContext();

    ShoppingListRepository getShoppingListRepository();

    void inject(ProductListsViewModel viewModel);
}
