package io.nolawnchairs.typo;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TypeList extends ArrayList<BaseClass> {

    TypeList(Type[] parameterizedTypes) {
        for (Type t : parameterizedTypes)
            add(BaseClass.fromType(t));
    }

    public BaseClass getFirst() {
        return get(0);
    }

    public BaseClass getLast() {
        return get(size() - 1);
    }
}
