#!/bin/bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
USER_PROTO="$ROOT_DIR/user-service/src/main/proto/user_service.proto"
INVENTORY_PROTO="$ROOT_DIR/inventory-service/src/main/proto/inventory_service.proto"

echo "Generating gRPC stubs if protoc and plugins are available..."

# Java outputs
mkdir -p "$ROOT_DIR/user-service/src/main/java"
mkdir -p "$ROOT_DIR/inventory-service/src/main/java"

if command -v protoc >/dev/null 2>&1; then
  echo "protoc found"
  if [ -f "$USER_PROTO" ]; then
    protoc --java_out="$ROOT_DIR/user-service/src/main/java" --grpc-java_out="$ROOT_DIR/user-service/src/main/java" -I"$ROOT_DIR/user-service/src/main/proto" "$USER_PROTO"
  fi
  if [ -f "$INVENTORY_PROTO" ]; then
    protoc --java_out="$ROOT_DIR/inventory-service/src/main/java" --grpc-java_out="$ROOT_DIR/inventory-service/src/main/java" -I"$ROOT_DIR/inventory-service/src/main/proto" "$INVENTORY_PROTO"
  fi
else
  echo "protoc not found on PATH; skipping Java generation"
fi

# Python stubs for gateway-fastapi
if python3 -c "import grpc_tools.protoc" >/dev/null 2>&1; then
  echo "grpc_tools.protoc found"
  mkdir -p "$ROOT_DIR/gateway-fastapi/empuje"
  if [ -f "$USER_PROTO" ]; then
    python3 -m grpc_tools.protoc -I"$ROOT_DIR/user-service/src/main/proto" --python_out="$ROOT_DIR/gateway-fastapi" --grpc_python_out="$ROOT_DIR/gateway-fastapi" "$USER_PROTO"
  fi
  if [ -f "$INVENTORY_PROTO" ]; then
    python3 -m grpc_tools.protoc -I"$ROOT_DIR/inventory-service/src/main/proto" --python_out="$ROOT_DIR/gateway-fastapi" --grpc_python_out="$ROOT_DIR/gateway-fastapi" "$INVENTORY_PROTO"
  fi
else
  echo "grpc_tools.protoc not available in Python env; skipping Python generation"
fi

echo "Done."
