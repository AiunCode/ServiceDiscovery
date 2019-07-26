package com.mec.service_discover.appServer;

import java.io.IOException;

import com.mec.CS_Frame_Work.core.Client;
import com.mec.CS_Frame_Work.core.IClientAction;
import com.mec.action.ActionFactory;
import com.mec.mec_rmi.core.RmiResourceFactory;
import com.mec.mec_rmi.core.RmiServer;
import com.my.util.core.ArguementMarker;

/**
 * ������
 */
public class AppServer {
	/**
	 * ����ע�����ģ�C-S���Ŀͻ���
	 */
	private Client client;
	/**
	 * �ÿͻ������ӵķ�����
	 */
	private RmiServer server;
	
	public AppServer() {
		try {
			this.client = new Client();
			server = new RmiServer();
			ActionFactory.scanAction("com.mec.service_discover.appServer");
			//RMI������ע��
			RmiResourceFactory.registery(ReportStatus.class, IReportStatus.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ÿͻ��˵�action
	 * @param clientAction
	 */
	public void setServiceDiscoverClientAction(IClientAction clientAction) {
		client.setClientAction(clientAction);
	}

	/**
	 * ���ͻ�������ע�����ĵ�port
	 * @param port
	 */
	public void setServerDiscoverPort(int port) {
		client.setServerPort(port);
	}

	/**
	 * ���ͻ������÷�������ip
	 * @param ip
	 */
	public void setServerDiscoverIp(String ip) {
		client.setServerIp(ip);
	}

	/**
	 * �����������RMI��������port
	 * @param port
	 */
	private void setHeartCheckPort(int port) {
		server.setPort(port);
	}
	
	public void startup() {
		try {
			setServiceDiscoverClientAction(new ClientActionAdapter());
			//����������ע������
			client.connectTOServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ע������ע��
	 * @param serverId
	 * @param servicePort
	 * @param serviceIp
	 */
	public void signIn(String serverId, int servicePort, String serviceIp) {
		try {
			System.out.println("��ʼע��!!!");
			String clientId = client.getConversation().getId();
			synchronized (AppServer.class) {
				if (clientId == null) {
					try {
						System.out.println("�ȴ�conversation���ؽ������������������!!");
						//���ӳɹ�����
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
			//��ע�����ķ���һ��ע�����󣬽���Ӧ�Ĳ���ͨ��Gsonת��Ϊ�ַ���
			client.sentRequest("signIn", "signIn", am.toString());
			setHeartCheckPort(servicePort);
			//������Ϻ�RMI����������
			server.startup();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �˳�ע������
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
