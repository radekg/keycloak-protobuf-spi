#!/usr/bin/env bash

NO_PROTOC_RUN="${NO_PROTOC_RUN:=false}"
if [ "$NO_PROTOC_RUN" = 'true' ]; then
  exit 0
fi

set -eu

base="$(cd $(dirname $0); pwd)"
genrootdir=$(mktemp -d)
protodir="$base/../spi-model/src/main/proto"
targetdir="$base/../gospi"

rm -rf "$targetdir"
mkdir -p "$targetdir"

protoc --go_out="plugins=grpc:$genrootdir" --proto_path="$protodir" -I "$protodir" "$protodir"/spi.proto

for sub in $(find "$protodir" -type d); do
    protoc --go_out="plugins=grpc:$genrootdir" --proto_path="$protodir" -I "$protodir" "$sub"/*.proto
done

pkgrealdir="$(find "$genrootdir" -type d -name gospi)"

if [ -z "$pkgrealdir" ]; then
    echo "Generated golang sources not found, can't continue."
    exit 1
fi

cp -R "$pkgrealdir"/* "$targetdir"

rm -rf "$genrootdir"