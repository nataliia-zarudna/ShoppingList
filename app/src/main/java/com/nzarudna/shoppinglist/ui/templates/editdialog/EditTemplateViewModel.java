package com.nzarudna.shoppinglist.ui.templates.editdialog;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class EditTemplateViewModel extends BaseEditTemplateViewModel<CategoryTemplateItem> {

    @Inject
    ProductTemplateRepository mTemplateRepository;
    @Inject
    UnitRepository mUnitRepository;
    @Inject
    CategoryRepository mCategoryRepository;

    private ProductTemplate mTemplate;

    @Override
    protected CategoryTemplateItem createItemObject() {
        mTemplate = new ProductTemplate();
        return new CategoryTemplateItem(mTemplate);
    }

    @Override
    public void setItem(CategoryTemplateItem item) {
        super.setItem(item);
        mTemplate = mItem.getTemplate();
    }

    @Override
    public String getName() {
        return mTemplate.getName();
    }

    @Override
    public void setName(String name) {
        mTemplate.setName(name);
    }

    @Override
    protected String getUniqueNameValidationMessage() {
        return mResourceResolver.getString(R.string.template_unique_name_validation_message);
    }

    @Override
    protected void updateItem(AsyncResultListener asyncResultListener) {
        mTemplateRepository.updateTemplate(mTemplate, asyncResultListener);
    }

    @Override
    protected void createItem(AsyncResultListener asyncResultListener) {
        mTemplateRepository.createTemplate(mTemplate, asyncResultListener);
    }

    @Override
    public UUID getCategoryID() {
        return mTemplate.getCategoryID();
    }

    @Override
    public UUID getUnitID() {
        return mTemplate.getUnitID();
    }

    @Override
    public void setUnit(Unit selectedUnit) {
        mTemplate.setUnitID(selectedUnit.getUnitID());
    }

    @Override
    public void setCategoryID(UUID categoryID) {
        mTemplate.setCategoryID(categoryID);
    }
}
