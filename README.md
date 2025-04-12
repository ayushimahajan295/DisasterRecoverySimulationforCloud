# Disaster Recovery Simulation Using CloudSim

A simulation of cloud infrastructure disaster recovery scenarios using CloudSim 3.0.

---

## Overview

This project simulates disaster recovery in cloud environments by analyzing the performance of failover mechanisms when a primary datacenter fails. It includes:

- Deployment of primary and backup datacenters
- Assignment of virtual machines (VMs) and cloudlets (tasks)
- Disaster simulation at a configurable time
- Metrics collection and visualization
- Evaluation of failover effectiveness

---

## Features

- Primary and backup datacenter configuration  
- Disaster simulation at user-defined time  
- Performance metrics tracking  
- Automated report generation with charts  
- Analysis of disaster impact and recovery

---

## Requirements

- Java JDK 8 or higher  
- Python 3.6+ with `matplotlib` and `pandas`  
- CloudSim 3.0.3 (included in `lib/`)  
- Commons Math 3.6.1 (included in `lib/`)  

---

## Quick Start

### For Windows users:
```bash
.
un_simulation.bat
```

### For Linux/Mac users:
```bash
chmod +x run_simulation.sh
./run_simulation.sh
```

---

## Understanding the Simulation

- A primary datacenter is created with several VMs.  
- Cloudlets (tasks) are assigned to these VMs.  
- At a specified time, a disaster event shuts down the primary datacenter.  
- The failover mechanism transfers tasks to a backup datacenter if possible.

### Key Metrics Collected:

- Average execution time  
- Average wait time  
- Task completion status  
- Impact of failover on cloudlet execution  

---

## Simulation Parameters

To customize the simulation (e.g., disaster timing, VM count, cloudlet specs), edit:

```
src/org/cloudsim/disaster/DisasterRecoverySimulation.java
```

---

## Output

After running the simulation:

- Logs saved to: `results/simulation_log.txt`  
- Metrics extracted to: `results/metrics.csv`  
- Charts generated in: `reports/`  
- Full report as: `index.html` (automatically opened in browser)

---

## Project Structure

```
src/org/cloudsim/disaster/    Core simulation classes  
scripts/                      Python scripts for metrics and reports  
lib/                          Required libraries (CloudSim & Commons Math)  
results/                      Simulation outputs and logs  
reports/                      Visual reports and charts  
```

---

## Contributing

Contributions are welcome.  
Feel free to fork the repository, create a feature branch, and submit a Pull Request.

---

## Contact

For any queries or suggestions, please open an issue in the repository.
