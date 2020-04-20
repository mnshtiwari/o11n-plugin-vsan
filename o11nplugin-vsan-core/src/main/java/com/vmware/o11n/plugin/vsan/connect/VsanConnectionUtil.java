/* ************************************************************************
 * Copyright 2020 VMware, Inc.  All rights reserved. -- VMware Confidential
 * ************************************************************************/

package com.vmware.o11n.plugin.vsan.connect;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.vmware.vim.vsan.binding.vsan.version.internal.versions.VSAN_VERSION_NEWEST;
import static com.vmware.vim.binding.vim.version.internal.versions.VIM_VERSION_NEWEST;

import com.google.common.base.Preconditions;
import com.vmware.vim.binding.vim.fault.InvalidLocale;
import com.vmware.vim.binding.vim.fault.InvalidLogin;
import com.vmware.vim.binding.vim.ServiceInstance;
import com.vmware.vim.binding.vim.ServiceInstanceContent;
import com.vmware.vim.binding.vim.SessionManager;
import com.vmware.vim.binding.vmodl.ManagedObjectReference;
import com.vmware.vim.vmomi.client.Client;
import com.vmware.vim.vmomi.client.common.ProtocolBinding;
import com.vmware.vim.vmomi.client.common.Session;
import com.vmware.vim.vmomi.client.http.HttpClientConfiguration;
import com.vmware.vim.vmomi.client.http.HttpConfiguration;
import com.vmware.vim.vmomi.client.http.ThumbprintVerifier;
import com.vmware.vim.vmomi.client.http.impl.ClientFactory;
import com.vmware.vim.vmomi.core.types.VmodlContext;


public class VsanConnectionUtil {
   private static final String SERVICE_INSTANCE = "ServiceInstance";
   private static final String[] VMODL_CONTEXT =
         new String[] {"com.vmware.vim.binding.vim", "com.vmware.vim.vsan.binding.vim"};
   private static final Logger logger = LoggerFactory.getLogger(VsanConnectionUtil.class);
   
   public static Client createVsanClient(String vcHost, String port, String sslThumbprint,
         String username, String password) {
      try {
         Client vcVmomiClient = createVcVmomiClient(vcHost, port, sslThumbprint);
         connect(vcVmomiClient, username, password);
      
         Client vsanVmomiClient = createVsanVmomiClient(vcHost, port, sslThumbprint);
         Session vcSession = getVcSession(vcVmomiClient);
         setSessionInClient(vsanVmomiClient, vcSession.getId());
         return vsanVmomiClient;
      } catch (Exception ex) {
         String msg = "Error creating vSAN client.";
         logger.error(msg);
         throw new RuntimeException(msg, ex);
      }
   }
   
   private static HttpClientConfiguration createConfiguration(String sslThumbprint) {
      HttpClientConfiguration clientConfig = HttpClientConfiguration.Factory.newInstance();
      clientConfig.setHttpConfiguration(getHttpConfig(sslThumbprint));
      clientConfig.setExecutor(Executors.newFixedThreadPool(1));
      return clientConfig;
   }
   
   private static HttpConfiguration getHttpConfig(final String sslThumbprint) {
      HttpConfiguration httpConfig = HttpConfiguration.Factory.newInstance();
      httpConfig.setTimeoutMs(1000);
      httpConfig.setConnectTimeoutMs(5000);
      httpConfig.setThumbprintVerifier(new ThumbprintVerifier() {
         public Result verify(String serverThumbprint) {
            if (sslThumbprint == null || serverThumbprint == null
                  || !sslThumbprint.equalsIgnoreCase(serverThumbprint)) {
               return Result.MISMATCH;
            }
            return Result.MATCH;
         }
         public void onSuccess(X509Certificate[] arg0, String arg1, Result arg2,
               boolean arg3, boolean arg4) throws SSLException {
            // NO-OP
         }
      });
      return httpConfig;
   }

   private synchronized static VmodlContext initVmodlContext(String[] context) {
      try {
         return VmodlContext.getContext();
      } catch (IllegalStateException ex) {
         return VmodlContext.initContext(context);
      }
   }
   
   private static void connect(Client vcClient, String username, String password)
         throws InvalidLogin, InvalidLocale {
      ManagedObjectReference svcRef = new ManagedObjectReference(SERVICE_INSTANCE,
            SERVICE_INSTANCE);

      ServiceInstance instance = vcClient.createStub(ServiceInstance.class, svcRef);
      ServiceInstanceContent sic = instance.retrieveContent();

      SessionManager sessionMgr = vcClient.createStub(
            SessionManager.class, sic.getSessionManager());
      sessionMgr.login(username, password, "en");
   }
   
   private static Client createVcVmomiClient(String vcHost, String vcPort,
         String sslThumbPrint) throws URISyntaxException {
      URI uri;
      try {
         uri = new URI("https://" + vcHost + ":" + vcPort + "/sdk");
      } catch (URISyntaxException e) {
         throw e;
      }

      return ClientFactory.createClient(uri, VIM_VERSION_NEWEST,
            createConfiguration(sslThumbPrint), initVmodlContext(VMODL_CONTEXT));
   }
   
   private static Client createVsanVmomiClient(String vcHost, String vcPort,
         String sslThumbPrint) throws URISyntaxException {
      URI uri;
      try {
         uri = new URI("https://" + vcHost + ":" + vcPort + "/vsanHealth");
      } catch (URISyntaxException e) {
         throw e;
      }

      return ClientFactory.createClient(uri, VSAN_VERSION_NEWEST,
            createConfiguration(sslThumbPrint), initVmodlContext(VMODL_CONTEXT));
   }
   
   private static Session getVcSession(Client vcClient) {
      ProtocolBinding pb = vcClient.getBinding();
      return pb != null ? pb.getSession() : null;
   }
   
   private static void setSessionInClient(Client client, String sessionId) {
      Preconditions.checkNotNull(sessionId, "sessionId");
      Preconditions.checkNotNull(client, "client");
      Session session = client.getBinding().createSession(sessionId);
      client.getBinding().setSession(session);
   }
}
