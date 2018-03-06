package com.nzarudna.shoppinglist.ui.templates;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class TemplatesFragment extends Fragment {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private TemplatesViewModel mViewModel;
    private RecyclerView mTemplatesView;
    private CategoryTemplateAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(TemplatesViewModel.class);
        ShoppingListApplication.getAppComponent().inject(mViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_recycler_view_with_fab, container, false);

        mTemplatesView = fragmentView.findViewById(R.id.recycler_view);
        mTemplatesView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new CategoryTemplateAdapter();
        mTemplatesView.setAdapter(mAdapter);

        mViewModel.getTemplates(true, DEFAULT_PAGE_SIZE)
                .observe(this, new Observer<PagedList<CategoryTemplateItem>>() {
            @Override
            public void onChanged(@Nullable PagedList<CategoryTemplateItem> categoryTemplateItems) {
                mAdapter.setList(categoryTemplateItems);
            }
        });

        return fragmentView;
    }

    private class CategoryTemplateViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding mDataBinding;
        CategoryTemplateItemViewModel mItemViewModel;

        public CategoryTemplateViewHolder(ViewDataBinding dataBinding) {
            super(dataBinding.getRoot());
            mDataBinding = dataBinding;

            mItemViewModel = new CategoryTemplateItemViewModel();
            mDataBinding.setVariable(BR.viewModel, mItemViewModel);
        }

        public void bind(CategoryTemplateItem item) {
            mItemViewModel.setItem(item);
            mDataBinding.executePendingBindings();
        }
    }

    private class CategoryTemplateAdapter extends PagedListAdapter<CategoryTemplateItem, CategoryTemplateViewHolder> {

        private static final int TYPE_TEMPLATE = 1;
        private static final int TYPE_CATEGORY = 2;

        protected CategoryTemplateAdapter() {
            super(DIFF_CALLBACK);
        }

        @Override
        public CategoryTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutResID = (viewType == TYPE_TEMPLATE)
                    ? R.layout.item_recycler_list
                    : R.layout.item_category_template_list;

            ViewDataBinding dataBinding =
                    DataBindingUtil.inflate(getLayoutInflater(), layoutResID, parent, false);
            return new CategoryTemplateViewHolder(dataBinding);
        }

        @Override
        public void onBindViewHolder(CategoryTemplateViewHolder holder, int position) {
            CategoryTemplateItem item = getItem(position);
            holder.bind(item);
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getType().equals(CategoryTemplateItem.TYPE_TEMPLATE)
                    ? TYPE_TEMPLATE : TYPE_CATEGORY;
        }
    }

    private static final DiffCallback<CategoryTemplateItem> DIFF_CALLBACK = new DiffCallback<CategoryTemplateItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CategoryTemplateItem oldItem, @NonNull CategoryTemplateItem newItem) {
            if (oldItem.getType().equals(newItem.getType())) {
                if (oldItem.getType().equals(CategoryTemplateItem.TYPE_TEMPLATE)) {
                    return oldItem.getTemplate().getTemplateID().equals(newItem.getTemplate().getTemplateID());
                } else {
                    return oldItem.getCategory().getCategoryID().equals(newItem.getCategory().getCategoryID());
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoryTemplateItem oldItem, @NonNull CategoryTemplateItem newItem) {
            return oldItem.equals(newItem);
        }
    };
}
