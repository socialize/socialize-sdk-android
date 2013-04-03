/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.net;

import org.apache.http.conn.ClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 */
public class IdleConnectionMonitorThread extends Thread {
	
    private final ClientConnectionManager connMgr;
    private volatile boolean shutdown;
    private final long timeout = 30000; // milliseconds
    
    public IdleConnectionMonitorThread(ClientConnectionManager connMgr) {
        super("IdleConnectionMonitorThread");
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(timeout);
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than [timeout] milliseconds
                    connMgr.closeIdleConnections(timeout, TimeUnit.MILLISECONDS);
                }
            }
        } 
        catch (InterruptedException ex) {
            // terminate
        }
    }
    
    public void trigger() {
        synchronized (this) {
            notifyAll();
        }
    }
    
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
