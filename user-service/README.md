# User Service

Microservicio de gestión de usuarios para el proyecto Empuje Comunitario.

## Requisitos

- Java 17 o superior
- Maven 3.8.6 o superior
- MySQL 8.0 o superior

## Configuración

1. Clonar el repositorio:
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd user-service
   ```

2. Configurar la base de datos:
   - Crear una base de datos MySQL llamada `empuje_comunitario`
   - Configurar las credenciales en `src/main/resources/application.properties`

3. Configurar el correo electrónico (opcional):
   - Actualizar la configuración de correo en `application.properties`

## Construir y ejecutar

### Usando Maven Wrapper (recomendado):

**Windows:**
```bash
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
chmod +x mvnw
./mvnw clean install
./mvnw spring-boot:run
```

### Usando Maven instalado:
```bash
mvn clean install
mvn spring-boot:run
```

## Estructura del proyecto

```
user-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/empuje/userservice/
│   │   │       ├── config/         # Configuraciones de Spring
│   │   │       ├── controller/     # Controladores REST
│   │   │       ├── dto/           # Objetos de transferencia de datos
│   │   │       ├── exception/     # Manejo de excepciones
│   │   │       ├── model/         # Entidades JPA
│   │   │       ├── repository/    # Repositorios de datos
│   │   │       ├── security/     # Configuración de seguridad
│   │   │       ├── service/      # Lógica de negocio
│   │   │       └── UserServiceApplication.java
│   │   └── resources/
│   │       ├── static/           # Recursos estáticos
│   │       ├── templates/        # Plantillas (si se usan)
│   │       └── application.properties
│   └── test/                     # Pruebas unitarias e integración
└── pom.xml
```

## Endpoints

### Autenticación
- `POST /api/auth/signin` - Iniciar sesión
- `POST /api/auth/signup` - Registrar nuevo usuario
- `GET /api/auth/refresh` - Refrescar token JWT

### Usuarios
- `GET /api/users` - Listar usuarios (requiere autenticación)
- `GET /api/users/{id}` - Obtener usuario por ID
- `PUT /api/users/{id}` - Actualizar usuario
- `DELETE /api/users/{id}` - Eliminar usuario

## Variables de entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `SPRING_DATASOURCE_URL` | URL de la base de datos | `jdbc:mysql://localhost:3306/empuje_comunitario` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `password` |
| `JWT_SECRET` | Clave secreta para JWT | `empujeSecretKey` |
| `JWT_EXPIRATION` | Tiempo de expiración del token en ms | `86400000` (24h) |

## Despliegue

### Docker

1. Construir la imagen:
   ```bash
   docker build -t empuje-user-service .
   ```

2. Ejecutar el contenedor:
   ```bash
   docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/empuje_comunitario empuje-user-service
   ```

## Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.
