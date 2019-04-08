package com.nzarudna.shoppinglist.ui.productlist.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.databinding.ToolbarEditTitleBinding;
import com.nzarudna.shoppinglist.ui.fabdialog.FABsDialog;
import com.nzarudna.shoppinglist.ui.productlist.CategoryProductItemViewModel;
import com.nzarudna.shoppinglist.ui.productlist.ProductListFragment;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;
import com.nzarudna.shoppinglist.ui.productlist.edit.template.ChooseTemplateActivity;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.EditProductDialogFragment;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditProductListFragment extends ProductListFragment {

    private static final String TAG = "EditProductListFragment";

    private static final int REQUEST_CODE_CREATE_FORM_TEMPLATE = 1;
    private static final int REQUEST_CODE_NEW_FRAGMENT = 2;

    private View mToolbarView;
    private View mFragmentView;
    private ImageButton mShowCreationMenuBtn;

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

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_edit_product_list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mToolbarView != null) {
            ToolbarEditTitleBinding viewDataBinding = DataBindingUtil.bind(mToolbarView);
            viewDataBinding.setEditListViewModel((EditProductListViewModel) mViewModel);

            getActivity().setTitle("");
        }

        mShowCreationMenuBtn = mFragmentView.findViewById(R.id.show_create_product_menu);
        configCreationMenu();

        return mFragmentView;
    }

    @Override
    protected boolean isDragAndDropEnabled() {
        return true;
    }

    @Nullable
    @Override
    protected ImageView getDraggableItemViewHandler(View itemView) {
        return itemView.findViewById(R.id.drag_handler);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CREATE_FORM_TEMPLATE:
                    Toast.makeText(getActivity(), R.string.save_item_success_msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected CategoryProductItemViewModel getListItemViewModel() {
        return new EditCategoryProductItemViewModel();
    }

    private void configCreationMenu() {

        mShowCreationMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FABsDialog.newInstance()
                        .addFAB(R.id.fab_create_from_template, R.string.create_product_from_template_title, R.drawable.ic_content_copy_black,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = ChooseTemplateActivity.newIntent(getActivity(), mViewModel.getProductListID());
                                        startActivity(intent);
                                    }
                                })
                        .addFAB(R.id.fab_new_product, R.string.new_product_title, R.drawable.ic_add_black,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        EditProductDialogFragment newProductDialog = EditProductDialogFragment.newInstance();
                                        newProductDialog.setListID(mViewModel.getProductListID());
                                        newProductDialog.setTargetFragment(EditProductListFragment.this, REQUEST_CODE_NEW_FRAGMENT);
                                        newProductDialog.show(getFragmentManager(), "EditProductDialogFragment");
                                    }
                                })
                        .show(getFragmentManager(), "FABsDialog");
            }
        });
    }

    public void setToolbar(View toolbarView) {
        mToolbarView = toolbarView;
    }
}
