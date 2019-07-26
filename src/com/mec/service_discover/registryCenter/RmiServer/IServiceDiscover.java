package com.mec.service_discover.registryCenter.RmiServer;

import com.mec.service_discover.registryCenter.core.ServiceDefinition;

/**
 * 远程调用的接口
 */
public interface IServiceDiscover {
	ServiceDefinition getNodeList(String serviceId);
	ServiceDefinition getNodeLIst(String serviceId, int version);
}
