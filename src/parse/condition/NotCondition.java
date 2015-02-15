package parse.condition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimish on 15/02/15.
 *
 * Return Universe - List A. Where Universe is initial data.
 */
public class NotCondition<T> implements Condition<T> {

    private final Condition<T> condition;

    public NotCondition(Condition<T> condition) {
        this.condition = condition;
    }

    @Override
    public List<T> filter(List<T> data) {
        List<T> to_filter = condition.filter(data);
        List<T> result = new ArrayList<T>();
        for (T t : data) {
            if (!to_filter.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("(NOT %s)", condition.toString());
    }
}
