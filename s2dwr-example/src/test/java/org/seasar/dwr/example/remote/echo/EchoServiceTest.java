package org.seasar.dwr.example.remote.echo;

import org.seasar.dwr.example.remote.echo.EchoService;

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
