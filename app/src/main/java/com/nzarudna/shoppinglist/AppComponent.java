package com.nzarudna.shoppinglist;

import android.content.Context;

import com.nzarudna.shoppinglist.product.ShoppingListRepository;
import com.nzarudna.shoppinglist.persistence.RoomDaoModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component with application dependencies
 */
@Component(modules = {RoomDaoModule.class, ContextModule.class})
@Singleton
public interface AppComponent {

    Context getContext();

    ShoppingListRepository getShoppingListRepository();

}
