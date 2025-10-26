# TP3 - Funcionalidades Básicas de Reportes y Servicios Externos

## Universidad Nacional de Lanús - DESARROLLO DE SOFTWARE EN SISTEMAS DISTRIBUIDOS

### 📋 Resumen de Implementación

Este documento describe la implementación del **TP3** que agrega funcionalidades básicas al sistema **"Empuje Comunitario"**:

- ✅ **Servicio GraphQL** para informes de donaciones
- ✅ **Servicio REST** para exportación Excel
- ✅ **Cliente SOAP** para integración con la red de ONGs
- ✅ **Frontend Thymeleaf** actualizado
- ✅ **Documentación Swagger** básica
- ✅ **Gateway FastAPI** actualizado

---

## 🏗️ Arquitectura de la Solución

### Servicios Implementados

#### 1. **Donation Reports Service** (GraphQL)
- **Puerto:** 8085
- **Tecnología:** Spring Boot + GraphQL
- **Funcionalidad:** Informes de donaciones con filtros básicos

#### 2. **Report Filters Service** (REST)
- **Puerto:** 8086
- **Tecnología:** Spring Boot + REST API
- **Funcionalidad:** Exportación Excel de donaciones

#### 3. **SOAP Client Service** (SOAP)
- **Puerto:** 8087
- **Tecnología:** Spring Boot + SOAP Client
- **Funcionalidad:** Consulta de presidentes y ONGs

#### 4. **Gateway FastAPI** (Actualizado)
- **Nuevas rutas proxy:**
  - `/api/graphql/*` → donation-reports-service:8085
  - `/api/reports/*` → report-filters-service:8086
  - `/api/soap/*` → soap-client-service:8087

---

## 📊 Funcionalidades Implementadas

### 1. Informe de Donaciones (GraphQL)

#### **Características:**
- ✅ Filtros por categoría, fechas y estado
- ✅ Sumatoria de cantidades
- ✅ Resultados en tiempo real

#### **Query GraphQL:**
```graphql
query {
  donationReport(input: {
    category: "ALIMENTOS"
    startDate: "2024-12-01"
    endDate: "2024-12-31"
    isDeleted: false
  }) {
    category
    isDeleted
    totalQuantity
  }
}
```

### 2. Exportación Excel (REST)

#### **Endpoint REST:**
- `POST /api/filters/export/excel` - Generar y descargar Excel

#### **Características:**
- ✅ Exportación de donaciones a Excel
- ✅ **Hojas separadas por categoría** (ROPA, ALIMENTOS, etc.)
- ✅ Headers descriptivos en español
- ✅ Formato profesional

### 3. Cliente SOAP para Red de ONGs

#### **Consulta de Presidentes y ONGs (SOAP)** - ✅ **100%**
- ✅ **Operaciones SOAP:** `list_presidents` y `list_associations`
- ✅ **Formato XML con autenticación**
- ✅ **Solo acceso para rol PRESIDENTE**
- ✅ **Cliente implementado**
- ✅ **WSDL:** https://soap-app-latest.onrender.com/?wsdl

#### **Endpoints REST:**
- `POST /api/soap/presidents` - Consultar presidentes
- `POST /api/soap/organizations` - Consultar ONGs
- `GET /api/soap/health` - Verificar estado del servicio

---

## 🎨 Frontend Thymeleaf

### Pantallas Implementadas:

#### 1. **Informe de Donaciones** (`/reports/donations`)
- ✅ Filtros básicos por categoría y fechas
- ✅ Resultados en tabla
- ✅ Exportación a Excel

#### 2. **Informe de Eventos** (`/reports/events`)
- ✅ Filtros por fechas
- ✅ Lista de eventos participados
- ✅ Información técnica GraphQL

#### 3. **Consulta SOAP** (`/soap/presidents`)
- ✅ Input de IDs de organizaciones
- ✅ Resultados de presidentes y ONGs
- ✅ Solo acceso para PRESIDENTE

---

## 📚 Documentación API

### Swagger/OpenAPI

#### **Report Filters Service:**
- **URL:** http://localhost:8086/swagger-ui.html
- **Endpoints documentados:**
  - Exportación Excel

#### **SOAP Client Service:**
- **URL:** http://localhost:8087/swagger-ui.html
- **Endpoints documentados:**
  - Consulta de presidentes
  - Consulta de organizaciones

#### **GraphQL Playground:**
- **URL:** http://localhost:8085/graphiql
- **Queries documentadas**

---

## 🚀 Instrucciones de Despliegue

