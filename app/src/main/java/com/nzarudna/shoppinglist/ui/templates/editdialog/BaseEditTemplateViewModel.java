package com.nzarudna.shoppinglist.ui.templates.editdialog;

import android.arch.lifecycle.LiveData;
import android.databinding.Bindable;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class BaseEditTemplateViewModel<T> extends EditDialogViewModel<T> {

    @Inject
    UnitRepository mUnitRepository;
    @Inject
    CategoryRepository mCategoryRepository;

    @Bindable
    private boolean mIsNewCategorySelected;

    private String mCategoryName;

    public void setCategoryName(String categoryName) {
        this.mCategoryName = categoryName;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void toggleIsNewCategorySelected() {
        this.mIsNewCategorySelected = !mIsNewCategorySelected;
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public boolean isNewCategorySelected() {
        return mIsNewCategorySelected;
    }

    public abstract UUID getCategoryID();

    public abstract UUID getUnitID();

    public abstract void setUnit(Unit selectedUnit);

    public abstract void setCategory(Category selectedCategory);

    public LiveData<List<Unit>> getUnits() {
        return mUnitRepository.getAvailableUnits();
    }

    public int getTemplateUnitIndex(List<Unit> units) {
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getUnitID().equals(getUnitID())) {
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
            if (categories.get(i).getCategoryID().equals(getCategoryID())) {
                return i;
            }
        }
        return -1;
    }
}
