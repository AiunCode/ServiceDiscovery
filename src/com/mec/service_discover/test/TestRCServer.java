package com.mec.service_discover.test;

import com.mec.service_discover.registryCenter.core.RegistryCenterServer;

/**
 * 注册中心测试类
 */
public class TestRCServer {
	
	public static void main(String[] args) {
		RegistryCenterServer rcs = new RegistryCenterServer();
		rcs.setPort(54188, 54199);
		rcs.startup();
	}
	
}
