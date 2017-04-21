/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hivemq.plugin.discovery.k8s;

import com.hivemq.spi.HiveMQPluginModule;
import com.hivemq.spi.PluginEntryPoint;
import com.hivemq.spi.plugin.meta.Information;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the plugin module class, which handles the initialization and configuration
 * of the plugin. 
 * 
 * @author Christian Rohmann, inovex GmbH
 */
@Information(name = "HiveMQ Kubernetes (K8S) Cluster Discovery Plugin", author = "Christian Rohmann - inovex GmbH", version = "0.2", description = "Uses Fabric8.io K8S client to query Kubernetes API for sibling pods (having the same value of label HIVEMQ_CLUSTER_ID) to then form a common HiveMQ Cluster")
public class K8SDiscoveryPluginModule extends HiveMQPluginModule {

    Logger log = LoggerFactory.getLogger(K8SDiscoveryPluginModule.class);
    String  hivemqClusterID;
        
    /**
     * This method is provided to execute some custom plugin configuration stuff. 
     * Is is the place to execute Google Guice bindings,etc if needed.
     */
    @Override
    protected void configurePlugin() {
        log.debug("Configuration of K8DiscoveryPluginModule called!");
    }

    /**
     * This method returns the main class of our the plugin.
     *
     * @return callback priority
     */
    @Override
    protected Class<? extends PluginEntryPoint> entryPointClass() {
        return K8SDiscoveryPluginEntryPoint.class;
    }
}
