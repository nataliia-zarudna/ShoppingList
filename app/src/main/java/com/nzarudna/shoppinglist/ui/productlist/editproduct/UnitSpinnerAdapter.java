package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nzarudna.shoppinglist.model.unit.Unit;

import java.util.List;

/**
 * Created by Nataliia on 01.03.2018.
 */

public class UnitSpinnerAdapter extends ArrayAdapter<Unit> {

    private int mResID;
    private int mTextViewResourceId;

    public UnitSpinnerAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Unit> objects) {
        super(context, resource, textViewResourceId, objects);
        mResID = resource;
        mTextViewResourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(mResID, parent, false);
        }

        //TODO: move to viewModel
        TextView textView = view.findViewById(mTextViewResourceId);
        Unit unit = getItem(position);
        textView.setText(unit.getName());

        return view;
    }
}
