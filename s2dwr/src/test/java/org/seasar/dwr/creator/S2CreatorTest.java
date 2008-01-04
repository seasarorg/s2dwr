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
        creator.setComponent("employee");
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
        creator.setComponent("employee");
        Class clazz = creator.getType();
        assertEquals(EmployeeServiceImpl.class, clazz);
    }
}
