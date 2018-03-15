package com.nzarudna.shoppinglist.ui.templates;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class CategoryTemplateItemViewModel extends RecyclerItemViewModel<CategoryTemplateItem> {

    @Inject
    ProductTemplateRepository mTemplateRepository;

    private CategoryTemplateItemViewModelObserver mCategoryTemplateItemViewModelObserver;

    public void setCategoryTemplateItemViewModelObserver(CategoryTemplateItemViewModelObserver observer) {
        this.mCategoryTemplateItemViewModelObserver = observer;
    }

    @Override
    public String getItemName() {
        if (mItem != null) {
            return isTemplateItem() ? mItem.getTemplate().getName() : mItem.getCategory().getName();
        } else {
            return "";
        }
    }

    @Override
    public boolean hasContextMenu() {
        return isTemplateItem();
    }

    @Override
    public void removeItem() {
        ProductTemplate template = getItem().getTemplate();
        mTemplateRepository.remove(template);
    }

    private boolean isTemplateItem() {
        return mItem.getType().equals(CategoryTemplateItem.TYPE_TEMPLATE);
    }

    public void setTemplate(ProductTemplate template) {
        mItem.setTemplate(template);
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    @Override
    public boolean onItemLongClick() {
        onSelect();
        if (mCategoryTemplateItemViewModelObserver != null) {
            mCategoryTemplateItemViewModelObserver.onTemplateSelected(mItem);
        }
        return true;
    }

    public interface CategoryTemplateItemViewModelObserver {
        void onTemplateSelected(CategoryTemplateItem item);
    }
}
