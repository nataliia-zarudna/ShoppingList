package com.nzarudna.shoppinglist.dao;

import com.nzarudna.shoppinglist.TestUtils;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nsirobaba on 1/31/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilDaoTest {

    private AppDatabase mDatabase;
    private UnitDao mSubject;

    @Before
    public void setUp() {

        mDatabase = TestUtils.buildInMemoryDB();
        mSubject = mDatabase.unitDao();

    }

    @Test
    public void findAll() {

        List<Unit> expectedUnits = new ArrayList<>();

        mDatabase.beginTransaction();
        try {
            for (int i = 0; i < 3; i++) {

                String name = "Unit #" + i;

                int unitID = (int) mDatabase
                        .compileStatement("INSERT INTO units(name) VALUES('" + name + "')")
                        .executeInsert();

                Unit unit = new Unit();
                unit.setName(name);
                unit.setUnitID(unitID);
                expectedUnits.add(unit);
            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }

        List<Unit> foundUnits = mSubject.findAll();

        TestUtils.assertEquals(expectedUnits, foundUnits);
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

}
