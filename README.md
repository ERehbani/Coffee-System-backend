# Coffee System

Sistema de gestión para una red de cafeterías con un programa de fidelización basado en puntos. La aplicación permite administrar cafeterías, productos, compras, beneficios canjeables y reseñas, con control de acceso por roles mediante JWT.

## Descripción general

Coffee System es una API REST construida con Spring Boot. El flujo principal es el siguiente:

- Las cafeterías publican productos en su catálogo.
- Los clientes arman un carrito y generan compras.
- Cada compra otorga puntos al cliente según los productos adquiridos.
- Los puntos acumulados ubican al cliente en un nivel de fidelización (tier).
- Los clientes canjean sus puntos por beneficios, que pueden ser globales o propios de una cafetería.
- Los clientes pueden dejar reseñas sobre las cafeterías.

## Tecnologías

- Java 21
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Spring Security con JWT (jjwt)
- MySQL 8
- MapStruct para el mapeo entre entidades y DTOs
- Lombok
- springdoc-openapi para la documentación (Swagger UI)
- Resend para el envío de correos
- Maven como gestor de dependencias
- Docker y Docker Compose

## Roles y permisos

El sistema define tres roles:

- **ROLE_SUPERADMIN**: acceso total. Administra usuarios, cafeterías, categorías, tiers y supervisa compras y beneficios.
- **ROLE_CAFETERIA**: gestiona sus productos, beneficios y consulta el historial de compras de su cafetería.
- **ROLE_CLIENTE**: navega productos, arma el carrito, realiza compras, canjea beneficios y deja reseñas.

Los permisos se asignan a cada rol al iniciar la aplicación a través de `DatabaseInitializer`.

## Estructura del proyecto

El código se organiza por features, donde cada módulo agrupa su controlador, servicio, entidad, repositorio y DTOs.

```
src/main/java/com/CoffeeGroup/Coffee/System
  auth/                  Autenticación, JWT, roles, permisos y configuración de seguridad
  common/                Utilidades compartidas, manejo de excepciones, email e inicialización de datos
  features/
    benefit/             Beneficios canjeables
    benefit_type/        Tipos de beneficio (descuento, producto por puntos, 2x1)
    cafe/                Cafeterías
    cart/                Carrito de compras
    category/            Categorías de productos
    client/              Perfiles de cliente y puntos
    product/             Productos del catálogo
    purchase/            Compras
    purchase_detail/     Detalle de cada compra
    redemption/          Canje de beneficios
    review/              Reseñas de cafeterías
    tier/                Niveles de fidelización
    user/                Cuentas de usuario
```

## Módulos principales

### Autenticación (`/auth`)
Registro de clientes y sesión. El login guarda el JWT en una cookie `auth_token` HttpOnly que el navegador envía sola en cada petición. `POST /auth/logout` borra la cookie y `GET /auth/me` devuelve el email y los roles de la sesión.

### Usuarios (`/users`)
Administración de cuentas de usuario. Acceso exclusivo de superadmin.

### Cafeterías (`/cafes`)
Registro y administración de cafeterías. El superadmin registra cafeterías; las cafeterías actualizan sus datos; los clientes pueden listarlas y filtrarlas.

### Productos (`/products`)
Catálogo de productos. Las cafeterías crean, actualizan y eliminan sus productos. Cualquier usuario autenticado puede consultarlos y filtrarlos por cafetería.

### Categorías (`/category`)
Categorías de productos administradas por el superadmin.

### Carrito (`/carts`)
Carrito de compras del cliente. Permite agregar ítems, quitarlos, consultar el carrito y vaciarlo.

### Compras (`/purchases`)
Creación de compras a partir del carrito y consulta de historiales por cafetería o por cliente. Cada compra otorga puntos al cliente.

### Clientes (`/clients`)
Gestión de perfiles de cliente y consulta de puntos acumulados.

### Tiers (`/tiers`)
Niveles de fidelización definidos por puntos mínimos. Los niveles iniciales son BRONCE, PLATA, ORO y DIAMANTE.

### Beneficios (`/benefits`) y tipos de beneficio (`/benefittypes`)
Beneficios que los clientes pueden canjear con puntos. Pueden ser globales o propios de una cafetería. Los tipos disponibles son código de descuento, producto por puntos y 2x1.

### Canjes (`/redemption`)
Canje de beneficios por parte del cliente y validación del canje por la cafetería mediante un código.

### Reseñas (`/reviews`)
Reseñas de cafeterías creadas por clientes.

## Documentación de la API

La documentación se genera automáticamente con springdoc-openapi. Con la aplicación en ejecución:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificación OpenAPI en JSON: `http://localhost:8080/v3/api-docs`

Estos endpoints están habilitados sin autenticación en la configuración de seguridad.

## Variables de entorno

