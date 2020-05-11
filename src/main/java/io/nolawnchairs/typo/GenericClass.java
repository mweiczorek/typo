package io.nolawnchairs.typo;

import java.lang.reflect.ParameterizedType;

public class GenericClass extends BaseClass {

    private final ParameterizedType baseType;

    GenericClass(ParameterizedType type) {
        super(type);
        this.baseType = type;
    }

    public TypeList getDeclaredTypes() {
        return new TypeList(baseType.getActualTypeArguments());
    }
}
