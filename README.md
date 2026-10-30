# TPO Paradigma Orientado a Objetos - UADE

Sistema de gestion de alojamientos y eventos para la visita del Papa Leon XIV a Argentina.

La aplicacion permite a los asistentes consultar eventos, inscribirse en actividades y reservar alojamientos; a los establecimientos administrar su informacion; y a un administrador gestionar la agenda.

El modelo de clases sigue el diagrama UML del proyecto (`uml+/uml.pdf`): herencia, atributos, metodos y relaciones por identificador. Se agregaron extensiones: `horarioFin` en `Evento` (superposiciones), `tipoDocumento` en `Asistente` (unicidad por tipo + numero), `preciosPorTipo` y `habitaciones` en `Hotel`, y `tipoHabitacion`, `cantidadPersonas` y `precioPorNoche` en `Reserva` (reserva de habitacion completa).

> **Aclaracion:** las fechas de la visita (8 al 11 de noviembre de 2026) y los eventos de ejemplo son datos de la consigna academica, no una agenda oficial confirmada.

---

## Requisitos

- Java 17 o superior.
- Sin dependencias externas: solo la biblioteca estandar de Java y Swing.
- Se compila y ejecuta desde terminal con `javac` y `java`.

---

## Estructura de carpetas

```text
tp7/
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
├── notas/
│   ├── README.md
└── uml+/
    └── uml.pdf
```

---

## Credenciales de prueba

| Rol           | Usuario        | Clave      |
|---------------|----------------|------------|
| Administrador | `admin`        | `admin123` |
| Asistente     | `juan`         | `1234`     |
| Asistente     | `maria`        | `1234`     |
| Asistente     | `pedro`        | `1234`     |
| Asistente     | `Santino`      | `aloha`    |
| Alojamiento   | `sancayetano`  | `1234`     |
| Alojamiento   | `sanfrancisco` | `1234`     |
| Hotel         | `granlujan`    | `1234`     |
| Hotel         | `hotelcentro`  | `1234`     |
| Alojamiento   | `caritas`      | `1234`     |
| Alojamiento   | `sanpablo`     | `1234`     |

---

## Como compilar y ejecutar

Desde la raiz del proyecto (donde esta la carpeta `datos`):

**Compilar:**

```bash
javac -encoding UTF-8 -d out src/Main.java src/modelo/*.java src/servicios/*.java src/persistencia/*.java src/interfaz/*.java
```

**Ejecutar:**

```bash
java -cp out Main
```

