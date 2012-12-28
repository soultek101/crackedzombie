#!/bin/bash

echo "== Creating jars =="

KEYPASS=ds1b8zs
STOREPASS=ds1b8zs

MCVERSION=1.4.6
FORGEVERSION=471

DATE=`date +%Y%m%d`

MCMODDATA="
[\n
{\n
  \"modid\": \"WalkingDeadMod\",\n
  \"name\": \"WalkingDead Mod\",\n
  \"description\": \"Walking Dead mod adds zombies in the day time! Walkers they are called.\",\n
  \"version\": \"$DATE\",\n
  \"mcversion\": \"$MCVERSION\",\n
  \"url\": \"\",\n
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

rm -f $RDIR/walkingdead/common/*.class
rm -f $RDIR/walkingdead/client/*.class

cp -R walkingdead/ $RDIR
echo -e $MCMODDATA > $RDIR/mcmod.info

echo "> making mod jar file"

cd $RDIR

JAR="walkingdead-$MCVERSION-forge-$FORGEVERSION.jar"

echo -e "Main-Class: walkingdead.common.WalkingDead\nClass-Path: $JAR\n" > $RDIR/manifest.txt

rm -f $JAR
jar -cfm $JAR manifest.txt walkingdead/ mcmod.info logo/

echo "> signing $JAR"
jarsigner -keystore $HOME/.keystore -keypass $KEYPASS -storepass $STOREPASS $JAR cracked 

echo " - Mod build complete - `date "+%H:%M:%S"`" 

