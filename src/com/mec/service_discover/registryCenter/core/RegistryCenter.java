package com.mec.service_discover.registryCenter.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mec.mec_rmi.core.INode;
import com.mec.mec_rmi.core.Node;

/**
 * ע�����ģ���Ҫ�����������������
 */
public class RegistryCenter {
    //�Է�����Ϊ������������ϢΪֵ
    private static Map<String, ServiceDefinition> serviceMap;
    //clientIdΪ�����������ڵ�Ϊֵ
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
        //�ͻ���ÿ������������б��жϰ汾���Ƿ���ͬ����ͬ����NULL
        if (definition.getVersion() == version) {
        	return null;
        }
        return definition;
    }

    /**
     * ��ע������ע���������ע��ɹ�����true
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
    	System.out.println("��ʼ��ʽע�������������");
    	//ÿ�ν���ͬ�����ܵķ���������ͬһ�������б���
        if (serviceMap.containsKey(serviceId)) {
            synchronized (RegistryCenter.class) {
                if (serviceMap.containsKey(serviceId)) {
                    ServiceDefinition definition = serviceMap.get(serviceId);
                    return definition.addServerNode(node);
                }
            }
        }
        System.out.println("������ע��ɹ�������");
        ServiceDefinition definiton = new ServiceDefinition();
        definiton.addServerNode(node);
        serviceMap.put(serviceId, definiton);
        return true;
    }

    /**
     * ��ӷ���������ӳɹ�����true
     * @param clientId
     * @param node
     * @return
     */
    private static boolean addServer(String clientId, INode node) {
    	if (appServerMap.containsKey(clientId)) {
    		return false;
    	}
    	System.out.println("ע��Ŀͻ���: " + clientId);
    	appServerMap.put(clientId, node);
    	return true;
    }

    public static boolean serverSignOut(String clientId) {
    	INode node = appServerMap.remove(clientId);
    	if (node != null) {
    		System.out.println("���������ߣ�����");
    		offLine(node);
    		return true;
    	}
    	return false;
    }

    /**
     * ����������
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
