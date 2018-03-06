package com.nzarudna.shoppinglist.ui.templates.editdialog;

import android.arch.lifecycle.LiveData;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;
import com.nzarudna.shoppinglist.ui.EditDialogViewModel;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class EditTemplateViewModel extends EditDialogViewModel<ProductTemplate> {

    @Inject
    UnitRepository mUnitRepository;
    @Inject
    CategoryRepository mCategoryRepository;

    @Override
    protected ProductTemplate createNewItem() {
        return new ProductTemplate("");
    }

    @Override
    public String getName() {
        return mItem.getName();
    }

    @Override
    public void setName(String name) {
        mItem.setName(name);
    }

    public UUID getCategoryID() {
        return mItem.getCategoryID();
    }

    public UUID getUnitID() {
        return mItem.getUnitID();
    }

    public LiveData<List<Unit>> getUnits() {
        return mUnitRepository.getAvailableUnits();
    }

    public int getTemplateUnitIndex(List<Unit> units) {
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getUnitID().equals(mItem.getUnitID())) {
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
            if (categories.get(i).getCategoryID().equals(mItem.getCategoryID())) {
                return i;
            }
        }
        return -1;
    }

    public void setUnit(Unit selectedUnit) {
        mItem.setUnitID(selectedUnit.getUnitID());
    }

    public void setCategory(Category selectedCategory) {
        mItem.setCategoryID(selectedCategory.getCategoryID());
    }
}
