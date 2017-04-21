package com.hivemq.plugin.discovery.k8s;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.ArrayList;
import java.util.List;

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
    */
    public List<String> getSiblingPodIPsByLabelAndValue(String label, String value){
        List<String> PodIPs = new ArrayList();
        
        Log.debug("Looking for K8S pods with label {} and value {}", label, value);
        PodList siblings = K8Sclient.pods().withLabel(label, value).list();
        
        Log.info("Found a total of {} siblings to form a cluster :-)", siblings.getItems().size());
        
        for (Pod pod : siblings.getItems()) {
            String name = pod.getMetadata().getName();
            String ip = pod.getStatus().getPodIP();
            Log.debug("Found a sibling pod with name {} and IP {}", name, ip);
            
            PodIPs.add(ip);
        }
        
        return PodIPs;
      }
 }
