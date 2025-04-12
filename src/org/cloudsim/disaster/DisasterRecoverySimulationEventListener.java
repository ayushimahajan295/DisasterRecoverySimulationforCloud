package org.cloudsim.disaster;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.SimEntity;

import java.util.List;
import java.util.ArrayList;

public class DisasterRecoverySimulationEventListener extends SimEntity {
    
    private static final int DISASTER_CHECK = 999;
    private static final double CHECK_INTERVAL = 10.0;
    
    private FailoverManager failoverManager;
    private DatacenterBroker broker;
    private Datacenter primaryDC;
    private Datacenter backupDC;
    
    public DisasterRecoverySimulationEventListener(
            FailoverManager failoverManager,
            DatacenterBroker broker,
            Datacenter primaryDC,
            Datacenter backupDC) {
        
        super("DisasterRecoveryListener");
        this.failoverManager = failoverManager;
        this.broker = broker;
        this.primaryDC = primaryDC;
        this.backupDC = backupDC;
        
        // Schedule the first disaster check
        CloudSim.send(getId(), getId(), CHECK_INTERVAL, DISASTER_CHECK, null);
    }
    
    @Override
    public void startEntity() {
        // Entity is started when it's created
    }
    
    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case DISASTER_CHECK:
                checkForDisaster();
                break;
            default:
                break;
        }
    }
    
    private void checkForDisaster() {
        double currentTime = CloudSim.clock();
        
        if (failoverManager.shouldTriggerFailure(currentTime)) {
            performFailover();
        }
        
        // Schedule next check if simulation is still running and failure hasn't occurred
        if (!failoverManager.didFailureOccur()) {
            CloudSim.send(getId(), getId(), CHECK_INTERVAL, DISASTER_CHECK, null);
        }
    }
    
    private void performFailover() {
        // Move all waiting cloudlets from primary to backup datacenter
        List<Cloudlet> cloudletsToReroute = new ArrayList<>();
        
        // Logic to reroute cloudlets
        // In a real implementation, we would find all waiting cloudlets and reroute them
        
        Log.printLine("Rerouted " + cloudletsToReroute.size() + " cloudlets to backup datacenter.");
    }
    
    @Override
    public void shutdownEntity() {
        // Nothing special to do
    }
}