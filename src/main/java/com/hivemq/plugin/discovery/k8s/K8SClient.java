package com.hivemq.plugin.discovery.k8s;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.validator.routines.InetAddressValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Simple fabric8 kubernetes client to query for sibling pod info
 *
 * @author Christian Rohmann, inovex GmbH
 */
public class K8SClient{
    
    private static final Logger Log = LoggerFactory.getLogger(K8SClient.class);
    private final KubernetesClient K8Sclient;
   
    
    public K8SClient(){
        this.K8Sclient = new DefaultKubernetesClient();
    }
    
    /**
    * Here we query the K8S api for pods (within out namespace) with a certain label and value of that label)
    * Of those pods we return their name and IP addresses
     * @param label Kubernetes label to look up in defining which pods belong to a common HiveMQ Cluster
     * @param value Value of the label for this particular cluster
     * @return List<String> list of all K8S pods IP addresses which belong to our cluster and are running
    */
    public List<String> getSiblingPodIPsByLabelAndValue(String label, String value){
        List<String> PodIPs = new ArrayList();
        
        Log.debug("Looking for K8S pods with label {} and value {}", label, value);
        PodList siblings = K8Sclient.pods().withLabel(label, value).list();
        
        Log.info("Found a total of {} siblings to form a cluster :-)", siblings.getItems().size());
        
        siblings.getItems().forEach((pod) -> {
            String name = pod.getMetadata().getName();
            String phase = pod.getStatus().getPhase();
            String ip = pod.getStatus().getPodIP();
            Log.debug("Found a sibling pod with name {} and IP {} currently in {} phase", name, ip, phase);
            
            
            // Skip pods without valid IP address or which are not in running state
            if ( ip != null && InetAddressValidator.getInstance().isValid(ip) && phase.equals("Running")){
                Log.debug("Adding pod {} (IP: {}) to list of cluster nodes", name, ip);
                PodIPs.add(ip);
            }
            else{
                Log.debug("Skipping pod {} (IP: {}) currently in {} phase", name, ip, phase);
            }
        });
        
        return PodIPs;
      }
 }
