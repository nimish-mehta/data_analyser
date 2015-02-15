package exception;

/**
 * Created by Nimish on 15/02/15.
 */
public class InvalidQueryKeyException extends Exception {
    public InvalidQueryKeyException() {
        super("Invalid Query Search Key");
    }
}
