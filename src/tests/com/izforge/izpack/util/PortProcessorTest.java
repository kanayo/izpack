package com.izforge.izpack.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.izforge.izpack.panels.ProcessingClient;


public class PortProcessorTest extends TestCase
{
    public void testProcessGenericBoundPort() throws IOException {
        // create a ServerSocket on any free port (for all available network interfaces)
        ServerSocket use = new ServerSocket(0); // create a serversocket on any free port
        int usedPort = use.getLocalPort();
        ProcessingClient pc = new ProcessingClientStub(usedPort);
        PortProcessor pp = new PortProcessor();
        String result = pp.process(pc);
        Assert.assertTrue((Integer.toString(usedPort)).equals(result));
        try {
            use.close();
        } catch (Throwable t) {
            //ignore cleanup errors
        }
    }

    public void testProcessSpecificBoundPort() throws IOException {
        // create a ServerSocket for localhost on any free port
        ServerSocket use = new ServerSocket(0,0,InetAddress.getByName("localhost"));
        int usedPort = use.getLocalPort();
        ProcessingClient pc = new ProcessingClientStub("localhost", usedPort);
        Assert.assertEquals(2, pc.getNumFields());
        Assert.assertEquals("localhost", pc.getFieldContents(0));
        Assert.assertEquals(Integer.toString(usedPort), pc.getFieldContents(1));
        PortProcessor pp = new PortProcessor();
        String result = pp.process(pc);
        Assert.assertFalse((Integer.toString(usedPort)).equals(result));
        try {
            use.close();
        } catch (Throwable t) {
            //ignore cleanup errors
        }
    }
    
    class ProcessingClientStub implements ProcessingClient {

        String[] fields;
        
        public ProcessingClientStub(String host, int port) {
            fields = new String[2];
            fields[0] = host;
            fields[1] = Integer.toString(port);
        }
        
        public ProcessingClientStub(int port) {
            fields = new String[1];
            fields[0] = Integer.toString(port);
        }
        
        public String getFieldContents(int index)
        {
            if (index < fields.length) {
                return fields[index];
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public int getNumFields()
        {
            return fields.length;
        }

        public String getText()
        {
            return null;
        }

        public Map<String, String> getValidatorParams()
        {
            return null;
        }

        public boolean hasParams()
        {
            return false;
        }
        
    }
}