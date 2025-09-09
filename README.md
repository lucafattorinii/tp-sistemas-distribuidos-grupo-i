# ONG Empuje Comunitario - Sistema de Gestión

Sistema distribuido para la gestión de donaciones y eventos solidarios de la ONG Empuje Comunitario.

## Arquitectura

El sistema está compuesto por los siguientes componentes:

- **Frontend**: Aplicación web desarrollada con Thymeleaf
- **API Gateway**: Desarrollado en FastAPI (Python)
- **Servicios gRPC**:
  - User Service: Gestión de usuarios y autenticación
  - Inventory Service: Gestión del inventario de donaciones
  - Event Service: Gestión de eventos solidarios
- **Base de datos**: MySQL

## Requisitos

- Docker y Docker Compose
- Java 17+
- Python 3.8+
- Node.js (opcional, para herramientas de desarrollo)

## Estructura del Proyecto

```
tp-empuje-comunitario/
├── proto/                  # Archivos .proto para gRPC
├── gateway-fastapi/        # API Gateway en FastAPI
├── frontend-thymeleaf/     # Interfaz de usuario
├── user-service/           # Servicio de usuarios
├── inventory-service/      # Servicio de inventario
├── event-service/         # Servicio de eventos
└── docker-compose.yml      # Configuración de Docker
```

## Configuración Inicial

1. Clonar el repositorio
2. Configurar las variables de entorno (ver .env.example)
3. Ejecutar `docker-compose up --build`

## Desarrollo

### Generación de stubs gRPC

```bash
# Desde el directorio del proyecto
./scripts/generate_protos.sh
```

### Ejecución local

1. Iniciar la base de datos: `docker-compose up -d db`
2. Iniciar los servicios individualmente desde sus respectivos directorios

## Documentación de la API

La documentación de la API estará disponible en `http://localhost:8000/docs` una vez que el gateway esté en ejecución.

## Licencia

[MIT](LICENSE)
