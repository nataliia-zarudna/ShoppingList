package com.nzarudna.shoppinglist.ui;

import android.arch.lifecycle.ViewModel;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class ObservableViewModel extends ViewModel implements Observable {

    protected PropertyChangeRegistry mPropertyChangeRegistry = new PropertyChangeRegistry();

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.remove(onPropertyChangedCallback);
    }
}
