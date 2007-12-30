package org.seasar.dwr.util;

import java.lang.reflect.Method;

import org.seasar.extension.unit.S2TestCase;

public class ReflectUtilTest extends S2TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("ReflectUtilTest.dicon");
    }

    public void testGetConcreteMethod() throws Exception {
        Method expected = EmployeeService.class.getMethod("getEmployeeName",
                new Class[] { String.class });
        EmployeeService emp = (EmployeeService) getComponent(EmployeeService.class);
        Method method = emp.getClass().getMethod("getEmployeeName",
                new Class[] { String.class });
        assertFalse(expected.equals(method));
        method = ReflectUtil.getConcreteMethod(method);
        assertEquals(expected, method);
    }

}
