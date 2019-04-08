package com.nzarudna.shoppinglist.ui.recyclerui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class BaseEditItemDialogFragment<T extends Parcelable, VM extends EditDialogViewModel<T>> extends DialogFragment {

    private static final String ARG_ITEM = "com.nzarudna.shoppinglist.ui.recyclerui.item";

    protected VM mViewModel;
    private AlertDialog mDialog;

    public static BaseEditItemDialogFragment newInstance() {
        return new BaseEditItemDialogFragment();
    }

    public void setItem(T item) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, item);

        this.setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = getViewModel();

        T item = null;
        if (getArguments() != null) {
            item = getArguments().getParcelable(ARG_ITEM);
        }
        mViewModel.setItem(item);
    }

    public void setViewModel(VM viewModel) {
        this.mViewModel = viewModel;
    }

    protected VM getViewModel() {
        return mViewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setPositiveButton(getViewModel().getSaveButtonTitle(), null)
                .setNegativeButton(R.string.cancel_btn, (dialogInterface, i) -> dismiss())
                .setView(getCustomView());

        View customTitleView = getCustomTitleView();
        if (customTitleView != null) {
            dialogBuilder.setCustomTitle(customTitleView);
        } else {
            dialogBuilder.setTitle(mViewModel.getDialogTitle());
        }

        if (hasSaveAndNextButton()) {
            dialogBuilder.setNeutralButton(R.string.save_and_next_button, null);
        }

        mDialog = dialogBuilder.create();

        mDialog.setOnShowListener(dialogInterface -> {

            Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(BaseEditItemDialogFragment.this::onSaveButtonClick);

            Button nextButton = mDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            nextButton.setOnClickListener(BaseEditItemDialogFragment.this::onSaveAndNextButtonClick);
        });

        return mDialog;
    }

    protected void onSaveButtonClick(View view) {
        mViewModel.saveItem(() -> {
            sendResponse();
            mDialog.dismiss();
        });
    }

    protected void onSaveAndNextButtonClick(View view) {
        mViewModel.saveItem(() -> {
            sendResponse();
            mViewModel.setItem(null);
        });
    }

    protected boolean hasSaveAndNextButton() {
        return true;
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

    protected void sendResponse() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getResponseIntent());
        }
    }

    @NonNull
    protected Intent getResponseIntent() {
        return new Intent();
    }

    protected View getCustomTitleView() {
        return null;
    }
}
