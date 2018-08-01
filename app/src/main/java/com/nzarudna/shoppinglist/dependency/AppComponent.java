package com.nzarudna.shoppinglist.dependency;

import android.content.Context;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.AppFirebaseMessagingService;
import com.nzarudna.shoppinglist.model.persistence.RoomDaoModule;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.ui.archivedproductlists.ArchivedListsItemViewModel;
import com.nzarudna.shoppinglist.ui.archivedproductlists.ArchivedListsViewModel;
import com.nzarudna.shoppinglist.ui.categories.CategoriesViewModel;
import com.nzarudna.shoppinglist.ui.categories.CategoryItemViewModel;
import com.nzarudna.shoppinglist.ui.categories.EditCategoryViewModel;
import com.nzarudna.shoppinglist.ui.productlist.CategoryProductItemViewModel;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;
import com.nzarudna.shoppinglist.ui.productlist.edit.template.ChooseTemplateViewModel;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.EditProductViewModel;
import com.nzarudna.shoppinglist.ui.productlists.ProductListItemViewModel;
import com.nzarudna.shoppinglist.ui.productlists.ProductListsViewModel;
import com.nzarudna.shoppinglist.ui.templates.CategoryTemplateItemViewModel;
import com.nzarudna.shoppinglist.ui.templates.TemplatesViewModel;
import com.nzarudna.shoppinglist.ui.templates.editdialog.EditTemplateViewModel;
import com.nzarudna.shoppinglist.ui.units.EditUnitViewModel;
import com.nzarudna.shoppinglist.ui.units.UnitItemViewModel;
import com.nzarudna.shoppinglist.ui.units.UnitsViewModel;
import com.nzarudna.shoppinglist.ui.users.EditUserViewModel;
import com.nzarudna.shoppinglist.ui.users.UserItemViewModel;
import com.nzarudna.shoppinglist.ui.users.UsersViewModel;

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

    void inject(ProductListsViewModel viewModel);

    void inject(CategoryProductItemViewModel viewModel);

    void inject(ShoppingList shoppingList);

    void inject(ProductListViewModel viewModel);

    void inject(ProductListItemViewModel viewModel);

    void inject(EditProductViewModel mViewModel);

    void inject(ChooseTemplateViewModel mViewModel);

    void inject(TemplatesViewModel mViewModel);

    void inject(EditTemplateViewModel mViewModel);

    void inject(CategoryTemplateItemViewModel itemViewModel);

    void inject(CategoriesViewModel viewModel);

    void inject(CategoryItemViewModel itemViewModel);

    void inject(EditCategoryViewModel viewModel);

    void inject(UnitsViewModel viewModel);

    void inject(UnitItemViewModel itemViewModel);

    void inject(EditUnitViewModel viewModel);

    void inject(ArchivedListsViewModel viewModel);

    void inject(ArchivedListsItemViewModel itemViewModel);

    void inject(UserItemViewModel itemViewModel);

    void inject(UsersViewModel viewModel);

    void inject(EditUserViewModel viewModel);

    void inject(AppFirebaseMessagingService appFirebaseMessagingService);
}
