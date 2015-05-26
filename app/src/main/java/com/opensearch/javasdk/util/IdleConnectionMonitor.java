package com.opensearch.javasdk.util;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ClientConnectionManager;

/**
 * @author vongosling.fengj 2013-5-8
 */
public class IdleConnectionMonitor extends Thread {
	private final ClientConnectionManager connMgr;


	public IdleConnectionMonitor(ClientConnectionManager connMgr) {
		super();
		this.connMgr = connMgr;
	}

	@Override
	public void run() {
		try {
			// Close expired connections
			connMgr.closeExpiredConnections();
			// Optionally, close connections
			// that have been idle longer than 30 sec
			connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
