#!/bin/sh
BASE_DIR=/alsmon/dw/kettle/
STARSCHEMA_JOB=/alsmon/dw/workspace_kettle/starschema/jobs/execstarschema.kjb
STARSCHEMA_LOGFILE=/alsmon/dw/workspace_kettle/logs/starschema.log

cd $BASE_DIR
./kitchen.sh -file=$STARSCHEMA_JOB -Level=Basic >> $STARSCHEMA_LOGFILE
