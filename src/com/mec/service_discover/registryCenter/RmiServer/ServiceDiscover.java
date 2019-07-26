package com.mec.service_discover.registryCenter.RmiServer;

import com.mec.service_discover.registryCenter.core.RegistryCenter;
import com.mec.service_discover.registryCenter.core.ServiceDefinition;

/**
 * 远程调用的实现类
 */
public class ServiceDiscover implements IServiceDiscover {

	@Override
	public ServiceDefinition getNodeList(String serviceId) {
		return RegistryCenter.getNodeList(serviceId);
	}

	@Override
	public ServiceDefinition getNodeLIst(String serviceId, int version) {
		return RegistryCenter.getNodeList(serviceId, version);
	}
		
}
