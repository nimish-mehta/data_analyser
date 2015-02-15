package exception;

/**
 * Created by Nimish on 15/02/15.
 */
public class InvalidCommandException extends Exception {
    public InvalidCommandException() {
        super("Invalid Command");
    }
}
