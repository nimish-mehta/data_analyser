import command.CommandManager;
import data.ProductData;
import model.Collection;
import model.Product;
import parse.Query;

import java.util.List;

/**
 * Created by Nimish on 15/02/15.
 */
public class Test {
    public static void main(String[] args) {
        String query = "brand = 'BRAND A' or (not (color = 'Yellow' and availability='y'))";
        Collection<Product> productCollection = new Collection<Product>();
        CommandManager commandManager = new CommandManager(productCollection);
        for (Product product : ProductData.getProductData()) {
            productCollection.addData(product);
        }
        try {
            commandManager.setSortKeyByIndex(1);
            System.out.println(commandManager.executeCommand(query));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
