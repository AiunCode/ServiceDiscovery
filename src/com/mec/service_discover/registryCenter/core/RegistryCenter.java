package com.mec.service_discover.registryCenter.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mec.mec_rmi.core.INode;
import com.mec.mec_rmi.core.Node;

/**
 * 注册中心，主要负责服务器的上下线
 */
public class RegistryCenter {
    //以服务编号为键，服务器信息为值
    private static Map<String, ServiceDefinition> serviceMap;
    //clientId为键，服务器节点为值
    private static Map<String, INode> appServerMap;
    
    static {
        serviceMap = new ConcurrentHashMap<>();
        appServerMap = new ConcurrentHashMap<>();
    }

    public RegistryCenter() { }

    public static ServiceDefinition getNodeList(String serviceId) {
        ServiceDefinition definition = serviceMap.get(serviceId);
        return definition;
    }

    public static ServiceDefinition getNodeList(String serviceId, int version) {
        ServiceDefinition definition = serviceMap.get(serviceId);
        //客户端每次请求服务器列表判断版本号是否相同，相同返回NULL
        if (definition.getVersion() == version) {
        	return null;
        }
        return definition;
    }

    /**
     * 往注册中心注册服务器，注册成功返回true
     * @param clientId
     * @param serviceId
     * @param port
     * @param ip
     * @return
     */
    public static boolean serverSignIn(String clientId, String serviceId, int port, String ip) {
    	INode node = new Node(port, ip, serviceId);
    	boolean result = addServer(clientId, node);
    	if (!result) {
    		return false;
    	}
    	System.out.println("开始正式注册服务器！！！");
    	//每次将相同服务功能的服务器加载同一个服务列表里
        if (serviceMap.containsKey(serviceId)) {
            synchronized (RegistryCenter.class) {
                if (serviceMap.containsKey(serviceId)) {
                    ServiceDefinition definition = serviceMap.get(serviceId);
                    return definition.addServerNode(node);
                }
            }
        }
        System.out.println("服务器注册成功！！！");
        ServiceDefinition definiton = new ServiceDefinition();
        definiton.addServerNode(node);
        serviceMap.put(serviceId, definiton);
        return true;
    }

    /**
     * 添加服务器，添加成功返回true
     * @param clientId
     * @param node
     * @return
     */
    private static boolean addServer(String clientId, INode node) {
    	if (appServerMap.containsKey(clientId)) {
    		return false;
    	}
    	System.out.println("注册的客户端: " + clientId);
    	appServerMap.put(clientId, node);
    	return true;
    }

    public static boolean serverSignOut(String clientId) {
    	INode node = appServerMap.remove(clientId);
    	if (node != null) {
    		System.out.println("服务器下线！！！");
    		offLine(node);
    		return true;
    	}
    	return false;
    }

    /**
     * 服务器下线
     * @param node
     */
    private static void offLine(INode node) {
    	String serviceId = node.getServiceId();
        if (serviceMap.containsKey(serviceId)) {
            synchronized (RegistryCenter.class) {
                if (serviceMap.containsKey(serviceId)) {
                    ServiceDefinition definition = serviceMap.get(serviceId);
                    definition.removeServerNode(node);
                }
            }
        }
    }
}
