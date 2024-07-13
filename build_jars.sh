#!/bin/bash

# Define the destination directory (use forward slashes for Git Bash compatibility)
DESTINATION_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"/compiled_jars
SUFFIX_PATTERN="-[0-9]*.[0-9]*.[0-9]*-SNAPSHOT"

# List of directories to skip
SKIP_DIRS=("compiled_jars/" "common/")

# Function to check if a directory is in the skip list
should_skip() {
    local dir=$1
    for skip in "${SKIP_DIRS[@]}"; do
        if [[ "$dir" == "$skip" ]]; then
            return 0  # True, should skip
        fi
    done
    return 1  # False, should not skip
}

# Ensure the destination directory exists
mkdir -p "$DESTINATION_DIR"

# Run mvn package
./generate_https_certs.sh
mvn package -DskipTests -Pproduction

# Traverse each subdirectory and copy jar
for dir in */ ; do
    if [ -d "$dir" ] && ! should_skip "$dir"; then
        echo "Entering directory: $dir"
        cd "$dir" || exit
        
        # Find the resulting JAR files and copy them with a new prefix to the destination directory
        find . -name "*.jar" | while read -r jar; do
            basejar=$(basename "$jar")
            # Remove the suffix pattern from the base name
            newname="${basejar%%$SUFFIX_PATTERN.jar}.jar"
            cp "$jar" "$DESTINATION_DIR/$newname"
        done

        # Return to the parent directory
        cd ..
    fi
done

echo "Packaging and copying complete!"

