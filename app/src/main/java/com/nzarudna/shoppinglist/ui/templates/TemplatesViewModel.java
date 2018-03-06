package com.nzarudna.shoppinglist.ui.templates;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.ui.ObservableViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class TemplatesViewModel extends ObservableViewModel {

    @Inject
    ProductTemplateRepository mTemplateRepository;

    public LiveData<PagedList<CategoryTemplateItem>> getTemplates(boolean isGroupedView, int pageSize) {
        DataSource.Factory<Integer, CategoryTemplateItem> factory =
                mTemplateRepository.getTemplates(isGroupedView);
        return new LivePagedListBuilder<>(factory, pageSize).build();
    }
}
