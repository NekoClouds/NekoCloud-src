package me.nekocloud.base.util;

import java.util.ArrayList;
import java.util.Collection;

public class DList<T> extends ArrayList<T> {

    private int index;

    public DList(Collection<T> collection){
        addAll(collection);
        index = 0;
    }

    public T getNext(){
        if (index + 1 == size()){
            index = 0;
        } else {
            index++;
        }
        return get(index);
    }

    public T get(){
        return get(index);
    }

    public boolean isLast(){
        return index + 1 == size();
    }

}
