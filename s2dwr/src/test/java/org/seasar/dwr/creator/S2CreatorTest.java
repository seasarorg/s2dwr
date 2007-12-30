package org.seasar.dwr.creator;

import org.directwebremoting.extend.Creator;
import org.seasar.extension.unit.S2TestCase;

public class S2CreatorTest extends S2TestCase {

    S2Creator creator = new S2Creator();

    protected void setUp() throws Exception {
        super.setUp();
        include("S2CreatorTest.dicon");
    }

    public void testGetInstance1() throws Exception {
        creator.setComponent("Employee");
        Object obj = creator.getInstance();
        assertNotNull(obj);
        assertTrue(obj instanceof EmployeeServiceImpl);
    }

    public void testGetInstance2() throws Exception {
        creator.setClass("org.seasar.dwr.creator.EmployeeService");
        Object obj = creator.getInstance();
        assertNotNull(obj);
        assertTrue(obj instanceof EmployeeServiceImpl);
    }

    public void testGetScope() {
        String scope = creator.getScope();
        assertEquals(Creator.PAGE, scope);
    }

    public void testGetType() {
        creator.setComponent("Employee");
        Class clazz = creator.getType();
        assertEquals(EmployeeServiceImpl.class, clazz);
    }
}