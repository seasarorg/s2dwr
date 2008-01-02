package org.seasar.dwr.example.service;

import junit.framework.TestCase;

public class EchoServiceTest extends TestCase {

    EchoService echoService;

    protected void setUp() throws Exception {
        super.setUp();
        echoService = new EchoService();
    }

    public void testEcho() {
        String aqtual = echoService.echo("test string");
        assertEquals("test string", aqtual);
    }

}
