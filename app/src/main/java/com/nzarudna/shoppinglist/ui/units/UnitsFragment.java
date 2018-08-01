package com.nzarudna.shoppinglist.ui.units;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class UnitsFragment extends BaseRecyclerViewFragment<Unit, UnitsViewModel, UnitItemViewModel> {

    public static UnitsFragment newInstance() {
        return new UnitsFragment();
    }

    @Override
    protected UnitsViewModel getFragmentViewModel() {
        UnitsViewModel viewModel = ViewModelProviders.of(this).get(UnitsViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    protected UnitItemViewModel getListItemViewModel() {
        UnitItemViewModel itemViewModel = new UnitItemViewModel();
        ShoppingListApplication.getAppComponent().inject(itemViewModel);
        return itemViewModel;
    }

    @Override
    protected EditDialogViewModel<Unit> getEditDialogViewModel() {
        EditUnitViewModel viewModel = new EditUnitViewModel();
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    protected DiffUtil.ItemCallback<Unit> getDiffCallback() {
        return new DiffUtil.ItemCallback<Unit>() {
            @Override
            public boolean areItemsTheSame(@NonNull Unit oldItem, @NonNull Unit newItem) {
                return oldItem.getUnitID().equals(newItem.getUnitID());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Unit oldItem, @NonNull Unit newItem) {
                return oldItem.equals(newItem);
            }
        };
    }
}
