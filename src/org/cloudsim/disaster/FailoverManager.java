package org.cloudsim.disaster;

import org.cloudbus.cloudsim.Log;

import java.util.Random;

public class FailoverManager {
    private double failureProbability;
    private boolean failureOccurred;
    private double failureTime;
    private Random random;
    
    public FailoverManager(double failureProbability) {
        this.failureProbability = failureProbability;
        this.failureOccurred = false;
        this.failureTime = -1;
        this.random = new Random(System.currentTimeMillis());
    }
    
    public boolean shouldTriggerFailure(double currentTime) {
        // Only check once and if failure hasn't occurred yet
        if (!failureOccurred && random.nextDouble() < failureProbability) {
            failureOccurred = true;
            failureTime = currentTime;
            Log.printLine("DISASTER! Primary datacenter failure at time: " + failureTime);
            return true;
        }
        return false;
    }
    
    public boolean didFailureOccur() {
        return failureOccurred;
    }
    
    public double getFailureTime() {
        return failureTime;
    }
}