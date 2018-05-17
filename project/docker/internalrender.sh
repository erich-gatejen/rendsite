#!/bin/sh

java -server -Xms64m -Xmx512m \
    -classpath /rendsite/lib/rendsite.jar:/rendsite/lib/rendsitesystem.jar:/rendsite/lib \
    rendsite.commands.Render /rendsite \
    /input /output $1 $2 $3 $4 $5 $6 $7 $8 $9
