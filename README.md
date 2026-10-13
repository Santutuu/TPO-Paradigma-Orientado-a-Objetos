# TPO Paradigma Orientado a Objetos - UADE

Sistema de gestión de alojamientos y eventos para la visita del Papa León XIV a Argentina.

La aplicación permite a los asistentes consultar eventos, inscribirse en actividades y reservar alojamientos; a los establecimientos administrar su información; y a un administrador gestionar la agenda.

> **Aclaración:** las fechas de la visita (8 al 11 de noviembre de 2026) y los eventos de ejemplo son datos de la consigna académica, no una agenda oficial confirmada.

---

## Requisitos

- Java 17 o superior.
- Sin dependencias externas: solo la biblioteca estándar de Java y Swing.
- Se compila y ejecuta desde terminal con `javac` y `java`.

---

## Estructura de carpetas

```text
TPOVisitaPapa/
├── src/
│   ├── Main.java
│   ├── modelo/        (entidades y enumeraciones)
│   ├── servicios/     (reglas de negocio)
│   ├── persistencia/  (lectura y escritura de JSON)
│   └── interfaz/      (ventanas y paneles Swing)
├── datos/
│   ├── cuentas.json
│   ├── eventos.json
│   └── reservas.json
└── README.md
```

---

## Credenciales de prueba

| Rol           | Usuario       | Clave    |
|---------------|---------------|----------|
| Administrador | `admin`       | `admin123` |
| Asistente     | `juan`        | `1234`   |
| Asistente     | `maria`       | `1234`   |
| Asistente     | `pedro`       | `1234`   |
| Alojamiento   | `sancayetano` | `1234`   |
| Alojamiento   | `sanfrancisco`| `1234`   |
| Alojamiento   | `granlujan`   | `1234`   |
| Alojamiento   | `caritas`     | `1234`   |
| Alojamiento   | `sanpablo`    | `1234`   |

---

## Cómo compilar y ejecutar

Desde la raíz del proyecto (donde está la carpeta `datos`):

**Compilar:**

```bash
javac -encoding UTF-8 -d out src/Main.java src/modelo/*.java src/servicios/*.java src/persistencia/*.java src/interfaz/*.java
```

**Ejecutar:**

```bash
java -cp out Main
```

> En Windows usar `\` en las rutas y `;` como separador del classpath. Los comandos de arriba usan `/` (funcionan en macOS/Linux). Si se usa PowerShell, reemplazar `/` por `\`.

Los archivos de datos se ubican en la carpeta `datos/`, relativa a la raíz del proyecto. Ejecutá el programa siempre desde la raíz del proyecto para que encuentre esa carpeta.

---

## Formato de los archivos JSON

### `cuentas.json`

Arreglo de cuentas. Cada cuenta lleva un campo `tipo` que indica su subtipo concreto:

- `ADMINISTRADOR`: `tipo, id, nombreUsuario, clave, nombre`
- `ASISTENTE`: `tipo, id, nombreUsuario, clave, nombre, dni, origen, eventos`
- `PARROQUIA`: `tipo, id, nombreUsuario, clave, nombre, ciudad, direccion, capacidad`
- `HOTEL`: igual que parroquia más `categoria`
- `ESPACIO_ORG`: igual que parroquia más `organizador`

El campo `eventos` de un asistente es un arreglo de identificadores de eventos.

### `eventos.json`

Arreglo de eventos con `id, fecha (AAAA-MM-DD), horario (HH:mm), actividad, ciudad`.

### `reservas.json`

Arreglo de reservas con `id, idAsistente, idAlojamiento, ingreso (AAAA-MM-DD), egreso (AAAA-MM-DD), estado (ACTIVA o CANCELADA)`.

Las fechas se guardan como `AAAA-MM-DD`, los horarios como `HH:mm` y las enumeraciones con su nombre en mayúsculas.

---

## Listado de clases y responsabilidades

### `modelo`

| Clase | Responsabilidad |
|---|---|
| `Ciudad` (enum) | Ciudades posibles: `CABA`, `CORDOBA`, `LUJAN`, `CLAYPOLE`. |
| `EstadoReserva` (enum) | Estados de una reserva: `ACTIVA`, `CANCELADA`. |
| `Usuario` (abstracta) | Datos comunes de una cuenta (`id`, `nombreUsuario`, `clave`, `nombre`). Método `validarClave` y `getRol()` abstracto. |
| `Asistente` | Datos del asistente (`dni`, `origen`, lista de eventos). Métodos `inscribir`/`desinscribir`. |
| `Administrador` | Cuenta de administrador (precargada, no se registra por interfaz). |
| `Alojamiento` (abstracta) | Datos comunes del alojamiento (`ciudad`, `direccion`, `capacidad`). Método abstracto `descripcion()`. |
| `Parroquia` | Alojamiento sin campos extra. |
| `Hotel` | Alojamiento con `categoria` (1 a 5 estrellas). |
| `EspacioOrg` | Alojamiento con `organizador`. |
| `Evento` | Evento de la agenda (`id`, `fecha`, `horario`, `actividad`, `ciudad`). |
| `Reserva` | Reserva (`id`, `idAsistente`, `idAlojamiento`, `ingreso`, `egreso`, `estado`). Métodos `seSuperpone` y `cancelar`. |

### `servicios`

| Clase | Responsabilidad |
|---|---|
| `GestorUsuarios` | Registro, autenticación, unicidad de usuario y búsqueda de cuentas. |
| `GestorEventos` | Alta, modificación, consulta de eventos e inscripción/desinscripción. |
| `GestorReservas` | Creación, consulta y cancelación de reservas y cálculo de disponibilidad. |
| `GestorAlojamientos` | Consulta y modificación de datos y capacidad, huéspedes por período. |

### `persistencia`

| Clase | Responsabilidad |
|---|---|
| `ParserJSON` | Parsea texto JSON a objetos (`Map`, `List`, `String`, `Number`, `Boolean`, `null`) y rechaza entradas inválidas. |
| `PersistenciaJSON` | Carga y guarda los tres archivos JSON. Escribe con `StringBuilder` y lee con `ParserJSON`. |

### `interfaz`

| Clase | Responsabilidad |
|---|---|
| `VentanaPrincipal` | `JFrame` principal, centrado, con la navegación entre paneles y la sesión actual. |
| `PanelInicioSesion` | Menú sin sesión: login y accesos públicos (registro, agenda, alojamientos). |
| `PanelRegistroAsistente` | Formulario de alta de asistente. |
| `PanelRegistroAlojamiento` | Formulario de alta de alojamiento (con subtipo). |
| `PanelAsistente` | Menú del asistente. |
| `PanelAlojamiento` | Menú del alojamiento. |
| `PanelAdministrador` | Menú del administrador. |
| `PanelAgenda` | Listado de eventos por fecha y gestión de inscripciones. |
| `PanelBusquedaAlojamientos` | Listado de alojamientos por ciudad. |
| `PanelReservas` | Reservar, consultar y cancelar reserva. |
| `UtilidadesInterfaz` | Helpers compartidos (diálogos, fechas, tablas, botones). |

---

## Relaciones de herencia y asociación

Herencia:

```
Usuario (abstracta)
 ├── Asistente
 ├── Administrador
 └── Alojamiento (abstracta)
      ├── Parroquia
      ├── Hotel
      └── EspacioOrg
