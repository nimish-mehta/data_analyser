package view;

import command.CommandManager;

import java.util.List;
import java.util.Scanner;

/**
 * Created by Nimish on 15/02/15.
 */
public class Shell {
    private final CommandManager commandManager;
    private String message;
    private final Scanner scanner;


    public Shell(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.message = "";
        this.scanner = new Scanner(System.in);
        this.initialInteraction();
        this.message = "Enter your search criteria";

    }

    private boolean initialInteraction() {
        List<String> keys = this.commandManager.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            this.displayPrompt(String.format("%s %s",i+1, keys.get(i)));
        }
        this.message = "Choose key no to sort and display data";
        String ip = this.getInput();
        try {
            int idx = Integer.parseInt(ip);
            this.commandManager.setSortKeyByIndex(idx-1);
            this.displayData(this.commandManager.executeCommand("_data"));
        } catch (Exception e) {
            this.displayPrompt(e.getMessage());
            this.message = "";
            return this.initialInteraction();
        }
        return true;
    }

    private String getInput() {
        System.out.print(String.format("%s>", this.message));
        return scanner.nextLine();
    }

    private void displayPrompt(String format) {
        System.out.println(String.format("%s>%s", this.message, format));
    }

    private void displayData(String data) {
        System.out.println(data);
    }

    public void mainLoop() {
        String usrInput = this.getInput();
        try {
            String result = this.commandManager.executeCommand(usrInput);
            this.displayData(result);
        } catch (Exception e) {
            this.displayPrompt(e.getMessage());
        }
    }
}
