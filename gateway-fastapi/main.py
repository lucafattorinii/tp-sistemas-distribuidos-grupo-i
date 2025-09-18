from fastapi import FastAPI, HTTPException, Depends, Header
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from pydantic import BaseModel
import grpc
import os
import jwt

# Attempt to import generated stubs
try:
    from pb import user_service_pb2_grpc as user_grpc
    from pb import user_service_pb2 as user_pb
except Exception:
    user_grpc = None
    user_pb = None

try:
    from pb import inventory_service_pb2_grpc as inv_grpc
    from pb import inventory_service_pb2 as inv_pb
except Exception:
    inv_grpc = None
    inv_pb = None

try:
    from pb import event_service_pb2_grpc as evt_grpc
    from pb import event_service_pb2 as evt_pb
except Exception:
    evt_grpc = None
    evt_pb = None

USER_SERVICE_ADDR = os.getenv("USER_SERVICE_URL", "localhost:50051")
INVENTORY_SERVICE_ADDR = os.getenv("INVENTORY_SERVICE_URL", "localhost:50052")
EVENT_SERVICE_ADDR = os.getenv("EVENT_SERVICE_URL", "localhost:50053")
JWT_SECRET = os.getenv("JWT_SECRET", "your_jwt_secret_key_here")

app = FastAPI(title="Gateway FastAPI - Empuje Comunitario")
auth_scheme = HTTPBearer(auto_error=False)


class LoginIn(BaseModel):
    username: str
    password: str


def require_auth(cred: HTTPAuthorizationCredentials = Depends(auth_scheme)):
    if not cred:
        raise HTTPException(status_code=401, detail="Missing Authorization header")
    token = cred.credentials
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=["HS256"])
        return payload
    except jwt.PyJWTError:
        raise HTTPException(status_code=401, detail="Invalid token")


def require_role(required_roles: list[str]):
    def checker(payload=Depends(require_auth)):
        role = payload.get("role")
        if role not in required_roles:
            raise HTTPException(status_code=403, detail="Forbidden: insufficient role")
        return payload
    return checker


@app.get("/")
def root():
    return {"status": "ok", "service": "gateway-fastapi"}


@app.get("/health")
def health():
    return {"status": "healthy"}


@app.post("/auth/login")
def login(payload: LoginIn):
    if not payload.username or not payload.password:
        raise HTTPException(status_code=400, detail="Username and password required")

    # Use gRPC if stubs available; otherwise fallback mock (dev only)
    if user_grpc and user_pb:
        try:
            with grpc.insecure_channel(USER_SERVICE_ADDR) as channel:
                stub = user_grpc.UserServiceStub(channel)
                req = user_pb.LoginRequest(username=payload.username, password=payload.password)
                resp = stub.Login(req)
                # Embed role in JWT from user response if present
                role = "VOLUNTARIO"
                try:
                    role = resp.user.role.name or role
                except Exception:
                    pass
                token = jwt.encode({"sub": resp.user.username, "uid": resp.user.id, "role": role}, JWT_SECRET, algorithm="HS256")
                return {"jwt": token, "user": {"id": resp.user.id, "username": resp.user.username, "role": role}}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.UNAUTHENTICATED:
                raise HTTPException(status_code=401, detail="Invalid credentials")
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


# Volunteer self-service participation endpoints
@app.post("/events/{event_id}/participate")
def events_participate(event_id: int, payload=Depends(require_auth)):
    if not (evt_grpc and evt_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    user_id = payload.get("uid")
    if not user_id:
        raise HTTPException(status_code=400, detail="Token missing uid")
    with grpc.insecure_channel(EVENT_SERVICE_ADDR) as channel:
        stub = evt_grpc.EventServiceStub(channel)
        try:
            resp = stub.AssignMember(evt_pb.AssignMemberRequest(event_id=event_id, user_id=int(user_id), assigned_by=int(user_id)))
            return {"id": resp.id}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="Event not found")
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


@app.post("/events/{event_id}/leave")
def events_leave(event_id: int, payload=Depends(require_auth)):
    if not (evt_grpc and evt_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    user_id = payload.get("uid")
    if not user_id:
        raise HTTPException(status_code=400, detail="Token missing uid")
    with grpc.insecure_channel(EVENT_SERVICE_ADDR) as channel:
        stub = evt_grpc.EventServiceStub(channel)
        try:
            resp = stub.RemoveMember(evt_pb.RemoveMemberRequest(event_id=event_id, user_id=int(user_id), removed_by=int(user_id)))
            return {"id": resp.id}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="Event not found")
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


