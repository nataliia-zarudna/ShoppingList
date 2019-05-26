package com.nzarudna.shoppinglist.ui.library;

public class LibraryFragmentData {

    private int fragmentID;
    private int nameResID;

    public LibraryFragmentData() {
    }

    public LibraryFragmentData(int fragmentID, int nameResID) {
        this.fragmentID = fragmentID;
        this.nameResID = nameResID;
    }

    public int getFragmentID() {
        return fragmentID;
    }

    public void setFragmentID(int fragmentID) {
        this.fragmentID = fragmentID;
    }

    public int getNameResID() {
        return nameResID;
    }

    public void setNameResID(int nameResID) {
        this.nameResID = nameResID;
    }

    @Override
    public String toString() {
        return "LibraryFragmentData{" +
                "fragmentID=" + fragmentID +
                ", nameResID=" + nameResID +
                '}';
    }
}
