# Coffee System

Sistema de gestion para una red de cafeterias con un programa de fidelizacion basado en puntos. La aplicacion permite administrar cafeterias, productos, compras, beneficios canjeables y resenas, con control de acceso por roles mediante JWT.

## Descripcion general

Coffee System es una API REST construida con Spring Boot. El flujo principal es el siguiente:

- Las cafeterias publican productos en su catalogo.
- Los clientes arman un carrito y generan compras.
- Cada compra otorga puntos al cliente segun los productos adquiridos.
- Los puntos acumulados ubican al cliente en un nivel de fidelizacion (tier).
- Los clientes canjean sus puntos por beneficios, que pueden ser globales o propios de una cafeteria.
- Los clientes pueden dejar resenas sobre las cafeterias.

## Tecnologias

- Java 21
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Spring Security con JWT (jjwt)
- MySQL 8
- MapStruct para el mapeo entre entidades y DTOs
- Lombok
- springdoc-openapi para la documentacion (Swagger UI)
- Resend para el envio de correos
- Maven como gestor de dependencias
- Docker y Docker Compose

## Roles y permisos

El sistema define tres roles:

- **ROLE_SUPERADMIN**: acceso total. Administra usuarios, cafeterias, categorias, tiers y supervisa compras y beneficios.
- **ROLE_CAFETERIA**: gestiona sus productos, beneficios y consulta el historial de compras de su cafeteria.
- **ROLE_CLIENTE**: navega productos, arma el carrito, realiza compras, canjea beneficios y deja resenas.

Los permisos se asignan a cada rol al iniciar la aplicacion a traves de `DatabaseInitializer`.

## Estructura del proyecto

El codigo se organiza por features, donde cada modulo agrupa su controlador, servicio, entidad, repositorio y DTOs.

```
src/main/java/com/CoffeeGroup/Coffee/System
  auth/                  Autenticacion, JWT, roles, permisos y configuracion de seguridad
  common/                Utilidades compartidas, manejo de excepciones, email e inicializacion de datos
  features/
    benefit/             Beneficios canjeables
    benefit_type/        Tipos de beneficio (descuento, producto por puntos, 2x1)
    cafe/                Cafeterias
    cart/                Carrito de compras
    category/            Categorias de productos
    client/              Perfiles de cliente y puntos
    product/             Productos del catalogo
    purchase/            Compras
    purchase_detail/     Detalle de cada compra
    redemption/          Canje de beneficios
    review/              Resenas de cafeterias
    tier/                Niveles de fidelizacion
    user/                Cuentas de usuario
```

## Modulos principales

### Autenticacion (`/auth`)
Registro de clientes y login. El login devuelve un token JWT que debe enviarse en el header `Authorization: Bearer <token>` para el resto de los endpoints.

### Usuarios (`/users`)
Administracion de cuentas de usuario. Acceso exclusivo de superadmin.

### Cafeterias (`/cafes`)
Registro y administracion de cafeterias. El superadmin registra cafeterias; las cafeterias actualizan sus datos; los clientes pueden listarlas y filtrarlas.

### Productos (`/products`)
Catalogo de productos. Las cafeterias crean, actualizan y eliminan sus productos. Cualquier usuario autenticado puede consultarlos y filtrarlos por cafeteria.

### Categorias (`/category`)
Categorias de productos administradas por el superadmin.

### Carrito (`/carts`)
Carrito de compras del cliente. Permite agregar items, quitarlos, consultar el carrito y vaciarlo.

### Compras (`/purchases`)
Creacion de compras a partir del carrito y consulta de historiales por cafeteria o por cliente. Cada compra otorga puntos al cliente.

### Clientes (`/clients`)
Gestion de perfiles de cliente y consulta de puntos acumulados.

### Tiers (`/tiers`)
Niveles de fidelizacion definidos por puntos minimos. Los niveles iniciales son BRONCE, PLATA, ORO y DIAMANTE.

