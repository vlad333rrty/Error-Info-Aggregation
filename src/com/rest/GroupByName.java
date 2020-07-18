package com.rest;
import com.data.IError;
import com.data.IGroup;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class GroupByName implements IGroup {
    private final Map<IError,Integer> entry=new ConcurrentHashMap<>();
    private final String groupingFieldValue;

    GroupByName(String groupingFieldValue){
        this.groupingFieldValue=groupingFieldValue;
    }

    @Override
    public void add(IError error) {
        if (!fits(error)) throw new IllegalArgumentException("Can not add error " +error+ " to this group");
        for (IError e:this){
            if (e.isDuplicate(error)){
                entry.put(e,entry.get(e)+1);
                return;
            }
        }
        entry.put(error,1);
    }

    @Override
    public void add(IError error,int n){
        if (!fits(error)) throw new IllegalArgumentException("Can not add error:\n " +error+ "\n to this group:\n"+this);
        for (IError e:this){
            if (e.isDuplicate(error)){
                entry.put(e,entry.get(e)+n);
                return;
            }
        }
        entry.put(error,n);
    }

    /**
     * Merges two groups
     * @param group group to be merged with the given one
     * @return a result of merging
     */
    @Override
    public IGroup merge(IGroup group) {
        if (!group.getGroupingFieldValue().equals(groupingFieldValue)) {
            throw new IllegalArgumentException("Can not merge groups with different grouping fields");
        }
        IGroup res=new GroupByName(groupingFieldValue);
        for (IError e:group) res.add(e,group.getDuplicatesNumber(e));
        for (IError e:this) res.add(e,getDuplicatesNumber(e));
        return res;
    }

    @Override
    public int getDuplicatesNumber(IError error)  {
        return entry.getOrDefault(error, 0);
    }

    @Override
    public String getGroupingFieldValue() {
        return groupingFieldValue;
    }

    @Override
    public boolean fits(IError error) {
        return error.getFieldValue("name").trim().equals(groupingFieldValue);
    }

    @Override
    public int size() {
        return entry.size();
    }

    @Override
    public Iterator<IError> iterator() {
        return entry.keySet().iterator();
    }

    @Override
    public String toString() {
        return "GroupByName{" +
                "entry=" + entry +
                ", \ngroupingFieldValue='" + groupingFieldValue + '\'' +
                '}';
    }
}