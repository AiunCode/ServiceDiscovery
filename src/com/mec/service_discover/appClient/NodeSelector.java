package com.mec.service_discover.appClient;

import java.util.List;

import com.mec.mec_rmi.core.INode;
import com.mec.mec_rmi.core.INodeSelector;
import com.mec.mec_rmi.core.RmiClient;
import com.mec.service_discover.registryCenter.RmiServer.IServiceDiscover;
import com.mec.service_discover.registryCenter.core.ServiceDefinition;
import com.my.util.core.TickTick;

/**
 * ά�������б�
 */
class NodeSelector implements INodeSelector {
	private static final int DEFAULT_FLASHTIME = 300000; //5����
	//��ʱ���
	private TickTick tickTick;
	//�ڵ�ѡ��ͻ���
	private RmiClient client;
	//������
	private String serviceId;
	//��ȡ�����б�ӿ�
	private IServiceDiscover isd;
	//�������ڵ��б�
	private List<INode> nodes;
	//ÿ�������б����һ���汾��
	private int version;
	//ˢ���б��ʱ��
	private int flashTime;
	private ServersHeartCheck heartCheck;
	private int nodeNumber;
	
	NodeSelector() {
		client = new RmiClient();
		heartCheck = new ServersHeartCheck();
		flashTime = DEFAULT_FLASHTIME;
		nodeNumber = 0;
	}

	void setPort(int port) {
		client.setPort(port);
	}

	/**
	 * ���ӵ�ע�����ģ���ʱˢ���б��������
	 */
	void connectToServiceDiscover() {
		isd = client.getProxy(IServiceDiscover.class);
		tickTick = new TickTick() {
			@Override
			public void doSomething() {
				synchronized (NodeSelector.class) {
					System.out.println("��ʼ��ʱ����������");
					//��ȡ�������б�
					flashList(serviceId, version);
					//������⣬ͨ�������ͻ������������RMI���ӣ��ж���Ӧʱ���
					heartCheck.healthCheck();
					nodeNumber = 0;
				}
			}
		}.setTiming(flashTime);
	}

	private void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * �����δ�δ��ʱ����ʱά�������б�
	 * @param serviceId
	 * @param sd
	 */
	void requestAppServerList(String serviceId, ServiceDefinition sd) {
		if (tickTick.isActive()) {
			tickTick.stop();
		}
		synchronized (NodeSelector.class) {
			nodes = sd.getNodeList();
			version = sd.getVersion();
			nodeNumber = 0;
			setServiceId(serviceId);
			heartCheck.setNodeList(nodes);
		}
		tickTick.startup();
	}

	/**
	 * ˢ�·������б�
	 * @param serviceId
	 * @param version
	 */
	private void flashList(String serviceId, int version) {
		//����ͨ��RMIԶ�̵��÷�������ע������ִ��getNodeLIst();������Ȼ�󽫽�����ص��ͻ���
		ServiceDefinition sd = isd.getNodeLIst(serviceId, version);
		if (sd == null) {
			return;
		}
		
		nodes = sd.getNodeList();
		this.version = sd.getVersion();
	}

	@Override
	public INode getNode() {
		return nodes.get(nodeNumber++);
	}

	@Override
	public int getDelayTime() {
		return 100;
	}

	@Override
	public void connectFaliure(INode node) {
		nodes.remove(node);
	}

	void setIp(String ip) {
		client.setServerIp(ip);
	}

	/**
	 * ÿ�λ�ȡ�����б���ĵ�һ��������
	 */
	void rewind() {
		nodeNumber = 0;
	}
	
	void stop() {
		tickTick.stop();
	}
	
}
