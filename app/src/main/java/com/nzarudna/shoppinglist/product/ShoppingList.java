package com.nzarudna.shoppinglist.product;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.nzarudna.shoppinglist.persistence.CategoryProductItem;
import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductListDao;
import com.nzarudna.shoppinglist.persistence.ProductTemplateDao;

import javax.inject.Inject;

/**
 * Class that contains productsList object
 * and operates with it
 */

public class ShoppingList {

    public static final String TAG = "ShoppingList";

    private int mListID;

    @Inject
    public ProductTemplateRepository mProductTemplateRepository;
    @Inject
    public ProductListDao mProductListDao;
    @Inject
    public ProductTemplateDao mProductTemplateDao;
    @Inject
    public ProductDao mProductDao;

    public ShoppingList(/*ProductListDao productListDao,*/ int listID) {
        //mProductListDao = productListDao;
        mListID = listID;
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
            product.setOrder(maxProductOrder + 10);
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
                    newProductOrder = mProductDao.getMinProductOrderByListID(mListID) - 10;
                } else if (productAfter == null) {
                    newProductOrder = mProductDao.getMaxProductOrderByListID(mListID) + 10;
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

    public void removeProduct(Product product) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public DataSource.Factory<Integer, CategoryProductItem> getProducts(@ProductList.ProductSorting int sorting, boolean isGroupedView) {

        return mProductDao.findByListIDSortByNameWithCategory(mListID);

    }
}
