#!/bin/bash

if http --check-status --ignore-stdin --timeout=2.5 HEAD localhost/courses &> /dev/null; then
    echo 'OK!'
else
    case $? in
        2) service vamk-timetable-api restart ;;
        4) service vamk-timetable-api restart ;;
        5) service vamk-timetable-api restart && service postgres restart ;;
        *) service vamk-timetable-api restart ;;
    esac
fi
