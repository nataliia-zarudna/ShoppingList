package com.nzarudna.shoppinglist.ui.productlist.edit.template;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;

/**
 * Created by Nataliia on 04.03.2018.
 */

public class ChooseTemplateFragment extends Fragment {

    public static ChooseTemplateFragment getInstance() {
        return new ChooseTemplateFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choose_template, container, false);

        return view;
    }
}
