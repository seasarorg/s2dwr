package org.seasar.dwr.creator;

import org.directwebremoting.extend.Creator;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.S2Container;

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
        // 型取得時は、Request、Responseが取得できない
        S2Container container2 = getContainer();
        container2.setRequest(null);
        container2.setResponse(null);
        setRequest(null);
        setResponse(null);
        creator.setComponent("Employee");
        Class clazz = creator.getType();
        assertEquals(EmployeeServiceImpl.class, clazz);
    }
}
