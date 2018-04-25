#!/bin/bash

set -euo pipefail

RELEASEDIR=$(mktemp -d)
trap 'rm -r "$RELEASEDIR"' EXIT

./node_modules/.bin/shadow-cljs release script
cp -r package.json out "$RELEASEDIR"
cd "$RELEASEDIR" && npm publish
