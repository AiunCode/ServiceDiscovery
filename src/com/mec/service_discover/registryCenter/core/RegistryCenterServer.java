package com.mec.service_discover.registryCenter.core;

import com.mec.CS_Frame_Work.core.Server;
import com.mec.action.ActionFactory;
import com.mec.mec_rmi.core.RmiResourceFactory;
import com.mec.mec_rmi.core.RmiServer;
import com.mec.service_discover.registryCenter.RmiServer.IServiceDiscover;
import com.mec.service_discover.registryCenter.RmiServer.ServiceDiscover;
import com.mec.service_discover.registryCenter.csServer.ServerAction;

/**
 * 注册中心服务器，主要负责服务器的启动、宕机
 */
public class RegistryCenterServer {
    //提供服务器（作为客户端）连接的注册中心服务器
	private Server csServer;
	//提供客户端远程连接的RMI服务器
    private RmiServer rmiServer;

    public RegistryCenterServer() {
    	csServer = new Server();
        rmiServer = new RmiServer();
    }
    
    public void startup() {
        try {
        	ActionFactory.scanAction("com.mec.service_discover.registryCenter.csServer");
        	//远程连接注册
        	RmiResourceFactory.registery(ServiceDiscover.class, IServiceDiscover.class);
        	csServer.setServerAction(new ServerAction());
            csServer.startup();
            rmiServer.startup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void shutdown() {
    	csServer.shutdown();
    	rmiServer.shutdown();
    }
    
	public void initActionFactory(String packagePath) {
		ActionFactory.scanAction(packagePath);
	}

	/**
	 * 设置注册中心作为服务器的Port
	 * @param port
	 */
	public void setCSServerPort(int port) {
		csServer.setport(port);
	}

	/**
	 * 设置注册中心作为RMI服务器的Port
	 * @param port
	 */
	public void setRmiServerPort(int port) {
		rmiServer.setPort(port);
	}
	
	public void setPort(int csServerPort, int rmiServerPort) {
		setCSServerPort(csServerPort);
		setRmiServerPort(rmiServerPort);
	}

	/**
	 * 通过配置文件初始化
	 * @param cfgPath
	 * @throws Exception
	 */
	public void initServer(String cfgPath) throws Exception {
		csServer.initServer(cfgPath);
		rmiServer.initServer(cfgPath);
	}
	
}
