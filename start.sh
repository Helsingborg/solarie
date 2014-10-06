#!/bin/bash

echo "Solarie startas nu i en ny bakgrundsprocess..."

nohup ./run.sh > /dev/null & 2>&1

exit 0