La aplicación se configura mediante variables de entorno. Copiar la plantilla y completar los datos del MySQL propio:

```
cp .env.example .env
```

Solo hace falta cambiar `DB_URL`, `DB_PASSWORD`, `JWT_SECRET` y, si se usa Docker, `MYSQL_ROOT_PASSWORD`. El resto de los valores conviene dejarlos igual: la plantilla trae `DEMO_DATA_ENABLED=true`, que crea los mismos usuarios de prueba para todo el equipo. En un entorno público tiene que ir en `false`. El archivo `.env` no se sube al repositorio.

`JWT_SECRET` viene vacío en la plantilla. Para generar uno en Git Bash:

```
openssl rand -base64 48
```

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de la base. Con Docker el puerto es `3307`, con un MySQL local suele ser `3306` |
| `DB_USER` / `DB_PASSWORD` | Usuario y contraseña de MySQL |
| `JWT_SECRET` | Secreto para firmar los tokens, de al menos 32 caracteres |
| `JWT_EXPIRATION` | Duración del token y de la cookie de sesión en milisegundos |
| `AUTH_COOKIE_SECURE` | Si la cookie de sesión lleva el atributo `Secure`. Por defecto `true`; los navegadores la aceptan igual en `localhost` |
| `AUTH_COOKIE_SAME_SITE` | Atributo `SameSite` de la cookie de sesión. Por defecto `Strict` |
| `SUPERADMIN_EMAIL` / `SUPERADMIN_PASSWORD` | Superadmin que se crea al arrancar si no existe |
| `DEMO_DATA_ENABLED` | Crea las cafeterías y el cliente de prueba. Por defecto `false`; ponerlo en `true` solo en desarrollo local |
| `RESEND_APIKEY` | API key de Resend. En local alcanza con un valor de prueba |
| `CORS_ALLOWED_ORIGINS` | Orígenes del frontend habilitados, separados por coma |
| `MYSQL_ROOT_PASSWORD` / `MYSQL_DATABASE` | Solo para Docker Compose |

## Ejecución

### Con Docker Compose

Levanta la base de datos MySQL y la aplicación en conjunto:

```
docker-compose up --build
```

La aplicación queda disponible en `http://localhost:8080` y MySQL en el puerto `3307`.

### En local

Requiere Java 21 y una instancia de MySQL accesible. Spring no lee el `.env` por sí solo, así que hay que cargarlo antes de ejecutar.

En Git Bash:

```
set -a; source .env; set +a
./mvnw spring-boot:run
```

En PowerShell:

```
Get-Content .env | Where-Object { $_ -match '^[A-Z_]+=' } | ForEach-Object { $name, $value = $_ -split '=', 2; Set-Item "env:$name" $value }
./mvnw spring-boot:run
```

## Datos iniciales

Al arrancar, `DatabaseInitializer` crea de forma automática:

- Los permisos y roles del sistema.
- El usuario superadmin con las credenciales de las variables de entorno.
- Los tiers iniciales (BRONCE, PLATA, ORO, DIAMANTE).
- Categorías de productos por defecto.
- Si `DEMO_DATA_ENABLED=true`, datos de demostración: dos cafeterías de prueba con productos y beneficios, y un cliente de prueba.

Cada dato se crea solo si no existe, así que reiniciar la aplicación no duplica nada ni pisa lo que ya esté cargado.

## Usuarios de prueba

Con el `.env` copiado de `.env.example` y `DEMO_DATA_ENABLED=true`, al arrancar quedan disponibles estos usuarios:

| Rol | Email | Contraseña | Origen |
|---|---|---|---|
| Superadmin | `admin@test.com` | `Admin123` | Variables `SUPERADMIN_EMAIL` y `SUPERADMIN_PASSWORD` |
| Cafetería | `cafe.central@test.com` | `Cafe1234` | `DatabaseInitializer` |
| Cafetería | `cafe.norte@test.com` | `Cafe1234` | `DatabaseInitializer` |
| Cliente | `cliente@test.com` | `Cliente123` | `DemoClientSeeder` |

Son solo para desarrollo: no usar estas credenciales en un entorno público.

## Autenticación en las peticiones

1. Registrar un cliente en `POST /auth/register` o usar uno de los usuarios de prueba.
2. Hacer login en `POST /auth/login`. La respuesta trae el email y los roles, y setea la cookie `auth_token`.
3. Desde el navegador o Swagger UI no hay que hacer nada más: la cookie viaja sola. Con curl se guarda y se reenvía:

```
curl -c cookies.txt -H "Content-Type: application/json" -d '{"email":"cliente@test.com","password":"Cliente123"}' http://localhost:8080/auth/login
curl -b cookies.txt http://localhost:8080/auth/me
```

El header `Authorization: Bearer <token>` se sigue aceptando, y tiene prioridad sobre la cookie.