> En Windows usar `\` en las rutas y `;` como separador del classpath. Los comandos de arriba usan `/` (funcionan en macOS/Linux). Si se usa PowerShell, reemplazar `/` por `\`.

Los archivos de datos se ubican en la carpeta `datos/`, relativa a la raiz del proyecto. Ejecuta el programa siempre desde la raiz del proyecto para que encuentre esa carpeta.

---

## Formato de los archivos JSON

El modelo relaciona las entidades por **identificador numerico**, tal como indica el UML.

### `cuentas.json`

Arreglo de cuentas. Cada cuenta lleva un campo `tipo` que indica su subtipo concreto:

- `ADMINISTRADOR`: `tipo, id, nombreUsuario, clave, nombre`
- `ASISTENTE`: `tipo, id, nombreUsuario, clave, nombre, tipoDocumento, dni, origen, eventos`
- `PARROQUIA`: `tipo, id, nombreUsuario, clave, nombre, ciudad, direccion, capacidad, responsable`
- `HOTEL`: igual que parroquia, sin `responsable` y con `categoria`, `preciosPorTipo` y `habitaciones`
- `ESPACIO_ORG`: igual que parroquia, sin `responsable` y con `organizador`

El campo `tipoDocumento` puede valer `DNI`, `PASAPORTE` u `OTRO`. Si un archivo viejo no lo trae, se asume `DNI`.

El campo `habitaciones` de un hotel indica cuantas habitaciones hay para 1, 2, 3 y 4 personas, y `preciosPorTipo` indica el precio por noche de cada tipo:

```json
"preciosPorTipo": { "1": 30000.0, "2": 50000.0, "3": 65000.0, "4": 80000.0 },
"habitaciones": { "1": 2, "2": 5, "3": 2, "4": 1 }
```

La capacidad total del hotel se calcula sumando `personas x cantidad` de cada tipo. Los tipos con 0 habitaciones pueden tener precio 0. Los archivos viejos sin `preciosPorTipo` usan `precioPorNoche` para todos los tipos; los que tampoco tienen `habitaciones` se cargan con la capacidad existente como habitaciones simples.

El campo `eventos` de un asistente es un arreglo de ids de eventos:

```json
"eventos": [101]
```

### `eventos.json`

Arreglo de eventos:

```json
{ "id": 101, "fecha": "2026-11-08", "horario": "10:00", "horarioFin": "12:00", "actividad": "Misa de apertura", "ciudad": "LUJAN" }
```

### `reservas.json`

Arreglo de reservas:

```json
{ "id": 3, "idAsistente": 4, "idAlojamiento": 7, "tipoHabitacion": 2, "cantidadPersonas": 1, "precioPorNoche": 45000.0, "ingreso": "2026-11-09", "egreso": "2026-11-11", "estado": "ACTIVA" }
```

`tipoHabitacion` indica la capacidad de la habitacion (1 a 4); `0` significa que no aplica (parroquia o espacio). `cantidadPersonas` es cuantas personas se alojan en esa habitacion y `precioPorNoche` es el precio vigente al momento de reservar. En alojamientos que no son hoteles, `tipoHabitacion` es `0`, `cantidadPersonas` es `1` y `precioPorNoche` es `0`.

Las fechas se guardan como `AAAA-MM-DD`, los horarios como `HH:mm` (`horario` es el inicio y `horarioFin` el fin estimado) y las enumeraciones con su nombre en mayusculas.

---

## Listado de clases y responsabilidades

### `modelo`

| Clase | Responsabilidad |
|---|---|
| `Ciudad` (enum) | Ciudades posibles: `CABA`, `CORDOBA`, `LUJAN`, `CLAYPOLE`. |
| `EstadoReserva` (enum) | Estados de una reserva: `ACTIVA`, `CANCELADA`. |
| `Usuario` (abstracta) | Datos comunes de una cuenta (`id`, `nombreUsuario`, `clave`, `nombre`). Metodos `validarClave` y `getRol()` abstracto. |
| `Asistente` | Datos del asistente (`tipoDocumento`, `dni`, `origen : String`, `List<Integer> eventos`). Metodos `inscribir(idEvento)` / `desinscribir(idEvento)`. |
| `Administrador` | Cuenta de administrador (precargada, no se registra por interfaz). |
| `Alojamiento` (abstracta) | Datos comunes (`ciudad`, `direccion`, `capacidad`). Metodo abstracto `descripcion()`. |
| `Parroquia` | Alojamiento con `responsable`. |
| `Hotel` | Alojamiento con `categoria` (1 a 5 estrellas), `preciosPorTipo` y `habitaciones` por cantidad de personas. La capacidad se calcula a partir de las habitaciones. |
| `EspacioOrg` | Alojamiento con `organizador`. |
| `Evento` | Evento de la agenda (`id`, `fecha`, `horario` de inicio, `horarioFin` estimado, `actividad`, `ciudad`). `seSuperpone(otro)` compara fecha y rangos horarios (extension). |
| `Reserva` | Reserva (`id`, `idAsistente`, `idAlojamiento`, `tipoHabitacion`, `cantidadPersonas`, `precioPorNoche`, `ingreso`, `egreso`, `estado`). En hoteles ocupa una habitacion completa; en otros alojamientos representa una persona. Metodos `seSuperpone(otra : Reserva)`, `cancelar()` y `estaActiva()`. |
| `UsuarioFactory` | Fabrica simple que centraliza la creacion de cada subtipo de `Usuario` (`Administrador`, `Asistente`, `Parroquia`, `Hotel`, `EspacioOrg`). La seleccion del tipo se hace en persistencia/formularios y la construccion pasa siempre por la fabrica. |

### `servicios`

| Clase | Responsabilidad |
|---|---|
| `GestorUsuarios` | Registro, autenticacion, unicidad de nombre de usuario, busqueda por id y asignacion de ids. |
| `GestorEventos` | Alta, modificacion y consulta de eventos; inscripcion/desinscripcion por id y control de superposiciones. |
| `GestorReservas` | Creacion, consulta y cancelacion de reservas y calculo de disponibilidad. |
| `GestorAlojamientos` | Consulta y modificacion de datos y capacidad, y consulta de huespedes por periodo. |
| `ReservaObserver` | Interfaz de observador con eventos `reservaCreada` y `reservaCancelada` (implementacion vacia por defecto). |
| `NotificadorReservas` | Observador concreto que informa por consola cuando se crea o cancela una reserva. |

### `persistencia`

| Clase | Responsabilidad |
|---|---|
| `ParserJSON` | Parsea texto JSON a objetos (`Map`, `List`, `String`, `Number`, `Boolean`, `null`) y rechaza entradas invalidas. |
| `PersistenciaJSON` | Carga y guarda los tres archivos JSON. Escribe con `StringBuilder` y lee con `ParserJSON`. |

### `interfaz`

| Clase | Responsabilidad |
|---|---|
| `VentanaPrincipal` | `JFrame` principal, centrado, con la navegacion entre paneles y la sesion actual. |
| `PanelInicioSesion` | Menu sin sesion: login y accesos publicos (registro, agenda, alojamientos). |
| `PanelRegistroAsistente` | Formulario de alta de asistente. |
| `PanelRegistroAlojamiento` | Formulario de alta de alojamiento (con subtipo y campo extra segun el tipo). |
| `PanelAsistente` | Menu del asistente. |
| `PanelAlojamiento` | Menu del alojamiento. |
| `PanelAdministrador` | Menu del administrador: alta y modificacion de eventos. |
| `PanelAgenda` | Listado de eventos por fecha, "mis inscripciones" y gestion de inscripciones. |
| `PanelBusquedaAlojamientos` | Listado de alojamientos por ciudad. |
| `PanelReservas` | Reservar, consultar y cancelar reserva. |
| `UtilidadesInterfaz` | Helpers compartidos (dialogos, fechas, tablas, botones). |

---

## Relaciones del diagrama

Herencia:

```text
Usuario (abstracta)
 ├── Asistente
 ├── Administrador
 └── Alojamiento (abstracta)
      ├── Parroquia
      ├── Hotel
      └── EspacioOrg
