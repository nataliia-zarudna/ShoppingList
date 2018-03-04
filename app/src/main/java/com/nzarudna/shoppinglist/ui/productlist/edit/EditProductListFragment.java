package com.nzarudna.shoppinglist.ui.productlist.edit;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.databinding.ToolbarEditTitleBinding;
import com.nzarudna.shoppinglist.ui.productlist.ProductListFragment;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;
import com.nzarudna.shoppinglist.ui.productlists.CopyListDialogFragment;

import java.util.UUID;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditProductListFragment extends ProductListFragment {

    private static final String TAG = "EditProductListFragment";

    private static final String ARG_PRODUCTS_LIST_ID = "products_list_id";
    private static final int REQUEST_CODE_CREATE_FORM_TEMPLATE = 1;

    private View mToolbarView;
    private View mFragmentView;
    private ImageButton mShowCreationMenuBtn;
    private ImageButton mCreateNewSubItem;
    private ImageButton mCreateFromTemplateSubItem;
    private TextView mCreateNewSubItemTitle;
    private TextView mCreateFromTemplateSubItemTitle;

    public static EditProductListFragment getInstance(UUID productListID) {
        EditProductListFragment instance = new EditProductListFragment();
        instance.setProductListID(productListID);
        return instance;
    }

    @Override
    protected Class<? extends ProductListViewModel> getViewModelClass() {
        return EditProductListViewModel.class;
    }

    @Override
    protected int getProductItemLayoutID() {
        return R.layout.item_edit_product_product_list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.fragment_edit_product_list, container, false);

        if (mToolbarView != null) {
            ToolbarEditTitleBinding viewDataBinding = DataBindingUtil.bind(mToolbarView);
            viewDataBinding.setEditListViewModel((EditProductListViewModel) mViewModel);
        }

        initProductsRecyclerView(mFragmentView);
        configProductsDragAndDrop();

        mShowCreationMenuBtn = mFragmentView.findViewById(R.id.show_create_product_menu);
        mCreateNewSubItem = mFragmentView.findViewById(R.id.btn_new_product);
        mCreateFromTemplateSubItem = mFragmentView.findViewById(R.id.btn_create_from_template);
        mCreateNewSubItemTitle = mFragmentView.findViewById(R.id.new_product_title);
        mCreateFromTemplateSubItemTitle = mFragmentView.findViewById(R.id.btn_create_from_template_title);
        configCreationMenu();

        return mFragmentView;
    }

    private void configProductsDragAndDrop() {
        ItemTouchHelper touchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                    }
                });
        touchHelper.attachToRecyclerView(mProductsRecyclerView);
    }

    private void configCreationMenu() {

        mShowCreationMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int subItemsVisibility = (mCreateNewSubItem.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;

                mCreateNewSubItem.setVisibility(subItemsVisibility);
                mCreateFromTemplateSubItem.setVisibility(subItemsVisibility);
                mCreateNewSubItemTitle.setVisibility(subItemsVisibility);
                mCreateFromTemplateSubItemTitle.setVisibility(subItemsVisibility);
            }
        });

        mCreateNewSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditProductListViewModel) mViewModel).onClickCreateListBtn();
            }
        });

        mCreateFromTemplateSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopyListDialogFragment copyListFragment = new CopyListDialogFragment();
                copyListFragment.setTargetFragment(EditProductListFragment.this, REQUEST_CODE_CREATE_FORM_TEMPLATE);
                copyListFragment.show(getFragmentManager(), CopyListDialogFragment.class.getSimpleName());
            }
        });
    }

    public void setToolbar(View toolbarView) {
        mToolbarView = toolbarView;
    }
}
