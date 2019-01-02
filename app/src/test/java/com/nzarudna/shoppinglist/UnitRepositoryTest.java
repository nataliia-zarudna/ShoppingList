package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.BaseRepository;
import com.nzarudna.shoppinglist.model.exception.EmptyNameException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitDao;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by nsirobaba on 2/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitRepositoryTest extends BaseRepositoryTest<Unit> {

    private UnitRepository mSubject;
    private AppExecutors mAppExecutors;

    @Mock
    private UnitDao mUnitDao;

    @Before
    public void setUp() {

        mAppExecutors = new TestAppExecutors();
        mSubject = new UnitRepository(mUnitDao, mAppExecutors);
    }

    @Override
    protected BaseRepository<Unit> getRepositorySubject() {
        return mSubject;
    }

    @Test
    public void create() throws InterruptedException {
        Unit unit = new Unit();
        unit.setName("some name");

        verifyCreate(unit);

        verify(mUnitDao).insert(unit);
    }

    @Test
    public void create_trimName() throws CloneNotSupportedException, InterruptedException {
        String name = " some name   ";

        Unit unit = new Unit();
        unit.setName(name);

        Unit resultUnit = unit.clone();
        resultUnit.setName(name.trim());

        verifyCreate(unit, resultUnit);

        verify(mUnitDao).insert(resultUnit);
    }

    @Test
    public void create_callListenerCallback_nullNameError() throws InterruptedException {
        final Unit newUnit = new Unit();

        verifyCreateWithException(newUnit, EmptyNameException.class);
    }

    @Test
    public void create_callListenerCallback_emptyNameError() throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        final Unit newUnit = new Unit();
        newUnit.setName("  ");

        verifyCreateWithException(newUnit, EmptyNameException.class);
    }

    @Test
    public void create_callListenerCallback_duplicateNameError() throws InterruptedException {

        String unitName = "some name";
        when(mUnitDao.isUnitsWithSameNameExists(unitName)).thenReturn(true);

        final Unit newUnit = new Unit();
        newUnit.setName(unitName);

        verifyCreateWithException(newUnit, UniqueNameConstraintException.class);
    }

    @Test
    public void update() throws InterruptedException {
        Unit unit = new Unit();
        unit.setName("Some name");

        verifyUpdate(unit);

        verify(mUnitDao).update(unit);
    }

    @Test
    public void update_trimName() throws CloneNotSupportedException, InterruptedException {
        String name = " some name   ";

        Unit unit = new Unit();
        unit.setName(name);

        Unit resultUnit = unit.clone();
        resultUnit.setName(name.trim());

        verifyUpdate(unit, resultUnit);

        verify(mUnitDao).update(unit);
    }

    @Test
    public void update_callListenerCallback_nullNameError() throws InterruptedException {
        final Unit newUnit = new Unit();
        verifyUpdateWithException(newUnit, EmptyNameException.class);
    }

    @Test
    public void update_callListenerCallback_emptyNameError() throws InterruptedException {
        final Unit newUnit = new Unit();
        newUnit.setName("   ");
        verifyUpdateWithException(newUnit, EmptyNameException.class);
    }

    @Test
    public void update_callListenerCallback_duplicateNameError() throws InterruptedException {

        String unitName = "some name";
        when(mUnitDao.isUnitsWithSameNameExists(unitName)).thenReturn(true);

        final Unit newUnit = new Unit();
        newUnit.setName(unitName);

        verifyUpdateWithException(newUnit, UniqueNameConstraintException.class);
    }

    @Test
    public void remove() throws InterruptedException {
        Unit unit = new Unit();

        verifyRemove(unit);

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
