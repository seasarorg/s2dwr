package org.seasar.dwr.util;

import java.lang.reflect.Method;

import org.seasar.framework.exception.ClassNotFoundRuntimeException;
import org.seasar.framework.util.ClassUtil;

public class ReflectUtil {

    /**
     * オリジナルクラス側のメソッドを返します。
     * <p>
     * 指定されたメソッドがS2AOPによりエンハンスされたクラスのものであれば、 元のクラスのMethodを返します。
     * </p>
     * 
     * @param method
     *                エンハンス済みクラスのMethod
     * @return 元になったクラスのMethod
     */
    public static Method getConcreteMethod(Method method) {
        String className = method.getDeclaringClass().getName();
        int oldLength = className.length();
        className = className
                .replaceAll("\\$\\$EnhancedByS2AOP\\$\\$\\w+$", "");
        if (className.length() == oldLength) {
            return method;
        }
        try {
            Class clazz = ClassUtil.forName(className);
            return clazz
                    .getMethod(method.getName(), method.getParameterTypes());
        } catch (ClassNotFoundRuntimeException ignore) {
            /* ignore exception */
        } catch (SecurityException ignore) {
            /* ignore exception */
        } catch (NoSuchMethodException ignore) {
            /* ignore exception */
        }
        return method;
    }

}
