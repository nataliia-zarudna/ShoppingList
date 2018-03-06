package com.nzarudna.shoppinglist.ui.productlist.edit.template;

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
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItemWithListStatistics;

import java.util.UUID;

/**
 * Created by Nataliia on 04.03.2018.
 */

public class ChooseTemplateFragment extends Fragment {

    private static final String ARG_LIST_ID = "com.nzarudna.shoppinglist.ui.productlist.edit.template.list_id";
    private static final int DEFAULT_PAGE_LIST = 20;

    private ChooseTemplateViewModel mViewModel;
    private RecyclerView mTemplatesRecyclerView;
    private CategoryTemplateAdapter mTemplatesAdapter;

    public static ChooseTemplateFragment getInstance(UUID listID) {
        ChooseTemplateFragment instance = new ChooseTemplateFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST_ID, listID);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(ChooseTemplateViewModel.class);
        ShoppingListApplication.getAppComponent().inject(mViewModel);

        if (getArguments() != null) {
            UUID listID = (UUID) getArguments().getSerializable(ARG_LIST_ID);
            mViewModel.setProductListID(listID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choose_template, container, false);

        mTemplatesRecyclerView = view.findViewById(R.id.templates_list);
        mTemplatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTemplatesAdapter = new CategoryTemplateAdapter();
        mTemplatesRecyclerView.setAdapter(mTemplatesAdapter);

        mViewModel.getTemplates(true, DEFAULT_PAGE_LIST).observe(
                this, new Observer<PagedList<CategoryTemplateItemWithListStatistics>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<CategoryTemplateItemWithListStatistics> list) {
                        mTemplatesAdapter.setList(list);
                    }
                });

        return view;
    }

    private class CategoryTemplateViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding mDataBinding;
        private CategoryTemplateItemViewModel mItemViewModel;

        public CategoryTemplateViewHolder(ViewDataBinding dataBinding) {
            super(dataBinding.getRoot());
            mDataBinding = dataBinding;

            mItemViewModel = new CategoryTemplateItemViewModel();
            mDataBinding.setVariable(BR.viewModel, mItemViewModel);
        }

        public void bind(CategoryTemplateItemWithListStatistics item) {
            mItemViewModel.setItem(item);
            mDataBinding.executePendingBindings();
        }
    }

    private class CategoryTemplateAdapter extends PagedListAdapter<CategoryTemplateItemWithListStatistics, CategoryTemplateViewHolder> {

        private static final int VIEW_TYPE_TEMPLATE = 1;
        private static final int VIEW_TYPE_CATEGORY = 2;

        protected CategoryTemplateAdapter() {
            super(DIFF_CALLBACK);
        }

        @Override
        public CategoryTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            int layoutResID;
            if (viewType == VIEW_TYPE_TEMPLATE) {
                layoutResID = R.layout.item_template_checkable_template_list;
            } else {
                layoutResID = R.layout.item_category_template_list;
            }
            ViewDataBinding dataBinding = DataBindingUtil.inflate(
                    getLayoutInflater(), layoutResID, parent, false);

            return new CategoryTemplateViewHolder(dataBinding);
        }

        @Override
        public void onBindViewHolder(CategoryTemplateViewHolder holder, int position) {
            CategoryTemplateItemWithListStatistics item = getItem(position);
            holder.bind(item);
        }

        @Override
        public int getItemViewType(int position) {
            CategoryTemplateItemWithListStatistics item = getItem(position);
            return CategoryTemplateItemWithListStatistics.TYPE_TEMPLATE.equals(item.getType())
                    ? VIEW_TYPE_TEMPLATE : VIEW_TYPE_CATEGORY;
        }
    }

    private final DiffCallback<CategoryTemplateItemWithListStatistics> DIFF_CALLBACK =
            new DiffCallback<CategoryTemplateItemWithListStatistics>() {
                @Override
                public boolean areItemsTheSame(@NonNull CategoryTemplateItemWithListStatistics oldItem, @NonNull CategoryTemplateItemWithListStatistics newItem) {
                    if (oldItem.getType().equals(newItem.getType())) {
                        if (oldItem.getType().equals(CategoryTemplateItemWithListStatistics.TYPE_TEMPLATE)) {
                            return oldItem.getTemplate().getTemplateID().equals(newItem.getTemplate().getTemplateID());
                        } else {
                            return oldItem.getCategory().getCategoryID().equals(newItem.getCategory().getCategoryID());
                        }
                    } else {
                        return false;
                    }
                }

                @Override
                public boolean areContentsTheSame(@NonNull CategoryTemplateItemWithListStatistics oldItem, @NonNull CategoryTemplateItemWithListStatistics newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
