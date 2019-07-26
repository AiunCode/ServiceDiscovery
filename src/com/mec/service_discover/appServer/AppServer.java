package com.mec.service_discover.appServer;

import java.io.IOException;

import com.mec.CS_Frame_Work.core.Client;
import com.mec.CS_Frame_Work.core.IClientAction;
import com.mec.action.ActionFactory;
import com.mec.mec_rmi.core.RmiResourceFactory;
import com.mec.mec_rmi.core.RmiServer;
import com.my.util.core.ArguementMarker;

/**
 * 服务器
 */
public class AppServer {
	/**
	 * 连接注册中心（C-S）的客户端
	 */
	private Client client;
	/**
	 * 让客户端连接的服务器
	 */
	private RmiServer server;
	
	public AppServer() {
		try {
			this.client = new Client();
			server = new RmiServer();
			ActionFactory.scanAction("com.mec.service_discover.appServer");
			//RMI服务器注册
			RmiResourceFactory.registery(ReportStatus.class, IReportStatus.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置客户端的action
	 * @param clientAction
	 */
	public void setServiceDiscoverClientAction(IClientAction clientAction) {
		client.setClientAction(clientAction);
	}

	/**
	 * 给客户端设置注册中心的port
	 * @param port
	 */
	public void setServerDiscoverPort(int port) {
		client.setServerPort(port);
	}

	/**
	 * 给客户端设置服务器的ip
	 * @param ip
	 */
	public void setServerDiscoverIp(String ip) {
		client.setServerIp(ip);
	}

	/**
	 * 设置心跳检测RMI服务器的port
	 * @param port
	 */
	private void setHeartCheckPort(int port) {
		server.setPort(port);
	}
	
	public void startup() {
		try {
			setServiceDiscoverClientAction(new ClientActionAdapter());
			//服务器连接注册中心
			client.connectTOServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向注册中心注册
	 * @param serverId
	 * @param servicePort
	 * @param serviceIp
	 */
	public void signIn(String serverId, int servicePort, String serviceIp) {
		try {
			System.out.println("开始注册!!!");
			String clientId = client.getConversation().getId();
			synchronized (AppServer.class) {
				if (clientId == null) {
					try {
						System.out.println("等待conversation返回结果！！进入阻塞队列!!");
						//连接成功后唤醒
						AppServer.class.wait();
						clientId = client.getConversation().getId();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			ArguementMarker am = new ArguementMarker();
			am.addParam("clientId", clientId);
			am.addParam("serviceId", serverId);
			am.addParam("port", servicePort);
			am.addParam("ip", serviceIp);
			//想注册中心发送一个注册请求，将对应的参数通过Gson转换为字符串
			client.sentRequest("signIn", "signIn", am.toString());
			setHeartCheckPort(servicePort);
			//发送完毕后RMI服务器启动
			server.startup();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退出注册中心
	 */
	public void signOut() {
		ArguementMarker am = new ArguementMarker();
		String clientId = client.getConversation().getId();
		am.addParam("clientId", clientId);
		client.sentRequest("signOut", "signOut", am.toString());
	}
	
	public void offline() {
		server.shutdown();
		client.shutdown();
	}
	
}
