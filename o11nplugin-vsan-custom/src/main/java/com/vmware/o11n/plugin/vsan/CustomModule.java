package com.vmware.o11n.plugin.vsan;



import java.util.Collections;

import com.google.inject.AbstractModule;
import com.vmware.o11n.sdk.modeldrivengen.mapping.AbstractMapping;
import com.vmware.o11n.sdk.modeldrivengen.model.Plugin;

public class CustomModule extends AbstractModule {

    private final Plugin plugin;

    @Override
    protected void configure() {
        bind(AbstractMapping.class).toInstance(new CustomMapping());
        bind(Plugin.class).toInstance(plugin);
        
    }

    public CustomModule() {
        this.plugin = new Plugin();

        plugin.setApiPrefix("Vsan");
        plugin.setIcon("default-32x32.png");
        plugin.setDescription("Vsan");
        plugin.setDisplayName("Vsan");
        plugin.setName("Vsan");
        plugin.setPackages(Collections.singletonList("o11nplugin-vsan-package-${project.version}.package"));
        plugin.setAdaptorClassName(com.vmware.o11n.plugin.vsan.VsanPluginAdaptor.class);
    }
}