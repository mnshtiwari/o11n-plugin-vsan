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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.vmware.vim.vsan.binding.vsan.version.versions.VSAN_VERSION_NEWEST;
import static com.vmware.vim.binding.vim.version.versions.VIM_VERSION_NEWEST;

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

@Component
public class VsanClientFactory {
   private static final String SERVICE_INSTANCE = "ServiceInstance";
   private static final Logger logger = LoggerFactory.getLogger(VsanClientFactory.class);
   
   public static VmodlContext vmodlContext = null;
   
   public Client createVsanClient(String vcHost, String port, String sslThumbprint,
         String username, String password) {
      try {
         Client vcVmomiClient = createVcVmomiClient(vcHost, port, sslThumbprint);
         connect(vcVmomiClient, username, password);
      
         Client vsanVmomiClient = createVsanVmomiClient(vcHost, port, sslThumbprint);
         Session vcSession = getVcSession(vcVmomiClient);
         setSessionInClient(vsanVmomiClient, vcSession.getId());
         return vsanVmomiClient;
      } catch (Exception ex) {
         String msg = "[createVsanClient] Error creating vSAN client.";
         logger.error(msg);
         throw new RuntimeException(msg, ex);
      }
   }
   
   private HttpClientConfiguration createConfiguration(String sslThumbprint) {
      HttpClientConfiguration clientConfig = HttpClientConfiguration.Factory.newInstance();
      clientConfig.setHttpConfiguration(getHttpConfig(sslThumbprint));
      clientConfig.setExecutor(Executors.newFixedThreadPool(1));
      return clientConfig;
   }
   
   private HttpConfiguration getHttpConfig(final String sslThumbprint) {
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
   
   private void connect(Client vcClient, String username, String password)
         throws InvalidLogin, InvalidLocale {
      ManagedObjectReference svcRef = new ManagedObjectReference(SERVICE_INSTANCE,
            SERVICE_INSTANCE);

      ServiceInstance instance = vcClient.createStub(ServiceInstance.class, svcRef);
      ServiceInstanceContent sic = instance.retrieveContent();

      SessionManager sessionMgr = vcClient.createStub(
            SessionManager.class, sic.getSessionManager());
      sessionMgr.login(username, password, "en");
   }
   
   private Client createVcVmomiClient(String vcHost, String vcPort,
         String sslThumbPrint) throws URISyntaxException {
      URI uri;
      try {
         uri = new URI("https://" + vcHost + ":" + vcPort + "/sdk");
      } catch (URISyntaxException e) {
         logger.error("[createVcVmomiClient] Bad URL. Reason: {}", e.getReason());
         throw e;
      }
      
      VmodlContext vmContext = getVmodlContext();
      String connUtilCL = VsanClientFactory.class.getClassLoader().toString();
      String vimCL = VIM_VERSION_NEWEST.getClassLoader().toString();
      String moCL = ManagedObjectReference.class.getClassLoader().toString();
      String vsanCL = VSAN_VERSION_NEWEST.getClassLoader().toString();
      String path = VIM_VERSION_NEWEST.getProtectionDomain().getCodeSource().getLocation().getPath();
      String latestVimVersionName = VIM_VERSION_NEWEST.getSimpleName();
      return ClientFactory.createClient(uri, VIM_VERSION_NEWEST,
            createConfiguration(sslThumbPrint), vmContext);
   }
   
   private Client createVsanVmomiClient(String vcHost, String vcPort,
         String sslThumbPrint) throws URISyntaxException {
      URI uri;
      try {
         uri = new URI("https://" + vcHost + ":" + vcPort + "/vsanHealth");
      } catch (URISyntaxException e) {
         logger.error("[createVsanVmomiClient] Bad URL. Reason: {}", e.getReason());
         throw e;
      }

      return ClientFactory.createClient(uri, VSAN_VERSION_NEWEST,
            createConfiguration(sslThumbPrint), getVmodlContext());
   }
   
   private Session getVcSession(Client vcClient) {
      ProtocolBinding pb = vcClient.getBinding();
      return pb != null ? pb.getSession() : null;
   }
   
   private void setSessionInClient(Client client, String sessionId) {
      Preconditions.checkNotNull(sessionId, "sessionId");
      Preconditions.checkNotNull(client, "client");
      Session session = client.getBinding().createSession(sessionId);
      client.getBinding().setSession(session);
   }
   
   private static synchronized VmodlContext getVmodlContext() {
      if (vmodlContext == null) {
         try {
            vmodlContext = VmodlContext.createContext(
                  new String[] {
                        "com.vmware.vim.binding.vim",
                        "com.vmware.vim.vsan.binding.vim"});
         } catch (Exception ex) {
            logger.error("[initVmodlContext] Failed to initialize vSAN vmodl context");
            throw ex;
         }
      }
      return vmodlContext;
   }
}