```

Asociaciones (por identificador, como en el UML):

- `Asistente` **0..\*** ── **0..\*** `Evento` ("asiste a"): `Asistente.eventos` guarda los ids.
- `Asistente` **1** ── **0..\*** `Reserva` ("realiza"): `Reserva.idAsistente`.
- `Alojamiento` **1** ── **0..\*** `Reserva` ("recibe"): `Reserva.idAlojamiento`. `Alojamiento` no conoce sus reservas; la navegabilidad va de `Reserva` al alojamiento.
- `Evento` y `Alojamiento` usan `Ciudad`; `Reserva` usa `EstadoReserva` como tipo de atributo.
- `GestorEventos` mantiene la coleccion de eventos (no esta modelado en el UML, pero es necesario para la aplicacion).
- `UsuarioFactory`, `ReservaObserver` y `NotificadorReservas` son infraestructura de la aplicacion (Factory y Observer) y tampoco estan modelados en el UML; no cambian las relaciones del dominio.

---

## Reglas de negocio

- **Una sola reserva activa por asistente**, considerando todos los alojamientos. Para reservar otro hay que cancelar la actual (las canceladas quedan como historico).
- **Cancelacion con 24 hs de anticipacion**: una reserva solo se puede cancelar hasta 24 horas antes de la fecha de ingreso. Pasado ese limite, el sistema rechaza la cancelacion.
- **Hoteles: una reserva ocupa una habitacion completa**: se elige el tipo (1 a 4 personas) y la cantidad de personas no puede superar esa capacidad. Las camas sobrantes de esa habitacion no se venden por separado.
- **Parroquias y espacios: cada reserva representa a 1 persona**: la capacidad se compara contra la cantidad de reservas activas superpuestas.
- **Disponibilidad por tramos**: en hoteles se cuentan habitaciones libres por tipo y dia; en los demas alojamientos, plazas libres por dia.
- **Solapamiento de reservas**: ingreso inclusivo, egreso exclusivo. Una reserva que termina el dia en que empieza otra no la solapa.
- **Alta de cuenta**: el nombre de usuario debe ser unico (sin distinguir mayusculas/minusculas); las claves se validan de forma exacta.
- **Documento unico por tipo y numero**: no puede haber dos asistentes con la misma combinacion `tipoDocumento + dni`. El numero se compara sin puntos, espacios ni diferencias de mayusculas. Un nacional con `DNI` y un extranjero con `PASAPORTE` pueden compartir el mismo numero sin conflicto.
- **Origen**: se elige de la lista completa de paises; Argentina es el valor por defecto.
- **Capacidad** de alojamiento: entero positivo. No se puede reducir por debajo de la ocupacion maxima por reservas activas.
- **Categoria de hotel**: entero entre 1 y 5 estrellas.
- **Precios de hotel**: un precio por noche para cada tipo de habitacion. Los tipos con habitaciones deben tener precio mayor a cero; los tipos sin habitaciones pueden tener precio 0.
- **Habitaciones de hotel**: cantidades enteras no negativas para 1, 2, 3 y 4 personas; debe haber al menos una habitacion. La capacidad total se calcula como `simples + 2*dobles + 3*triples + 4*cuadruples`.
- **Modificacion de habitaciones**: por cada tipo, no se puede dejar una cantidad menor a la ocupacion maxima simultanea de ese tipo. Si se habilita un tipo nuevo, primero hay que configurar su precio.
- **Precio de la reserva**: se guarda el precio por noche vigente al reservar; si el hotel cambia sus precios, las reservas existentes conservan el suyo.
- **Fechas**: el egreso debe ser posterior al ingreso y ambos son obligatorios.
- **Eventos**: el horario de fin debe ser posterior al de inicio (no cruzan la medianoche).
- **Sin eventos superpuestos por asistente**: no se puede inscribir a un evento cuyo rango horario se pise con otro evento al que ya esta inscripto, sin importar la ciudad. El fin es exclusivo: un evento que termina a las 12:00 no se superpone con otro que empieza a las 12:00.
- **Modificacion de eventos**: si un evento ya tiene asistentes, no se puede cambiar su horario si eso genera una superposicion con otro evento de alguno de ellos.

---

## Decisiones de diseno

- **Identificadores numericos**: el modelo usa `id` en `Usuario`, `Evento` y `Reserva`, y relaciones por id (`idAsistente`, `idAlojamiento`, `Asistente.eventos`), como el UML. Al dar de alta se usa el mayor id existente + 1.
- **Campo `tipo`** en `cuentas.json` para reconstruir el subtipo correcto al leer (resuelve el polimorfismo).
- **Logica de reservas en el servicio**: `GestorReservas` aplica las reglas de capacidad, solapamiento y reserva unica, porque `Alojamiento` no conoce sus reservas en este modelo.
- **Factory de usuarios**: `UsuarioFactory` centraliza la construccion de cuentas para que persistencia y formularios no repitan constructores concretos. La seleccion por `tipo` sigue estando en `PersistenciaJSON` y en los paneles de registro.
- **Observer de reservas**: `GestorReservas` notifica `reservaCreada` y `reservaCancelada` a los observadores registrados. `NotificadorReservas` es el observador concreto de ejemplo y escribe en consola; la interfaz no queda acoplada a esa notificacion.
- **Horario de fin estimado**: el UML solo modela `horario`; se agrego `horarioFin` para poder detectar superposiciones reales entre eventos y validar las inscripciones. La interfaz lo pide en el alta y la modificacion, y lo muestra en la agenda.
- **Cancelacion con 24 hs**: la regla se aplica en `GestorReservas.cancelarReserva` comparando la fecha de ingreso con `LocalDateTime.now()`. No hay reloj configurable: la validacion depende del reloj del sistema.
- **`GestorEventos` fuera del UML**: el diagrama modela solo el dominio; el gestor sigue existiendo para alta, modificacion y agenda.
- **Tipo de documento**: extension al UML (`TipoDocumento`: `DNI`, `PASAPORTE`, `OTRO`) para que la unicidad del documento sea por tipo + numero y no bloquee a un extranjero con el mismo numero que un nacional. Los archivos viejos sin `tipoDocumento` se cargan como `DNI`.
- **Origen por pais**: el combo de origen lista todos los paises ISO 3166-1 en espanol, ordenados alfabeticamente, con **Argentina** seleccionada por defecto. El modelo lo guarda como `String`.
- **Capacidad derivada en hoteles**: `Hotel` calcula `capacidad` a partir de la distribucion de habitaciones. Por eso el alta no pide capacidad para hoteles y la modificacion de capacidad directa se rechaza; se editan las habitaciones.
- **Reserva por habitacion completa**: en hoteles, `Reserva.tipoHabitacion` indica la capacidad de la habitacion y `cantidadPersonas` cuantas personas la ocupan. La disponibilidad se cuenta por tipo y fecha; no se asigna un numero de habitacion.
- **Precio por tipo y snapshot**: cada tipo tiene su precio por noche y la reserva guarda el precio usado, para que un cambio posterior no altere el total historico.
- **Logica mixta**: los hoteles usan disponibilidad por habitaciones; parroquias y espacios siguen usando capacidad por personas.

---

## Limitaciones conocidas

- Las claves se guardan en **texto plano** exclusivamente por la limitacion academica del TPO. No es una practica segura para produccion.
- El parser JSON es un parser sencillo que soporta el subconjunto de JSON usado por la aplicacion (objetos, arreglos, cadenas, numeros, booleanos y `null`). No soporta features avanzadas.
- La ubicacion de los archivos es relativa a la raiz del proyecto: hay que ejecutar el programa desde esa carpeta.
- El horario de fin de cada evento es **estimado**: el modelo no maneja duraciones reales ni eventos que crucen la medianoche.
- La interfaz grafica no fue probada de forma manual exhaustiva (clic por clic); si se verifico que compila y que todos los paneles se construyen y navegan sin errores.
- La regla de cancelacion con 24 hs usa el reloj del sistema; en los datos de ejemplo (noviembre de 2026) las reservas son cancelables mientras falte mas de un dia para el ingreso.
- El precio se muestra y se guarda como snapshot, pero **no se modela un pago** ni se cobra desde la aplicacion.
- La notificacion de reservas es por consola (`NotificadorReservas`); no se persiste un log ni se envia un correo.
- No se asigna un **numero de habitacion** concreto: se controla la cantidad de habitaciones libres por tipo. Tampoco se venden camas sueltas dentro de una habitacion reservada.
- La reduccion de habitaciones se valida contra la ocupacion maxima simultanea de cada tipo, no contra una fecha puntual.

---

## Pruebas realizadas

El arnes historico del 27/10 (54/54) no forma parte del repositorio, por lo que no se puede volver a ejecutar. El 28/10 se corrio una verificacion nueva (tambien externa a la entrega) sobre una copia de los datos, con resultado **56/56 OK**:

**Autenticacion**
- Credenciales correctas e incorrectas.

**Origen**
- Los asistentes de ejemplo tienen origen `Argentina`.
- El combo de alta lista todos los paises, con Argentina seleccionada por defecto.

**Documento de identidad**
- Archivo viejo sin `tipoDocumento`: se carga como `DNI`.
- Documento duplicado con el mismo tipo rechazado.
- Mismo numero con otro tipo permitido (nacional con DNI y extranjero con pasaporte).
- Duplicado con distinto formato (puntos y espacios) rechazado.

**Hoteles**
- Login de `hotelcentro` con precios por tipo, capacidad y habitaciones.
- Precio cargado solo para los tipos con habitaciones.
- Capacidad calculada a partir de las habitaciones.
- Modificacion de habitaciones con recalculo de capacidad.
- Modificacion de precios por tipo.
- Modificacion directa de capacidad rechazada.
- Hotel sin habitaciones rechazado.

**Reservas de hotel**
- Habitacion ocupada y habitacion libre segun solapamiento.
- Cantidad de personas mayor a la capacidad del tipo rechazada.
- Tipo de habitacion inexistente rechazado.
- Reserva de hotel sin tipo rechazada.
- La reserva guarda tipo de habitacion, cantidad de personas y precio.
- Disponibilidad de dobles 5 -> 4 -> 3 al reservar.
- Cambio de precios no altera reservas ya hechas.
- Reduccion de habitaciones por debajo de la ocupacion rechazada.
- Cancelacion: la disponibilidad de dobles vuelve de 3 a 4.

**Eventos**
- Inscripcion duplicada rechazada.
- Id autogenerado de eventos.
- Inscripcion a un evento superpuesto rechazada.
- Inscripcion a un evento contiguo permitida (el fin es exclusivo).
- Inscripcion a otro horario del mismo dia y a otro dia permitida.
- Modificacion de un evento con asistentes rechazada si genera superposicion.
- Modificacion sin superposicion permitida.

**Reservas**
- Cancelacion con mas de 24 hs de anticipacion permitida.
- Cancelacion de una reserva ya cancelada rechazada.
- Cancelacion con menos de 24 hs rechazada.
- Segunda reserva activa rechazada.
- Cancelacion de una reserva futura permitida.

**Persistencia**
- Inscripciones, tipo de documento, `horarioFin`, estado de reserva cancelada y reserva activa persistidos.
- Precios por tipo y habitaciones del hotel persistidos.
- Reserva de hotel persistida con tipo, cantidad de personas y precio.
- Reserva vieja de hotel migrada al primer tipo disponible y reserva de parroquia con tipo 0.
- Hotel de archivo viejo sin precios ni habitaciones: precio 0 y capacidad existente como habitaciones simples.

Ademas, una prueba de humo construyo y navego todos los paneles de los tres roles (publico, asistente, administrador y alojamiento) sobre una copia de los datos.

El 30/10 se detecto que el fuente habia quedado en una version anterior y se reintegro la logica del 28/10 conservando el diseno actual de los paneles. Se recompilo desde cero y se corrio una verificacion nueva sobre una copia de los datos: **48/48 OK** (modelo, servicios, Factory, Observer y round-trip de persistencia) y **smoke test OK** de todos los paneles. Tambien se migraron los datos al esquema nuevo conservando la cuenta local `Santino` y la reserva 5, y se reincorporo `hotelcentro` como id 11.

### Que quedo sin verificar

- La interaccion manual completa de la interfaz (clics, ingreso de datos y mensajes de dialogo).
