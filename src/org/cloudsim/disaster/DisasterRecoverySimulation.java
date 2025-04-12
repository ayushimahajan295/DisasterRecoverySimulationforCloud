package org.cloudsim.disaster;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;

public class DisasterRecoverySimulation {
    /** The cloudlet list. */
    private static List<Cloudlet> cloudletList;

    /** The vm list. */
    private static List<Vm> vmList;

    private static FailoverManager failoverManager;
    private static MetricsCollector metricsCollector;

    public static void main(String[] args) {
        Log.printLine("Initialising...");

        try {
            // Number of users/brokers
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;  // trace events

            // Initialize the CloudSim library
            CloudSim.init(numUsers, calendar, traceFlag);

            // Create a datacenter broker
            DatacenterBroker broker = new DatacenterBroker("Broker_0");
            int brokerId = broker.getId();

            // Create primary and backup datacenters
            Datacenter primaryDC = DatacenterFactory.createDatacenter("PrimaryDC", true);
            Datacenter backupDC = DatacenterFactory.createDatacenter("BackupDC", false);

            // Create VMs for primary datacenter (4 VMs)
            vmList = DatacenterFactory.createVms(4, true, brokerId);
            
            // Create VMs for backup datacenter (2 VMs)
            vmList.addAll(DatacenterFactory.createVms(2, false, brokerId));
            
            // Submit VM list to the broker
            broker.submitVmList(vmList);

            // Create cloudlets (20 tasks)
            cloudletList = CloudletManager.createCloudlets(20, brokerId);
            
            // Submit cloudlet list to the broker
            broker.submitCloudletList(cloudletList);
            
            // Configure disaster event (50% chance of failure at simulation time 50)
            failoverManager = new FailoverManager(0.5);
            DisasterRecoverySimulationEventListener listener = new DisasterRecoverySimulationEventListener(failoverManager, broker, primaryDC, backupDC);
            
            // Set up metrics collector
            metricsCollector = new MetricsCollector();

            // Start the simulation
            CloudSim.startSimulation();

            // Stop the simulation
            CloudSim.stopSimulation();

            // Process the results
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            
            // Print results
            printResults(newList);
            
            // Save metrics to CSV file
            metricsCollector.saveToCSV(newList, failoverManager, "results/metrics.csv");

            Log.printLine("Simulation finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Simulation error: " + e.getMessage());
        }
    }

    private static void printResults(List<Cloudlet> list) {
        int totalTasks = list.size();
        int completed = 0;
        int failed = 0;
        
        double totalExecutionTime = 0.0;
        double totalWaitTime = 0.0;
        
        DecimalFormat dft = new DecimalFormat("###.##");
        
        Log.printLine();
        Log.printLine("========== SIMULATION RESULTS ==========");
        Log.printLine("Number of Cloudlets: " + totalTasks);
        
        for (Cloudlet cloudlet : list) {
            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                completed++;
                totalExecutionTime += cloudlet.getActualCPUTime();
                totalWaitTime += cloudlet.getWaitingTime();
            } else {
                failed++;
            }
        }
        
        double avgWaitTime = completed > 0 ? totalWaitTime / completed : 0;
        double avgExecutionTime = completed > 0 ? totalExecutionTime / completed : 0;
        
        Log.printLine("===== PERFORMANCE METRICS =====");
        Log.printLine("avgWaitTime: " + dft.format(avgWaitTime));
        Log.printLine("avgExecutionTime: " + dft.format(avgExecutionTime));
        
        Log.printLine("===== FAILOVER INFORMATION =====");
        if (failoverManager.didFailureOccur()) {
            Log.printLine("    Failure occurred at time: " + dft.format(failoverManager.getFailureTime()));
        } else {
            Log.printLine("    No failure occurred during simulation.");
        }
        
        Log.printLine("===== CLOUDLET DETAILS =====");
        Log.printLine("\tID\tSTATUS\tDC\tVM\tTime\tStart Time\tFinish Time");
        
        for (Cloudlet cloudlet : list) {
            Log.printLine("\t" + cloudlet.getCloudletId() + "\t" + 
                          cloudlet.getCloudletStatusString() + "\t" + 
                          cloudlet.getResourceId() + "\t" + 
                          cloudlet.getVmId() + "\t" + 
                          dft.format(cloudlet.getActualCPUTime()) + "\t" + 
                          dft.format(cloudlet.getExecStartTime()) + "\t" + 
                          dft.format(cloudlet.getFinishTime()));
        }
    }
}