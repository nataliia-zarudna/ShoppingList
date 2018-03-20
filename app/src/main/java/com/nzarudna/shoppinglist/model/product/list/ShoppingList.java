package com.nzarudna.shoppinglist.model.product.list;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    public ShoppingList(UUID listID, ProductListDao productListDao, ProductDao productDao,
                        ProductTemplateRepository productTemplateRepository) {

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

    private static class UpdateListAsyncTask extends AsyncTask<ProductList, Void, Void> {

        ProductListDao mProductListDao;

        UpdateListAsyncTask(ProductListDao productListDao) {
            mProductListDao = productListDao;
        }

        @Override
        protected Void doInBackground(ProductList... productLists) {
            mProductListDao.update(productLists[0]);
            return null;
        }
    }

    public void updateProductList(ProductList productList) {
        new UpdateListAsyncTask(mProductListDao).execute(productList);
    }

    public void addProduct(@NonNull Product product, @Nullable AsyncResultListener<Product> listener) {
        insertProduct(product, listener);
        mProductTemplateRepository.createTemplateFromProduct(product, listener);
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

    private static class CreateProductAsyncTask extends ListenedAsyncTask<Product, Product> {

        ProductListDao mProductListDao;
        ProductDao mProductDao;

        CreateProductAsyncTask(ProductListDao productListDao, ProductDao productDao,
                               @Nullable AsyncResultListener<Product> listener) {
            super(listener);
            this.mProductListDao = productListDao;
            this.mProductDao = productDao;
        }

        @Override
        protected Product doInBackground(Product... products) {
            Product product = products[0];

            try {
                validateName(mProductDao, product.getName(), product.getListID());

                ProductList productList = mProductListDao.findByIDSync(product.getListID());
                if (productList.getSorting() == ProductList.SORT_PRODUCTS_BY_ORDER) {
                    double maxProductOrder = mProductDao.getMaxProductOrderByListID(product.getListID());
                    product.setOrder(maxProductOrder + PRODUCT_ORDER_STEP);
                }

                mProductDao.insert(product);

                return product;
            } catch (NameIsEmptyException | UniqueNameConstraintException e) {
                mResultException = e;
                return null;
            }
        }
    }

    private void insertProduct(Product product, @Nullable AsyncResultListener<Product> listener) {
        product.setListID(mListID);
        new CreateProductAsyncTask(mProductListDao, mProductDao, listener).execute(product);
    }

    private static class UpdateProductAsyncTask extends ListenedAsyncTask<Product, Product> {

        ProductDao mProductDao;

        UpdateProductAsyncTask(ProductDao productDao, @Nullable AsyncResultListener<Product> listener) {
            super(listener);
            mProductDao = productDao;
        }

        @Override
        protected Product doInBackground(Product... products) {
            Product product = products[0];

            try {
                validateName(mProductDao, product.getName(), product.getListID());

                Product oldProduct = mProductDao.findByIDSync(product.getProductID());
                if (!oldProduct.getName().equals(product.getName())
                        || oldProduct.getCategoryID() != product.getCategoryID()
                        || oldProduct.getUnitID() != product.getUnitID()) {
                    product.setTemplateID(null);
                }

                mProductDao.update(product);

                return product;
            } catch (NameIsEmptyException | UniqueNameConstraintException e) {
                mResultException = e;
                return null;
            }
        }
    }

    public void updateProduct(@NonNull Product product, @Nullable AsyncResultListener<Product> listener) {
        new UpdateProductAsyncTask(mProductDao, listener).execute(product);
    }

    private static void validateName(ProductDao productDao, String name, UUID listID) throws NameIsEmptyException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        if (productDao.isProductsWithSameNameAndListExists(name, listID)) {
            throw new UniqueNameConstraintException("Product with name '" + name + "' already exists in list " + listID);
        }
    }

    private static class MoveProductAsyncTask extends AsyncTask<Product, Void, Void> {

        ProductListDao mProductListDao;
        ProductDao mProductDao;

        public MoveProductAsyncTask(ProductListDao productListDao, ProductDao productDao) {
            this.mProductListDao = productListDao;
            this.mProductDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            Product product = products[0];
            Product productBefore = products[1];
            Product productAfter = products[2];

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
            if (productBefore == null) {
                newProductOrder = mProductDao.getMinProductOrderByListID(listID) - PRODUCT_ORDER_STEP;
            } else if (productAfter == null) {
                newProductOrder = mProductDao.getMaxProductOrderByListID(listID) + PRODUCT_ORDER_STEP;
            } else {
                if (resortPerformed) {
                    productAfter = mProductDao.findByIDSync(productAfter.getProductID());
                    productBefore = mProductDao.findByIDSync(productBefore.getProductID());
                }

                newProductOrder = (productAfter.getOrder() + productBefore.getOrder()) / 2;
            }
            product.setOrder(newProductOrder);
            mProductDao.update(product);

            return null;
        }
    }

    public void moveProduct(Product product, Product productBefore, Product productAfter) throws ShoppingListException {
        if (productAfter == null && productBefore == null) {
            throw new ShoppingListException("Either productAfter or productBefore must not be null");
        }

        new MoveProductAsyncTask(mProductListDao, mProductDao).execute(product, productBefore, productAfter);
    }

    private static class RemoveProductAsyncTask extends AsyncTask<Product, Void, Void> {

        ProductDao mProductDao;

        RemoveProductAsyncTask(ProductDao productDao) {
            this.mProductDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            mProductDao.delete(products[0]);
            return null;
        }
    }

    public void removeProduct(Product product) {
        new RemoveProductAsyncTask(mProductDao).execute(product);
    }

    private static class RemoveProductsByTemplateAsyncTask extends AsyncTask<Void, Void, Void> {

        ProductDao mProductDao;
        UUID mTemplateID;
        UUID mListID;

        RemoveProductsByTemplateAsyncTask(ProductDao productDao, UUID templateID, UUID listID) {
            mProductDao = productDao;
            mTemplateID = templateID;
            mListID = listID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mProductDao.delete(mTemplateID, mListID);
            return null;
        }
    }

    public void removeProductsWithTemplate(UUID templateID) {
        new RemoveProductsByTemplateAsyncTask(mProductDao, templateID, mListID).execute();
    }

    private static class RemoveProductsByStatusAsyncTask extends AsyncTask<Void, Void, Void> {

        ProductDao mProductDao;
        int mStatus;
        UUID mListID;

        RemoveProductsByStatusAsyncTask(ProductDao productDao, int status, UUID listID) {
            mProductDao = productDao;
            mStatus = status;
            mListID = listID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mProductDao.delete(mStatus, mListID);
            return null;
        }
    }

    public void removeProductsByStatus(@Product.ProductStatus int status) {
        new RemoveProductsByStatusAsyncTask(mProductDao, status, mListID).execute();
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

    private static class SaveSortingAndViewAsyncTask extends AsyncTask<Void, Void, Void> {

        ProductListDao mProductListDao;
        UUID mListID;
        int mSorting;
        boolean mIsGroupedView;

        SaveSortingAndViewAsyncTask(ProductListDao productListDao, UUID listID, int sorting, boolean isGroupedView) {
            mProductListDao = productListDao;
            mSorting = sorting;
            mIsGroupedView = isGroupedView;
            mListID = listID;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ProductList productList = mProductListDao.findByIDSync(mListID);
            if (productList.getSorting() != mSorting || productList.isGroupedView() != mIsGroupedView) {
                productList.setSorting(mSorting);
                productList.setIsGroupedView(mIsGroupedView);
                mProductListDao.update(productList);
            }
            return null;
        }
    }

    private void saveSortingAndView(@ProductList.ProductSorting int sorting, boolean isGroupedView) {
        new SaveSortingAndViewAsyncTask(mProductListDao, mListID, sorting, isGroupedView).execute();
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
            mProductDao.updateStatus(mStatus, mListID);
            return null;
        }
    }

    public void updateProductsStatus(@Product.ProductStatus int status) {
        new UpdateProductStatusAsyncTask(mProductDao, status, mListID).execute();
    }
}
