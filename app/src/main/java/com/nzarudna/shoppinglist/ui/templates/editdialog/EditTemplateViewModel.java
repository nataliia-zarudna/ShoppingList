package com.nzarudna.shoppinglist.ui.templates.editdialog;

import android.arch.lifecycle.LiveData;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class EditTemplateViewModel extends EditDialogViewModel<CategoryTemplateItem> {

    @Inject
    ProductTemplateRepository mTemplateRepository;
    @Inject
    UnitRepository mUnitRepository;
    @Inject
    CategoryRepository mCategoryRepository;

    private ProductTemplate mTemplate;

    @Override
    protected CategoryTemplateItem createItemObject() {
        mTemplate = new ProductTemplate("");
        return new CategoryTemplateItem(mTemplate);
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
    protected void updateItem() {
        mTemplateRepository.updateTemplate(mTemplate);
    }

    @Override
    protected void createItem() {
        mTemplateRepository.createTemplate(mTemplate);
    }

    public UUID getCategoryID() {
        return mTemplate.getCategoryID();
    }

    public UUID getUnitID() {
        return mTemplate.getUnitID();
    }

    public LiveData<List<Unit>> getUnits() {
        return mUnitRepository.getAvailableUnits();
    }

    public int getTemplateUnitIndex(List<Unit> units) {
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getUnitID().equals(mTemplate.getUnitID())) {
                return i;
            }
        }
        return -1;
    }

    public LiveData<List<Category>> getCategories() {
        return mCategoryRepository.getAvailableCategories();
    }

    public int getTemplateCategoryIndex(List<Category> categories) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCategoryID().equals(mTemplate.getCategoryID())) {
                return i;
            }
        }
        return -1;
    }

    public void setUnit(Unit selectedUnit) {
        mTemplate.setUnitID(selectedUnit.getUnitID());
    }

    public void setCategory(Category selectedCategory) {
        mTemplate.setCategoryID(selectedCategory.getCategoryID());
    }
}
