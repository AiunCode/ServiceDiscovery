package com.mec.service_discover.registryCenter.csServer;

import com.mec.CS_Frame_Work.core.IServerAction;
import com.mec.service_discover.registryCenter.core.RegistryCenter;

/**
 * 服务器action
 */
public class ServerAction implements IServerAction {

    @Override
    public void dealpeerAbnormalDrop(String clientId) {
    	RegistryCenter.serverSignOut(clientId);
    }

	@Override
	public void dealOffline(String clientId) {
		RegistryCenter.serverSignOut(clientId);
	}

	@Override
	public void dealLogin(String clientId) {
	}
}
