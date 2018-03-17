package com.nzarudna.shoppinglist.ui.fabdialog;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nzarudna.shoppinglist.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nataliia on 17.03.2018.
 */

public class FABsAlertDialog extends AlertDialog {

    protected FABsAlertDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder extends AlertDialog.Builder {

        private ArrayList<FAB> fabs;

        public Builder(@NonNull Context context) {
            super(context);
            fabs = new ArrayList<>();
            setView(R.layout.dialog_fabs);
        }

        public AlertDialog.Builder addFAB(@StringRes int titleResID, @DrawableRes int iconResID,
                                          View.OnClickListener onClickListener) {
            fabs.add(new FAB(titleResID, iconResID, onClickListener));
            return this;
        }

        @Override
        public AlertDialog create() {
            AlertDialog dialog = super.create();

            ViewGroup dialogView = dialog.findViewById(R.id.root_view);
            View fab = dialog.getLayoutInflater().inflate(R.layout.item_fab, dialogView, false);
            TextView fabTitle = fab.findViewById(R.id.fab_title);
            fabTitle.setText("www");

            return dialog;
        }

        private class FAB {
            @StringRes int mTitleResID;
            @DrawableRes int mIconResID;
            View.OnClickListener mOnClickListener;

            public FAB(int titleResID, int iconResID, View.OnClickListener onClickListener) {
                this.mTitleResID = titleResID;
                this.mIconResID = iconResID;
                this.mOnClickListener = onClickListener;
            }
        }
    }
}
