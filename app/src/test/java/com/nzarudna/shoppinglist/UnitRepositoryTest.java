package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitDao;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by nsirobaba on 2/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitRepositoryTest {

    private UnitRepository mSubject;

    @Mock
    private UnitDao mUnitDao;
    @Before
    public void setUp() {

        mSubject = new UnitRepository(mUnitDao);

    }

    @Test
    public void create() {
        Unit unit = new Unit();
        unit.setName("some name");
        mSubject.createUnit(unit,  null);

        verify(mUnitDao).insert(unit);
    }

    @Test
    public void create_trimName() {
        String name = " some name   ";

        Unit unit = new Unit();
        unit.setName(name);
        mSubject.createUnit(unit,  null);

        unit.setName(name.trim());

        verify(mUnitDao).insert(unit);
    }

    @Test
    public void create_callListenerCallback() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Unit newUnit = new Unit();
        newUnit.setName("some name");
        mSubject.createUnit(newUnit, new AsyncResultListener<Unit>() {
            @Override
            public void onAsyncSuccess(Unit unit) {

                assertEquals(newUnit, unit);
                countDown.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);

        verify(mUnitDao).insert(newUnit);
    }

    @Test
    public void create_callListenerCallback_nullNameError() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Unit newUnit = new Unit();
        mSubject.createUnit(newUnit, new AsyncResultListener<Unit>() {
            @Override
            public void onAsyncSuccess(Unit unit) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void create_callListenerCallback_emptyNameError() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Unit newUnit = new Unit();
        newUnit.setName("  ");
        mSubject.createUnit(newUnit, new AsyncResultListener<Unit>() {
            @Override
            public void onAsyncSuccess(Unit unit) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void create_callListenerCallback_duplicateNameError() throws InterruptedException {

        String unitName = "some name";
        when(mUnitDao.isUnitsWithSameNameExists(unitName)).thenReturn(true);

        final CountDownLatch countDown = new CountDownLatch(1);


        final Unit newUnit = new Unit();
        newUnit.setName(unitName);
        mSubject.createUnit(newUnit, new AsyncResultListener<Unit>() {
            @Override
            public void onAsyncSuccess(Unit unit) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof UniqueNameConstraintException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void update() {
        Unit unit = new Unit();
        unit.setName("Some name");
        mSubject.updateUnit(unit, null);

        verify(mUnitDao).update(unit);
    }

    @Test
    public void update_trimName() {
        String name = " some name   ";

        Unit unit = new Unit();
        unit.setName(name);
        mSubject.updateUnit(unit,  null);

        unit.setName(name.trim());

        verify(mUnitDao).update(unit);
    }

    @Test
    public void update_callListenerCallback() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Unit newUnit = new Unit();
        newUnit.setName("some name");
        mSubject.updateUnit(newUnit, new AsyncResultListener<Unit>() {
            @Override
            public void onAsyncSuccess(Unit unit) {

                assertEquals(newUnit, unit);
                countDown.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);

        verify(mUnitDao).update(newUnit);
    }

    @Test
    public void update_callListenerCallback_nullNameError() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Unit newUnit = new Unit();
        mSubject.updateUnit(newUnit, new AsyncResultListener<Unit>() {
            @Override
            public void onAsyncSuccess(Unit unit) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void update_callListenerCallback_emptyNameError() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Unit newUnit = new Unit();
        newUnit.setName("   ");
        mSubject.updateUnit(newUnit, new AsyncResultListener<Unit>() {
            @Override
            public void onAsyncSuccess(Unit unit) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void update_callListenerCallback_duplicateNameError() throws InterruptedException {

        String unitName = "some name";
        when(mUnitDao.isUnitsWithSameNameExists(unitName)).thenReturn(true);

        final CountDownLatch countDown = new CountDownLatch(1);

        final Unit newUnit = new Unit();
        newUnit.setName(unitName);
        mSubject.updateUnit(newUnit, new AsyncResultListener<Unit>() {
            @Override
            public void onAsyncSuccess(Unit unit) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof UniqueNameConstraintException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void remove() {
        Unit unit = new Unit();
        mSubject.removeUnit(unit);

        verify(mUnitDao).delete(unit);
    }

    @Test
    public void findAllLiveData() {
        mSubject.getAvailableUnits();

        verify(mUnitDao).findAllLiveData();
    }

    @Test
    public void findAll() {
        mSubject.getAllUnits();

        verify(mUnitDao).findAll();
    }
}
