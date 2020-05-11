package io.nolawnchairs.typo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Typo {

    static final int F_GENERIC = 1;
    static final int F_INTERFACE = 2;
    static final int F_ABSTRACT = 4;

    private final Class<?> testingClass;
    private final Type superClass;
    private final Type[] interfaces;

    public Typo(Class<?> clazz) {
        this.testingClass = clazz;
        this.superClass = clazz.getGenericSuperclass();
        this.interfaces = clazz.getGenericInterfaces();
    }

    public Typo(Object o) {
        this(o.getClass());
    }

    /**
     * Determines whether or not the testing class inherits from a super
     * class that employs generic types. Returns false if there is no super
     * class used.
     *
     * @return <code>true</code> if super class is generic
     */
    public boolean hasGenericSuperClass() {
        return superClass instanceof ParameterizedType;
    }

    /**
     * Gets the super class of the testing class as a BaseClass object
     *
     * @return new BaseClass object
     */
    public BaseClass getSuperClass() {
        Class c = testingClass.getSuperclass();
        if (c != null)
            return new BaseClass(c);
        return null;
    }

    /**
     * Gets a list of all implemented interfaces as {@link BaseClass}
     * regardless of their use of generics.
     *
     * @return list of {@link BaseClass}
     */
    public List<BaseClass> getAllInterfaces() {
        List<BaseClass> interfaces = new ArrayList<>();
        for (Class<?> c : testingClass.getInterfaces())
            interfaces.add(new BaseClass(c));
        return interfaces;
    }

    /**
     * Tests whether or not the given testing class implements any
     * interfaces that employ generics
     *
     * @return <code>true</code> if generic interfaces are present
     */
    public boolean hasGenericInterfaces() {
        for (Type t : interfaces)
            if (t instanceof ParameterizedType)
                return true;
        return false;
    }

    /**
     * Gets the {@link GenericClass} instance of the inherited
     * super class that uses generics
     *
     * @return GenericClass class instance or <code>null</code> if
     * no generic super class is extended
     */
    public GenericClass getGenericSuperClass() {
        if (hasGenericSuperClass()) {
            ParameterizedType t = (ParameterizedType) superClass;
            return new GenericClass(t);
        }
        return null;
    }

    /**
     * Gets an {@link ArrayList} of {@link GenericClass} objects
     * that the testing class implements. This list will not contain any
     * implemented interfaces that do not employ generics. To get a list of
     * all implemented interfaces, use {@link #getAllInterfaces()} instead.
     *
     * @return list of implemented generic interfaces, or an empty list if
     * none were found
     */
    public List<GenericClass> getGenericInterfaces() {
        ArrayList<GenericClass> interfaces = new ArrayList<>();
        for (Type t : this.interfaces)
            if (t instanceof ParameterizedType)
                interfaces.add(new GenericClass((ParameterizedType) t));
        return interfaces;
    }

    /**
     * Find a {@link BaseClass} instance from an Iterable set of BaseClass objects
     * and return the first instance of one that matches needle
     *
     * @param haystack Iterable collection of BaseClass objects
     * @param needle   Class type to find
     * @return Optional of BaseClass object
     */
    public static Optional<BaseClass> findClass(Iterable<? extends BaseClass> haystack, Class<?> needle) {
        for (BaseClass baseClass : haystack) {
            if (baseClass.isOfClass(needle))
                return Optional.of(baseClass);
        }
        return Optional.empty();
    }

    /**
     * Create a Class object from a {@link ParameterizedType}
     * using the raw type name. Will return <code>null</code> if
     * the class name is not found in the classpath.
     * @param type Parameterized type to convert to Class
     * @return Class object or <code>null</code>
     */
    public static Class<?> getClass(Type type) {
        try {
            return Class.forName(type.getTypeName());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Create a new instance of InstanceBuilder
     *
     * @param clazz target class type
     * @param <T>   target class type
     * @return new instance of InstanceBuilder
     */
    public static <T> InstanceBuilder<T> instanceBuilder(Class<T> clazz) {
        return new InstanceBuilder<T>(clazz);
    }


    /**
     * Calculate flags to determine class traits
     *
     * @param clazz Class to inspect
     * @return int bitmask
     */
    static int createFlags(Class<?> clazz) {
        int flag = 0;
        if (clazz.getTypeParameters().length > 0)
            flag |= F_GENERIC;
        if (clazz.isInterface())
            flag |= F_INTERFACE;
        if (Modifier.isAbstract(clazz.getModifiers()))
            flag |= F_ABSTRACT;
        return flag;
    }

    /**
     * Builder that creates an instance of a class
     *
     * @param <T> The resultant type
     */
    public static class InstanceBuilder<T> {

        private Class<T> clazz;
        private ArrayList<Class<?>> argumentTypes = new ArrayList<>();
        private ArrayList<Object> arguments = new ArrayList<>();

        InstanceBuilder(Class<T> clazz) {
            this.clazz = clazz;
        }

        /**
         * Add an argument to the constructor. Arguments must
         * @param type the type the argument value is
         * @param value the argument value
         * @return this builder
         */
        public <U> InstanceBuilder<T> addArgument(Class<U> type, U value) {
            this.argumentTypes.add(type);
            this.arguments.add(value);
            return this;
        }

        /**
         * Build the new instance of the target class. This will return <code>null</code>
         * if any exceptions were thrown and caught.
         *
         * @return newly created instance of the target class
         */
        @SuppressWarnings("unchecked")
        public Optional<T> build() {
            try {
                Class<?>[] types = new Class[argumentTypes.size()];
                Object[] args = new Object[arguments.size()];
                Constructor<?> constructor = clazz.getConstructor(argumentTypes.toArray(types));
                T instance = (T) constructor.newInstance(arguments.toArray(args));
                return Optional.of(instance);
            } catch (NoSuchMethodException
                    | IllegalAccessException
                    | InstantiationException
                    | InvocationTargetException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
    }
}
