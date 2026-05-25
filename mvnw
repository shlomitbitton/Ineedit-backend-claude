#!/bin/sh
# Maven wrapper script
set -e

MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"

if [ -f "$MAVEN_WRAPPER_PROPERTIES" ]; then
  DOWNLOAD_URL=$(grep distributionUrl "$MAVEN_WRAPPER_PROPERTIES" | cut -d= -f2)
fi

# If mvn is already installed, just use it
if command -v mvn > /dev/null 2>&1; then
  exec mvn "$@"
fi

# Otherwise download maven
MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-3.9.6"
MAVEN_BIN="$MAVEN_HOME/bin/mvn"

if [ ! -f "$MAVEN_BIN" ]; then
  echo "Downloading Maven 3.9.6..."
  mkdir -p "$MAVEN_HOME"
  curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.tar.gz" \
    | tar -xz -C "$HOME/.m2/wrapper/dists/" 
  mv "$HOME/.m2/wrapper/dists/apache-maven-3.9.6" "$MAVEN_HOME" 2>/dev/null || true
fi

exec "$MAVEN_BIN" "$@"
