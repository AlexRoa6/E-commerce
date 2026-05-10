# Ecommerce API - Spring Boot

API REST para un sistema de ecommerce desarrollada con Spring Boot. Proyecto de aprendizaje para practicar arquitectura en capas, seguridad con JWT, y buenas prácticas en el desarrollo de APIs.

## Demo

- API: https://e-commerce-production-33eb.up.railway.app
- Swagger: https://e-commerce-production-33eb.up.railway.app/swagger-ui/index.html

## Tecnologias

- **Java 21**
- **Spring Boot 4.0.1**
- **Spring Security** - Autenticacion y autorizacion
- **JWT (JSON Web Tokens)** - Tokens de acceso
- **Spring Data JPA** - Persistencia de datos
- **MySQL** - Base de datos
- **Bean Validation** - Validacion de datos
- **SpringDoc OpenAPI** - Documentacion Swagger
- **Maven** - Gestion de dependencias
- **Docker** - Contenedorizacion
- **Railway** - Despliegue en la nube

## Arquitectura

El proyecto sigue una arquitectura en capas:

```
com.alexr.ecommerce/
├── controller/     # Endpoints REST
├── service/        # Logica de negocio
├── repository/     # Acceso a datos (JPA)
├── model/          # Entidades JPA
├── dto/            # Data Transfer Objects
├── mappers/        # Conversion Entity <-> DTO
├── config/         # Configuraciones (Security, JWT)
├── handler/        # Manejo global de excepciones
└── exception/      # Excepciones personalizadas
```

## Funcionalidades

- **Autenticacion JWT**: Registro y login con tokens de acceso
- **CRUD de Productos**: Crear, leer, actualizar y eliminar productos
- **CRUD de Categorias**: Gestion de categorias de productos
- **Paginacion**: Soporte para paginacion en listados
- **Validacion**: Validacion de datos en DTOs y entidades
- **Manejo de errores**: Respuestas de error estandarizadas
- **Documentacion API**: Swagger UI disponible
- **Tests**: Tests unitarios e integracion con JUnit y Mockito

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

| Campo    | Tipo   | Descripcion                    |
|----------|--------|--------------------------------|
| id       | Long   | Identificador unico            |
| nombre   | String | Nombre de usuario (unico)      |
| password | String | Contrasena encriptada (BCrypt) |
| rol      | Enum   | ROLE_USUARIO o ROLE_ADMIN      |

### Producto

| Campo       | Tipo       | Descripcion                      |
|-------------|------------|----------------------------------|
| id          | Long       | Identificador unico              |
| nombre      | String     | Nombre del producto              |
| stock       | int        | Cantidad disponible              |
| precio      | BigDecimal | Precio del producto              |
| descripcion | String     | Descripcion (opcional)           |
| disponible  | boolean    | Calculado automaticamente        |
| categoria   | Categoria  | Categoria asociada               |

### Categoria

| Campo  | Tipo   | Descripcion             |
|--------|--------|-------------------------|
| id     | Long   | Identificador unico     |
| nombre | String | Nombre de la categoria  |

## Endpoints

### Autenticacion (publicos)

| Metodo | Endpoint            | Descripcion             |
|--------|---------------------|-------------------------|
| POST   | /api/auth/register  | Registrar nuevo usuario |
| POST   | /api/auth/login     | Iniciar sesion          |

### Productos (requieren autenticacion)

| Metodo | Endpoint            | Descripcion                 |
|--------|---------------------|-----------------------------|
| GET    | /api/productos      | Listar productos (paginado) |
| GET    | /api/productos/{id} | Obtener producto por ID     |
| POST   | /api/productos      | Crear producto              |
| PUT    | /api/productos/{id} | Actualizar producto         |
| DELETE | /api/productos/{id} | Eliminar producto           |

### Categorias (requieren autenticacion)

| Metodo | Endpoint             | Descripcion                  |
|--------|----------------------|------------------------------|
| GET    | /api/categorias      | Listar categorias (paginado) |
| GET    | /api/categorias/{id} | Obtener categoria por ID     |
| POST   | /api/categorias      | Crear categoria              |
| PUT    | /api/categorias/{id} | Actualizar categoria         |
| DELETE | /api/categorias/{id} | Eliminar categoria           |

## Requisitos

- Java 21+
- Maven 3.8+
- MySQL 8.0+
- Docker (opcional)

## Configuracion

### Opcion A: Ejecutar con Docker (recomendado)

```bash
# Clonar el repositorio
git clone https://github.com/AlexRoa6/E-commerce

# Copiar variables de entorno
cp .env.example .env

# Editar .env con tus valores
# DB_USERNAME, DB_PASSWORD, JWT_SECRET, MYSQL_ROOT_PASSWORD

# Levantar con Docker
docker-compose up --build
```

La API estara disponible en `http://localhost:8080`

### Opcion B: Ejecutar en local

#### 1. Crear la base de datos

```sql
CREATE DATABASE ecommerce_db;
```

#### 2. Configurar variables de entorno

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

#### 3. Ejecutar la aplicacion

```bash
mvn spring-boot:run
```

La API estara disponible en `http://localhost:8080`

## Documentacion API

- Local: `http://localhost:8080/swagger-ui/index.html`
- Produccion: `https://e-commerce-production-33eb.up.railway.app/swagger-ui/index.html`

## Uso

### Registrar usuario

```bash
curl -X POST https://e-commerce-production-33eb.up.railway.app/api/auth/register \
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
curl -X POST https://e-commerce-production-33eb.up.railway.app/api/productos \
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

## Tests

El proyecto cuenta con tests unitarios e integración:

```bash
mvn test
```

| Tipo | Clases | Herramientas |
|------|--------|--------------|
| Unitarios | ProductoServiceTest, CategoriaServiceTest | JUnit, Mockito |
| Integracion | ProductoControllerTest, CategoriaControllerTest | MockMvc |

## Lo que aprendi

Este proyecto me permitio practicar:

- Arquitectura en capas y separacion de responsabilidades
- Implementacion de autenticacion JWT desde cero
- Configuracion de Spring Security
- Validacion de datos con Bean Validation
- Manejo centralizado de excepciones
- Paginacion con Spring Data
- Documentacion de APIs con Swagger/OpenAPI
- Tests unitarios e integracion
- Contenedorizacion con Docker
- Despliegue en la nube con Railway
- Buenas practicas en desarrollo de APIs REST

## Posibles mejoras

- [ ] Implementar roles y permisos mas granulares
- [ ] Agregar carrito de compras
- [ ] Implementar ordenes de compra
- [ ] Agregar imagenes a productos
- [ ] Implementar busqueda y filtros avanzados
- [ ] Cache con Redis

## Autor

Alex Roa
