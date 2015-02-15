package parse.condition;

import java.util.List;

/**
 * Created by Nimish on 15/02/15.
 *
 * Returns (List A and List B)
 */
public class AndCondition<T> implements Condition<T> {
    private final Condition<T> condition1;
    private final Condition<T> condition2;

    public AndCondition(Condition<T> condition1, Condition<T> condition2) {
        this.condition1 = condition1;
        this.condition2 = condition2;
    }

    @Override
    public List<T> filter(List<T> data) {
        List<T> result;
        result = condition1.filter(data);
        // A and 0 = 0.
        if (result.size() != 0) {
            result = condition2.filter(result);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("(%s AND %s)", condition1.toString(), condition2.toString());
    }
}
