import pandas as pd
import argparse
import os
import sys
from pathlib import Path
import re

def extract_metrics_from_log(log_file_path):
    """Extract metrics from CloudSim simulation log"""
    try:
        log_file_path = Path(log_file_path)
        if not log_file_path.exists():
            print(f"Error: Log file not found at {log_file_path}")
            return pd.DataFrame()
            
        with open(log_file_path, 'r', encoding='utf-8') as f:
            log_content = f.read()
            
        # Parse the simulation log to get cloudlet details
        cloudlet_data = []
        
        # Find the cloudlet details section
        cloudlet_sections = re.findall(r"===== CLOUDLET DETAILS =====\s+([\s\S]+?)(?=\n\n|$)", log_content)
        cloudlet_section = cloudlet_sections[0] if cloudlet_sections else ""
        
        if cloudlet_section:
            # Skip the header line and process data lines
            lines = cloudlet_section.strip().split('\n')
            if len(lines) > 1:  # Ensure we have data beyond the header
                header_line = lines[0]
                data_lines = lines[1:]
                
                for line in data_lines:
                    # Parse each line of cloudlet data - handle both space and tab delimiters
                    parts = [part.strip() for part in re.split(r'\s+', line) if part.strip()]
                    if len(parts) >= 7:  # Ensure we have enough data points
                        try:
                            cloudlet_id = int(parts[0])
                            status = parts[1]
                            dc_id = int(parts[2])
                            vm_id = int(parts[3])
                            exec_time = float(parts[4])
                            start_time = float(parts[5])
                            finish_time = float(parts[6])
                            
                            # Calculate wait time (usually 0 in this simulation)
                            wait_time = 0.0  # Can be calculated as start_time - submission_time if available
                            
                            # Determine if affected by failover - look for rerouting information in log
                            affected_by_failover = "No"
                            routing_pattern = rf"Cloudlet {cloudlet_id}.*rerouted|migrated|failover"
                            if re.search(routing_pattern, log_content, re.IGNORECASE):
                                affected_by_failover = "Yes"
                            # Backup detection method based on VM ID or increased execution time
                            elif vm_id >= 4 or exec_time > 30.0:  # Assuming VMs 4+ are backup or longer execution indicates failover
                                affected_by_failover = "Yes"
                            
                            cloudlet_data.append({
                                'CloudletID': cloudlet_id,
                                'Status': status,
                                'DatacenterID': dc_id,
                                'VMId': vm_id,
                                'ExecutionTime': exec_time,
                                'StartTime': start_time,
                                'FinishTime': finish_time,
                                'WaitTime': wait_time,
                                'AffectedByFailover': affected_by_failover
                            })
                        except (ValueError, IndexError) as e:
                            print(f"Warning: Error parsing cloudlet data line: {line}")
                            print(f"Error details: {e}")
        else:
            print("Warning: Could not find CLOUDLET DETAILS section in log")
        
        # Find the disaster event time
        disaster_time = None
        disaster_pattern = r"DISASTER!.*time:\s*(\d+\.\d+)"
        disaster_match = re.search(disaster_pattern, log_content)
        if disaster_match:
            try:
                disaster_time = float(disaster_match.group(1))
                print(f"Found disaster event at time: {disaster_time}")
                
                # Update affected_by_failover based on disaster time if available
                for item in cloudlet_data:
                    if item['StartTime'] > disaster_time or (
                       item['StartTime'] <= disaster_time and item['FinishTime'] > disaster_time):
                        item['AffectedByFailover'] = "Yes"
            except Exception as e:
                print(f"Warning: Could not parse disaster time: {e}")
        
        # Create a DataFrame
        df = pd.DataFrame(cloudlet_data)
        
        if df.empty:
            print("Warning: No cloudlet data found in the log")
        
        # Add disaster time to metadata if available
        if disaster_time is not None:
            # Create a metadata dataframe to store simulation parameters
            metadata = pd.DataFrame([{
                'Parameter': 'DisasterTime',
                'Value': disaster_time
            }])
            
            # Save metadata to results directory (create if it doesn't exist)
            results_dir = Path(log_file_path).parent.parent / 'results'
            results_dir.mkdir(exist_ok=True, parents=True)
            
            metadata_path = results_dir / 'metadata.csv'
            metadata.to_csv(metadata_path, index=False)
            print(f"Saved simulation metadata to {metadata_path}")
        
        return df
    
    except Exception as e:
        print(f"Error extracting metrics from log: {e}")
        import traceback
        traceback.print_exc()
        return pd.DataFrame()  # Return empty DataFrame on error

def main():
    parser = argparse.ArgumentParser(description="Generate metrics CSV from CloudSim log")
    parser.add_argument("--log", default="cloudsim_log.txt", help="Path to CloudSim log file")
    parser.add_argument("--output", default=None, help="Output path for metrics CSV")
    
    args = parser.parse_args()
    
    # Use platform-independent path handling
    log_path = Path(args.log)
    
    # Default output path is in 'results' directory relative to script's parent directory
    if args.output is None:
        # Get the script's directory and navigate to parent
        script_dir = Path(__file__).resolve().parent
        project_dir = script_dir.parent
        output_path = project_dir / 'results' / 'metrics.csv'
    else:
        output_path = Path(args.output)
    
    # Ensure the output directory exists
    output_dir = output_path.parent
    output_dir.mkdir(exist_ok=True, parents=True)
    
    print(f"Reading log from: {log_path}")
    print(f"Output will be saved to: {output_path}")
    
    # Extract metrics
    df = extract_metrics_from_log(log_path)
    
    if not df.empty:
        # Save to CSV
        df.to_csv(output_path, index=False)
        print(f"Successfully extracted metrics from log and saved to {output_path}")
        print(f"Total cloudlets processed: {len(df)}")
    else:
        print("No metrics data was extracted from the log")
        sys.exit(1)  # Exit with error code

if __name__ == "__main__":
    main()