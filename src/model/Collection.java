package model;

import exception.InvalidQueryKeyException;
import exception.SyntaxErrorException;
import parse.Query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Nimish on 15/02/15.
 */
public class Collection<T> {
    // Store a comparator based on key name instead of key itself
    private ByFieldNameComparator<T> comparator;
    // track whether data is in original state.
    private boolean isDirty;
    // row keys on data.
    private final List<String> keys;
    // data stored for future reference. Do not modify this.
    private final List<T> data;
    private final List<T> result;

    public Collection() {
        keys = new ArrayList<String>();
        data = new ArrayList<T>();
        result = new ArrayList<T>();
    }

    public void setSortKey(String sortKey) {
        comparator = new ByFieldNameComparator<T>(sortKey);
    }

    public void addData(T t) {
        this.data.add(t);
    }

    public String getSortKey() {
        return comparator.getKey();
    }

    public List<String> getKeys() {
        // Find all fields of object by reflection.
        if (keys.size() == 0) {
            if (data.size() != 0) {
                for (Field f : data.get(0).getClass().getDeclaredFields()) {
                    keys.add(f.getName());
                }
            }
        }
        return keys;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public List<T> getData() {
        if (this.isDirty()) {
            return this.result;
        }
        return this.data;
    }

    public List<T> getSortedData() {
        List<T> data_temp = new ArrayList<T>(this.getData().size());
        for (T d: this.getData()) {
            data_temp.add(d);
        }
        Collections.sort(data_temp, comparator);
        return data_temp;
    }

    public void filter(String query) throws InvalidQueryKeyException, SyntaxErrorException {
        // Builds a query from user input to parse the data.
        Query<T> filter_query = new Query.QueryBuilder<T>(query).build();
        // Only allow if keys on query are present in the model
        if (this.isValidQuery(filter_query.getKeys())) {
            List<T> temp = filter_query.filter(this.getData());
            result.clear();
            result.addAll(temp);
            this.setDirty(true);
        } else {
            throw new InvalidQueryKeyException();
        }
    }

    private boolean isValidQuery(List<String> keys) {
        for (String key : keys) {
            if (!this.getKeys().contains(key)) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        this.result.clear();
        setDirty(false);
    }
}
