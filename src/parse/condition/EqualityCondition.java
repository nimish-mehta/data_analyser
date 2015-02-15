package parse.condition;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimish on 15/02/15.
 */
public class EqualityCondition<T> implements Condition<T> {

    // Compare Field provided in name as key
    private final String key;
    private final String value;

    public EqualityCondition(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public List<T> filter(List<T> data) {
        List<T> result = new ArrayList<T>();
        try {
            for (T t : data) {
                Field toCompare = t.getClass().getDeclaredField(this.key);
                // in case the field in private
                toCompare.setAccessible(true);
                String toCompareValue = (String) toCompare.get(t);
                if (toCompareValue.equals(value)) {
                    result.add(t);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("(%s == %s)", this.key, this.value);
    }
}
