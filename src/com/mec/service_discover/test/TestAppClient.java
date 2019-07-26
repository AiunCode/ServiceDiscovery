package com.mec.service_discover.test;

import com.mec.mec_rmi.core.INode;
import com.mec.service_discover.appClient.AppClient;

/**
 * 客户端测试类
 */
public class TestAppClient {
	
	public static void main(String[] args) {
		AppClient client = new AppClient();
		//连接到注册中心
		client.connectToServiceDiscoverPort(54199, "127.0.0.1");
		//根据服务编号返回一个服务器
		INode node = client.requestService("001");
		//如果第一次连接失败，再调用此方法继续连接
		client.connectToServiceDiscoverPort(node.getPort(), node.getIp());
	}
	
}
