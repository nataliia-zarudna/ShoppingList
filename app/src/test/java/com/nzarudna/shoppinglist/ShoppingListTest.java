package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.exception.EmptyNameException;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test methods on shopping list object
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingListTest extends BaseAsyncTest {

    private static final UUID MOCKED_PRODUCTS_LIST_ID = UUID.randomUUID();
    private static final UUID MOCKED_SELF_USER_ID = UUID.randomUUID();

    @Mock
    private ProductListDao mProductListDao;

    @Mock
    private ProductDao mProductDao;

    @Mock
    private ProductTemplateRepository mProductTemplateRepository;

    @Mock
    private UserRepository mUserRepository;

    private UUID mMockedCategoryID;
    private UUID mMockedTemplateID;
    private ProductList mMockedProductList;

    private ShoppingList mSubject;
    private AppExecutors mAppExecutors;

    @Before
    public void setUp() {

        mAppExecutors = new TestAppExecutors();
        mSubject = new ShoppingList(MOCKED_PRODUCTS_LIST_ID, mProductListDao, mProductDao,
                mProductTemplateRepository, mUserRepository, mAppExecutors);

        mMockedCategoryID = UUID.randomUUID();
        mMockedCategoryID = UUID.randomUUID();
        mMockedProductList = Mockito.mock(ProductList.class);
    }

    @Test
    public void updateList() throws InterruptedException, CloneNotSupportedException {

        UUID createdByUserID = MOCKED_SELF_USER_ID;
        ProductList updatedList = new ProductList("Some name", createdByUserID);

        when(mUserRepository.getSelfUserID()).thenReturn(createdByUserID);

        ProductList expectedList = updatedList.clone();
        expectedList.setCreatedAt(new Date());
        expectedList.setCreatedBy(createdByUserID);
        expectedList.setModifiedAt(new Date());
        expectedList.setModifiedBy(createdByUserID);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AsyncResultListener<ProductList> listener = Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {

            ProductList resultList = invocation.getArgument(0);
            assertTrue(AssertUtils.matches(resultList, expectedList));

            countDownLatch.countDown();
            return null;
        }).when(listener).onAsyncSuccess(any(ProductList.class));

        mSubject.updateProductList(updatedList, listener);
        await(countDownLatch);

        verify(mProductListDao).update(updatedList);
    }

    @Test
    public void addProductToList_withNotCustomSorting() throws InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        String productName = "Some product name";
        Product product = new Product();
        product.setName(productName);

        final Product expectedProduct = new Product();
        expectedProduct.setName(productName);
        expectedProduct.setListID(mSubject.getListID());

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AsyncResultListener<Product> listener = Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {

            Product resultProduct = invocation.getArgument(0);
            assertTrue(AssertUtils.matchesExceptID(resultProduct, expectedProduct));

            countDownLatch.countDown();
            return null;
        }).when(listener).onAsyncSuccess(any(Product.class));

        mSubject.addProduct(product, listener);
        await(countDownLatch);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository).createFromProductAsync(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)),
                isNull());
        verify(listener).onAsyncSuccess(any(Product.class));
    }

    @Test
    public void create_nullNameError() throws InterruptedException {

        final Product newProduct = new Product();

        final CountDownLatch countDown = new CountDownLatch(1);

        AsyncResultListener<Product> listener = getAsyncResultListenerForError(EmptyNameException.class, countDown);
        mSubject.addProduct(newProduct, listener);
        await(countDown);

        verify(listener).onAsyncError(any(EmptyNameException.class));
    }

    @Test
    public void create_emptyNameError() throws InterruptedException {

        final Product newProduct = new Product();
        newProduct.setName("   ");

        final CountDownLatch countDown = new CountDownLatch(1);

        AsyncResultListener<Product> listener = getAsyncResultListenerForError(EmptyNameException.class, countDown);
        mSubject.addProduct(newProduct, listener);
        await(countDown);

        verify(listener).onAsyncError(any(EmptyNameException.class));
    }

    @Test
    public void create_duplicateNameError() throws InterruptedException {

        String name = "some name";
        when(mProductDao.isProductsWithSameNameAndListExists(name, MOCKED_PRODUCTS_LIST_ID)).thenReturn(true);

        final Product newProduct = new Product();
        newProduct.setName(name);

        final CountDownLatch countDown = new CountDownLatch(1);

        AsyncResultListener<Product> listener = getAsyncResultListenerForError(EmptyNameException.class, countDown);
        mSubject.addProduct(newProduct, listener);
        await(countDown);

        verify(listener).onAsyncError(any(UniqueNameConstraintException.class));
    }

    @Test
    public void addProductToList_withCustomSorting() throws InterruptedException {

        ProductList productList = new ProductList("Some list", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        when(mProductDao.getMaxProductOrderByListID(MOCKED_PRODUCTS_LIST_ID)).thenReturn(7.81);

        Product product = new Product();
        product.setName("Some product name");

        final Product expectedProduct = new Product();
        expectedProduct.setName(product.getName());
        expectedProduct.setListID(mSubject.getListID());
        expectedProduct.setOrder(17.81);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.addProduct(product, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertTrue(AssertUtils.matchesExceptID(resultProduct, expectedProduct));
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        await(countDownLatch);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository).createFromProductAsync(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)),
                isNull());
    }

    @Test
    public void addProductFromTemplate_withNotCustomSorting() throws InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        String mockedName = "Template name";

        ProductTemplate template = new ProductTemplate();
        template.setName(mockedName);
        template.setTemplateID(mMockedTemplateID);
        template.setCategoryID(mMockedCategoryID);

        final Product expectedProduct = new Product();
        expectedProduct.setName(mockedName);
        expectedProduct.setListID(MOCKED_PRODUCTS_LIST_ID);
        expectedProduct.setCategoryID(mMockedCategoryID);
        expectedProduct.setTemplateID(mMockedTemplateID);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.addProductFromTemplate(template, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertTrue(AssertUtils.matchesExceptID(resultProduct, expectedProduct));
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        await(countDownLatch);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository, never())
                .createFromProductAsync(eq(expectedProduct), any(AsyncResultListener.class));
    }

    @Test
    public void addProductFromTemplate_withCustomSorting() throws InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        when(mProductDao.getMaxProductOrderByListID(MOCKED_PRODUCTS_LIST_ID)).thenReturn(2.);

        String mockedName = "Template name";

        ProductTemplate template = new ProductTemplate();
        template.setName(mockedName);
        template.setTemplateID(mMockedTemplateID);
        template.setCategoryID(mMockedCategoryID);

        final Product expectedProduct = new Product();
        expectedProduct.setName(mockedName);
        expectedProduct.setListID(MOCKED_PRODUCTS_LIST_ID);
        expectedProduct.setCategoryID(mMockedCategoryID);
        expectedProduct.setTemplateID(mMockedTemplateID);
        expectedProduct.setOrder(12);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.addProductFromTemplate(template, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertTrue(AssertUtils.matchesExceptID(resultProduct, expectedProduct));
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        await(countDownLatch);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository, never())
                .createFromProductAsync(eq(expectedProduct), any(AsyncResultListener.class));
    }

    @Test
    public void updateProduct() throws InterruptedException {

        UUID productID = UUID.randomUUID();
        final Product product = new Product();
        product.setName("Some name");
        product.setProductID(productID);
        product.setTemplateID(UUID.randomUUID());
        when(mProductDao.findByIDSync(productID)).thenReturn(product);

        verifyProductUpdate(product, product);
    }

    @Test
    public void update_nullNameError() throws InterruptedException {

        final Product newProduct = new Product();

        verifyProductUpdateWithException(newProduct, EmptyNameException.class);
    }

    @Test
    public void update_emptyNameError() throws InterruptedException {

        final Product newProduct = new Product();
        newProduct.setName("   ");

        verifyProductUpdateWithException(newProduct, EmptyNameException.class);
    }

    @Test
    public void update_duplicateNameError() throws InterruptedException {

        String name = "some name";
        UUID listID = MOCKED_PRODUCTS_LIST_ID;

        when(mProductDao.isProductsWithSameNameAndListExists(name, listID)).thenReturn(true);

        final Product newProduct = new Product();
        newProduct.setName(name);
        newProduct.setListID(listID);

        verifyProductUpdateWithException(newProduct, UniqueNameConstraintException.class);
    }

    private void verifyProductUpdateWithException(Product newProduct, Class<? extends Exception> exceptionClass) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AsyncResultListener<Product> listener = getAsyncResultListenerForError(exceptionClass, countDownLatch);
        mSubject.updateProduct(newProduct, listener);
        await(countDownLatch);

        verify(listener).onAsyncError(any(exceptionClass));
    }

    @Test
    public void updateProductName_testClearTemplateID() throws InterruptedException {

        UUID productID = UUID.randomUUID();
        UUID mockedTemplateID = UUID.randomUUID();

        Product oldProduct = new Product();
        oldProduct.setName("Old name");
        oldProduct.setProductID(productID);
        oldProduct.setTemplateID(mockedTemplateID);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product();
        productWithNewName.setName("New name");
        productWithNewName.setProductID(productID);
        productWithNewName.setTemplateID(mockedTemplateID);

        final Product expectedProduct = new Product();
        expectedProduct.setName("New name");
        expectedProduct.setProductID(productID);
        expectedProduct.setTemplateID(null);

        verifyProductUpdate(productWithNewName, expectedProduct);
    }

    private void verifyProductUpdate(Product inputProduct, Product resultProduct) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AsyncResultListener<Product> listener = getAsyncResultListener(resultProduct, countDownLatch);
        mSubject.updateProduct(inputProduct, listener);
        await(countDownLatch);

        verify(mProductDao).update(resultProduct);
        verify(listener).onAsyncSuccess(resultProduct);
    }

    @Test
    public void updateCategoryID_testClearTemplateID() throws InterruptedException {

        UUID productID = UUID.randomUUID();

        String mockedName = "Template name";

        Product oldProduct = new Product();
        oldProduct.setName("Some name");
        oldProduct.setProductID(productID);
        oldProduct.setCategoryID(mMockedCategoryID);
        oldProduct.setTemplateID(mMockedTemplateID);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product();
        productWithNewName.setName("Some name");
        productWithNewName.setProductID(productID);
        productWithNewName.setCategoryID(mMockedCategoryID);
        productWithNewName.setTemplateID(mMockedTemplateID);

        final Product expectedProduct = new Product();
        expectedProduct.setName("Some name");
        expectedProduct.setProductID(productID);
        expectedProduct.setCategoryID(mMockedCategoryID);
        expectedProduct.setTemplateID(null);

        verifyProductUpdate(productWithNewName, expectedProduct);
    }

    @Test
    public void updateUniID_testClearTemplateID() throws InterruptedException {

        UUID productID = UUID.randomUUID();
        String name = "Some name";
        UUID mockedUnitID = UUID.randomUUID();

        Product oldProduct = new Product();
        oldProduct.setName(name);
        oldProduct.setProductID(productID);
        oldProduct.setUnitID(UUID.randomUUID());
        oldProduct.setTemplateID(mMockedTemplateID);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product();
        productWithNewName.setName("Some name");
        productWithNewName.setProductID(productID);
        productWithNewName.setUnitID(mockedUnitID);
        productWithNewName.setTemplateID(mMockedTemplateID);

        final Product expectedProduct = new Product();
        expectedProduct.setName("Some name");
        expectedProduct.setProductID(productID);
        expectedProduct.setUnitID(mockedUnitID);
        expectedProduct.setTemplateID(null);

        verifyProductUpdate(productWithNewName, expectedProduct);
    }

    @Test
    public void updateProductsStatus() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.updateProductsStatus(Product.ABSENT, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        verify(mProductDao).updateStatus(Product.ABSENT, MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void moveProduct_insideList_WithCustomOrder() throws ShoppingListException, InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setOrder(8.7);
        products.add(product3);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(product3, product1, product2, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        product3.setOrder((8.7 - 4.1) / 2);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithCustomOrder() throws ShoppingListException, InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setOrder(8.7);
        products.add(product3);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(product1, null, product2, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        product1.setOrder(2 - 10);

        verify(mProductDao).update(product1);
    }

    @Test
    public void moveProduct_toListEnd_WithCustomOrder() throws ShoppingListException, InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setOrder(8.7);
        products.add(product3);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(product2, product3, null, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        product2.setOrder(8.7 + 10);

        verify(mProductDao).update(product2);
    }

    @Test
    public void moveProduct_insideList_WithOrderByName() throws ShoppingListException, InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 1");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product4);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(product3, product1, product2, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        product3.setOrder(5);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithOrderByName() throws ShoppingListException, InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 1");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product4);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(product3, product2, null, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        product3.setOrder(-10);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListEnd_WithOrderByName() throws ShoppingListException, InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 1");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product4);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(product1, null, product3, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        product1.setOrder(40);

        verify(mProductDao).update(product1);
    }

    @Test
    public void moveProduct_insideList_WithOrderByStatus() throws ShoppingListException, InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setStatus(Product.ACTIVE);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        product4.setStatus(Product.BOUGHT);
        products.add(product4);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(product3, product1, product2, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        // list order: 2 1 3 4
        product3.setOrder(5);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithOrderByStatus() throws ShoppingListException, InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setStatus(Product.ACTIVE);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        product4.setStatus(Product.BOUGHT);
        products.add(product4);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(product3, product2, null, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        product3.setOrder(-10);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListEnd_WithOrderByStatus() throws ShoppingListException, InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setStatus(Product.ACTIVE);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        product4.setStatus(Product.BOUGHT);
        products.add(product4);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(product1, null, product3, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        product1.setOrder(40);

        verify(mProductDao).update(product1);
    }

    @Test(expected = ShoppingListException.class)
    public void moveProduct_exceptionOnNullBeforeAndAfterProducts() throws ShoppingListException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.moveProduct(new Product(), null, null, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);
    }

    @Test
    public void removeProduct() throws InterruptedException {

        Product product = new Product();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.removeProduct(product, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        verify(mProductDao).delete(product);
    }

    @Test
    public void removeProductsWithTemplate() throws InterruptedException {

        UUID templateID = UUID.randomUUID();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        AsyncListener listener = getEmptyAsyncListener(countDownLatch);
        mSubject.removeProductsWithTemplate(templateID, listener);
        await(countDownLatch);

        verify(mProductDao).delete(templateID, MOCKED_PRODUCTS_LIST_ID);
        verify(listener).onAsyncSuccess();
    }

    @Test
    public void removeProductsByStatus() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        AsyncListener listener = getEmptyAsyncListener(countDownLatch);
        mSubject.removeProductsByStatus(Product.BOUGHT, listener);
        await(countDownLatch);

        verify(mProductDao).delete(Product.BOUGHT, MOCKED_PRODUCTS_LIST_ID);
        verify(listener).onAsyncSuccess();
    }

    @Test
    public void findProductsByListID_sortByName() throws ShoppingListException, InterruptedException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_NAME, false, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        verify(mProductDao).findByListIDSortByName(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByStatus() throws ShoppingListException, InterruptedException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_STATUS, false, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        verify(mProductDao).findByListIDSortByStatusAndName(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByOrder() throws ShoppingListException, InterruptedException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_ORDER, false, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        verify(mProductDao).findByListIDSortByProductOrder(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByName_groupeView() throws ShoppingListException, InterruptedException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_NAME, true, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        verify(mProductDao).findByListIDSortByNameWithCategory(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByStatus_groupeView() throws ShoppingListException, InterruptedException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_STATUS, true, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        verify(mProductDao).findByListIDSortByStatusAndNameWithCategory(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByOrder_groupeView() throws ShoppingListException, InterruptedException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_ORDER, true, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        verify(mProductDao).findByListIDSortByProductOrderWithCategory(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_saveSortAndView() throws ShoppingListException, InterruptedException {

        ProductList list = new ProductList("Some name", UUID.randomUUID());
        list.setListID(mSubject.getListID());
        list.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        list.setIsGroupedView(false);
        when(mProductListDao.findByIDSync(list.getListID())).thenReturn(list);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_STATUS, true, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);

        ProductList expectedList = new ProductList(list.getName(), list.getCreatedBy());
        expectedList.setCreatedAt(list.getCreatedAt());
        expectedList.setListID(list.getListID());
        expectedList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        expectedList.setIsGroupedView(true);

        verify(mProductListDao).update(expectedList);
    }

    @Test(expected = ShoppingListException.class)
    public void findProductsByListID_exceptionOnUnknownSorting() throws ShoppingListException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.getProducts(99, false, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);
    }

    @Test(expected = ShoppingListException.class)
    public void findProductsByListID_exceptionOnUnknownSorting_groupeView() throws ShoppingListException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.getProducts(99, true, getEmptyAsyncListener(countDownLatch));
        await(countDownLatch);
    }
}
