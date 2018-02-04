package com.nzarudna.shoppinglist.ui.productlists;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.databinding.ItemTextListBinding;
import com.nzarudna.shoppinglist.model.product.list.ProductList;

import java.util.List;
import java.util.UUID;

/**
 * Dialog fragment that shows all product lists
 * to choose one to copy
 */

public class CopyListDialogFragment extends DialogFragment {

    private static final String EXTRA_LIST_ID = "list_id";

    private ProductListsViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(ProductListsViewModel.class);
        ShoppingListApplication.getAppComponent().inject(mViewModel);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ProductListAdapter listAdapter = new ProductListAdapter(mViewModel.getAllLists());

        return new AlertDialog.Builder(getActivity())
                .setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ProductList list = (ProductList) listAdapter.getItem(i);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(EXTRA_LIST_ID, list.getListID());
                        getTargetFragment().onActivityResult(getTargetRequestCode(),
                                Activity.RESULT_OK, resultIntent);
                    }
                }).create();
    }

    public static UUID getListID(Intent data) {
        return (UUID) data.getSerializableExtra(EXTRA_LIST_ID);
    }

    public class ProductListAdapter extends BaseAdapter {

        private List<ProductList> mProductList;

        public ProductListAdapter(LiveData<List<ProductList>> listLiveData) {
            listLiveData.observe(CopyListDialogFragment.this, new Observer<List<ProductList>>() {
                @Override
                public void onChanged(@Nullable List<ProductList> productLists) {
                    mProductList = productLists;
                    ProductListAdapter.this.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getCount() {
            return mProductList != null ? mProductList.size() : 0;
        }

        @Override
        public Object getItem(int i) {
            return mProductList != null ? mProductList.get(i) : null;
        }

        @Override
        public long getItemId(int i) {
            return mProductList != null ? mProductList.get(i).getListID().getMostSignificantBits() : 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ProductList item = mProductList.get(i);

            ItemTextListBinding binding;
            if (view == null) {
                binding
                        = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_list, viewGroup, false);
                view = binding.getRoot();

                ViewModel viewModel = new ViewModel();
                binding.setViewModel(viewModel);

                view.setTag(binding);
            } else {
                binding = (ItemTextListBinding) view.getTag();
            }

            binding.getViewModel().setList(item);

            return view;
        }

        public class ViewModel extends BaseObservable {

            @Bindable
            private ProductList mList;

            public void setList(ProductList list) {
                this.mList = list;
                notifyChange();
            }

            public String getTitle() {
                return mList.getName();
            }
        }
    }

    public interface OnSelectListListener {
        void onSelect(UUID listID);
    }
}
