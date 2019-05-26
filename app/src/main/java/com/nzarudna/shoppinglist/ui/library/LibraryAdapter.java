package com.nzarudna.shoppinglist.ui.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nzarudna.shoppinglist.R;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    private List<LibraryFragmentData> mFragmentList;
    private OnLibraryItemClickListener mOnLibraryItemClickListener;

    public LibraryAdapter(List<LibraryFragmentData> fragmentList, OnLibraryItemClickListener onLibraryItemClickListener) {
        this.mFragmentList = fragmentList;
        this.mOnLibraryItemClickListener = onLibraryItemClickListener;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        LibraryFragmentData item = mFragmentList.get(position);
        holder.fragmentName.setText(item.getNameResID());

        holder.itemView.setOnClickListener(v -> mOnLibraryItemClickListener.onLibraryItem(item));
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }

    class LibraryViewHolder extends RecyclerView.ViewHolder {

        private final TextView fragmentName;

        LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            fragmentName = itemView.findViewById(R.id.fragment_name);
        }
    }

    public interface OnLibraryItemClickListener {
        void onLibraryItem(LibraryFragmentData item);
    }
}
