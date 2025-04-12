#!/bin/bash

echo "Creating directories..."
mkdir -p results
mkdir -p reports

echo "Compiling simulation..."
javac -cp "lib/cloudsim-3.0.3.jar:lib/commons-math3-3.6.1.jar:." src/org/cloudsim/disaster/*.java

echo "Running simulation..."
java -cp "lib/cloudsim-3.0.3.jar:lib/commons-math3-3.6.1.jar:src:." org.cloudsim.disaster.DisasterRecoverySimulation > results/simulation_log.txt

echo "Generating metrics from log..."
python scripts/generate_metrics.py --log results/simulation_log.txt --output results/metrics.csv

echo "Checking for metrics file..."
if [ ! -f results/metrics.csv ]; then
    echo "Error: metrics.csv not found. Simulation may have failed."
    exit 1
fi

echo "Generating HTML report..."
python scripts/generate_html_report.py

echo "Opening HTML report..."
if [[ "$OSTYPE" == "darwin"* ]]; then
    open index.html
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    xdg-open index.html
fi

echo "All done! Report opened in your browser."