package com.nzarudna.shoppinglist.model.unit;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Unit of products
 */
@Entity(tableName = "units")
public class Unit implements Parcelable, Cloneable {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "unit_id")
    private UUID unitID;

    private String name;

    public Unit() {
        this.unitID = UUID.randomUUID();
    }

    protected Unit(Parcel in) {
        unitID = (UUID) in.readSerializable();
        name = in.readString();
    }

    public static final Creator<Unit> CREATOR = new Creator<Unit>() {
        @Override
        public Unit createFromParcel(Parcel in) {
            return new Unit(in);
        }

        @Override
        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };

    @NonNull
    public UUID getUnitID() {
        return unitID;
    }

    public void setUnitID(@NonNull UUID unitID) {
        this.unitID = unitID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null) ? name.trim() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Unit unit = (Unit) o;

        if (!unitID.equals(unit.unitID)) return false;
        return name != null ? name.equals(unit.name) : unit.name == null;
    }

    @Override
    public int hashCode() {
        int result = unitID.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "unitID=" + unitID +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(unitID);
        parcel.writeString(name);
    }

    @Override
    public Unit clone() throws CloneNotSupportedException {
        return (Unit) super.clone();
    }
}
