# 💪 GymApp — Backend

REST API for gym management built with **Spring Boot 3.5** and **Java 21**.

---

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Security + JWT (JJWT 0.12.6)
- Spring Data JPA + Hibernate
- MySQL 8
- Maven

---

## Prerequisites

- Java 21
- MySQL 8 running locally
- Maven 3.8+

---

## Configuration

Create the database in MySQL:

```sql
CREATE DATABASE gym_db;
```

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym_db?allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update

jwt.secret=4d79536563726574436c617665506172614a57543132333435363738393031323334
jwt.expiration=86400000
```

> ⚠️ In production, `jwt.secret` must come from an environment variable — never hardcoded.

---

## Running the project

```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## Project structure

```
src/main/java/com/gym/gym_app/
├── controller/        # REST endpoints
├── dto/               # Data transfer objects
├── exception/         # Global error handling
├── models/            # JPA entities
├── repository/        # Spring Data interfaces
├── scheduler/         # Scheduled tasks
├── security/          # JWT, filters and Spring Security config
└── service/           # Business logic
```

---

## Main endpoints

### Authentication — `/api/auth`

| Method | Route | Description | Auth |
|--------|-------|-------------|------|
| POST | `/api/auth/registro` | Register new user | No |
| POST | `/api/auth/login` | Login with email and password | No |
| POST | `/api/auth/login-id` | Client login with numeric ID | No |

**Login example:**
```json
POST /api/auth/login
{
  "correo": "admin@gym.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGci...",
  "correo": "admin@gym.com",
  "rol": "ADMIN",
  "id": 1
}
```

---

### Users — `/api/usuarios`

| Method | Route | Description | Role |
|--------|-------|-------------|------|
| GET | `/api/usuarios` | List all users | ADMIN |
| GET | `/api/usuarios/{id}` | Get user by ID | ADMIN / own |
| POST | `/api/usuarios` | Create user | ADMIN |
| DELETE | `/api/usuarios/{id}` | Delete user | ADMIN |

---

### Memberships — `/api/membresias`

| Method | Route | Description | Role |
|--------|-------|-------------|------|
| POST | `/api/membresias/usuario/{id}` | Create or renew membership | ADMIN, CLIENTE |
| GET | `/api/membresias/usuario/{id}/activa` | Get active membership | ADMIN, CLIENTE |
| GET | `/api/membresias/usuario/{id}/historial` | Membership history | ADMIN, CLIENTE |
| POST | `/api/membresias/verificar-vencimientos` | Check and mark expired | ADMIN |

**Membership types:** `MENSUAL`, `TRIMESTRAL`, `SEMESTRAL`, `ANUAL`

---

### Attendance — `/api/asistencias`

| Method | Route | Description | Role |
|--------|-------|-------------|------|
| POST | `/api/asistencias/entrada/{usuarioId}` | Register check-in | ADMIN, CLIENTE |
| PUT | `/api/asistencias/salida/{usuarioId}` | Register check-out | ADMIN, CLIENTE |
| GET | `/api/asistencias/usuario/{id}/historial` | History by user | ADMIN, CLIENTE |
| GET | `/api/asistencias/hoy` | Today's attendance | ADMIN |

> Check-outs are automatically registered after 2 hours if the client forgets to check out.

---

### Body Data — `/api/datos-cuerpo`

| Method | Route | Description | Role |
|--------|-------|-------------|------|
| POST | `/api/datos-cuerpo/usuario/{id}` | Save own data | CLIENTE |
| POST | `/api/datos-cuerpo/admin/usuario/{id}` | Save data for any client | ADMIN |
| GET | `/api/datos-cuerpo/usuario/{id}/ultimo` | Latest record | ADMIN, CLIENTE |
| GET | `/api/datos-cuerpo/usuario/{id}/historial` | Full history | ADMIN, CLIENTE |
| GET | `/api/datos-cuerpo/reporte/mensual` | Monthly progress report | ADMIN |

**Available fields:**

| Field | Type | Description |
|-------|------|-------------|
| peso | Double | kg |
| altura | Double | meters |
| imc | Double | auto-calculated |
| categoria | String | BMI interpretation |
| musculo | Double | kg |
| grasaCorporal | Double | % |
| grasaVisceral | Double | level |
| edadMetabolica | Integer | years |
| busto | Double | cm |
| cintura | Double | cm |
| abdomen | Double | cm |
| cadera | Double | cm |
| muslo | Double | cm |
| brazo | Double | cm |

---

## Security

All protected endpoints require the header:

```
Authorization: Bearer <token>
```

Available roles are `ADMIN` and `CLIENTE`. Access control is applied at two levels:

- **By route** in `SecurityConfig` — defines which roles can access each endpoint group
- **By method** with `@PreAuthorize` — fine-grained control, e.g. a CLIENTE can only access their own data

---

## Scheduled tasks

| Task | Frequency | Description |
|------|-----------|-------------|
| `MembresiaScheduler` | Daily at 00:01 AM | Marks expired memberships |
| `AsistenciaScheduler` | Every 15 minutes | Auto check-out after 2 hours |

---

## Main models

```
Usuario
├── id, nombre, correo, password (BCrypt), telefono, rol

Membresia
├── id, usuario, tipo, fechaPago, fechaVencimiento, activa

Asistencia
├── id, usuario, fechaEntrada, fechaSalida

DatosCuerpo
├── id, usuario, peso, altura, imc, musculo, grasaCorporal,
│   grasaVisceral, edadMetabolica, busto, cintura, abdomen,
│   cadera, muslo, brazo, fechaRegistro
```

---

## Frontend

The frontend for this application is available at:
[gym-frontend](https://github.com/eliasuriel/gym-frontend)

---

## Author

Developed by **eliasuriel**
