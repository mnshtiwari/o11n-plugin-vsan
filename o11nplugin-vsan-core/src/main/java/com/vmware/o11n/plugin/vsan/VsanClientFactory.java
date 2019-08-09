package com.vmware.o11n.plugin.vsan;

import java.lang.reflect.Method;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.vmware.vim.vsan.binding.vsan.version.internal.versions.VSAN_VERSION_NEWEST;

import com.vmware.o11n.plugin.vsphere.connect.SdkSession;
import com.vmware.o11n.plugin.vsphere.SdkConnection;
import com.vmware.vim.vmomi.client.Client;
import com.vmware.vim.vmomi.client.http.impl.HttpClientConfigurationImpl;
import com.vmware.vim.vmomi.core.types.VmodlContext;

public class VsanClientFactory {
	
   private static final Logger logger = LoggerFactory.getLogger(VsanClientFactory.class);
	
   public VsanClient createClient(SdkConnection sdkConnection) {
      Client vmomiClient;

      /* Initialize vsan vmodl context. */
      VmodlContext vsanVmodlContext = null;
      ClassLoader bundleClassLoader = VsanClientFactory.class.getClassLoader();
      ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
      try {
         Thread.currentThread().setContextClassLoader(bundleClassLoader);
         vsanVmodlContext = VmodlContext.createContext(
            new String[] { "com.vmware.vim.binding.vim", "com.vmware.vim.vsan.binding.vim" });
      } catch (Exception ex) {
         logger.error("Failed to initialize vSAN vmodl context");
      } finally {
         Thread.currentThread().setContextClassLoader(currentClassLoader);
      }

      try {
    	  SdkSession sdkSession = getSdkSession(sdkConnection);
          String vsphereUrl = sdkSession.getEndpointUri().toString();
          String vsanUrlPath = sdkSession.isVcenter() ? "/vsanHealth" : "/vsan";
          String vsanhealthServiceUrl = vsphereUrl.replace("/sdk", vsanUrlPath);
    	  URI vsanUri = new URI(vsanhealthServiceUrl);
         vmomiClient =
            Client.Factory.createClient(
            		vsanUri,
                    VSAN_VERSION_NEWEST,
                    vsanVmodlContext,
                    new HttpClientConfigurationImpl());
         return new VsanClient(vmomiClient, sdkSession);
      } catch (Exception ex) {
    	  logger.error("Failed to initialize vSAN client", ex);
         //throw VsanHealthException.fromEx(ex, "Error in creating HTTP client configuration.");
      }
      
      return null;
   }
   
   private SdkSession getSdkSession(SdkConnection sdkConnection)
		   throws java.lang.NoSuchMethodException, java.lang.IllegalAccessException, java.lang.reflect.InvocationTargetException {

	   Method getSdkSessionMethod = SdkConnection.class.
	           getDeclaredMethod("getSdkSession");

	   getSdkSessionMethod.setAccessible(true);

	   SdkSession sdkSession = (SdkSession)
			   getSdkSessionMethod.invoke(sdkConnection);

	   return sdkSession;
   }

}
