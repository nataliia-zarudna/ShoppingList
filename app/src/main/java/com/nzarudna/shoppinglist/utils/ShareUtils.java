package com.nzarudna.shoppinglist.utils;

import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.list.ProductList;

import java.util.List;

public class ShareUtils {

    public static String formatProductList(/*ProductList list,*/ List<CategoryProductItem> items) {

        StringBuilder shareText = new StringBuilder();
//        shareText.append(list.getName());
//        shareText.append(".");

        for (CategoryProductItem item : items) {

            if (item.isCategory()) {
                shareText.append(item.getCategory().getName())
                        .append(":\n");
            } else {
                shareText
                        .append("* ")
                        .append(item.getProduct().getName())
                        .append("\n");
            }
        }

//        for (int i = 0; i < products.size(); i++) {
//
//            CategoryProductItem item = products.get(i);
//            if (item.isCategory()) {
//                shareText.append()
//            }
//            shareText.append(list.getName());
//            shareText.append(".");
//            shareText
//                    .append(i)
//                    .append(". ")
//                    .append(products.get(i).getName())
//                    .append("\n");
//        }
        return shareText.toString();
    }

}
