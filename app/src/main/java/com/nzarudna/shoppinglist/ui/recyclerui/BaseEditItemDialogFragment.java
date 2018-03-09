package com.nzarudna.shoppinglist.ui.recyclerui;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.ui.ViewModelArrayAdapter;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.CategoryItemViewModel;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.UnitItemViewModel;
import com.nzarudna.shoppinglist.ui.templates.editdialog.EditTemplateViewModel;

import java.util.List;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class BaseEditItemDialogFragment<T, VM extends EditDialogViewModel<T>> extends DialogFragment {

    private static final String ARG_ITEM = "com.nzarudna.shoppinglist.ui.recyclerui.item";
    private VM mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = getViewMode();

        T item = null;
        if (getArguments() != null) {
            item = getArguments().getParcelable(ARG_ITEM);
        }
        mViewModel.setItem(item);
    }

    protected abstract VM getViewMode();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(mViewModel.getDialogTitle())
                .setPositiveButton(R.string.save_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.saveItem();
                        sendResponse();
                    }
                })
                .setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setView(getCustomView())
                .create();
    }

    @LayoutRes
    protected int getDialogFragmentResID() {
        return R.layout.fragment_edit_item_dialog;
    }

    protected View getCustomView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(inflater, getDialogFragmentResID(), null, false);
        dataBinding.setVariable(BR.viewModel, mViewModel);

        return dataBinding.getRoot();
    }

    private void sendResponse() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
        }
    }
}
