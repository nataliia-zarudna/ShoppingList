package com.nzarudna.shoppinglist.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nzarudna.shoppinglist.R;

import java.util.Arrays;
import java.util.List;

public class LibraryFragment  extends Fragment {

    private RecyclerView mLibraryFragments;

    private List<LibraryFragmentData> libraryFragments = Arrays.asList(
            new LibraryFragmentData(R.id.templatesFragment, R.string.templates_nav_item),
            new LibraryFragmentData(R.id.categoriesFragment, R.string.categories_nav_item),
            new LibraryFragmentData(R.id.unitsFragment, R.string.units_nav_item),
            new LibraryFragmentData(R.id.archivedListsFragment, R.string.archive_nav_item)
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        setupFragmentsView(view);

        return view;
    }

    private void setupFragmentsView(View view) {

        mLibraryFragments = view.findViewById(R.id.library_fragments);

        mLibraryFragments.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLibraryFragments.setHasFixedSize(true);

        LibraryAdapter adapter = new LibraryAdapter(libraryFragments, this::onItemClicked);
        mLibraryFragments.setAdapter(adapter);
    }

    private void onItemClicked(LibraryFragmentData libraryFragmentData) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(libraryFragmentData.getFragmentID());
    }
}
