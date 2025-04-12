package org.cloudsim.disaster;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatacenterFactory {

    public static Datacenter createDatacenter(String name, boolean isPrimary) {
        // Create host list
        List<Host> hostList = new ArrayList<>();

        // Host specifications
        int hostId = 0;
        int ram = isPrimary ? 16384 : 8192; // More RAM for primary datacenter
        int bw = isPrimary ? 10000 : 5000;  // More bandwidth for primary datacenter
        int storage = 1000000;             // 1 TB storage for both
        int numCores = isPrimary ? 4 : 2;  // More cores for primary datacenter

        // Create Processing Elements (cores)
        List<Pe> peList = new ArrayList<>();
        int mips = isPrimary ? 3000 : 1500; // Primary has faster CPUs
        
        for (int i = 0; i < numCores; i++) {
            peList.add(new Pe(i, new PeProvisionerSimple(mips)));
        }

        // Create hosts (2 hosts per datacenter)
        for (int i = 0; i < 2; i++) {
            hostList.add(
                new Host(
                    hostId++,
                    new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(bw),
                    storage,
                    peList,
                    new VmSchedulerTimeShared(peList)
                )
            );
        }

        // Create datacenter characteristics
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double costPerSec = isPrimary ? 0.1 : 0.05; // Primary is more expensive
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.1;
        
        LinkedList<Storage> storageList = new LinkedList<>();
        
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, timeZone, costPerSec, costPerMem, costPerStorage, costPerBw
        );

        try {
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Vm> createVms(int count, boolean isPrimary, int brokerId) {
        List<Vm> vms = new ArrayList<>();
        
        // VM specifications
        int mips = isPrimary ? 1000 : 500;    // Primary VMs are faster
        int ram = isPrimary ? 2048 : 1024;    // Primary VMs have more RAM
        long bw = isPrimary ? 1000 : 500;     // Primary VMs have more bandwidth
        long size = 10000;                    // 10 GB disk size for all VMs
        int pesNumber = isPrimary ? 2 : 1;    // Primary VMs have more cores
        String vmm = "Xen";
        
        // VM ID offset (primary VMs: 0-3, backup VMs: 4-5)
        int idOffset = isPrimary ? 0 : 4;
        
        for (int i = 0; i < count; i++) {
            Vm vm = new Vm(
                idOffset + i, 
                brokerId, 
                mips, 
                pesNumber, 
                ram, 
                bw, 
                size, 
                vmm, 
                new CloudletSchedulerTimeShared()
            );
            vms.add(vm);
        }
        
        return vms;
    }
}