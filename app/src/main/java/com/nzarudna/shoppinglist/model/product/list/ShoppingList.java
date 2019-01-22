package com.nzarudna.shoppinglist.model.product.list;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.ModelUtils;
import com.nzarudna.shoppinglist.model.exception.EmptyNameException;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.utils.AppExecutors;
import com.nzarudna.shoppinglist.utils.ErrorHandler;

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
                        ProductTemplateRepository productTemplateRepository, UserRepository userRepository,
                        AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
        mListID = listID;
        mProductListDao = productListDao;
        mProductTemplateRepository = productTemplateRepository;
        mProductDao = productDao;
        mUserRepository = userRepository;
    }

    public LiveData<ProductList> getListData() {
        return mProductListDao.findByID(mListID);
    }

    public UUID getListID() {
        return mListID;
    }

    public void updateProductList(ProductList productList, AsyncResultListener<ProductList> listener) {
        mAppExecutors.loadAsync(() -> {

            productList.setModifiedAt(new Date());
            productList.setModifiedBy(mUserRepository.getSelfUserID());
            mProductListDao.update(productList);

            return productList;

        }, listener);
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

            validateName(product.getName(), product.getListID());

            ProductList productList = mProductListDao.findByIDSync(product.getListID());
            if (productList.isUseCustomSorting()) {
                double maxProductOrder = mProductDao.getMaxProductOrderByListID(product.getListID());
                product.setOrder(maxProductOrder + PRODUCT_ORDER_STEP);
            }

            mProductDao.insert(product);

            return product;

        }, listener);
    }

    public void updateProduct(@NonNull Product product, @Nullable AsyncResultListener<Product> listener) {
        mAppExecutors.loadAsync(() -> {

            ModelUtils.validateNameIsNotEmpty(product.getName());

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

    private void validateName(String name, UUID listID) throws EmptyNameException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        if (mProductDao.isProductsWithSameNameAndListExists(name, listID)) {
            throw new UniqueNameConstraintException("Product with name '" + name + "' already exists in list " + listID);
        }
    }

    public void moveProduct(Product product, Product productBefore, Product productAfter, AsyncListener asyncListener) throws ShoppingListException {
        if (productAfter == null && productBefore == null) {
            throw new ShoppingListException("Either productAfter or productBefore must not be null");
        }

        mAppExecutors.loadAsync(() -> {

            UUID listID = product.getListID();

            ProductList productList = mProductListDao.findByIDSync(listID);
            boolean resortPerformed = false;
            if (!productList.isUseCustomSorting()) {

                if (productList.getSorting() == ProductList.SORT_PRODUCTS_BY_NAME) {
                    mProductDao.updateProductOrdersByListIDSortByName(listID);
                } else {
                    mProductDao.updateProductOrdersByListIDSortByStatusAndName(listID);
                }
                productList.setUseCustomSorting(true);

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

        }, asyncListener);
    }

    public void removeProduct(Product product, AsyncListener listener) {
        mAppExecutors.loadAsync(() -> mProductDao.delete(product), listener);
    }

    public void removeProductsWithTemplate(UUID templateID, AsyncListener listener) {
        mAppExecutors.loadAsync(() -> mProductDao.delete(templateID, mListID), listener);
    }

    public void removeProductsByStatus(@Product.ProductStatus int status, AsyncListener listener) {
        mAppExecutors.loadAsync(() -> mProductDao.delete(status, mListID), listener);
    }

    public DataSource.Factory<Integer, CategoryProductItem> getProducts(boolean useCustomSorting,
                                                                        @ProductList.ProductSorting int sorting,
                                                                        boolean isGroupedView,
                                                                        AsyncListener asyncListener) {

        DataSource.Factory<Integer, CategoryProductItem> resultProducts = null;

        if (isGroupedView) {
            if (useCustomSorting) {
                resultProducts = mProductDao.findByListIDSortByProductOrderWithCategory(mListID);
            } else {
                switch (sorting) {
                    case ProductList.SORT_PRODUCTS_BY_NAME:
                        resultProducts = mProductDao.findByListIDSortByNameWithCategory(mListID);
                        break;
                    case ProductList.SORT_PRODUCTS_BY_STATUS:
                        resultProducts = mProductDao.findByListIDSortByStatusAndNameWithCategory(mListID);
                        break;
                    default:
                        ErrorHandler.logError(TAG, "Unknown products sorting " + sorting);
                }
            }
        } else {
            if (useCustomSorting) {
                resultProducts = mProductDao.findByListIDSortByProductOrder(mListID);
            } else {
                switch (sorting) {
                    case ProductList.SORT_PRODUCTS_BY_NAME:
                        resultProducts = mProductDao.findByListIDSortByName(mListID);
                        break;
                    case ProductList.SORT_PRODUCTS_BY_STATUS:
                        resultProducts = mProductDao.findByListIDSortByStatusAndName(mListID);
                        break;
                    default:
                        ErrorHandler.logError(TAG, "Unknown products sorting " + sorting);
                }
            }
        }
        saveSortingAndView(useCustomSorting, sorting, isGroupedView, asyncListener);

        return resultProducts;
    }

    private void saveSortingAndView(boolean useCustomSorting, @ProductList.ProductSorting int sorting, boolean isGroupedView, AsyncListener asyncListener) {
        mAppExecutors.loadAsync(() -> {

            ProductList productList = mProductListDao.findByIDSync(mListID);
            if (productList.getSorting() != sorting
                    || productList.isUseCustomSorting() != useCustomSorting
                    || productList.isGroupedView() != isGroupedView) {
                productList.setSorting(sorting);
                productList.setIsGroupedView(isGroupedView);
                mProductListDao.update(productList);
            }
        }, asyncListener);
    }

    public void updateProductsStatus(@Product.ProductStatus int status, AsyncListener asyncListener) {
        mAppExecutors.loadAsync(() -> mProductDao.updateStatus(status, mListID), asyncListener);
    }
}
