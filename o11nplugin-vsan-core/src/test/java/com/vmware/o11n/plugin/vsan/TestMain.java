/* ************************************************************************
 * Copyright 2020 VMware, Inc.  All rights reserved. -- VMware Confidential
 * ************************************************************************/

package com.vmware.o11n.plugin.vsan;

import java.util.Arrays;

import com.vmware.o11n.plugin.vsan.model.Connection;
import com.vmware.o11n.plugin.vsan.model.ConnectionInfo;
import com.vmware.vim.binding.vmodl.ManagedObjectReference;
import com.vmware.vim.vsan.binding.vim.cluster.VsanVcClusterConfigSystem;
import com.vmware.vim.vsan.binding.vim.cluster.VsanCapability;
import com.vmware.vim.vsan.binding.vim.cluster.VsanCapabilitySystem;
import com.vmware.vim.vsan.binding.vim.vsan.ConfigInfoEx;

public class TestMain {

   public static void main(String[] args) throws Exception {
      System.out.println("Test Main class");
      
      String vcHost = "sc1-10-182-13-192.eng.vmware.com";
      int port = 443;
      String sslThumbprint = "CE:52:EC:83:18:BB:A8:AE:6B:E4:D5:48:1E:94:E7:C4:F3:CA:9E:67";
      String username = "administrator@vsphere.local";
      String password = "Admin!23";
      ConnectionInfo connectionInfo = new ConnectionInfo(
            vcHost, port, sslThumbprint, username, password);
      Connection vsanConnection = new Connection(connectionInfo);
      
      VsanCapabilitySystem capabilitySystem = vsanConnection.getVsanCapabilitySystem();
      VsanCapability[] capabilities = capabilitySystem.getCapabilities(null);
      if (capabilities != null && capabilities.length > 0) {
         System.out.println("Capabilities:");
         System.out.println(Arrays.toString(capabilities[0].getCapabilities()));
      }
      
      VsanVcClusterConfigSystem configSystem = vsanConnection.getVsanVcClusterConfigSystem();
      ConfigInfoEx configInfo = configSystem.getConfigInfoEx(
            new ManagedObjectReference("ClusterComputeResource", "domain-c8"));
      System.out.println();
      System.out.println("Dedup Enabled:");
      System.out.println(configInfo.getDataEfficiencyConfig().isDedupEnabled());
   }

}
