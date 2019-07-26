package com.mec.service_discover.appServer;

import com.mec.cs_annocation.ActionMethod;
import com.mec.cs_annocation.ActionPara;
import com.mec.cs_annocation.HasActionMethod;

@HasActionMethod
public class ClientMethod {
	
	@ActionMethod(action="signIn")
	public void signInSuccess(@ActionPara(para="result")boolean result) {
		if (result) {
			System.out.println("�������ɹ�ע�ᵽע�����ģ�����");
			return;
		}
		System.out.println("������ע��ʧ�ܣ�����");
	}
	
	@ActionMethod(action="signOut")
	public void signOutSuccess(@ActionPara(para="result")boolean result) {
		if (result) {
			System.out.println("������ȡ��ע��ɹ�������");
			return;
		}
		System.out.println("������ȡ��ע��ʧ�ܣ�����");
	}
}
