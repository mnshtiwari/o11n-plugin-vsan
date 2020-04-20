/* ************************************************************************
 * Copyright 2020 VMware, Inc.  All rights reserved. -- VMware Confidential
 * ************************************************************************/

package com.vmware.o11n.plugin.vsan.config;

import com.vmware.o11n.plugin.vsan.model.ConnectionInfo;

/**
 * An extension point of the configuration persister. Serves the role of an
 * Observer when a certain connection is created, modified or deleted.
 */
public interface ConfigurationChangeListener {

    /**
     * Invoked when a connection is updated or created
     */
    void connectionUpdated(ConnectionInfo info);

    /**
     * Invoked when the connection is deleted
     */
    void connectionRemoved(ConnectionInfo info);
}