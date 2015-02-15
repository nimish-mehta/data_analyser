import command.CommandManager;
import data.ProductData;
import model.Collection;
import model.Product;
import view.Shell;

/**
 * Created by Nimish on 15/02/15.
 */
public class ShellRunner {
    public static void main(String[] args) {
        Collection<Product> productCollection = new Collection<Product>();
        CommandManager commandManager = new CommandManager(productCollection);
        for (Product product : ProductData.getProductData()) {
            productCollection.addData(product);
        }
        Shell shell = new Shell(commandManager);
        while (true) {
            shell.mainLoop();
        }
    }
}
