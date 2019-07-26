package com.mec.service_discover.registryCenter.core;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.mec.mec_rmi.core.INode;

/**
 * 服务器详细信息
 */
public class ServiceDefinition implements Serializable{

	private static final long serialVersionUID = 60469885897101780L;
	//服务器列表
	private List<INode> serverList;
	//服务器列表版本号，只要列表有所改变，版本号就改变
    private int version;

    ServiceDefinition(){
        version = 0;
        serverList = new LinkedList<>();
    }

    private void listModify() {
        version++;
    }

    public int getVersion() {
    	return version;
    }
    
    public boolean isVersionEqual(int version) {
    	return this.version == version;
    }
    
    boolean addServerNode(INode node) {
        if (serverList.contains(node)){
            synchronized (ServiceDefinition.class) {
                if (serverList.contains(node)){
                    return false;
                }
            }
        }
        serverList.add(node);
        listModify();
        
        return true;
    }

    void removeServerNode(INode node) {
        if (!serverList.contains(node)){
            synchronized (ServiceDefinition.class) {
                if (!serverList.contains(node)){
                    return;
                }
            }
        }
        serverList.remove(node);
        listModify();
    }

    public List<INode> getNodeList() {
        return serverList;
    }

    public List<INode> getNodeList(int version) {
        return this.version == version ? null : serverList;
    }

	@Override
	public String toString() {
		return "ServiceDefinition [serverList=" + serverList + ", version=" + version + "]";
	}
    
    
}
