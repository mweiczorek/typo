package io.nolawnchairs.typo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class BaseClass {

    private final Type type;
    private final int flags;

    /**
     * BaseClass constructor. Package-only access
     * @param type Class
     */
    BaseClass(Class type) {
        this.type = type;
        this.flags = Typo.createFlags(type);
    }

    /**
     * BaseClass constructor. Package-only access
     * @param type ParameterizedType
     */
    BaseClass(ParameterizedType type) {
        this.type = type;
        Class c = Typo.getClass(type);
        this.flags = c == null ? 0 : Typo.createFlags(c);
    }

    /**
     * Create a new instance of BaseClass from broader {@link Type} interface.
     * Made static to avoid ambiguity with narrower constructor arguments. There
     * is no need for implementations to create new instances of BaseClass
     * @param type Type of
     * @return new BaseClass object
     */
    static BaseClass fromType(Type type) {
        if (type instanceof Class )
            return new BaseClass((Class) type);
        if (type instanceof ParameterizedType)
            return new BaseClass((ParameterizedType) type);
        String m = "Not a Class or ParameterizedType type [%s]. Cannot use other Type implementations due to type erasure";
        throw new IllegalArgumentException(String.format(m, type.getTypeName()));
    }

    /**
     * Determine whether the base class type is generic
     * @return <code>true</code> if generic
     */
    public boolean isGeneric() {
        return (flags & Typo.F_GENERIC) == Typo.F_GENERIC;
    }

    /**
     * Determine whether the base class type is an interface
     * @return <code>true</code> if is interface
     */
    public boolean isInterface() {
        return (flags & Typo.F_INTERFACE) == Typo.F_INTERFACE;
    }

    /**
     * Determine whether the base class type is abstract in nature.
     * This will return <code>true</code> if the type is an interface
     * OR an abstract class
     * @return <code>true</code> if is abstract
     */
    public boolean isAbstract() {
        return (flags & Typo.F_ABSTRACT) == Typo.F_ABSTRACT;
    }

    /**
     * Determine whether this base class type is an abstract class.
     * This will return <code>false</code> if it's an interface or not
     * abstract in nature
     * @return <code>true</code> if is an abstract class
     */
    public boolean isAbstractClass() {
        return isAbstract() && !isInterface();
    }

    /**
     * Determines whether this base class type is of type defined
     * by the @param class
     * @param clazz class to test against
     * @return <code>true</code> if the type is of class specified
     */
    public boolean isOfClass(Class<?> clazz) {
        if (type instanceof Class) {
            Class c = (Class) type;
            return c.equals(clazz);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return p.getRawType().getTypeName().equals(clazz.getName());
        }
        return false;
    }

    /**
     * Cast the base class type to a class object. Will return <code>null</code>
     * if the class is not found, or is not of supertype {@link Class} or of
     * {@link ParameterizedType}. We can't convert other type interfaces due to
     * type erasure
     * @return {@link Class} object
     */
    public Class<?> toClass() {
        if (type instanceof Class)
            return (Class<?>) type;
        else if (type instanceof ParameterizedType)
            return Typo.getClass(type);
        return null;
    }

    @Override
    public String toString() {
        return type.getTypeName();
    }

}
