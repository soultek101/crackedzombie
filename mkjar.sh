#!/bin/bash

echo "== Creating jars =="

MCVERSION=1.4.5
FORGEVERSION=430

DATE=`date +%Y%m%d`

MCMODDATA="
[\n
{\n
  \"modid\": \"WalkingDeadMod\",\n
  \"name\": \"WalkingDead Mod\",\n
  \"description\": \"Walking Dead mod adds monitor lizards, turtles, iguanas, chameleons, and crocodiles. Komodo Dragons! Man-eating Crocodiles! And cute little turtles.\",\n
  \"version\": \"$DATE\",\n
  \"mcversion\": \"$MCVERSION\",\n
  \"url\": \"http://www.minecraftforum.net/topic/585469-132modloader-crackedeggs-mods-reptiles-parachute-updated-08272012/\",\n
  \"updateUrl\": \"\",\n
  \"authors\": [\n
    \"crackedEgg\"\n
  ],\n
  \"credits\": \"Authored by crackedEgg\",\n
  \"logoFile\": \"/logo/deadLogo.png\",\n
  \"screenshots\": [],\n
  \"parent\": \"\",\n
  \"dependencies\": []\n
}\n
]"

echo "> copying files"

REOBF="reobf/minecraft/"
cd $REOBF

RDIR="$HOME/projects/walkingdead-src-1.4.x"

rm -f $RDIR/walkingDead/common/*.class
rm -f $RDIR/walkingDead/client/*.class

cp -R walkingDead/ $RDIR
echo -e $MCMODDATA > $RDIR/mcmod.info

echo "> making mod jar file"

cd $RDIR

JAR="walkingdead-$MCVERSION-forge-$FORGEVERSION.jar"

rm -f $JAR
jar -cf $JAR walkingDead/  mcmod.info mob/ sound/ logo/

echo " - Mod build complete"

