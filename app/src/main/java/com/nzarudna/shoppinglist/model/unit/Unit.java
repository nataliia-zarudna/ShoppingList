package com.nzarudna.shoppinglist.model.unit;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Unit of products
 */
@Entity(tableName = "units")
public class Unit {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "unit_id")
    private int unitID;

    @NonNull
    private String name;

    public int getUnitID() {
        return unitID;
    }

    public void setUnitID(int unitID) {
        this.unitID = unitID;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Unit unit = (Unit) o;

        if (unitID != unit.unitID) return false;
        return name.equals(unit.name);
    }

    @Override
    public int hashCode() {
        int result = unitID;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "unitID=" + unitID +
                ", name='" + name + '\'' +
                '}';
    }
}
