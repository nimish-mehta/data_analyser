package data;

import model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimish on 15/02/15.
 */
public class ProductData {
    public static List<Product> getProductData() {
        // Can be and should be replaced by a proper data source or json parser.
        List<Product> products = new ArrayList<Product>();
        products.add(new Product("1", "y", "BRAND A", "Yellow"));
        products.add(new Product("2", "n", "BRAND B", "Red"));
        products.add(new Product("3", "n", "BRAND D", "Green"));
        products.add(new Product("4", "y", "BRAND A", "Red"));
        products.add(new Product("5", "n", "BRAND B", "Blue"));
        products.add(new Product("6", "n", "BRAND C", "Green"));
        products.add(new Product("7", "y", "BRAND C", "Red"));
        products.add(new Product("8", "n", "BRAND D", "Blue"));
        products.add(new Product("9", "n", "BRAND A", "Yellow"));
        products.add(new Product("10", "y", "BRAND B", "Yellow"));
        products.add(new Product("11", "n", "BRAND D", "Green"));
        products.add(new Product("12", "n", "BRAND D", "Yellow"));
        products.add(new Product("13", "n", "BRAND A", "Blue"));
        products.add(new Product("14", "n", "BRAND D", "Blue"));
        products.add(new Product("15", "n", "BRAND B", "Green"));
        products.add(new Product("16", "y", "BRAND B", "Yellow"));
        products.add(new Product("17", "y", "BRAND A", "Green"));
        products.add(new Product("18", "y", "BRAND D", "Blue"));
        products.add(new Product("19", "n", "BRAND C", "Green"));
        products.add(new Product("20", "n", "BRAND A", "Yellow"));
        return products;
    }
}
