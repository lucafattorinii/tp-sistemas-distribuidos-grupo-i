import os
from grpc_tools import protoc

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
THIS = os.path.abspath(os.path.dirname(__file__))

OUT_DIR = os.path.join(THIS, 'pb')
os.makedirs(OUT_DIR, exist_ok=True)

protos = [
    os.path.join(ROOT, 'user-service', 'src', 'main', 'proto', 'user_service.proto'),
    os.path.join(ROOT, 'inventory-service', 'src', 'main', 'proto', 'inventory_service.proto'),
    os.path.join(ROOT, 'event-service', 'src', 'main', 'proto', 'event_service.proto'),
]

# include paths: project proto dirs + system proto paths if needed
include_paths = [
    os.path.join(ROOT, 'user-service', 'src', 'main', 'proto'),
    os.path.join(ROOT, 'inventory-service', 'src', 'main', 'proto'),
    os.path.join(ROOT, 'event-service', 'src', 'main', 'proto'),
]

common_args = [
    'grpc_tools.protoc',
    *(f'-I{p}' for p in include_paths),
    f'--python_out={OUT_DIR}',
    f'--grpc_python_out={OUT_DIR}',
]

for proto in protos:
    args = common_args + [proto]
    print("Generating:", proto)
    if protoc.main(args) != 0:
        raise SystemExit(f'Failed to generate stubs for {proto}')

print('gRPC Python stubs generated into', OUT_DIR)
