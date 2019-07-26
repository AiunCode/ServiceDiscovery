package com.mec.service_discover.registryCenter.csServer;

import com.mec.cs_annocation.ActionMethod;
import com.mec.cs_annocation.ActionPara;
import com.mec.cs_annocation.HasActionMethod;
import com.mec.service_discover.registryCenter.core.RegistryCenter;

/**
 * 注册中心对于的方法
 */
@HasActionMethod
public class ServiceMethod {
	
		@ActionMethod(action="signIn")
		public boolean signIn(@ActionPara(para="clientId")String clientId, @ActionPara(para="serviceId")String serviceId, @ActionPara(para="ip")String ip, @ActionPara(para="port")int port) {
			return RegistryCenter.serverSignIn(clientId, serviceId, port, ip);
		}
		
		@ActionMethod(action="signOut")
		public boolean signOut(@ActionPara(para="clientId")String clientId) {
			return RegistryCenter.serverSignOut(clientId);
		}
}
