package com.nzarudna.shoppinglist.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import com.nzarudna.shoppinglist.TestUtils;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by nsirobaba on 1/31/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitDaoTest {

    private AppDatabase mDatabase;
    private UnitDao mSubject;

    @Before
    public void setUp() {
        mDatabase = TestUtils.buildInMemoryDB();
        mSubject = mDatabase.unitDao();

        TestUtils.insertDefaultCategory(mDatabase.categoryDao());
    }

    @Test
    public void create() {

        Unit unit = new Unit();
        unit.setName("New name");

        mSubject.insert(unit);
    }

    @Test
    public void createAndRead() throws InterruptedException {

        Unit unit = new Unit();
        unit.setName("New name");

        mSubject.insert(unit);

        LiveData<List<Unit>> unitLiveData = mSubject.findAllLiveData();
        List<Unit> foundUnits = TestUtils.getLiveDataValueSync(unitLiveData);

        List<Unit> expectedUnits = new ArrayList<>();
        expectedUnits.add(unit);

        assertEquals(expectedUnits, foundUnits);
    }

    @Test
    public void remove() throws InterruptedException {

        Unit unit = createUnit();

        mSubject.delete(unit);

        LiveData<List<Unit>> unitLiveData = mSubject.findAllLiveData();
        List<Unit> foundUnits = TestUtils.getLiveDataValueSync(unitLiveData);

        assertTrue(foundUnits.isEmpty());
    }

    @Test
    public void remove_testOnDeleteCascade() throws InterruptedException {

        Unit unit = createUnit();

        UUID userID = TestUtils.insertUser(mDatabase.userDao());
        UUID listID = TestUtils.insertProductsList(mDatabase.productListDao(), userID);

        Product product = new Product();
        product.setListID(listID);
        product.setUnitID(unit.getUnitID());
        mDatabase.productDao().insert(product);

        ProductTemplate template = new ProductTemplate();
        template.setUnitID(unit.getUnitID());
        mDatabase.productTemplateDao().insert(template);

        mSubject.delete(unit);

        Product foundProduct = mDatabase.productDao().findByIDSync(product.getProductID());
        assertNotNull(foundProduct);
        assertNull(foundProduct.getUnitID());

        ProductTemplate foundTemplate = mDatabase.productTemplateDao().findByIDSync(template.getTemplateID());
        assertNotNull(foundTemplate);
        assertNull(foundTemplate.getUnitID());
    }

    @Test
    public void findAll() throws InterruptedException {

        List<Unit> expectedUnits = new ArrayList<>();

        mDatabase.beginTransaction();
        try {
            for (int i = 0; i < 3; i++) {

                String name = "Unit #" + i;
                UUID unitID = UUID.randomUUID();

                mDatabase
                        .compileStatement("INSERT INTO units(unit_id, name)" +
                                "                VALUES('" + unitID + "', '" + name + "')")
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

        LiveData<List<Unit>> foundUnitsLiveData = mSubject.findAllLiveData();
        List<Unit> foundUnits = TestUtils.getLiveDataValueSync(foundUnitsLiveData);

        TestUtils.assertEquals(expectedUnits, foundUnits);
    }

    @Test
    public void checkExistenceBySimilarName() throws InterruptedException {

        createUnit("unit 1");
        createUnit("UnIt");
        createUnit("something");

        boolean isExists = mSubject.isUnitsWithSameNameExists("unit");

        assertTrue(isExists);
    }

    @Test
    public void findBySimilarName_emptyResult() throws InterruptedException {

        createUnit("unit 1");
        createUnit("uNIt123");
        createUnit("something");

        boolean isExists = mSubject.isUnitsWithSameNameExists("unit");

        assertFalse(isExists);
    }

    @Test
    public void findAllLiveData() throws InterruptedException {

        List<Unit> createdCategories = createCategories(3);

        DataSource.Factory<Integer, Unit> foundCategoriesLiveData = mSubject.findAll();
        List<Unit> actualCategories = TestUtils.getPagedListSync(foundCategoriesLiveData);

        TestUtils.assertEquals(createdCategories, actualCategories);
    }

    private Unit createUnit() throws InterruptedException {
        return createUnit("Some name");
    }

    private Unit createUnit(String name) throws InterruptedException {
        Unit unit = new Unit();
        unit.setName(name);
        mSubject.insert(unit);
        return unit;
    }

    private List<Unit> createCategories(int count) throws InterruptedException {
        List<Unit> categories = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = "Unit #" + i;
            categories.add(createUnit(name));
        }
        return categories;
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

}
