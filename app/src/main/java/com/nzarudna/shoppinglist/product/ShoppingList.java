package com.nzarudna.shoppinglist.product;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.nzarudna.shoppinglist.persistence.CategoryProductItem;
import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductListDao;

import static com.nzarudna.shoppinglist.Constants.PRODUCT_ORDER_STEP;

/**
 * Class that contains productsList object
 * and operates with it
 */

public class ShoppingList {

    public static final String TAG = "ShoppingList";

    private int mListID;

    private ProductTemplateRepository mProductTemplateRepository;
    private ProductListDao mProductListDao;
    private ProductDao mProductDao;

    public ShoppingList(int listID, ProductListDao productListDao, ProductDao productDao,
                        ProductTemplateRepository productTemplateRepository) {

        mListID = listID;
        mProductListDao = productListDao;
        mProductTemplateRepository = productTemplateRepository;
        mProductDao = productDao;
    }

    public LiveData<ProductList> getListData() {
        return mProductListDao.findByID(mListID);
    }

    public int getListID() {
        return mListID;
    }

    public void updateProductList(final ProductList productList) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                mProductListDao.update(productList);
                return null;
            }
        }.execute();
    }

    public void addProduct(@NonNull final Product product) {
        insertProduct(product);
        mProductTemplateRepository.createTemplateFromProduct(product);
    }

    public void addProductFromTemplate(ProductTemplate template) {

        Product product = new Product();
        product.setName(template.getName());
        product.setCategoryID(template.getCategoryID());
        product.setTemplateID(template.getTemplateID());

        insertProduct(product);
    }

    private void insertProduct(final Product product) {
        product.setListID(mListID);

        ProductList productList = mProductListDao.findByIDSync(mListID);
        if (productList.getSorting() == ProductList.SORT_PRODUCTS_BY_ORDER) {
            double maxProductOrder = mProductDao.getMaxProductOrderByListID(mListID);
            product.setOrder(maxProductOrder + PRODUCT_ORDER_STEP);
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mProductDao.insert(product);
                return null;
            }
        }.execute();
    }

    public void updateProduct(@NonNull final Product product) {

        Product oldProduct = mProductDao.findByIDSync(product.getProductID());
        if (!oldProduct.getName().equals(product.getName())
                || oldProduct.getCategoryID() != product.getCategoryID()
                || oldProduct.getUnitID() != product.getUnitID()) {
            product.setTemplateID(0);
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                mProductDao.update(product);
                return null;
            }
        }.execute();
    }

    public void moveProduct(Product product, Product productAfter, Product productBefore) throws ShoppingListException {
        if (productAfter == null && productBefore == null) {
            throw new ShoppingListException("Either productAfter or productBefore must not be null");
        }

        new AsyncTask<Product, Void, Void>() {
            @Override
            protected Void doInBackground(Product... products) {
                Product product = products[0];
                Product productAfter = products[1];
                Product productBefore = products[2];

                ProductList productList = mProductListDao.findByIDSync(mListID);
                boolean resortPerformed = false;
                if (productList.getSorting() != ProductList.SORT_PRODUCTS_BY_ORDER) {

                    if (productList.getSorting() == ProductList.SORT_PRODUCTS_BY_NAME) {
                        mProductDao.updateProductOrdersByListIDSortByName(mListID);
                    } else {
                        mProductDao.updateProductOrdersByListIDSortByStatusAndName(mListID);
                    }
                    productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
                    mProductListDao.update(productList);
                }

                double newProductOrder;
                if (productBefore == null) {
                    newProductOrder = mProductDao.getMinProductOrderByListID(mListID) - PRODUCT_ORDER_STEP;
                } else if (productAfter == null) {
                    newProductOrder = mProductDao.getMaxProductOrderByListID(mListID) + PRODUCT_ORDER_STEP;
                } else {
                    if (resortPerformed) {
                        productAfter = mProductDao.findByIDSync(productAfter.getProductID());
                        productBefore = mProductDao.findByIDSync(productBefore.getProductID());
                    }

                    newProductOrder = (productAfter.getOrder() - productBefore.getOrder()) / 2;
                }
                product.setOrder(newProductOrder);
                mProductDao.update(product);

                return null;
            }
        }.execute(product, productAfter, productBefore);
    }

    public void removeProduct(final Product product) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mProductDao.delete(product);
                return null;
            }
        }.execute();
    }

    public DataSource.Factory<Integer, CategoryProductItem> getProducts(@ProductList.ProductSorting int sorting, boolean isGroupedView) throws ShoppingListException {

        if (isGroupedView) {
            switch (sorting) {
                case ProductList.SORT_PRODUCTS_BY_NAME:
                    return mProductDao.findByListIDSortByNameWithCategory(mListID);
                case ProductList.SORT_PRODUCTS_BY_STATUS:
                    return mProductDao.findByListIDSortByStatusAndNameWithCategory(mListID);
                case ProductList.SORT_PRODUCTS_BY_ORDER:
                    return mProductDao.findByListIDSortByProductOrderWithCategory(mListID);
                default:
                    throw new ShoppingListException("Unknown products sorting " + sorting);
            }
        } else {
            switch (sorting) {
                case ProductList.SORT_PRODUCTS_BY_NAME:
                    return mProductDao.findByListIDSortByName(mListID);
                case ProductList.SORT_PRODUCTS_BY_STATUS:
                    return mProductDao.findByListIDSortByStatusAndName(mListID);
                case ProductList.SORT_PRODUCTS_BY_ORDER:
                    return mProductDao.findByListIDSortByProductOrder(mListID);
                default:
                    throw new ShoppingListException("Unknown products sorting " + sorting);
            }
        }

    }
}
