package com.mec.service_discover.appClient;

import java.util.List;

import com.mec.mec_rmi.core.INode;
import com.mec.mec_rmi.core.INodeSelector;
import com.mec.mec_rmi.core.RmiClient;
import com.mec.service_discover.registryCenter.RmiServer.IServiceDiscover;
import com.mec.service_discover.registryCenter.core.ServiceDefinition;
import com.my.util.core.TickTick;

/**
 * 维护服务列表
 */
class NodeSelector implements INodeSelector {
	private static final int DEFAULT_FLASHTIME = 300000; //5分钟
	//定时检测
	private TickTick tickTick;
	//节点选择客户端
	private RmiClient client;
	//服务编号
	private String serviceId;
	//获取服务列表接口
	private IServiceDiscover isd;
	//服务器节点列表
	private List<INode> nodes;
	//每个服务列表对于一个版本号
	private int version;
	//刷新列表的时间
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
	 * 连接到注册中心，定时刷新列表，心跳检测
	 */
	void connectToServiceDiscover() {
		isd = client.getProxy(IServiceDiscover.class);
		tickTick = new TickTick() {
			@Override
			public void doSomething() {
				synchronized (NodeSelector.class) {
					System.out.println("开始定时检测服务器！");
					//获取服务器列表
					flashList(serviceId, version);
					//健康检测，通过建立客户端与服务器的RMI连接，判断响应时间戳
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
	 * 启动滴答滴答计时器定时维护服务列表
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
	 * 刷新服务器列表
	 * @param serviceId
	 * @param version
	 */
	private void flashList(String serviceId, int version) {
		//这里通过RMI远程调用方法，由注册中心执行getNodeLIst();方法，然后将结果返回到客户端
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
	 * 每次获取服务列表里的第一个服务器
	 */
	void rewind() {
		nodeNumber = 0;
	}
	
	void stop() {
		tickTick.stop();
	}
	
}
