package test.java.org.cloudsim.disaster;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsim.disaster.DatacenterFactory;
import org.cloudsim.disaster.FailoverManager;

import java.util.Calendar;
import java.util.List;

public class DisasterRecoveryTest {
    
    public void testDatacenterCreation() {
        try {
            // Initialize CloudSim
            CloudSim.init(1, Calendar.getInstance(), false);
            
            // Create datacenters
            Datacenter primaryDC = DatacenterFactory.createDatacenter("PrimaryDC", true);
            Datacenter backupDC = DatacenterFactory.createDatacenter("BackupDC", false);
            
            // Verify datacenters were created
            if (primaryDC == null) {
                throw new Exception("Primary datacenter should not be null");
            }
            if (backupDC == null) {
                throw new Exception("Backup datacenter should not be null");
            }
            
            System.out.println("DatacenterCreation test passed");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
        }
    }
    
    public void testVmCreation() {
        try {
            // Test VM creation for primary and backup
            List<Vm> primaryVms = DatacenterFactory.createVms(4, true, 0);
            List<Vm> backupVms = DatacenterFactory.createVms(2, false, 0);
            
            // Verify correct number of VMs created
            if (primaryVms.size() != 4) {
                throw new Exception("Should create 4 primary VMs, but got " + primaryVms.size());
            }
            if (backupVms.size() != 2) {
                throw new Exception("Should create 2 backup VMs, but got " + backupVms.size());
            }
            
            // Verify primary VMs have higher specs
            if (!(primaryVms.get(0).getRam() > backupVms.get(0).getRam())) {
                throw new Exception("Primary VM should have more RAM");
            }
            if (!(primaryVms.get(0).getMips() > backupVms.get(0).getMips())) {
                throw new Exception("Primary VM should have more MIPS");
            }
            
            System.out.println("VmCreation test passed");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
        }
    }
    
    public void testFailoverLogic() {
        try {
            // Create failover manager with 100% failure probability
            FailoverManager manager = new FailoverManager(1.0);
            
            // Should trigger failure
            boolean triggered = manager.shouldTriggerFailure(10.0);
            if (!triggered) {
                throw new Exception("Failure should be triggered with 100% probability");
            }
            
            // Get failure time
            double failureTime = manager.getFailureTime();
            if (Math.abs(failureTime - 10.0) > 0.001) {
                throw new Exception("Failure time should be 10.0, but got " + failureTime);
            }
            
            // Second try should not trigger (already failed)
            boolean secondTry = manager.shouldTriggerFailure(20.0);
            if (secondTry) {
                throw new Exception("Should not trigger second failure");
            }
            
            System.out.println("FailoverLogic test passed");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
        }
    }
    
    // Main method to run all tests
    public static void main(String[] args) {
        DisasterRecoveryTest test = new DisasterRecoveryTest();
        test.testDatacenterCreation();
        test.testVmCreation();
        test.testFailoverLogic();
    }
}