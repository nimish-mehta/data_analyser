package parse.condition;

import java.util.List;

/**
 * Created by Nimish on 15/02/15.
 */
public interface Condition<T> {
    public List<T> filter(List<T> data);
}