@app.get("/users/{user_id}", dependencies=[Depends(require_role(["PRESIDENTE"]))])
def get_user(user_id: int):
    if not (user_grpc and user_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(USER_SERVICE_ADDR) as channel:
        stub = user_grpc.UserServiceStub(channel)
        try:
            resp = stub.GetUser(user_pb.GetUserRequest(id=user_id))
            return {"id": resp.id, "username": resp.username, "email": resp.email}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="User not found")
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


@app.put("/users/{user_id}", dependencies=[Depends(require_role(["PRESIDENTE"]))])
def update_user(user_id: int, data: dict):
    if not (user_grpc and user_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(USER_SERVICE_ADDR) as channel:
        stub = user_grpc.UserServiceStub(channel)
        req = user_pb.UpdateUserRequest(
            id=user_id,
            username=data.get("username", ""),
            email=data.get("email", ""),
            password=data.get("password", ""),
            role=data.get("role", ""),
            first_name=data.get("first_name", ""),
            last_name=data.get("last_name", ""),
            phone=data.get("phone", ""),
            address=data.get("address", ""),
            is_active=bool(data.get("is_active", True))
        )
        try:
            resp = stub.UpdateUser(req)
            return {"id": resp.id, "username": resp.username, "email": resp.email}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="User not found")
            if code == grpc.StatusCode.INVALID_ARGUMENT:
                raise HTTPException(status_code=400, detail=e.details())
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


@app.delete("/users/{user_id}", dependencies=[Depends(require_role(["PRESIDENTE"]))])
def delete_user(user_id: int):
    if not (user_grpc and user_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(USER_SERVICE_ADDR) as channel:
        stub = user_grpc.UserServiceStub(channel)
        try:
            stub.DeleteUser(user_pb.DeleteUserRequest(id=user_id))
            return {"success": True}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="User not found")
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")

    # Fallback mock (only if stubs not present) to allow UI testing
    token = jwt.encode({"sub": payload.username, "uid": 1, "role": "PRESIDENTE"}, JWT_SECRET, algorithm="HS256")
    return {"jwt": token, "user": {"id": 1, "username": payload.username, "role": "PRESIDENTE"}}


