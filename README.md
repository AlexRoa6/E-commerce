# Ecommerce API - Spring Boot

API REST para un sistema de ecommerce desarrollada con Spring Boot. Proyecto de aprendizaje para practicar arquitectura en capas, seguridad con JWT, y buenas prácticas en el desarrollo de APIs.

## Tecnologías

- **Java 25**
- **Spring Boot 4.0.1**
- **Spring Security** - Autenticación y autorización
- **JWT (JSON Web Tokens)** - Tokens de acceso
- **Spring Data JPA** - Persistencia de datos
- **MySQL** - Base de datos
- **Bean Validation** - Validación de datos
- **SpringDoc OpenAPI** - Documentación Swagger
- **Maven** - Gestión de dependencias

## Arquitectura

El proyecto sigue una arquitectura en capas:

```
com.alexr.ecommerce/
├── controller/     # Endpoints REST
├── service/        # Lógica de negocio
├── repository/     # Acceso a datos (JPA)
├── model/          # Entidades JPA
├── dto/            # Data Transfer Objects
├── mappers/        # Conversión Entity <-> DTO
├── config/         # Configuraciones (Security, JWT)
├── handler/        # Manejo global de excepciones
└── exception/      # Excepciones personalizadas
```

## Funcionalidades

- **Autenticación JWT**: Registro y login con tokens de acceso
- **CRUD de Productos**: Crear, leer, actualizar y eliminar productos
- **CRUD de Categorías**: Gestión de categorías de productos
- **Paginación**: Soporte para paginación en listados
- **Validación**: Validación de datos en DTOs y entidades
- **Manejo de errores**: Respuestas de error estandarizadas
- **Documentación API**: Swagger UI disponible

## Modelo de Datos

### Diagrama de Base de Datos
```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│   Usuario    │       │   Producto   │       │  Categoria   │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id           │       │ id           │       │ id           │
│ nombre       │       │ nombre       │──────>│ nombre       │
│ password     │       │ stock        │       └──────────────┘
│ rol          │       │ precio       │
└──────────────┘       │ descripcion  │
                       │ disponible   │
                       │ categoria_id │
                       └──────────────┘
```

### Usuario
| Campo    | Tipo   | Descripción                    |
|----------|--------|--------------------------------|
| id       | Long   | Identificador único            |
| nombre   | String | Nombre de usuario (único)      |
| password | String | Contraseña encriptada (BCrypt) |
| rol      | Enum   | ROLE_USUARIO o ROLE_ADMIN      |

### Producto
| Campo       | Tipo       | Descripción                      |
|-------------|------------|----------------------------------|
| id          | Long       | Identificador único              |
| nombre      | String     | Nombre del producto              |
| stock       | int        | Cantidad disponible              |
| precio      | BigDecimal | Precio del producto              |
| descripcion | String     | Descripción (opcional)           |
| disponible  | boolean    | Calculado automáticamente        |
| categoria   | Categoria  | Categoría asociada               |

### Categoria
| Campo  | Tipo   | Descripción             |
|--------|--------|-------------------------|
| id     | Long   | Identificador único     |
| nombre | String | Nombre de la categoría  |

## Endpoints

### Autenticación (públicos)

| Método | Endpoint            | Descripción            |
|--------|---------------------|------------------------|
| POST   | /api/auth/register  | Registrar nuevo usuario|
| POST   | /api/auth/login     | Iniciar sesión         |

### Productos (requieren autenticación)

| Método | Endpoint            | Descripción                |
|--------|---------------------|----------------------------|
| GET    | /api/productos      | Listar productos (paginado)|
| GET    | /api/productos/{id} | Obtener producto por ID    |
| POST   | /api/productos      | Crear producto             |
| PUT    | /api/productos/{id} | Actualizar producto        |
| DELETE | /api/productos/{id} | Eliminar producto          |

### Categorías (requieren autenticación)

| Método | Endpoint             | Descripción                 |
|--------|----------------------|-----------------------------|
| GET    | /api/categorias      | Listar categorías (paginado)|
| GET    | /api/categorias/{id} | Obtener categoría por ID    |
| POST   | /api/categorias      | Crear categoría             |
| PUT    | /api/categorias/{id} | Actualizar categoría        |
| DELETE | /api/categorias/{id} | Eliminar categoría          |

## Requisitos

- Java 25+
- Maven 3.8+
- MySQL 8.0+

## Configuración

### 1. Crear la base de datos

```sql
CREATE DATABASE ecommerce_db;
```

### 2. Configurar variables de entorno

```bash
# Linux/Mac
export DB_USERNAME=tu_usuario
export DB_PASSWORD=tu_contrasena
export JWT_SECRET=tu_clave_secreta_base64_minimo_32_caracteres

# Windows (CMD)
set DB_USERNAME=tu_usuario
set DB_PASSWORD=tu_contrasena
set JWT_SECRET=tu_clave_secreta_base64_minimo_32_caracteres

# Windows (PowerShell)
$env:DB_USERNAME="tu_usuario"
$env:DB_PASSWORD="tu_contrasena"
$env:JWT_SECRET="tu_clave_secreta_base64_minimo_32_caracteres"
```

### 3. Ejecutar la aplicación

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`

## Documentación API

Swagger UI disponible en: `http://localhost:8080/swagger-ui.html`

## Uso

### Registrar usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre": "usuario1", "password": "12345"}'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Crear producto (con token)

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {tu_token}" \
  -d '{
    "nombre": "Laptop",
    "stock": 10,
    "precio": 999.99,
    "descripcion": "Laptop gaming",
    "idCategoria": 1
  }'
```

## Lo que aprendí

Este proyecto me permitió practicar:

- Arquitectura en capas y separación de responsabilidades
- Implementación de autenticación JWT desde cero
- Configuración de Spring Security
- Validación de datos con Bean Validation
- Manejo centralizado de excepciones
- Paginación con Spring Data
- Documentación de APIs con Swagger/OpenAPI
- Buenas prácticas en desarrollo de APIs REST

## Posibles mejoras

- [ ] Implementar roles y permisos más granulares
- [ ] Agregar carrito de compras
- [ ] Implementar órdenes de compra
- [ ] Agregar imágenes a productos
- [ ] Implementar búsqueda y filtros avanzados
- [ ] Cache con Redis
- [ ] Dockerizar la aplicación

## Autor

Alex Roa

---

