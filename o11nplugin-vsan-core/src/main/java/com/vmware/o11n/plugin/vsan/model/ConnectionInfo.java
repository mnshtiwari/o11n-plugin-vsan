/* ************************************************************************
 * Copyright 2020 VMware, Inc.  All rights reserved. -- VMware Confidential
 * ************************************************************************/

package com.vmware.o11n.plugin.vsan.model;

import com.vmware.o11n.sdk.modeldriven.Sid;

/**
 * Object for all the vSAN connection details.
 */
public class ConnectionInfo {

   /**
    * Host of the vCenter service
    */
   private final String host;
 
   /**
    * Port of the vCenter service
    */
   private final int port;
 
   /**
    * The SSL thumb print of the vCenter server
    */
   private final String sslThumbPrint;
    
   private final String username;
   private final String password;

   public ConnectionInfo(String host, int port, String sslThumbPrint,
         String username, String password) {
      this.host = host;
      this.port = port;
      this.sslThumbPrint = sslThumbPrint;
      this.username = username;
      this.password = password;
   }

   public String getName() {
      return String.format("%s:%d/user=%s", host, port, username);
   }

   public String getHost() {
      return host;
   }

   public int getPort() {
      return port;
   }
    
   public String getSslThumbPrint() {
      return sslThumbPrint;
   }

   public String getUsername() {
      return username;
   }

   public String getPassword() {
      return password;
   }
    
   public Sid getId() {
      return Sid.valueOf(getHost());
   }

   @Override
   public String toString() {
      return String.format("ConnectionInfo [user=%s, id=%s, uri=%s, port=%d]",
            username, getId().toString(), host, port);
   }

}
