package com.mec.service_discover.appClient;

import com.mec.mec_rmi.core.INode;
import com.mec.mec_rmi.core.RmiClient;
import com.mec.service_discover.registryCenter.RmiServer.IServiceDiscover;
import com.mec.service_discover.registryCenter.core.ServiceDefinition;

/**
 * 客户端
 */
public class AppClient {
    private RmiClient sdClient;
    private NodeSelector nodeSelector;
    private IServiceDiscover isd;
    private String serviceId;

    public AppClient() {
        sdClient = new RmiClient();
        nodeSelector = new NodeSelector();
    }

    public void connectToServiceDiscoverPort(int port, String ip) {
        sdClient.setPort(port);
        sdClient.setServerIp(ip);
        nodeSelector.setPort(port);
        nodeSelector.setIp(ip);
        isd = sdClient.getProxy(IServiceDiscover.class);
        nodeSelector.connectToServiceDiscover();
    }

    /**
     * 请求一个服务器
     * @param serviceId
     * @return
     */
    public INode requestService(String serviceId) {
        if (serviceId.equals("")) {
            return null;
        }

        if (this.serviceId != null && this.serviceId.equals(serviceId)) {
        	//每次请求服务器，返回列表里排在第一的服务器
            nodeSelector.rewind();
            return nodeSelector.getNode();
        }
        //是一个新的服务编号
        this.serviceId = serviceId;
        //通过该服务编号，返回支持该服务的服务列表，然后由NodeSelector类维护表
        ServiceDefinition sd = isd.getNodeList(serviceId);
        //开始维护服务列表
        nodeSelector.requestAppServerList(serviceId, sd);
        //返回最优服务器
        return nodeSelector.getNode();
    }
    
    public INode getNode() {
        return nodeSelector.getNode();
    }

    public void shutdown() {
        nodeSelector.stop();
    }
}
