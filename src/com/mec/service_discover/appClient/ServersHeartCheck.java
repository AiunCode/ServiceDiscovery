package com.mec.service_discover.appClient;

import java.util.*;

import com.mec.mec_rmi.core.INode;
import com.mec.mec_rmi.core.RmiClient;
import com.mec.service_discover.appServer.IReportStatus;

/**
 * 心跳检测（通过客户端与服务器连接判断响应时间）
 */
class ServersHeartCheck {
	private List<INode> nodes;
	private Map<Long, INode> nodeMap;
	private RmiClient client;
	
	ServersHeartCheck() {
		client = new RmiClient();
		nodeMap = new HashMap<>();
	}

	void setNodeList(List<INode> nodes) {
		this.nodes = nodes;
	}
	
	void healthCheck() {
		for (INode node : nodes) {
			client.setPort(node.getPort());
			client.setServerIp(node.getIp());
			try {
				IReportStatus irs = client.getProxy(IReportStatus.class);
				long connectTime = getNetQuality(irs);
				//以相差时间为键，服务器节点为值
				nodeMap.put(connectTime, node);
			} catch(Exception e) {
				continue;
			}
		}
		Set<Long> times = nodeMap.keySet();
		sort(times);
		nodes.clear();
		for (Long time : times) {
			//给响应时间排序，讲最优的服务器放在列表里
			nodes.add(nodeMap.get(time));
		}
		nodeMap.clear();
	}

	/**
	 * 通过响应时间来检测服务器情况
	 * @param irs
	 * @return
	 */
	private long getNetQuality(IReportStatus irs) {
		long startTime = System.currentTimeMillis();
		//通过建立客户端与服务器的RMI远程连接，判断服务器响应时间差
		irs.getConnectQuality();
		long endTime = System.currentTimeMillis();
		
		return endTime - startTime;
	}
	
	private void sort(Set<Long> set) {
		Set<Long> sortSet = new TreeSet<>(new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				return o1.compareTo(o2);
			}
		});
		sortSet.addAll(set);
	}
}
