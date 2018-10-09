package com.nzarudna.shoppinglist.model.product.list;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.ListenedAsyncTask;
import com.nzarudna.shoppinglist.model.ModelUtils;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import java.util.Date;
import java.util.UUID;

import static com.nzarudna.shoppinglist.Constants.PRODUCT_ORDER_STEP;

/**
 * Class that contains product list object
 * and operates with it
 */

public class ShoppingList {

    public static final String TAG = "ShoppingList";

    private UUID mListID;

    private ProductTemplateRepository mProductTemplateRepository;
    private ProductListDao mProductListDao;
    private ProductDao mProductDao;
    private UserRepository mUserRepository;
    private AppExecutors mAppExecutors;

    public ShoppingList(UUID listID, ProductListDao productListDao, ProductDao productDao,
                        ProductTemplateRepository productTemplateRepository, AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
        mListID = listID;
        mProductListDao = productListDao;
        mProductTemplateRepository = productTemplateRepository;
        mProductDao = productDao;
    }

    public LiveData<ProductList> getListData() {
        return mProductListDao.findByID(mListID);
    }

    public UUID getListID() {
        return mListID;
    }

    public void updateProductList(ProductList productList) {
        mAppExecutors.loadAsync(() -> {

            productList.setModifiedAt(new Date());
            productList.setModifiedBy(mUserRepository.getSelfUserID());
            mProductListDao.update(productList);

        }, null);
    }

    public void addProduct(@NonNull Product product, @Nullable AsyncResultListener<Product> listener) {
        insertProduct(product, listener);
        mProductTemplateRepository.createFromProductAsync(product, null);
    }

    public void addProductFromTemplate(ProductTemplate template, @Nullable AsyncResultListener<Product> listener) {

        Product product = new Product();
        product.setName(template.getName());
        product.setListID(mListID);
        product.setCategoryID(template.getCategoryID());
        product.setTemplateID(template.getTemplateID());
        product.setUnitID(template.getUnitID());

        insertProduct(product, listener);
    }

    @WorkerThread
    private void insertProduct(Product product, @Nullable AsyncResultListener<Product> listener) {
        mAppExecutors.loadAsync(() -> {
            product.setListID(mListID);

            validateName(mProductDao, product.getName(), product.getListID());

            ProductList productList = mProductListDao.findByIDSync(product.getListID());
            if (productList.getSorting() == ProductList.SORT_PRODUCTS_BY_ORDER) {
                double maxProductOrder = mProductDao.getMaxProductOrderByListID(product.getListID());
                product.setOrder(maxProductOrder + PRODUCT_ORDER_STEP);
            }

            mProductDao.insert(product);

            return product;

        }, listener);
    }

    public void updateProduct(@NonNull Product product, @Nullable AsyncResultListener<Product> listener) {
        mAppExecutors.loadAsync(() -> {

            validateName(mProductDao, product.getName(), product.getListID());

            Product oldProduct = mProductDao.findByIDSync(product.getProductID());
            if (!oldProduct.getName().equals(product.getName())
                    || oldProduct.getCategoryID() != product.getCategoryID()
                    || oldProduct.getUnitID() != product.getUnitID()) {
                product.setTemplateID(null);
            }

            mProductDao.update(product);

            mProductListDao.update(product.getListID(), new Date(), mUserRepository.getSelfUserID());

            return product;

        }, listener);
    }

