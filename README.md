# Sistema de Gestión para ONG Empuje Comunitario

Sistema distribuido para la gestión de usuarios, inventario de donaciones y eventos solidarios de la ONG Empuje Comunitario.

## Primeros Pasos

### Configuración del Entorno

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/lucafattorinii/tp-sistemas-distribuidos-grupo-i.git
   cd tp-sistemas-distribuidos-grupo-i
   ```

2. **Configurar variables de entorno**
   - Copiar el archivo `.env.example` a `.env`
   - Generar contraseñas seguras con el script:
     ```bash
     python scripts/generate-secrets.py
     ```
   - Esto creará automáticamente un archivo `.env` con valores seguros

3. **Iniciar la aplicación**
   ```bash
   docker compose up --build
   ```

### Acceso
- **Frontend**: http://localhost:8080
- **API Gateway**: http://localhost:8000
- **Base de datos**: localhost:3306

### Usuarios de prueba
Se creará automáticamente un usuario administrador:
- **Email**: admin@empuje.org
- **Contraseña**: Ver logs de inicio (`docker compose logs user-service`)

## Seguridad

### Variables de Entorno
Nunca compartas tu archivo `.env` ni lo subas a control de versiones. El archivo `.env.example` muestra las variables necesarias sin valores sensibles.

### Generación de Secretos
El sistema utiliza los siguientes secretos:
- `JWT_SECRET`: Para firmar tokens JWT
- `MYSQL_ROOT_PASSWORD`: Contraseña del usuario root de MySQL
- `MYSQL_PASSWORD`: Contraseña del usuario de la aplicación

### Actualización de Secretos
Si necesitas regenerar los secretos:
1. Detén los contenedores: `docker compose down`
2. Elimina el archivo `.env`
3. Vuelve a generar los secretos: `python scripts/generate-secrets.py`
4. Reinicia la aplicación: `docker compose up --build`

## Arquitectura

- **Frontend**: `frontend-thymeleaf/` (Spring Boot + Thymeleaf)
- **API Gateway**: `gateway-fastapi/` (FastAPI - Python). Expone REST y mapea a gRPC
- **Servicios gRPC** (Java/Spring Boot):
  - `user-service/`: usuarios y autenticación (puerto gRPC 50051)
  - `inventory-service/`: inventario (puerto gRPC 50052)
  - `event-service/`: eventos (puerto gRPC 50053)
- **Base de datos**: MySQL (contenedor `db`)

## Requisitos

- Docker y Docker Compose
- Java 17+
- Python 3.10+ (Gateway)

## Estructura del Proyecto

```bash
.
├── gateway-fastapi/          # FastAPI REST → gRPC + JWT/roles
├── frontend-thymeleaf/       # UI (login, usuarios, inventario, eventos)
├── user-service/             # gRPC usuarios (Spring Boot + JPA + Flyway)
├── inventory-service/        # gRPC inventario (Spring Boot + JPA + Flyway)
├── event-service/            # gRPC eventos (Spring Boot + JPA + Flyway)
├── db/                       # SQL inicial de referencia
└── docker-compose.yml        # Orquestación
```

## Ejecución

### Opción A: Docker Compose (recomendado)

```bash
docker-compose up --build
# Frontend:   http://localhost:8080
# Gateway:    http://localhost:8000
# MySQL:      localhost:3306 (empuje / empuje)
```

### Opción B: Local (servicios Java + Gateway Python)

1) Base de datos
```bash
docker-compose up -d db
```

2) Compilar servicios Java (en cada módulo)
```bash
mvn -DskipTests package
```

3) Generar stubs y levantar Gateway
```bash
cd gateway-fastapi
python -m pip install -r requirements.txt
python generate_stubs.py
uvicorn main:app --host 0.0.0.0 --port 8000
```

4) Levantar Frontend
```bash
cd frontend-thymeleaf
mvn spring-boot:run
# http://localhost:8080
```

## Configuración Frontend

- `frontend-thymeleaf/src/main/resources/application.yml`: `app.gateway-url: http://localhost:8000`
- `frontend-thymeleaf/src/main/resources/application-docker.yml`: `app.gateway-url: http://gateway:8000`

## Endpoints del Gateway (REST)

- Autenticación
  - `POST /auth/login` → gRPC `UserService.Login` → `{ jwt, user }`
- Usuarios (rol: PRESIDENTE)
  - `GET /users?page=&size=`
  - `POST /users`
  - `GET /users/{id}`
  - `PUT /users/{id}`
  - `DELETE /users/{id}`
- Inventario (rol: PRESIDENTE o VOCAL)
  - `GET /inventory`
  - `POST /inventory`
  - `PUT /inventory/{id}`
  - `DELETE /inventory/{id}`
  - `POST /inventory/{id}/adjust` → `{ delta }`
- Eventos (autenticado; crear/editar/borrar/assign/remove: PRESIDENTE o COORDINADOR)
  - `GET /events`
  - `POST /events` (fecha futura)
  - `PUT /events/{id}`
  - `DELETE /events/{id}` (sólo eventos a futuro)
  - `POST /events/{id}/assign` → `{ user_id }`
  - `POST /events/{id}/remove` → `{ user_id }`

## Reglas y validaciones del TP

- **Roles**: PRESIDENTE, VOCAL, COORDINADOR, VOLUNTARIO
- **Usuarios**: alta con password encriptada (si no se envía, se genera)
- **Inventario**: categorías (ROPA, ALIMENTOS, JUGUETES, UTILES_ESCOLARES), baja lógica, cantidades ≥ 0
- **Eventos**: crear a futuro, baja física sólo a futuro, asignaciones controladas por rol; VOLUNTARIO se agrega/quita a sí mismo

## Pruebas rápidas (curl)

```bash
# Login
curl -s -X POST http://localhost:8000/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'

# Listar usuarios (con JWT)
JWT=... # pegar token
curl -s http://localhost:8000/users -H "Authorization: Bearer $JWT"

# Crear ítem de inventario
curl -s -X POST http://localhost:8000/inventory \
  -H 'Content-Type: application/json' -H "Authorization: Bearer $JWT" \
  -d '{"category":"ALIMENTOS","description":"Arroz","quantity":10}'

# Crear evento a futuro
curl -s -X POST http://localhost:8000/events \
  -H 'Content-Type: application/json' -H "Authorization: Bearer $JWT" \
  -d '{"name":"Visita escuela 99","description":"Juegos","event_datetime":"2025-12-01T15:00:00"}'
```

## Notas

- `gateway-fastapi/generate_stubs.py` genera stubs Python desde los `.proto` de cada servicio hacia `gateway-fastapi/pb/`.
- En `user-service` la clase `UserGrpcService.java` es de referencia y está comentada para que el IDE no marque errores. La implementación activa es `UserGrpcServiceImpl`.
- Si configurás correo en `user-service` (`spring.mail.*`), el alta puede enviar emails reales.
