package com.nzarudna.shoppinglist.ui.templates;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.ResourceResolver;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.ui.ObservableViewModel;
import com.nzarudna.shoppinglist.ui.RecyclerViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class TemplatesViewModel extends RecyclerViewModel<CategoryTemplateItem> {

    private static final String SHARED_PREFERENCE_TEMPLATES_IS_GROUP_VIEW = "templates_is_group_view";

    @Inject
    ProductTemplateRepository mTemplateRepository;
    @Inject
    SharedPreferences mSharedPreferences;

    @Override
    public LiveData<PagedList<CategoryTemplateItem>> getItems() {
        boolean isGroupedView = mSharedPreferences.getBoolean(SHARED_PREFERENCE_TEMPLATES_IS_GROUP_VIEW, false);
        return getItems(isGroupedView);
    }

    public LiveData<PagedList<CategoryTemplateItem>> getItems(boolean isGroupedView) {
        DataSource.Factory<Integer, CategoryTemplateItem> factory =
                mTemplateRepository.getTemplates(isGroupedView);
        return new LivePagedListBuilder<>(factory, DEFAULT_PAGE_SIZE).build();
    }
}