### Beneficios (`/benefits`) y tipos de beneficio (`/benefittypes`)
Beneficios que los clientes pueden canjear con puntos. Pueden ser globales o propios de una cafeteria. Los tipos disponibles son codigo de descuento, producto por puntos y 2x1.

### Canjes (`/redemption`)
Canje de beneficios por parte del cliente y validacion del canje por la cafeteria mediante un codigo.

### Resenas (`/reviews`)
Resenas de cafeterias creadas por clientes.

## Documentacion de la API

La documentacion se genera automaticamente con springdoc-openapi. Con la aplicacion en ejecucion:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificacion OpenAPI en JSON: `http://localhost:8080/v3/api-docs`

Estos endpoints estan habilitados sin autenticacion en la configuracion de seguridad.

## Variables de entorno

La aplicacion se configura mediante variables de entorno (archivo `.env`):

```
DB_URL=jdbc:mysql://localhost:3307/coffee
DB_USER=root
DB_PASSWORD=tu_password
JWT_SECRET=tu_secreto
JWT_EXPIRATION=3600000
SUPERADMIN_EMAIL=admin@coffee.com
SUPERADMIN_PASSWORD=tu_password
RESEND_APIKEY=tu_api_key

MYSQL_ROOT_PASSWORD=tu_password
MYSQL_DATABASE=coffee
```

## Ejecucion

### Con Docker Compose

Levanta la base de datos MySQL y la aplicacion en conjunto:

```
docker-compose up --build
```

La aplicacion queda disponible en `http://localhost:8080` y MySQL en el puerto `3307`.

### En local

Requiere Java 21 y una instancia de MySQL accesible. Definir las variables de entorno y ejecutar:

```
./mvnw spring-boot:run
```

## Datos iniciales

Al arrancar, `DatabaseInitializer` crea de forma automatica:

- Los permisos y roles del sistema.
- El usuario superadmin con las credenciales de las variables de entorno.
- Los tiers iniciales (BRONCE, PLATA, ORO, DIAMANTE).
- Categorias de productos por defecto.
- Datos de demostracion: dos cafeterias de prueba con productos y beneficios.

## Autenticacion en las peticiones

1. Registrar un cliente en `POST /auth/register` o usar el superadmin generado al inicio.
2. Hacer login en `POST /auth/login` para obtener el token.
3. Incluir el token en cada peticion protegida:

```
Authorization: Bearer <token>
```


## Reviews

### Axel 
###### Team Leader
Team, immense gratitude to UTN for providing the foundational space that allowed me to pursue this critical quarter while simultaneously nurturing my family. This isn't just about finishing a project; it's about building generational impact. I'm more bullish than ever on what we're achieving together. We're not just completing tasks; we're demonstrating mastery of first principles and fueling our flywheel at scale. The long game is unfolding, and our collective vision is leading us to inevitable success.


### Elian
##### CS Coach
yo chat 🗣️ we just dropped a SIGMA project no cap 😭🚀 this whole arc was absolutely bussin from the jump fr fr 💀 we got that real skibidi rizz energy with the whole team Alan Devin Claude Opus 4.8 Medium all understood the assignment 💯 we achieved max synergy cooked the competition into oblivion 🙏 no cap the lore of this project is gonna be legendary 🗿 im so grateful this experience was next level fr fr this is giving major gyatt vibes 😭


### Nahuel
##### NAVI's Awper
Thrilled to announce the successful completion of a high-impact, cross-functional project! 🚀 It's been an absolute privilege to collaborate with this exceptional team. Their dedication, innovative thinking, and unwavering commitment to excellence have been truly inspiring. We've not only delivered outstanding results but also forged some incredible professional bonds. Onwards and upwards! 📈 #Teamwork #ProjectCompletion #Collaboration #Success #ProfessionalGrowth

### Alan
##### Prestigious Vtuber
Thrilled to announce the successful completion of our "Programación 3" initiative! 🚀 Working with this exceptionally collaborative group has been an absolute highlight, despite navigating a dynamic sprint that saw a robust 3 commits! My personal performance metrics have never looked better. #ProjectCompletion #Teamwork #AgileDevelopment #HighAchievers #Innovation

