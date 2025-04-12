package org.cloudsim.disaster;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;

import java.util.ArrayList;
import java.util.List;

public class CloudletManager {
    
    public static List<Cloudlet> createCloudlets(int count, int brokerId) {
        List<Cloudlet> cloudletList = new ArrayList<>();
        
        // Cloudlet properties
        long length = 10000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        
        UtilizationModel utilizationModel = new UtilizationModelFull();
        
        for (int i = 0; i < count; i++) {
            Cloudlet cloudlet = new Cloudlet(
                i, 
                length, 
                pesNumber, 
                fileSize, 
                outputSize, 
                utilizationModel, 
                utilizationModel, 
                utilizationModel
            );
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
        }
        
        return cloudletList;
    }
}