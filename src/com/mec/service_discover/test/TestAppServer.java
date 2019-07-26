package com.mec.service_discover.test;

import com.mec.service_discover.appServer.AppServer;

/**
 * 服务器端测试类
 */
public class TestAppServer {
	
	public static void main(String[] args) {
		AppServer server = new AppServer();
		server.setServerDiscoverIp("127.0.0.1");
		//注册中心服务器port
		server.setServerDiscoverPort(54188);
		server.startup();
		server.signIn("001", 54190, "192.168.34.233");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		server.signIn("001", 54190, "192.168.34.23");
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		server.signOut();
	}
	
}
