package com.vmware.o11n.plugin.vsan.singleton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vmware.o11n.plugin.vsan.config.ConnectionPersister;
import com.vmware.o11n.plugin.vsan.model.ConnectionInfo;
import com.vmware.o11n.plugin.sdk.spring.platform.GlobalPluginNotificationHandler;

/**
 * A scripting singleton for managing vSAN connections.
 */
@Component
@Scope(value = "prototype")
public class ConnectionManager {

    @Autowired
    private ConnectionPersister persister;

    /*
     * A vRO SDK object which is responsible for notifying the platform of
     * changes in the inventory of the plug-in
     */
    @Autowired
    private GlobalPluginNotificationHandler notificationHandler;

    /**
     * This method creates a vsan connection by the provided host, port,
     * SSL thumbprint, username and password.
     *
     * @param vcHost vCenter host
     * @param vcPort vCenter port
     * @param sslThumbPrint SSL thumbprint of the vCenter
     * @param username vCenter username
     * @param password vCenter password
     * @return the ID of the newly created connection
     */
    public String save(String vcHost, int vcPort, String sslThumbPrint,
          String username, String password) {
        Assert.notNull(vcHost, "vCenter host cannot be null.");
        Assert.isTrue(vcPort > 0, "vCenter port needs to be a positive number.");
        Assert.notNull(sslThumbPrint, "vCenter SSL thumbprint cannot be null.");
        Assert.notNull(vcHost, "vCenter username cannot be null.");
        Assert.notNull(vcHost, "vCenter password cannot be null.");

        ConnectionInfo info = new ConnectionInfo(vcHost, vcPort, sslThumbPrint,
              username, password);

        // Save the connection through the persister
        info = persister.save(info);

        // Invalidate all elements of the vSAN inventory
        notificationHandler.notifyElementsInvalidate();

        // Return the ID of the newly created connection
        return info.getId().toString();
    }

}