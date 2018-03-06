package com.nzarudna.shoppinglist.ui.productlist.edit;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.databinding.ToolbarEditTitleBinding;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.ui.productlist.CategoryProductItemViewModel;
import com.nzarudna.shoppinglist.ui.productlist.ProductListFragment;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;
import com.nzarudna.shoppinglist.ui.productlist.edit.template.ChooseTemplateActivity;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.EditProductDialogFragment;
import com.nzarudna.shoppinglist.ui.productlists.CopyListDialogFragment;

import java.util.UUID;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditProductListFragment extends ProductListFragment {

    private static final String TAG = "EditProductListFragment";

    private static final String ARG_PRODUCTS_LIST_ID = "products_list_id";
    private static final int REQUEST_CODE_CREATE_FORM_TEMPLATE = 1;
    private static final int REQUEST_CODE_NEW_FRAGMENT = 2;

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

        mShowCreationMenuBtn = mFragmentView.findViewById(R.id.show_create_product_menu);
        mCreateNewSubItem = mFragmentView.findViewById(R.id.btn_new_product);
        mCreateFromTemplateSubItem = mFragmentView.findViewById(R.id.btn_create_from_template);
        mCreateNewSubItemTitle = mFragmentView.findViewById(R.id.new_product_title);
        mCreateFromTemplateSubItemTitle = mFragmentView.findViewById(R.id.btn_create_from_template_title);
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
                    break;
                case REQUEST_CODE_NEW_FRAGMENT:
                    Product newProduct = EditProductDialogFragment.getResultProduct(data);
                    ((EditProductListViewModel) mViewModel).createProduct(newProduct, new ShoppingList.OnSaveProductCallback() {
                        @Override
                        public void onSaveProduct() {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), R.string.save_product_success_msg, Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            }
                        }
                    });
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected CategoryProductItemViewModel getCategoryProductItemViewModel() {
        return new EditCategoryProductItemViewModel();
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
                EditProductDialogFragment newProductDialog = EditProductDialogFragment.newInstance(mViewModel.getProductListID());
                newProductDialog.setTargetFragment(EditProductListFragment.this, REQUEST_CODE_NEW_FRAGMENT);
                newProductDialog.show(getFragmentManager(), "EditProductDialogFragment");
            }
        });

        mCreateFromTemplateSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ChooseTemplateActivity.newIntent(getActivity(), mViewModel.getProductListID());
                startActivity(intent);
            }
        });
    }

    public void setToolbar(View toolbarView) {
        mToolbarView = toolbarView;
    }
}
