package com.hivemq.plugin.discovery.k8s;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hivemq.spi.callback.cluster.ClusterNodeAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This callback will be registered with the HiveMQ callbackRegistry to called at cluster discovery intervals 
 *
 * @author Christian Rohmann, inovex GmbH
 */
public class K8SDiscoveryCallback implements com.hivemq.spi.callback.cluster.ClusterDiscoveryCallback {

    private static final Logger Log =  LoggerFactory.getLogger(K8SDiscoveryCallback.class);
    private int clusterPort; // all pods will use the same config and port
        
    @Override
    public void init(String nodeid, ClusterNodeAddress cna) {
        Log.info("This node has IP " + cna.getHost() + " listens on port " + cna.getPort() + " and has cluster-ID " + nodeid);
        this.clusterPort = cna.getPort();
    }

    @Override
    public ListenableFuture<List<ClusterNodeAddress>> getNodeAddresses() {
        Log.debug("List of cluster node addresses requested via ClusterDiscoveryCallback!");
        
        String hivemqClusterID = System.getenv("HIVEMQ_CLUSTER_ID");
        List<ClusterNodeAddress> clusterNodes = new ArrayList<>();
        
        // check if we even know which cluster we belong to
        if (hivemqClusterID == null || hivemqClusterID.isEmpty()){
            Log.warn("HIVEMQ_CLUSTER_ID environment variable is not set or empty!");
            
        }else{
            Log.info("We have HIVEMQ_CLUSTER_ID set to > {} <. Let's find our sibling pods with that same tag and value", hivemqClusterID );
        
            K8SClient k8s = new K8SClient();
            List<String> nodes = k8s.getSiblingPodIPsByLabelAndValue("HIVEMQ_CLUSTER_ID", hivemqClusterID);
        
            for (Iterator<String> iterator = nodes.iterator(); iterator.hasNext();) {
                String next = iterator.next();
                clusterNodes.add(new ClusterNodeAddress(next, this.clusterPort));
            }
            
        }
              
        // concat all node names / IPs
        String joinedClusterNodes = clusterNodes.stream()
            .map(ClusterNodeAddress::getHost)
            .collect(Collectors.joining(", "));
     
        Log.info("Those are our cluster node IPs: {}", joinedClusterNodes );
        return Futures.immediateFuture(clusterNodes);
    }

    @Override
    public void destroy() {
        Log.debug("Destroying K8SDiscoveryCallback.");
    }
    
}