# Users endpoints (PRESIDENTE)
@app.get("/users", dependencies=[Depends(require_role(["PRESIDENTE"]))])
def list_users(page: int = 1, size: int = 10):
    if not (user_grpc and user_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(USER_SERVICE_ADDR) as channel:
        stub = user_grpc.UserServiceStub(channel)
        req = user_pb.ListUsersRequest(page=page, size=size)
        users = []
        try:
            for resp in stub.ListUsers(req):
                users.append({"id": resp.id, "username": resp.username, "email": resp.email})
        except grpc.RpcError as e:
            raise HTTPException(status_code=502, detail=f"gRPC error: {e.code().name}")
        return {"users": users}


@app.post("/users", dependencies=[Depends(require_role(["PRESIDENTE"]))])
def create_user(data: dict):
    if not (user_grpc and user_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(USER_SERVICE_ADDR) as channel:
        stub = user_grpc.UserServiceStub(channel)
        req = user_pb.CreateUserRequest(
            username=data.get("username", ""),
            email=data.get("email", ""),
            password=data.get("password", ""),
            role=data.get("role", "VOLUNTARIO"),
            first_name=data.get("first_name", ""),
            last_name=data.get("last_name", ""),
            phone=data.get("phone", ""),
            address=data.get("address", ""),
            profile_image=data.get("profile_image", ""),
        )
        try:
            resp = stub.CreateUser(req)
            return {"id": resp.id}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.INVALID_ARGUMENT:
                raise HTTPException(status_code=400, detail=e.details())
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


# Inventory endpoints (PRESIDENTE, VOCAL)
def require_inventory_role(payload=Depends(require_auth)):
    if payload.get("role") not in ("PRESIDENTE", "VOCAL"):
        raise HTTPException(status_code=403, detail="Forbidden: requires PRESIDENTE or VOCAL")
    return payload


@app.get("/inventory", dependencies=[Depends(require_inventory_role)])
def inventory_list():
    if not (inv_grpc and inv_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(INVENTORY_SERVICE_ADDR) as channel:
        stub = inv_grpc.InventoryServiceStub(channel)
        items = []
        try:
            for resp in stub.ListItems(inv_pb.ListItemsRequest()):
                items.append({"id": resp.id, "category": resp.category.name, "description": resp.description, "quantity": resp.quantity})
        except grpc.RpcError as e:
            raise HTTPException(status_code=502, detail=f"gRPC error: {e.code().name}")
        return {"items": items}


@app.post("/inventory", dependencies=[Depends(require_inventory_role)])
def inventory_add(data: dict):
    if not (inv_grpc and inv_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(INVENTORY_SERVICE_ADDR) as channel:
        stub = inv_grpc.InventoryServiceStub(channel)
        cat_name = (data.get("category") or "CATEGORY_UNKNOWN").upper()
        try:
            cat = getattr(inv_pb.Category, cat_name)
        except Exception:
            cat = inv_pb.Category.CATEGORY_UNKNOWN
        req = inv_pb.AddItemRequest(category=cat, description=data.get("description", ""), quantity=int(data.get("quantity", 0)))
        try:
            resp = stub.AddItem(req)
            return {"id": resp.id}
        except grpc.RpcError as e:
            raise HTTPException(status_code=400 if e.code()==grpc.StatusCode.INVALID_ARGUMENT else 502, detail=e.details())


@app.put("/inventory/{item_id}", dependencies=[Depends(require_inventory_role)])
def inventory_update(item_id: int, data: dict):
    if not (inv_grpc and inv_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(INVENTORY_SERVICE_ADDR) as channel:
        stub = inv_grpc.InventoryServiceStub(channel)
        req = inv_pb.UpdateItemRequest(id=item_id, description=data.get("description",""), quantity=int(data.get("quantity", -1)))
        try:
            resp = stub.UpdateItem(req)
            return {"id": resp.id, "quantity": resp.quantity}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="Item not found")
            if code == grpc.StatusCode.INVALID_ARGUMENT:
                raise HTTPException(status_code=400, detail=e.details())
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


@app.delete("/inventory/{item_id}", dependencies=[Depends(require_inventory_role)])
def inventory_delete(item_id: int):
    if not (inv_grpc and inv_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(INVENTORY_SERVICE_ADDR) as channel:
        stub = inv_grpc.InventoryServiceStub(channel)
        try:
            resp = stub.DeleteItem(inv_pb.DeleteItemRequest(id=item_id))
            return {"success": resp.success}
        except grpc.RpcError as e:
            raise HTTPException(status_code=502, detail=f"gRPC error: {e.code().name}")


@app.post("/inventory/{item_id}/adjust", dependencies=[Depends(require_inventory_role)])
def inventory_adjust(item_id: int, data: dict):
    if not (inv_grpc and inv_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    delta = int(data.get("delta", 0))
    with grpc.insecure_channel(INVENTORY_SERVICE_ADDR) as channel:
        stub = inv_grpc.InventoryServiceStub(channel)
        try:
            resp = stub.AdjustQuantity(inv_pb.AdjustQtyRequest(id=item_id, delta=delta))
            return {"id": resp.id, "quantity": resp.quantity}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="Item not found")
            if code == grpc.StatusCode.FAILED_PRECONDITION:
                raise HTTPException(status_code=412, detail=e.details())
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


# Event endpoints
def require_event_role(payload=Depends(require_auth)):
    if payload.get("role") not in ("PRESIDENTE", "COORDINADOR"):
        raise HTTPException(status_code=403, detail="Forbidden: requires PRESIDENTE or COORDINADOR")
    return payload


@app.get("/events", dependencies=[Depends(require_auth)])
def events_list():
    if not (evt_grpc and evt_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(EVENT_SERVICE_ADDR) as channel:
        stub = evt_grpc.EventServiceStub(channel)
        events = []
        try:
            for resp in stub.ListEvents(evt_pb.ListEventsRequest()):
                events.append({"id": resp.id, "name": resp.name})
        except grpc.RpcError as e:
            raise HTTPException(status_code=502, detail=f"gRPC error: {e.code().name}")
        return {"events": events}


@app.post("/events", dependencies=[Depends(require_event_role)])
def events_create(data: dict):
    if not (evt_grpc and evt_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    # Expect ISO datetime in data["event_datetime"]
    from datetime import datetime, timezone
    iso = data.get("event_datetime")
    if not iso:
        raise HTTPException(status_code=400, detail="event_datetime is required (ISO)")
    dt = datetime.fromisoformat(iso)
    ts = evt_pb.google_dot_protobuf_dot_timestamp__pb2.Timestamp(seconds=int(dt.replace(tzinfo=timezone.utc).timestamp()))
    with grpc.insecure_channel(EVENT_SERVICE_ADDR) as channel:
        stub = evt_grpc.EventServiceStub(channel)
        req = evt_pb.CreateEventRequest(name=data.get("name",""), description=data.get("description",""), event_datetime=ts)
        try:
            resp = stub.CreateEvent(req)
            return {"id": resp.id}
        except grpc.RpcError as e:
            raise HTTPException(status_code=400 if e.code()==grpc.StatusCode.INVALID_ARGUMENT else 502, detail=e.details())


@app.put("/events/{event_id}", dependencies=[Depends(require_event_role)])
def events_update(event_id: int, data: dict):
    if not (evt_grpc and evt_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    from datetime import datetime, timezone
    ts = None
    if data.get("event_datetime"):
        dt = datetime.fromisoformat(data["event_datetime"])
        ts = evt_pb.google_dot_protobuf_dot_timestamp__pb2.Timestamp(seconds=int(dt.replace(tzinfo=timezone.utc).timestamp()))
    with grpc.insecure_channel(EVENT_SERVICE_ADDR) as channel:
        stub = evt_grpc.EventServiceStub(channel)
        req = evt_pb.UpdateEventRequest(id=event_id, name=data.get("name",""), description=data.get("description",""))
        if ts:
            req.event_datetime.CopyFrom(ts)
        try:
            resp = stub.UpdateEvent(req)
            return {"id": resp.id, "name": resp.name}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="Event not found")
            if code == grpc.StatusCode.INVALID_ARGUMENT:
                raise HTTPException(status_code=400, detail=e.details())
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


@app.delete("/events/{event_id}", dependencies=[Depends(require_event_role)])
def events_delete(event_id: int):
    if not (evt_grpc and evt_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    with grpc.insecure_channel(EVENT_SERVICE_ADDR) as channel:
        stub = evt_grpc.EventServiceStub(channel)
        try:
            resp = stub.DeleteEvent(evt_pb.DeleteEventRequest(id=event_id, requested_by=0))
            return {"success": resp.success, "message": resp.message}
        except grpc.RpcError as e:
            raise HTTPException(status_code=502, detail=f"gRPC error: {e.code().name}")


@app.post("/events/{event_id}/assign", dependencies=[Depends(require_event_role)])
def events_assign(event_id: int, data: dict):
    if not (evt_grpc and evt_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    user_id = int(data.get("user_id", 0))
    with grpc.insecure_channel(EVENT_SERVICE_ADDR) as channel:
        stub = evt_grpc.EventServiceStub(channel)
        try:
            resp = stub.AssignMember(evt_pb.AssignMemberRequest(event_id=event_id, user_id=user_id, assigned_by=0))
            return {"id": resp.id}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="Event not found")
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")


@app.post("/events/{event_id}/remove", dependencies=[Depends(require_event_role)])
def events_remove(event_id: int, data: dict):
    if not (evt_grpc and evt_pb):
        raise HTTPException(status_code=501, detail="gRPC stubs not generated")
    user_id = int(data.get("user_id", 0))
    with grpc.insecure_channel(EVENT_SERVICE_ADDR) as channel:
        stub = evt_grpc.EventServiceStub(channel)
        try:
            resp = stub.RemoveMember(evt_pb.RemoveMemberRequest(event_id=event_id, user_id=user_id, removed_by=0))
            return {"id": resp.id}
        except grpc.RpcError as e:
            code = e.code()
            if code == grpc.StatusCode.NOT_FOUND:
                raise HTTPException(status_code=404, detail="Event not found")
            raise HTTPException(status_code=502, detail=f"gRPC error: {code.name}")
