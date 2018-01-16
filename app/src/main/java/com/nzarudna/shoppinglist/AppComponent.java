package com.nzarudna.shoppinglist;

import android.content.Context;

import com.nzarudna.shoppinglist.model.ShoppingListRepository;
import com.nzarudna.shoppinglist.model.dao.RoomDaoModule;

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
