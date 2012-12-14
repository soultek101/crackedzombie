#!/bin/bash

MCVERSION=1.4.5
FORGEVERSION=430

MCCLIENTDIR="$HOME/minecraft-$MCVERSION-forge-$FORGEVERSION/mods"
MCSERVERDIR="$HOME/mc-server-forge-$FORGEVERSION/mods"
JARDIR="$HOME/projects/walkingdead-src-1.4.x"
JAR="walkingdead-$MCVERSION-forge-$FORGEVERSION.jar"

cp -f "$JARDIR/$JAR" $MCCLIENTDIR
cp -f "$JARDIR/$JAR" $MCSERVERDIR
