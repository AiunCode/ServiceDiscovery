package com.mec.service_discover.appServer;

import com.mec.CS_Frame_Work.core.IClientAction;

/**
 *
 * 客户端action适配器
 */
public class ClientActionAdapter implements IClientAction{
	
	@Override
	public void OutOfRoom() {
	}

	@Override
	public void connectTooFast() {
		
	}

	@Override
	public void connectSuccess() {
		synchronized (AppServer.class) {
			System.out.println("开始唤醒主线程!!!");
			AppServer.class.notify();
		}
	}

	@Override
	public void dealAbnormalDrop() {
		
	}

	
}
