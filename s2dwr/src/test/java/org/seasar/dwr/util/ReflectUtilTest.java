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

import org.seasar.extension.unit.S2TestCase;

public class ReflectUtilTest extends S2TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("ReflectUtilTest.dicon");
    }

    public void testGetConcreteMethod1() throws Exception {
        Method expected = EmployeeService.class.getMethod("getEmployeeName",
                new Class[] { String.class });
        EmployeeService emp = (EmployeeService) getComponent(EmployeeService.class);
        Method method = emp.getClass().getMethod("getEmployeeName",
                new Class[] { String.class });
        assertFalse(expected.equals(method));
        method = ReflectUtil.getConcreteMethod(method);
        assertEquals(expected, method);
    }

    public void testGetConcreteMethod2() throws Exception {
        Method expected = EmployeeService.class.getMethod("getEmployeeName",
                new Class[] { String.class });
        Method method = ReflectUtil.getConcreteMethod(expected);
        assertEquals(expected, method);
    }

}
