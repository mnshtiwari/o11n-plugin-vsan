package com.vmware.o11n.plugin.vsan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.o11n.plugin.vsphere.connect.SdkSession;
import com.vmware.vim.vmomi.client.common.ProtocolBinding;
import com.vmware.vim.vmomi.client.common.Session;
import com.vmware.vim.vmomi.client.Client;
import com.vmware.vim.vsan.binding.vim.cluster.VsanVcClusterConfigSystem;


public class VsanClient  {
	
   private static final Logger logger = LoggerFactory.getLogger(VsanClient.class);
	
   private SdkSession sdkSession;
   private final Client client;

   public VsanClient(Client client, SdkSession sdkSession) {
	   logger.info("Creating vSAN client");
	   this.sdkSession = sdkSession;
	   this.client = client;
	   this.client.getBinding().setSession(sdkSession.getHttpSession());
   }
   
   public void setSdkConnection(SdkSession sdkSession) {
	   this.sdkSession = sdkSession;
   }
   
   public String toString(String name) {
       return sdkSession.getDisplayName();
   }
   
   public VsanVcClusterConfigSystem getVsanVcClusterConfigSystem() {
	   logger.info("Creating stub for cluster config system");
	   return this.client.createStub(
			   VsanVcClusterConfigSystem.class,
			   "VsanVcClusterConfigSystem-vsan-cluster-config-system");
   }
}