    private static void validateName(ProductDao productDao, String name, UUID listID) throws NameIsEmptyException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        if (productDao.isProductsWithSameNameAndListExists(name, listID)) {
            throw new UniqueNameConstraintException("Product with name '" + name + "' already exists in list " + listID);
        }
    }

    public void moveProduct(Product product, Product productBefore, Product productAfter) throws ShoppingListException {
        if (productAfter == null && productBefore == null) {
            throw new ShoppingListException("Either productAfter or productBefore must not be null");
        }

        mAppExecutors.loadAsync(() -> {

            UUID listID = product.getListID();

            ProductList productList = mProductListDao.findByIDSync(listID);
            boolean resortPerformed = false;
            if (productList.getSorting() != ProductList.SORT_PRODUCTS_BY_ORDER) {

                if (productList.getSorting() == ProductList.SORT_PRODUCTS_BY_NAME) {
                    mProductDao.updateProductOrdersByListIDSortByName(listID);
                } else {
                    mProductDao.updateProductOrdersByListIDSortByStatusAndName(listID);
                }
                productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);

                //TODO: fix list status 2
                productList.setStatus(ProductList.STATUS_ACTIVE);

                mProductListDao.update(productList);
            }

            double newProductOrder;
            Product finalProductBefore = productBefore;
            Product finalProductAfter = productAfter;
            if (finalProductBefore == null) {
                newProductOrder = mProductDao.getMinProductOrderByListID(listID) - PRODUCT_ORDER_STEP;
            } else if (finalProductAfter == null) {
                newProductOrder = mProductDao.getMaxProductOrderByListID(listID) + PRODUCT_ORDER_STEP;
            } else {
                if (resortPerformed) {
                    finalProductAfter = mProductDao.findByIDSync(finalProductAfter.getProductID());
                    finalProductBefore = mProductDao.findByIDSync(finalProductBefore.getProductID());
                }

                newProductOrder = (finalProductAfter.getOrder() + finalProductBefore.getOrder()) / 2;
            }
            product.setOrder(newProductOrder);
            mProductDao.update(product);

        }, null);
    }

    public void removeProduct(Product product, AsyncListener listener) {
        mAppExecutors.loadAsync(() -> mProductDao.delete(product), listener);
    }

    public void removeProductsWithTemplate(UUID templateID) {
        mAppExecutors.loadAsync(() -> mProductDao.delete(templateID, mListID), null);
    }

    public void removeProductsByStatus(@Product.ProductStatus int status) {
        mAppExecutors.loadAsync(() -> mProductDao.delete(status, mListID), null);
    }

    public DataSource.Factory<Integer, CategoryProductItem> getProducts(@ProductList.ProductSorting int sorting, boolean isGroupedView) throws ShoppingListException {

        DataSource.Factory<Integer, CategoryProductItem> resultProducts;

        if (isGroupedView) {
            switch (sorting) {
                case ProductList.SORT_PRODUCTS_BY_NAME:
                    resultProducts = mProductDao.findByListIDSortByNameWithCategory(mListID);
                    break;
                case ProductList.SORT_PRODUCTS_BY_STATUS:
                    resultProducts = mProductDao.findByListIDSortByStatusAndNameWithCategory(mListID);
                    break;
                case ProductList.SORT_PRODUCTS_BY_ORDER:
                    resultProducts = mProductDao.findByListIDSortByProductOrderWithCategory(mListID);
                    break;
                default:
                    throw new ShoppingListException("Unknown products sorting " + sorting);
            }
        } else {
            switch (sorting) {
                case ProductList.SORT_PRODUCTS_BY_NAME:
                    resultProducts = mProductDao.findByListIDSortByName(mListID);
                    break;
                case ProductList.SORT_PRODUCTS_BY_STATUS:
                    resultProducts = mProductDao.findByListIDSortByStatusAndName(mListID);
                    break;
                case ProductList.SORT_PRODUCTS_BY_ORDER:
                    resultProducts = mProductDao.findByListIDSortByProductOrder(mListID);
                    break;
                default:
                    throw new ShoppingListException("Unknown products sorting " + sorting);
            }
        }
        saveSortingAndView(sorting, isGroupedView);

        return resultProducts;
    }

    private void saveSortingAndView(@ProductList.ProductSorting int sorting, boolean isGroupedView) {
        mAppExecutors.loadAsync(() -> {

            ProductList productList = mProductListDao.findByIDSync(mListID);
            if (productList.getSorting() != sorting || productList.isGroupedView() != isGroupedView) {
                productList.setSorting(sorting);
                productList.setIsGroupedView(isGroupedView);
                mProductListDao.update(productList);
            }
        }, null);
    }

    static class UpdateProductStatusAsyncTask extends AsyncTask<Void, Void, Void> {

        ProductDao mProductDao;
        int mStatus;
        UUID mListID;

        UpdateProductStatusAsyncTask(ProductDao productDao, int status, UUID listID) {
            mProductDao = productDao;
            mStatus = status;
            mListID = listID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    public void updateProductsStatus(@Product.ProductStatus int status) {
        mAppExecutors.loadAsync(() -> mProductDao.updateStatus(status, mListID), null);
    }
}
