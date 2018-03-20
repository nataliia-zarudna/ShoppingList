package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.utils.DateUtils;

import org.mockito.ArgumentMatcher;

import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by nsirobaba on 2/2/18.
 */

public class AssertUtils {

    public static ArgumentMatcher<Product> getArgumentMatcher(final Product expected) {

        return new ArgumentMatcher<Product>() {
            @Override
            public boolean matches(Product argument) {
                return matchesExceptID(expected, argument);
            }
        };
    }

    public static boolean matchesExceptID(Product expected, Product argument) {

        if (Double.compare(argument.getCount(), expected.getCount()) != 0) return false;
        if (expected.getStatus() != argument.getStatus()) return false;
        if (Double.compare(argument.getOrder(), expected.getOrder()) != 0) return false;
        if (!expected.getName().equals(argument.getName())) return false;
        if (expected.getCategoryID() != null
                ? !expected.getCategoryID().equals(argument.getCategoryID()) : argument.getCategoryID() != null)
            return false;
        if (expected.getUnitID() != null
                ? !expected.getUnitID().equals(argument.getUnitID()) : argument.getUnitID() != null)
            return false;
        if (expected.getComment() != null
                ? !expected.getComment().equals(argument.getComment()) : argument.getComment() != null)
            return false;
        if (expected.getListID() != null
                ? !expected.getListID().equals(argument.getListID()) : argument.getListID() != null)
            return false;

        return expected.getTemplateID() != null
                ? expected.getTemplateID().equals(argument.getTemplateID()) : argument.getTemplateID() == null;
    }

    public static ArgumentMatcher<Product> getProductArgumentMatcheWithoutPKAndListID(final Product expected) {

        return new ArgumentMatcher<Product>() {
            @Override
            public boolean matches(Product argument) {

                if (Double.compare(argument.getCount(), expected.getCount()) != 0) return false;
                if (expected.getStatus() != argument.getStatus()) return false;
                if (Double.compare(argument.getOrder(), expected.getOrder()) != 0) return false;
                if (!expected.getName().equals(argument.getName())) return false;
                if (expected.getCategoryID() != null
                        ? !expected.getCategoryID().equals(argument.getCategoryID()) : argument.getCategoryID() != null)
                    return false;
                if (expected.getUnitID() != null
                        ? !expected.getUnitID().equals(argument.getUnitID()) : argument.getUnitID() != null)
                    return false;
                if (expected.getComment() != null
                        ? !expected.getComment().equals(argument.getComment()) : argument.getComment() != null)
                    return false;
                return expected.getTemplateID() != null
                        ? expected.getTemplateID().equals(argument.getTemplateID()) : argument.getTemplateID() == null;
            }
        };
    }

    public static ArgumentMatcher<ProductList> getArgumentMatcher(final ProductList expected) {

        return new ArgumentMatcher<ProductList>() {
            @Override
            public boolean matches(ProductList argument) {

                if (expected.getStatus() != argument.getStatus()) return false;
                if (expected.getSorting() != argument.getSorting()) return false;
                if (expected.isGroupedView() != argument.isGroupedView()) return false;
                if (!expected.getName().equals(argument.getName())) return false;
                if (!isDateEqualsToSeconds(expected.getCreatedAt(), argument.getCreatedAt()))
                    return false;
                if (!expected.getCreatedBy().equals(argument.getCreatedBy())) return false;
                if (expected.getModifiedAt() != null
                        ? !isDateEqualsToSeconds(expected.getModifiedAt(), argument.getModifiedAt()) : argument.getModifiedAt() != null)
                    return false;
                if (expected.getModifiedBy() != null
                        ? !expected.getModifiedBy().equals(argument.getModifiedBy()) : argument.getModifiedBy() != null)
                    return false;
                return expected.getAssignedID() != null ? expected.getAssignedID().equals(argument.getAssignedID()) : argument.getAssignedID() == null;
            }
        };
    }

    public static ArgumentMatcher<ProductTemplate> getArgumentMatcher(final ProductTemplate expected) {

        return new ArgumentMatcher<ProductTemplate>() {
            @Override
            public boolean matches(ProductTemplate argument) {
                return matchesExceptID(expected, argument);
            }
        };
    }

    public static boolean matchesExceptID(ProductTemplate expected, ProductTemplate argument) {
        if (!expected.getName().equals(argument.getName())) {
            return false;
        }
        if (!expected.getCategoryID().equals(argument.getCategoryID())) {
            return false;
        }
        if (expected.getTemplateID() != null
                ? expected.getTemplateID().equals(argument.getTemplateID()) : argument.getTemplateID() == null) {
            return false;
        }
        return true;
    }

    public static boolean isDateEqualsToSeconds(Date expected, Date actual) {
        return expected.compareTo(actual) < 60; // seconds
    }
}
