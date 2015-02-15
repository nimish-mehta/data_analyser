package command;

import exception.InvalidCommandException;
import exception.InvalidSortKeyException;
import model.Collection;
import model.Product;
import parse.Query;

import java.util.List;

/**
 * Created by Nimish on 15/02/15.
 *
 *
 * Controller Class
 */
public class CommandManager {

    private final Collection<Product> collection;

    public CommandManager(Collection<Product> collection) {
        this.collection = collection;
    }

    private boolean isQuery(String user_input) {
        return this.getCommand(user_input) == Commands.QUERY;
    }

    // Based on input determines type of command
    private Commands getCommand(String command) {
        if (command.equals("exit")) {
            return Commands.EXIT;
        } else if (command.equals("clear")) {
            return Commands.CLEAR;
        } else if (command.equals("_data")) {
            return Commands.DATA;
        } else if (command.startsWith("sort")) {
            return Commands.SORT;
        } else {
            return Commands.QUERY;
        }
    }

    private String runQuery(String query) throws Exception {
        try {
            this.collection.filter(query);
            return this.makeDataTable();
        } catch (Exception e) {
            throw e;
        }
    }

    private String runCommand(String command) throws Exception {
        try {
            switch (this.getCommand(command)) {
                case EXIT:
                    this.exit();
                    return "quitting";
                case CLEAR:
                    return this.clearCollection();
                case DATA:
                    return this.makeDataTable();
                case SORT:
                    int colonIndex = command.indexOf(":"); // Anything after colon is considered a key.
                    String keyName = command.substring(colonIndex+1, command.length());
                    return this.setSortKeyByName(keyName);
                default:
                    throw new InvalidCommandException();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public String executeCommand(String userInput) throws Exception {
        if (this.isQuery(userInput))  {
            return this.runQuery(userInput);
        } else {
            return this.runCommand(userInput);
        }
    }

    private void exit() {
        System.exit(0);
    }

    private String clearCollection() {
        this.collection.clear();
        return "Collection Cleared";
    }

    private String makeDataTable() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.collection.getSortedData().size() == 0) {
            return "No Data Found\n";
        }
        for (Product product : this.collection.getSortedData()) {
            stringBuilder.append(product.toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private String setSortKeyByName(String key) throws InvalidSortKeyException{
        if (this.getKeys().contains(key)) {
            this.collection.setSortKey(key);
            return String.format("Sort key reset to %s !", key);
        } else {
            throw new InvalidSortKeyException();
        }
    }

    public void setSortKeyByIndex(int keyIndex) throws InvalidSortKeyException {
        if (keyIndex < this.getKeys().size()) {
            this.setSortKeyByName(this.getKeys().get(keyIndex));
        }
    }

    public List<String> getKeys() {
        return this.collection.getKeys();
    }

}
