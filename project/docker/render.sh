#!/bin/sh

print_help() {
    echo 'Render using a container.'
    echo 'Command format:'
    echo '  ./render.sh input_directory output_directory working_directory OPTIONS'
    echo 'OPTIONS:'
    echo '  -F        : force every file to be updated'
    echo '  -C        : dont clean output'
    echo '  -V        : verbose logging'
    echo 'A file named changes.log will be placed in the working_directory'
    echo 'EXAMPLE:'
    echo './render.sh /Users/erichgatejen/remote/web/READY /Users/erichgatejen/remote/web/OUTPUT /Users/erichgatejen/remote/web'
    echo
}

if [ $# -lt 3 ] ; then
    echo "Not enough parameters"
    print_help
    exit 9
fi

if [ ! -d $1 ] ; then
    echo "input_directory passed as $1 does not exist."
    print_help
    exit 9
fi

if [ ! -d $2 ] ; then
    echo "output_directory passed as $2 does not exist."
    print_help
    exit 9
fi

if [ ! -d $3 ] ; then
    echo "working_directory passed as $3 does not exist."
    print_help
    exit 9
fi

docker run -it --rm -v $1:/input -v $2:/output -v $3:/working erichgatejen/rendsite:latest /internalrender.sh /intput /output changes=/working/changes.log $4 $5
