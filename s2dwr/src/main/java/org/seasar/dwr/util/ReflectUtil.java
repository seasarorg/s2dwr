/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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
