package com.nzarudna.shoppinglist.ui.productlists;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class ProductListsFragment extends BaseRecyclerViewFragment<ProductListWithStatistics, ProductListsViewModel, ProductListItemViewModel> {

    private static final int REQUEST_CODE_LIST_TO_COPY = 1;

    private FloatingActionButton mShowCreationMenuBtn;
    private FloatingActionButton mCreateNewSubItem;
    private FloatingActionButton mCopySubItem;
    private TextView mCreateNewSubItemTitle;
    private TextView mCopySubItemTitle;

    public static Fragment getInstance() {
        return new ProductListsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = super.onCreateView(inflater, container, savedInstanceState);

        mShowCreationMenuBtn = fragmentView.findViewById(R.id.show_create_list_menu);
        mCreateNewSubItem = fragmentView.findViewById(R.id.btn_new_list);
        mCopySubItem = fragmentView.findViewById(R.id.btn_copy_list);
        mCreateNewSubItemTitle = fragmentView.findViewById(R.id.new_list_title);
        mCopySubItemTitle = fragmentView.findViewById(R.id.copy_list_title);
        configCreationMenu();

        return fragmentView;
    }

    private void configCreationMenu() {

        mShowCreationMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int subItemsVisibility = (mCreateNewSubItem.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;

                mCreateNewSubItem.setVisibility(subItemsVisibility);
                mCopySubItem.setVisibility(subItemsVisibility);
                mCreateNewSubItemTitle.setVisibility(subItemsVisibility);
                mCopySubItemTitle.setVisibility(subItemsVisibility);
            }
        });

        mCreateNewSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.onClickCreateListBtn();
            }
        });

        mCopySubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopyListDialogFragment copyListFragment = new CopyListDialogFragment();
                copyListFragment.setTargetFragment(ProductListsFragment.this, REQUEST_CODE_LIST_TO_COPY);
                copyListFragment.show(getFragmentManager(), CopyListDialogFragment.class.getSimpleName());
            }
        });
    }

    @Override
    protected ProductListsViewModel getFragmentViewModel() {
        mViewModel = ViewModelProviders.of(this).get(ProductListsViewModel.class);
        //mViewModel.setObserver(this);
        ShoppingListApplication.getAppComponent().inject(mViewModel);
        return mViewModel;
    }

    @Override
    protected ProductListItemViewModel getListItemViewModel() {
        ProductListItemViewModel itemViewModel = new ProductListItemViewModel();
        ShoppingListApplication.getAppComponent().inject(itemViewModel);
        return itemViewModel;
    }

    @Override
    protected EditDialogViewModel getEditDialogViewModel() {
        return null;
    }

    @Override
    protected DiffCallback<ProductListWithStatistics> getDiffCallback() {
        return new DiffCallback<ProductListWithStatistics>() {
            @Override
            public boolean areItemsTheSame(@NonNull ProductListWithStatistics oldItem, @NonNull ProductListWithStatistics newItem) {
                return oldItem.getListID() == newItem.getListID();
            }

            @Override
            public boolean areContentsTheSame(@NonNull ProductListWithStatistics oldItem, @NonNull ProductListWithStatistics newItem) {
                return oldItem.equals(newItem);
            }
        };
    }
}
