# TP2 - Mensajería con Apache Kafka
## Sistema Distribuido para ONG Empuje Comunitario

Este proyecto implementa mensajería asíncrona usando Apache Kafka para comunicación inter-organizaciones según los requerimientos del TP2.

##  Inicio Rápido

### 1. Ejecutar el sistema completo
```bash
docker compose up --build -d
```

### 2. Verificar servicios
```bash
docker compose ps
```

### 3. URLs disponibles
- **Frontend**: http://localhost:8080
- **API Gateway**: http://localhost:8000
- **Servicio Mensajería Kafka**: http://localhost:8084
- **Kafka**: localhost:9092
- **MailHog**: http://localhost:8025

##  Servicios Implementados

### **Core Services (TP1)**
- **user-service** (puerto 50051) - Gestión de usuarios y eventos
- **inventory-service** (puerto 50052) - Gestión de inventario
- **messaging-service-kafka** (puerto 50054) - Mensajería y Kafka
- **gateway-fastapi** (puerto 8000) - API Gateway REST
- **frontend** (puerto 8080) - Interfaz web

### **Kafka Infrastructure (TP2)**
- **zookeeper** (puerto 2181) - Coordinación Kafka
- **kafka** (puerto 9092) - Broker de mensajes
- **messaging-service-kafka** (puerto 8084) - Servicio de mensajería


## Pruebas Básicas

### 1. Publicar Solicitud de Donaciones
```bash
curl -X POST http://localhost:8084/api/messaging/solicitud-donaciones \
  -d "organizationId=empuje-org-001" \
  -d "category=ALIMENTOS" \
  -d "description=Arroz" \
  -d "quantity=10kg"
```

### 2. Publicar Transferencia de Donaciones
```bash
curl -X POST http://localhost:8084/api/messaging/transferencia-donaciones/ong-destino-001 \
  -d "requestId=req-123" \
  -d "donorOrganizationId=empuje-org-001" \
  -d "category=ALIMENTOS" \
  -d "description=Arroz" \
  -d "quantity=5kg"
```

### 3. Publicar Evento Externo
```bash
curl -X POST http://localhost:8084/api/messaging/eventos-externos \
  -d "organizationId=org-externa" \
  -d "eventId=evt-789" \
  -d "eventName=Evento Externo" \
  -d "description=Evento de prueba" \
  -d "eventDateTime=2024-02-01T15:00:00"
```

### 4. Participar en Evento Externo
```bash
curl -X POST http://localhost:8084/api/messaging/adhesion-evento/org-externa \
  -d "eventId=evt-789" \
  -d "volunteerOrganizationId=empuje-org-001" \
  -d "volunteerId=user-123" \
  -d "volunteerName=Juan" \
  -d "volunteerLastName=Pérez" \
  -d "volunteerPhone=123456789" \
  -d "volunteerEmail=juan.perez@empuje.org"
```