#!/usr/bin/env bash
set -e
if [[ -f  ~/.sdkman/bin/sdkman-init.sh ]]; then
  source ~/.sdkman/bin/sdkman-init.sh
fi
if command -v jdk17 >/dev/null 2>&1; then
  source jdk17
fi
./gradlew clean build release
PROJECT=$(basename "$PWD")
echo "$PROJECT uploaded and released"
echo "see https://central.sonatype.com/publishing/deployments for more info"
# browse https://oss.sonatype.org &
