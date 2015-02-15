package model;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Created by Nimish on 15/02/15.
 *
 * Reflection based comparator to compare two objects with field name
 */
public class ByFieldNameComparator<T> implements Comparator<T>{

    private final String key;

    public ByFieldNameComparator(String key) {
        this.key = key;
    }

    @Override
    public int compare(T o1, T o2) {
        try {
            Field toCompare = o1.getClass().getDeclaredField(this.key);
            toCompare.setAccessible(true);
            Object v1 = toCompare.get(o1);
            Object v2 = toCompare.get(o2);

            if (v1 instanceof Comparable<?> && v2 instanceof Comparable<?>) {
                Comparable c1 = (Comparable) v1;
                Comparable c2 = (Comparable) v2;
                return c1.compareTo(c2);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getKey() {
        return key;
    }
}
