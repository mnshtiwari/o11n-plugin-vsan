/* ************************************************************************
 * Copyright 2020 VMware, Inc.  All rights reserved. -- VMware Confidential
 * ************************************************************************/

package com.vmware.o11n.plugin.vsan.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vmware.o11n.plugin.vsan.connect.VsanConnectionUtil;
import com.vmware.o11n.sdk.modeldriven.Findable;
import com.vmware.o11n.sdk.modeldriven.Sid;
import com.vmware.vim.vmomi.client.Client;
//import com.vmware.vim.vsan.binding.vim.cluster.VsanVcClusterConfigSystem;
import com.vmware.vim.vsan.binding.vim.cluster.VsanCapabilitySystem;

@Component
@Qualifier(value = "connection")
@Scope(value = "prototype")
public class Connection implements Findable  {
   
   private static final Logger logger = LoggerFactory.getLogger(Connection.class);
   private volatile Client vmomiClient;
   private ConnectionInfo connectionInfo;

   public Connection(ConnectionInfo connectionInfo) {
      init(connectionInfo);
   }
   
   @Override
   public Sid getInternalId() {
       return getConnectionInfo().getId();
   }

   @Override
   public void setInternalId(Sid id) {
      // do nothing, we set the Id in
      // the constructor via connection info
   }
   
   public String getName() {
      return getConnectionInfo().getName();
   }

   public String getHost() {
      return getConnectionInfo().getHost();
   }

   public int getPort() {
      return getConnectionInfo().getPort();
   }
   
   public String getUsername() {
      return getConnectionInfo().getUsername();
   }

   public synchronized ConnectionInfo getConnectionInfo() {
      return connectionInfo;
   }
   
   /*
    * Updates this connection with the provided info. This operation will
    * destroy the existing third party client, causing all associated
    * operations to fail.
    */
   public synchronized void update(ConnectionInfo connectionInfo) {
       if (this.connectionInfo != null && !connectionInfo.getId().equals(this.connectionInfo.getId())) {
           throw new IllegalArgumentException("Cannot update using different id");
       }
       destroy();
       init(connectionInfo);
   }
   
   private void init(ConnectionInfo connectionInfo) {
      this.connectionInfo = connectionInfo;
   }

   public synchronized void destroy() {
      if (vmomiClient != null) {
         vmomiClient.shutdown();
      }
   }
   
   private synchronized Client getVsanVmomiClient() {
      if (vmomiClient == null) {
         vmomiClient = VsanConnectionUtil.createVsanClient(
               getHost(), String.valueOf(getPort()),
               getConnectionInfo().getSslThumbPrint(), getUsername(),
               getConnectionInfo().getPassword());
      }
      return vmomiClient;
   }

   public String toString() {
       return connectionInfo.toString();
   }
   
   /*
   public VsanVcClusterConfigSystem getVsanVcClusterConfigSystem() {
      logger.info("Creating stub for cluster config system");
      return getVsanVmomiClient().createStub(
            VsanVcClusterConfigSystem.class,
            "vsan-cluster-config-system");
   }*/
   
   public VsanCapabilitySystem getVsanCapabilitySystem() {
      logger.info("Creating stub for capability system");
      return getVsanVmomiClient().createStub(
            VsanCapabilitySystem.class,
            "vsan-vc-capability-system");
   }
}

