# Microservicio Reactivo — Spring Boot + MySQL (R2DBC) + Security + Micrometer + Swagger

Servicio REST **reactivo** con **arquitectura hexagonal**, persistencia **no bloqueante** vía **R2DBC**, seguridad **JWT**, observabilidad con **Micrometer/Prometheus**, migraciones con **Flyway**, **reintentos** en búsquedas y **manejador de errores** consistente.

> **Importante:** Antes de **crear** o **listar** usuarios debes **autenticarse y obtener el Bearer token**.

---

## Tabla de contenidos
- Arquitectura
- Requisitos
- Configuración esperada
- Ejecución
- Migraciones y datos semilla
- Seguridad (JWT)
- Documentación y observabilidad
- Patrones de reintento
- Convención de errores
- Endpoints funcionales
- Stack y versiones (derivado de tu build)
- Troubleshooting
- Notas finales

---

## Arquitectura
- **Hexagonal (Ports & Adapters):**
    - **Dominio:** entidades y reglas de negocio.
    - **Aplicación:** casos de uso y orquestación.
    - **Adaptadores de entrada:** HTTP (WebFlux).
    - **Adaptadores de salida:** repositorios R2DBC hacia MySQL.
- **Beneficios:** bajo acoplamiento, fácil testeo y reemplazo de infraestructura.

---

## Requisitos
- JDK 21+ (compatible con tu toolchain de proyecto).
- Gradle/Maven.
- MySQL 8.x accesible.
- Docker (opcional para levantar MySQL/Prometheus).

---

## Configuración esperada
- Conexión **R2DBC** a MySQL (URL, usuario y contraseña).
- **Flyway** habilitado con URL JDBC para ejecutar migraciones al inicio.
- Propiedades de **JWT** (secreto, expiraciones).
- **Actuator** expuesto con métricas y salud.
- **Swagger/OpenAPI** habilitado en entorno dev/test.

> Las rutas públicas de autenticación deben excluirse del filtro de seguridad; los endpoints de negocio requieren **Authorization: Bearer <token>**.

---

## Ejecución
1. Tener MySQL corriendo y con el **schema** configurado para la app.
2. Arrancar la aplicación con tus comandos habituales (Gradle o IDE).
3. Verificar health y métricas (sección “Documentación y observabilidad”).

---

## Migraciones y datos semilla
- **Flyway** crea el esquema y carga datos iniciales.
- Usuario semilla para autenticación de pruebas:
    - **username:** juan
    - **password:** password
- Cambiar estas credenciales en ambientes productivos.

---

## Seguridad (JWT)
- **Flujo:**
    - Autenticarse en el endpoint de login para obtener **access token (Bearer)**.
    - Usar ese token en la cabecera **Authorization** para **crear** o **listar** usuarios.
    - Endpoint de **refresh** disponible para renovar el access token.
- **Regla operativa:** cualquier operación de usuarios **requiere** token válido.

---

## Documentación y observabilidad
- **Swagger UI:** `URL_BASE/swagger-ui/index.html`
- **Prometheus (Actuator):** `URL_BASE/actuator/prometheus`
- Endpoints de salud/infos de Actuator disponibles según configuración.

---

## Patrones de reintento
- Reintentos **exponenciales** (con backoff y límite) en **operaciones de búsqueda** para manejar fallos transitorios (timeouts, pool saturado, etc.).
- **No** se reintentan errores de negocio (4xx).

---

## Convención de errores
- Respuestas con cuerpo **estandarizado**: timestamp, status, code, message, path, traceId.
- Mapeo sugerido (ejemplos):
    - 400: validación.
    - 401: sin token / token inválido.
    - 403: sin permisos.
    - 404: no encontrado.
    - 409: conflicto/duplicado.
    - 422: reglas de negocio.
    - 500: error inesperado.

---

## Endpoints funcionales (resumen)
- **Autenticación:** obtener **Bearer token** (login) y **refresh**.
- **Usuarios:** **listar** y **crear** (ambos requieren **Bearer token**).

> Antes de consumir endpoints de usuarios, **autentícate y usa Authorization: Bearer <token>**.

---

## Stack y versiones (derivado de tu build)
**Build / Plugins**
- Java (plugin “java”).
- Spring Boot **3.5.4** (plugin).
- Dependency Management **1.1.7** (plugin).
- Toolchain Java configurado a **Java 24** (compilación).

**Framework**
- Spring Boot **3.5.4** (WebFlux, Security, Actuator **3.5.3**).

**Persistencia**
- Spring Data R2DBC.
- Driver R2DBC MySQL **1.4.1**.
- (JDBC solo para migraciones) MySQL Connector Java **8.0.33**.

**Migraciones**
- Flyway Core **10.13.0**.
- Flyway MySQL **10.13.0**.

**Validación**
- Jakarta Validation **3.0.2**.
- Hibernate Validator **8.0.1.Final**.

**Documentación API**
- Swagger Annotations **2.2.34**.
- springdoc-openapi WebFlux UI **2.8.6**.

**Observabilidad**
- Micrometer Registry Prometheus **1.14.9** (también referenciado 1.15.2; unifica a una sola versión para evitar conflictos).
- Spring Boot Actuator **3.5.3**.

**Mapeo**
- MapStruct **1.6.2** (con annotation processors).

**Lombok**
- Lombok **1.18.38** (compileOnly / annotationProcessor y para tests).

**Testing**
- Spring Boot Starter Test.
- Reactor Test.
- JUnit Platform Launcher.


---

## Troubleshooting
- **401/403:** asegúrate de haber obtenido el **Bearer token** y de enviarlo en **Authorization**.
- **Errores R2DBC / timeouts:** revisa la conectividad a MySQL, tamaño del pool y política de reintentos.
- **Migraciones fallan:** valida la URL JDBC de Flyway y permisos del usuario.
- **Swagger no carga:** revisa que springdoc esté habilitado y el contexto no esté detrás de un proxy sin configuración de path.

---

## Notas finales
- El diseño es **escalable** y **resiliente** al separar el **core de negocio** de la infraestructura (hexagonal), utilizar **I/O reactivo**, exponer **métricas** y **health checks**, y aplicar **reintentos con backoff** en consultas.