```

Asociaciones (por identificador, no por objeto):

- `Asistente` → muchos `Evento` (lista `List<Integer> eventos`).
- `Reserva` → un `Asistente` (`idAsistente`) y un `Alojamiento` (`idAlojamiento`).
- `Evento` y `Alojamiento` → una `Ciudad` (valor del enum).

---

## Decisiones de diseño

- **Campo `tipo`** en `cuentas.json` para reconstruir el subtipo correcto al leer (resuelve el polimorfismo).
- **Cada reserva representa a 1 persona** (el asistente que reserva), por eso la capacidad se compara contra la cantidad de reservas activas superpuestas.
- **Solapamiento**: ingreso inclusivo, egreso exclusivo. Una reserva que termina el día en que empieza otra no la solapa.
- **Ids**: al dar de alta se usa el mayor id existente + 1; se conservan entre sesiones porque se guardan en el JSON.
- **Categoría de hotel**: entre 1 y 5 estrellas.
- **Rango de capacidad**: entero positivo.

---

## Limitaciones conocidas

- Las claves se guardan en **texto plano** exclusivamente por la limitación académica del TPO. No es una práctica segura para producción.
- El parser JSON es un parser sencillo que soporta el subconjunto de JSON usado por la aplicación (objetos, arreglos, cadenas, números, booleanos y `null`). No soporta features avanzadas.
- La ubicación de los archivos es relativa a la raíz del proyecto: hay que ejecutar el programa desde esa carpeta.
- La interfaz gráfica no fue probada de forma manual exhaustiva (clic por clic); sí se verificó que compila y que todos los paneles se construyen y navegan sin errores.

---

## Pruebas realizadas

Se implementó un arnés de pruebas (no forma parte de la entrega) que validó lo siguiente con resultado **40/40 OK**:

1. Carga correcta de los tres archivos JSON (9 cuentas, 7 eventos, 4 reservas).
2. Persistencia round-trip: guardar y volver a leer conserva cantidades, subtipos (`Administrador`, `Hotel`, `EspacioOrg`, `Parroquia`), inscripciones y fechas.
3. Autenticación: admin correcto, clave incorrecta → `null`, usuario inexistente → `null`.
4. Unicidad de nombre de usuario: duplicado rechazado.
5. Inscripción a evento, inscripción duplicada rechazada, inscripción a evento inexistente rechazada, desinscripción.
6. Reservas: egreso anterior o igual al ingreso rechazado; reserva activa previa rechazada; falta de capacidad rechazada; asistente inexistente rechazado; reserva exitosa; cancelación y nueva reserva; las canceladas no cuentan en la ocupación.
7. Solapamiento: frontera sin solapamiento (egreso exclusivo) y solapamiento real detectado.
8. Modificación de alojamiento: reducción de capacidad por debajo de la ocupación rechazada; aumento permitido; no se pueden modificar datos ajenos; sí los propios.
9. Parser JSON: rechazo de JSON inválido y escape correcto de comillas, barras invertidas y saltos de línea.

### Qué quedó sin verificar

- La interfaz gráfica se compiló y se verificó que todos los paneles se construyen y navegan sin errores de ejecución, pero **no se probó de forma manual la interacción completa** (clics, ingreso de datos y mensajes de diálogo).
