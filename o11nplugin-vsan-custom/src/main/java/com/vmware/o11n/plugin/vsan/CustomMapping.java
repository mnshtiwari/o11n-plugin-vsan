package com.vmware.o11n.plugin.vsan;

import com.vmware.o11n.plugin.vsan.VsanClientFactory;
import com.vmware.o11n.plugin.vsan.VsanClient;
import com.vmware.o11n.sdk.modeldrivengen.mapping.AbstractMapping;

import com.vmware.vim.vsan.binding.vim.cluster.VsanClusterConfig;
import com.vmware.vim.vsan.binding.vim.cluster.VsanVcClusterConfigSystem;

public class CustomMapping extends AbstractMapping {
    @SuppressWarnings("unchecked")
    @Override
    public void define() {
    	
    	// convertWellKnownTypes();
    	
    	singleton(VsanClientFactory.class);
    	
    	wrap(VsanClient.class);
    	wrap(VsanVcClusterConfigSystem.class);
    	wrap(VsanClusterConfig.class);
    }
}