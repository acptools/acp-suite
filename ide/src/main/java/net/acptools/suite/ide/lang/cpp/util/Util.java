package net.acptools.suite.ide.lang.cpp.util;


import net.acptools.suite.ide.lang.cpp.core.Expression;
import net.acptools.suite.ide.lang.cpp.core.Function;
import net.acptools.suite.ide.lang.cpp.core.Type;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static <T> ArrayList<T> newList(T... elements) {

        ArrayList<T> list = new ArrayList<T>();
        for (int i = 0; i < elements.length; i++) {
            list.add(elements[i]);
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] addArray(T[] array, T object) {

        List<T> list = new ArrayList<T>();
        for (int i = 0; i < array.length; i++)
            list.add(array[i]);
        list.add(object);

        return (T[]) list.toArray();
    }

    public static Type[] convertToTypeArray(List<Expression> expressions) {
        Type[] types = new Type[expressions.size()];
        for (int i = 0; i < types.length; i++) {
            if (expressions.get(i) != null) {
                types[i] = expressions.get(i).getType();
            } else {
                types[i] = new Type("void");
            }
        }
        return types;
    }

    public static Expression asExpression(Object o) {
        if (o instanceof Expression) {
            return (Expression) o;
        } else if (o instanceof Function) {
            return new Expression(((Function) o).getReturnType());
        }
        return new Expression("null");
    }

}
