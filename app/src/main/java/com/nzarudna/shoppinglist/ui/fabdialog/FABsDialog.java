package com.nzarudna.shoppinglist.ui.fabdialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nzarudna.shoppinglist.R;

import java.util.ArrayList;

/**
 * Created by Nataliia on 17.03.2018.
 */

public class FABsDialog extends DialogFragment {

    private ArrayList<FAB> fabs;

    public static FABsDialog newInstance() {
        return new FABsDialog();
    }

    public FABsDialog() {
        fabs = new ArrayList<>();
    }

    public FABsDialog addFAB(@IdRes int uniqueID, @StringRes int titleResID, @DrawableRes int iconResID,
                             View.OnClickListener onClickListener) {
        fabs.add(new FAB(uniqueID, titleResID, iconResID, onClickListener));
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), R.style.FABsDialogTheme)
                .setView(getCustomView())
                .setCancelable(true)
                .create();
    }

    public View getCustomView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fabs, null, false);

        ConstraintLayout rootConstraintsView = dialogView.findViewById(R.id.root_view);
        addFABViews(inflater, rootConstraintsView);

        dialogView.findViewById(R.id.fabs_menu_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        dialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return dialogView;
    }

    private void addFABViews(@NonNull LayoutInflater inflater, ConstraintLayout rootConstraintsView) {

        int prevFABView = R.id.fabs_menu_btn;
        for (final FAB fab : fabs) {
            View fabView = inflater.inflate(R.layout.item_fab, rootConstraintsView, false);
            fabView.setId(fab.mFabID);

            TextView fabTitle = fabView.findViewById(R.id.fab_title);
            fabTitle.setText(fab.mTitleResID);

            final FloatingActionButton fabButton = fabView.findViewById(R.id.fab_btn);
            fabButton.setImageResource(fab.mIconResID);
            fabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fab.mOnClickListener.onClick(view);
                    dismiss();
                }
            });
            fabTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fab.mOnClickListener.onClick(view);
                    dismiss();
                }
            });

            rootConstraintsView.addView(fabView);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(rootConstraintsView);
            constraintSet.connect(fabView.getId(), ConstraintSet.BOTTOM, prevFABView, ConstraintSet.TOP);
            constraintSet.connect(fabView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.applyTo(rootConstraintsView);

            prevFABView = fabView.getId();
        }
    }

    private class FAB {

        @IdRes
        int mFabID;
        @StringRes
        int mTitleResID;
        @DrawableRes
        int mIconResID;
        View.OnClickListener mOnClickListener;

        FAB(int fabID, int titleResID, int iconResID, View.OnClickListener onClickListener) {
            this.mFabID = fabID;
            this.mTitleResID = titleResID;
            this.mIconResID = iconResID;
            this.mOnClickListener = onClickListener;
        }
    }
}
