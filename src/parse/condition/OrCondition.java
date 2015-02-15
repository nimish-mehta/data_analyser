package parse.condition;

import java.util.List;

/**
 * Created by Nimish on 15/02/15.
 *
 * return List A or List B
 */
public class OrCondition<T> implements Condition<T> {

    private final Condition<T> condition1;
    private final Condition<T> condition2;

    public OrCondition(Condition<T> condition1, Condition<T> condition2) {
        this.condition1 = condition1;
        this.condition2 = condition2;
    }

    @Override
    public List<T> filter(List<T> data) {
        List<T> result = condition1.filter(data);
        List<T> result_temp = condition2.filter(data);
        if (result.size() == 0) {
            return result_temp;
        } else if (result_temp.size() == 0) {
            return result;
        } else {
            for (T t : result_temp) {
                if (!result.contains(t)) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("(%s OR %s)", condition1.toString(), condition2.toString());
    }
}
