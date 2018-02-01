package com.nzarudna.shoppinglist.model.unit;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Unit of products
 */
@Entity(tableName = "units")
public class Unit {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "unit_id")
    private UUID unitID;

    @NonNull
    private String name;

    public Unit(@NonNull String name) {
        this.unitID = UUID.randomUUID();
        this.name = name;
    }

    @NonNull
    public UUID getUnitID() {
        return unitID;
    }

    public void setUnitID(@NonNull UUID unitID) {
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

        if (!unitID.equals(unit.unitID)) return false;
        return name.equals(unit.name);
    }

    @Override
    public int hashCode() {
        int result = unitID.hashCode();
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