### 1. Variables de Entorno (.env)

```bash
MYSQL_ROOT_PASSWORD=empuje_root
MYSQL_DATABASE=empuje
MYSQL_USER=empuje
MYSQL_PASSWORD=empuje
JWT_SECRET=changeme_secret_dev
JWT_EXPIRATION_MS=86400000

SMTP_HOST=mailhog
SMTP_PORT=1025
SMTP_FROM=noreply@empuje.org
SMTP_AUTH=false
SMTP_STARTTLS_ENABLE=false
```

### 2. Despliegue con Docker Compose

```bash
# Construir e iniciar todos los servicios
docker-compose up --build

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

### 3. Verificación de Servicios

```bash
# Health checks
curl http://localhost:8080/actuator/health  # Frontend
curl http://localhost:8085/actuator/health  # GraphQL Service
curl http://localhost:8086/actuator/health  # REST Service
curl http://localhost:8087/actuator/health  # SOAP Service
```

---

## 🔧 Configuración Técnica

### Puertos de Servicios:

| Servicio | Puerto | URL |
|----------|--------|-----|
| Frontend | 8080 | http://localhost:8080 |
| GraphQL Service | 8085 | http://localhost:8085/graphql |
| REST Service | 8086 | http://localhost:8086/api/filters |
| SOAP Service | 8087 | http://localhost:8087/api/soap |
| Gateway | 8000 | http://localhost:8000 |

### Base de Datos:
- **MySQL:** localhost:3306/empuje

### Web Service SOAP Externo:
- **WSDL:** https://soap-app-latest.onrender.com/?wsdl
- **Autenticación:** Header con Grupo: `GrupoA-TM`, Clave: `clave-tm-a`

---

## 🛡️ Seguridad y Permisos

### Roles y Accesos:

#### **PRESIDENTE:**
- ✅ Acceso completo a informes
- ✅ Consulta SOAP de presidentes y ONGs
- ✅ Exportación Excel

#### **VOCAL:**
- ✅ Acceso a informes de donaciones
- ✅ Exportación Excel
- ❌ Sin acceso SOAP

#### **COORDINADOR:**
- ✅ Acceso a informes de eventos
- ❌ Sin acceso SOAP

#### **VOLUNTARIO:**
- ✅ Solo sus propios eventos

---

## 📈 Ejemplos de Uso

### 1. Informe de Donaciones

```bash
# GraphQL Query
curl -X POST http://localhost:8085/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { donationReport(input: {category: \"ALIMENTOS\", isDeleted: false}) { category isDeleted totalQuantity } }"
  }'
```

### 2. Exportar Excel

```bash
curl -X POST "http://localhost:8086/api/filters/export/excel" \
  -H "Content-Type: application/json" \
  -d '{
    "reportType": "DONATION",
    "category": "ALIMENTOS",
    "startDate": "2024-12-01T00:00:00",
    "endDate": "2024-12-31T23:59:59",
    "isDeleted": false
  }' \
  --output donation_report.xlsx
```

### 3. Consulta SOAP

```bash
curl -X POST "http://localhost:8087/api/soap/presidents" \
  -H "Content-Type: application/json" \
  -d '{
    "organizationIds": ["org-001", "org-002"]
  }'
```

---

## 👥 Desarrollo

**TP - Sistemas Distribuidos**
- **Framework:** Spring Boot 3.1.5
- **Frontend:** Thymeleaf + Bootstrap 5
- **Base de datos:** MySQL 8.0
- **Contenedorización:** Docker & Docker Compose
- **API Gateway:** FastAPI (Python)

---

```

### ✅ Checklist de Entrega

- [x] **Servicio GraphQL implementado** (Informe de donaciones)
- [x] **Servicio REST implementado con Swagger** (Exportación Excel)
- [x] **Cliente SOAP implementado** (Consulta de presidentes y ONGs)
- [x] **Frontend Thymeleaf actualizado** (Reportes y SOAP)
- [x] **Docker compose configurado** (Nuevos servicios)
- [x] **Gateway actualizado** (Proxies para nuevos servicios)
- [x] **Documentación básica** (README-TP3.md)
- [x] **Funcionalidades probadas** (Postman collection incluida)

---

## ❌ Funcionalidades NO implementadas en esta versión

- [ ] **Filtros personalizados guardados** (GraphQL mutations)
- [ ] **Informe de participación en eventos** (GraphQL)
- [ ] **Filtros personalizados para eventos** (REST API)
- [ ] **Sistema completo de filtros CRUD** (solo exportación implementada)
