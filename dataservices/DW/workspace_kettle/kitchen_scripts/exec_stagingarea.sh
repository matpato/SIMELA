#!/bin/sh
BASE_DIR=/alsmon/dw/kettle/
STAGINGAREA_JOB=/alsmon/dw/workspace_kettle/stagingarea/jobs/execstagingarea.kjb
STAGINGAREA_LOGFILE=/alsmon/dw/workspace_kettle/logs/stagingarea.log

cd $BASE_DIR
./kitchen.sh -file=$STAGINGAREA_JOB -Level=Basic >> $STAGINGAREA_LOGFILE
