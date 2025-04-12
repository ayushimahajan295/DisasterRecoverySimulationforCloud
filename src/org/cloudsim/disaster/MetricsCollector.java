package org.cloudsim.disaster;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MetricsCollector {
    
    public void saveToCSV(List<Cloudlet> cloudletList, FailoverManager failoverManager, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            
            // Write CSV header
            writer.append("CloudletID,Status,DatacenterId,VMId,ExecutionTime,WaitTime,StartTime,FinishTime,AffectedByFailover\n");
            
            for (Cloudlet cloudlet : cloudletList) {
                double executionTime = cloudlet.getActualCPUTime();
                double waitTime = cloudlet.getWaitingTime();
                double startTime = cloudlet.getExecStartTime();
                double finishTime = cloudlet.getFinishTime();
                
                // Check if cloudlet was affected by failover
                String affectedByFailover = "No";
                if (failoverManager.didFailureOccur()) {
                    // A cloudlet is affected if it started after the failure
                    if (startTime > failoverManager.getFailureTime()) {
                        affectedByFailover = "Yes";
                    }
                }
                
                // Write cloudlet data
                writer.append(String.valueOf(cloudlet.getCloudletId())).append(",");
                writer.append(cloudlet.getCloudletStatusString()).append(",");
                writer.append(String.valueOf(cloudlet.getResourceId())).append(",");
                writer.append(String.valueOf(cloudlet.getVmId())).append(",");
                writer.append(String.valueOf(executionTime)).append(",");
                writer.append(String.valueOf(waitTime)).append(",");
                writer.append(String.valueOf(startTime)).append(",");
                writer.append(String.valueOf(finishTime)).append(",");
                writer.append(affectedByFailover).append("\n");
            }
            
            writer.flush();
            writer.close();
            
            Log.printLine("Metrics saved to " + fileName);
        } catch (IOException e) {
            Log.printLine("Error saving metrics: " + e.getMessage());
        }
    }
}