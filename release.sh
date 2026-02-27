#!/usr/bin/env bash
set -e
if [[ -f  ~/.sdkman/bin/sdkman-init.sh ]]; then
  source ~/.sdkman/bin/sdkman-init.sh
fi
if command -v jdk17 >/dev/null 2>&1; then
  source jdk17
fi
version=$(java -version 2>&1 >/dev/null | grep 'version' | awk '{print $3}')
if [[ ! $version == *17.* ]]; then
  echo "Java version 17 required to release but was $version"
  exit 1
fi
artifactName=$(./gradlew -q properties | grep "^name:" | awk '{print $2}')
artifactVersion=$(./gradlew -q properties | grep "^version:" | awk '{print $2}')

if [[ "$artifactVersion" == *"-SNAPSHOT"* ]]; then
  echo "Detected Snapshot: $artifactVersion"
  ./gradlew clean publishToMavenLocal
  echo
  echo " ✅ $artifactName $artifactVersion published to maven local"
else
  echo "Detected Release: $artifactVersion"
  ./gradlew clean build release
  echo
  echo " ✅ $artifactName $artifactVersion uploaded and released"
  echo "See https://central.sonatype.com/publishing/deployments for more info"
fi

