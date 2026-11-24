# TPO Paradigma Orientado a Objetos - UADE

Sistema de gestion de alojamientos y eventos para la visita del Papa Leon XIV a Argentina.

La aplicacion permite a los asistentes consultar eventos, inscribirse en actividades y reservar alojamientos; a los establecimientos administrar su informacion; y a un administrador gestionar la agenda.

El modelo de clases sigue el diagrama UML del proyecto (`uml+/uml.pdf`), que es la fuente de verdad: herencia, atributos, metodos y relaciones por identificador. El diagrama actual ya incluye `horarioFin`, `direccion`, `capacidadMaxima` y `estado` en `Evento` (superposiciones, ubicacion y cancelacion), `tipoDocumento` en `Asistente` (unicidad por tipo + numero), `preciosPorTipo` y `habitaciones` en `Hotel`, `tipoHabitacion`, `cantidadPersonas` y `precioPorNoche` en `Reserva` (reserva de habitacion completa), y `Notificacion`. Quedan fuera del diagrama los gestores, la persistencia y la interfaz.

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
│   ├── reservas.json
│   └── notificaciones.json
├── notas/
│   ├── README.md
│   ├── CODIGO.md
│   ├── modelo.md
│   ├── servicios.md
│   ├── alma.md
│   └── Informe_TP7.doc
└── uml+/
    ├── uml.puml
    ├── uml.pdf
    └── uml-actualizado.pdf / .png / .svg
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

### Archivos de datos obligatorios

La aplicacion necesita estos archivos dentro de `datos/`:

| Archivo | Obligatorio | Si no existe | Si esta vacio |
|---|---|---|---|
| `cuentas.json` | Si | Aviso y la app no arranca. | Se carga como lista vacia. |
| `eventos.json` | Si | Aviso y la app no arranca. | Se carga como lista vacia. |
| `reservas.json` | Si | Aviso y la app no arranca. | Se carga como lista vacia. |
| `notificaciones.json` | No | Arranca sin notificaciones. | Se carga como lista vacia. |

Si falta alguno de los tres primeros, al iniciar aparece un dialogo que lista **todos** los faltantes y la carpeta donde se esperaban, y la aplicacion termina con codigo 1:

```text
Faltan archivos de datos obligatorios en:
C:\...\tp7\datos

  - cuentas.json
  - eventos.json

Restaura los archivos desde la carpeta datos/ del proyecto
y ejecuta la aplicacion desde la raiz.

notificaciones.json es opcional: se crea automaticamente.
```

Un archivo **existente pero vacio** es valido: se interpreta como una lista vacia. Con `cuentas.json` vacio la aplicacion abre, pero no hay ninguna cuenta para iniciar sesion; solo funcionan las pantallas publicas y los registros. No se crea un administrador por defecto.

Un archivo con JSON invalido o con una estructura que no sea un arreglo sí impide el arranque, con un mensaje de formato.

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
{ "id": 101, "fecha": "2026-11-08", "horario": "10:00", "horarioFin": "12:00", "actividad": "Misa de apertura", "ciudad": "LUJAN", "direccion": "Basilica de Lujan, Av. Nuestra Senora de Lujan 555", "capacidadMaxima": 0, "estado": "ACTIVA" }
```

`direccion` es obligatoria en el alta y la modificacion. `capacidadMaxima` es la cantidad maxima de asistentes y `0` significa sin limite. `estado` vale `ACTIVA` o `CANCELADA`. Los archivos viejos que no traen `direccion` la cargan como `A confirmar`, los que no traen `capacidadMaxima` se cargan con `0` (sin limite) y los que no traen `estado` se asumen `ACTIVA`, para no impedir el arranque.

### `reservas.json`

Arreglo de reservas:

```json
{ "id": 3, "idAsistente": 4, "idAlojamiento": 7, "tipoHabitacion": 2, "cantidadPersonas": 1, "precioPorNoche": 45000.0, "ingreso": "2026-11-09", "egreso": "2026-11-11", "estado": "ACTIVA" }
```

`tipoHabitacion` indica la capacidad de la habitacion (1 a 4); `0` significa que no aplica (parroquia o espacio). `cantidadPersonas` es cuantas personas se alojan en esa habitacion y `precioPorNoche` es el precio vigente al momento de reservar. En alojamientos que no son hoteles, `tipoHabitacion` es `0`, `cantidadPersonas` es `1` y `precioPorNoche` es `0`.

Las fechas se guardan como `AAAA-MM-DD`, los horarios como `HH:mm` (`horario` es el inicio y `horarioFin` el fin estimado) y las enumeraciones con su nombre en mayusculas.

### `notificaciones.json`

Arreglo de notificaciones persistidas:

```json
{ "id": 1, "idDestinatario": 7, "fechaHora": "2026-10-30T18:20:00", "tipo": "RESERVA", "mensaje": "Nueva reserva #6 de Pedro Lopez en Hotel Gran Lujan (habitacion para 2 personas) del 2026-11-20 al 2026-11-22.", "leida": false }
```

- `idDestinatario` es el id de la cuenta que debe verla.
- `tipo` vale `EVENTO` o `RESERVA`.
- `leida` indica si el usuario ya abrio la ventana de notificaciones.
- Si el archivo no existe, la aplicacion arranca con una lista vacia.

---

## Como funcionan las notificaciones

Las notificaciones son internas de la aplicacion: no se envian por correo ni push. Cada aviso se dirige a una cuenta por `idDestinatario`, se guarda en `notificaciones.json` y se muestra en una ventana Swing. El circuito completo es:

```text
Reservar / cancelar reserva / alta, modificacion o cancelacion de evento
        |
        v
GestorReservas / GestorEventos  --notifica-->  GestorNotificaciones
                                                      |
                                      1. crea una Notificacion por destinatario
                                      2. agrega el lote y guarda notificaciones.json
                                      3. avisa a los NotificacionListener
                                                      |
                                                      v
                                        VentanaPrincipal (EDT)
                                          - actualiza "Notificaciones (n)"
                                          - refresca VentanaNotificaciones si esta abierta
                                                      |
                                              al cerrar: marca leidas
```

### Piezas

| Clase | Rol en el circuito |
|---|---|
| `Notificacion` | Dato persistido: destinatario, fecha y hora, tipo (`EVENTO`/`RESERVA`), mensaje y `leida`. |
| `GestorNotificaciones` | Implementa `ReservaObserver` y `EventoObserver`: arma los avisos, decide destinatarios, persiste y avisa a la interfaz. |
| `NotificacionListener` | Interfaz de salida hacia Swing; `VentanaPrincipal` la implementa. |
| `VentanaNotificaciones` | `JDialog` no modal con el listado del usuario; al cerrarse marca todo como leido. |
| `PersistenciaJSON` | Carga y guarda `notificaciones.json`; si no existe o esta vacio, arranca sin avisos. |

`Main` registra una sola vez el gestor como observador de eventos y reservas, y `VentanaPrincipal` se registra como listener en su constructor. `NotificadorReservas` es otro observador de reservas que solo imprime en consola y no participa de este circuito.

### Destinatarios

| Cambio | Quien recibe el aviso |
|---|---|
| Alta de evento | Todas las cuentas registradas. |
| Modificacion de evento | Solo los asistentes inscriptos en ese evento. |
| Cancelacion de evento | Solo los asistentes inscriptos en ese evento. |
| Alta de reserva | El asistente y el alojamiento. |
| Cancelacion de reserva | El asistente y el alojamiento. |

No se generan avisos por inscripciones o desinscripciones, altas de cuentas ni cambios en alojamientos.

### Persistencia

- Cada aviso nuevo recibe el proximo id (mayor existente + 1), `fechaHora = LocalDateTime.now()` y `leida = false`.
- El lote se guarda **una sola vez** por operacion, antes de avisar a los listeners. Un alta de evento genera un aviso por usuario y una unica escritura del archivo.
- Si un observer o un listener lanza una excepcion, se captura y se informa en `stderr`; la reserva o el evento ya quedaron guardados y la operacion no se deshace.
- Si el archivo no existe o esta vacio, la lista arranca vacia.

### Aviso en la interfaz

- Al llegar un aviso, `VentanaPrincipal` lo ignora si no hay sesion o si el destinatario no es el usuario logueado.
- Si corresponde, actualiza el boton: muestra `Notificaciones (n)` en rojo y negrita cuando hay no leidas.
- Si `VentanaNotificaciones` ya esta abierta y visible, la refresca; si no, no se abre: el usuario la abre con el boton.
- Al iniciar sesion, si el usuario tiene notificaciones no leidas, la ventana se abre automaticamente (`VentanaPrincipal.setSesion`). Asi se ven los avisos generados mientras la cuenta estaba desconectada.
- Al cerrar sesion se cierra la ventana y se oculta el boton.

### Lectura

- Abrir la ventana **no** marca las notificaciones como leidas: se marcan todas al cerrarla con "Cerrar" o con la X.
- El cambio se persiste en `notificaciones.json` y el boton vuelve a "Notificaciones" sin contador.
- La ventana muestra tanto las leidas como las no leidas; las no leidas llevan un `*` adelante.
- Las notificaciones **no se borran nunca**: no hay purga automatica ni limite de cantidad. Solo desaparecen si se borra o vacia `notificaciones.json` con la aplicacion cerrada.
- Como cada guardado reescribe el archivo completo desde la lista en memoria, editar el JSON con la aplicacion abierta no tiene efecto: el proximo guardado lo pisa.

---

## Listado de clases y responsabilidades

### `modelo`

| Clase | Responsabilidad |
|---|---|
| `Ciudad` (enum) | Ciudades posibles: `CABA`, `CORDOBA`, `LUJAN`, `CLAYPOLE`. |
| `EstadoReserva` (enum) | Estados de una reserva: `ACTIVA`, `CANCELADA`. |
| `EstadoEvento` (enum) | Estados de un evento: `ACTIVA`, `CANCELADA`. |
| `Usuario` (abstracta) | Datos comunes de una cuenta (`id`, `nombreUsuario`, `clave`, `nombre`). Metodos `validarClave`, `getTipoCuenta()` y `getRol()` (estos dos abstractos), consultas `esAsistente()` / `esAlojamiento()` / `esAdministrador()`, y `equals` / `hashCode` por nombre de usuario normalizado. |
| `Asistente` | Datos del asistente (`tipoDocumento`, `dni`, `origen : String`, `Set<Integer> eventos`). `getEventos()` devuelve una copia no modificable y `inscribir(idEvento)` / `desinscribir(idEvento)` estan sincronizados. |
| `Administrador` | Cuenta de administrador (precargada, no se registra por interfaz). |
| `Alojamiento` (abstracta) | Datos comunes (`ciudad`, `direccion`, `capacidad`). Metodo abstracto `descripcion()` y consulta `esHotel()`. |
| `Parroquia` | Alojamiento con `responsable`. |
| `Hotel` | Alojamiento con `categoria` (1 a 5 estrellas), `preciosPorTipo` y `habitaciones` por cantidad de personas. La capacidad se calcula a partir de las habitaciones. |
| `EspacioOrg` | Alojamiento con `organizador`. |
| `Evento` | Evento de la agenda (`id`, `fecha`, `horario` de inicio, `horarioFin` estimado, `actividad`, `ciudad`, `direccion`, `capacidadMaxima`, `estado`). `capacidadMaxima` es la cantidad maxima de asistentes; `0` significa sin limite. `seSuperpone(otro)` compara fecha y rangos horarios, y `cancelar()` / `estaActiva()` manejan la cancelacion. |
| `Reserva` | Reserva (`id`, `idAsistente`, `idAlojamiento`, `tipoHabitacion`, `cantidadPersonas`, `precioPorNoche`, `ingreso`, `egreso`, `estado`). En hoteles ocupa una habitacion completa; en otros alojamientos representa una persona. Metodos `seSuperpone(otra : Reserva)`, `cancelar()` y `estaActiva()`. |
| `UsuarioFactory` | Fabrica simple que centraliza la creacion de cada subtipo de `Usuario` (`Administrador`, `Asistente`, `Parroquia`, `Hotel`, `EspacioOrg`). La seleccion del tipo se hace en persistencia/formularios y la construccion pasa siempre por la fabrica. |
| `Notificacion` | Notificacion persistida para una cuenta (`idDestinatario`, `fechaHora`, `tipo`, `mensaje`, `leida`). |

### `servicios`

| Clase | Responsabilidad |
|---|---|
| `GestorUsuarios` | Registro, autenticacion, unicidad de nombre de usuario, busqueda por id y asignacion de ids. |
| `GestorEventos` | Alta, modificacion, cancelacion y consulta de eventos; inscripcion/desinscripcion por id, control de superposiciones y notificacion a observadores. |
| `GestorReservas` | Creacion, consulta y cancelacion de reservas, calculo de disponibilidad y notificacion a observadores. |
| `GestorAlojamientos` | Consulta y modificacion de datos y capacidad, y consulta de huespedes por periodo. |
| `ReservaObserver` | Interfaz de observador con eventos `reservaCreada` y `reservaCancelada` (implementacion vacia por defecto). |
| `EventoObserver` | Interfaz de observador con eventos `eventoCreado`, `eventoModificado` y `eventoCancelado`. |
| `NotificadorReservas` | Observador concreto que informa por consola cuando se crea o cancela una reserva. |
| `GestorNotificaciones` | Implementa los dos observers, decide los destinatarios, persiste las notificaciones y avisa a la interfaz. |
| `NotificacionListener` | Interfaz que usa la ventana principal para enterarse de una notificacion nueva. |

### `persistencia`

| Clase | Responsabilidad |
|---|---|
| `ParserJSON` | Parsea texto JSON a objetos (`Map`, `List`, `String`, `Number`, `Boolean`, `null`) y rechaza entradas invalidas. |
| `PersistenciaJSON` | Carga y guarda los cuatro archivos JSON. Arma el texto con `StringBuilder`, escribe con `BufferedWriter` y lee con `BufferedReader` + `ParserJSON`, siempre en UTF-8. |

### `interfaz`

| Clase | Responsabilidad |
|---|---|
| `VentanaPrincipal` | `JFrame` principal, centrado, con la navegacion entre paneles, la sesion actual y el boton de notificaciones con contador de no leidas. |
| `PanelInicioSesion` | Menu sin sesion: login y accesos publicos (registro, agenda, alojamientos). |
| `PanelRegistroAsistente` | Formulario de alta de asistente. |
| `PanelRegistroAlojamiento` | Formulario de alta de alojamiento (con subtipo y campo extra segun el tipo). |
| `PanelAsistente` | Menu del asistente: agenda e inscripciones, mis inscripciones, busqueda de alojamientos y mis reservas. |
| `PanelAlojamiento` | Menu del alojamiento. |
| `PanelAdministrador` | Menu del administrador: alta, modificacion y cancelacion de eventos. |
| `PanelAgenda` | Listado de eventos por fecha con su estado, "mis inscripciones" y gestion de inscripciones. |
| `PanelBusquedaAlojamientos` | Listado de alojamientos por ciudad. |
| `PanelReservas` | Reservar, consultar y cancelar la reserva activa; muestra el historial de reservas canceladas en modo lectura. |
| `VentanaNotificaciones` | Ventana no modal con las notificaciones del usuario; al cerrarse las marca como leidas. |
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
- `Evento` y `Alojamiento` usan `Ciudad`; `Evento` usa `EstadoEvento` y `Reserva` usa `EstadoReserva` como tipo de atributo.
- `GestorEventos` mantiene la coleccion de eventos (no esta modelado en el UML, pero es necesario para la aplicacion).
- `UsuarioFactory`, `ReservaObserver`, `EventoObserver`, `NotificadorReservas`, `GestorNotificaciones` y `VentanaNotificaciones` son infraestructura de la aplicacion (Factory, Observer y ventana de notificaciones) y no estan modelados en el UML; no cambian las relaciones del dominio. `Notificacion` sí forma parte del diagrama.

---

## Reglas de negocio

- **Sin reservas activas superpuestas por asistente**, considerando todos los alojamientos. Se puede tener mas de una reserva activa si los periodos no se cruzan; no se puede reservar un periodo que se superponga con otra reserva activa propia (las canceladas quedan como historico).
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
- **Eventos**: el horario de fin debe ser posterior al de inicio (no cruzan la medianoche) y la direccion es obligatoria.
- **Capacidad de evento**: entero no negativo; `0` significa sin limite. No se puede inscribir a un evento que ya alcanzo su capacidad, y una reduccion no puede dejar la capacidad por debajo de la cantidad de asistentes ya inscriptos.
- **Cancelacion de eventos**: solo un evento `ACTIVA` se puede modificar, cancelar o usar para inscribirse. El evento cancelado conserva sus datos con estado `CANCELADA` y las inscripciones quedan como historial; no se puede reactivar. Un evento cancelado no bloquea la inscripcion a otro que se superponga en horario.
- **Sin eventos superpuestos por asistente**: no se puede inscribir a un evento cuyo rango horario se pise con otro evento al que ya esta inscripto, sin importar la ciudad. El fin es exclusivo: un evento que termina a las 12:00 no se superpone con otro que empieza a las 12:00.
- **Modificacion de eventos**: si un evento ya tiene asistentes, no se puede cambiar su horario si eso genera una superposicion con otro evento de alguno de ellos.

---

## Decisiones de diseno

- **Identificadores numericos**: el modelo usa `id` en `Usuario`, `Evento` y `Reserva`, y relaciones por id (`idAsistente`, `idAlojamiento`, `Asistente.eventos`), como el UML. Al dar de alta se usa el mayor id existente + 1.
- **Campo `tipo`** en `cuentas.json` para reconstruir el subtipo correcto al leer (resuelve el polimorfismo).
- **Logica de reservas en el servicio**: `GestorReservas` aplica las reglas de capacidad, disponibilidad y no superposición de reservas activas, porque `Alojamiento` no conoce sus reservas en este modelo.
- **Factory de usuarios**: `UsuarioFactory` centraliza la construccion de cuentas para que persistencia y formularios no repitan constructores concretos. La seleccion por `tipo` sigue estando en `PersistenciaJSON` y en los paneles de registro.
- **Observer de reservas y eventos**: `GestorReservas` notifica `reservaCreada`/`reservaCancelada` y `GestorEventos` notifica `eventoCreado`/`eventoModificado`/`eventoCancelado`. `NotificadorReservas` sigue escribiendo en consola como traza.
- **Notificaciones persistentes**: `GestorNotificaciones` implementa los dos observers, decide los destinatarios y guarda cada aviso en `notificaciones.json`. El alta de un evento se avisa a todos los usuarios; la modificacion y la cancelacion solo a los asistentes inscriptos; las reservas avisan al asistente y al alojamiento.
- **Boton y ventana de notificaciones**: `VentanaPrincipal` muestra `Notificaciones (n)` en rojo cuando hay no leidas. Al abrir `VentanaNotificaciones` se listan los avisos del usuario y al cerrarla se marcan como leidos y se persiste el cambio.
- **Horario de fin estimado**: el UML original solo modelaba `horario`; se agrego `horarioFin`, que ya forma parte del diagrama actual, para poder detectar superposiciones reales entre eventos y validar las inscripciones. La interfaz lo pide en el alta y la modificacion, y lo muestra en la agenda.
- **Direccion del evento**: el UML original tampoco modelaba donde se realiza el evento; se agrego `Evento.direccion`, que ya forma parte del diagrama. Se pide en el alta y la modificacion, se muestra en la agenda y se incluye en el mensaje de la notificacion. Los eventos viejos sin el campo se cargan como `A confirmar`.
- **Estado de evento**: se agrego `EstadoEvento`, ya incluido en el diagrama, para cancelar un evento sin borrarlo. El administrador lo hace desde "Cancelar evento"; la agenda muestra el estado, los inscriptos reciben un aviso y las inscripciones se conservan como historial. Los eventos viejos sin `estado` se cargan como `ACTIVA`.
- **Capacidad de evento**: `Evento.capacidadMaxima` se controla contra la cantidad de asistentes inscriptos; `0` significa sin limite. La validacion de cupo vive en `GestorEventos.inscribir`, y una reduccion de capacidad se rechaza si deja menos lugares que inscriptos.
- **Cancelacion con 24 hs**: la regla se aplica en `GestorReservas.cancelarReserva` comparando la fecha de ingreso con `LocalDateTime.now()`. No hay reloj configurable: la validacion depende del reloj del sistema.
- **`GestorEventos` fuera del UML**: el diagrama modela solo el dominio; el gestor sigue existiendo para alta, modificacion, cancelacion y agenda.
- **Tipo de documento**: `TipoDocumento` (`DNI`, `PASAPORTE`, `OTRO`) forma parte del diagrama actual y permite que la unicidad del documento sea por tipo + numero, sin bloquear a un extranjero con el mismo numero que un nacional. Los archivos viejos sin `tipoDocumento` se cargan como `DNI`.
- **Origen por pais**: el combo de origen lista todos los paises ISO 3166-1 en espanol, ordenados alfabeticamente, con **Argentina** seleccionada por defecto. El modelo lo guarda como `String`.
- **Capacidad derivada en hoteles**: `Hotel` calcula `capacidad` a partir de la distribucion de habitaciones. Por eso el alta no pide capacidad para hoteles y la modificacion de capacidad directa se rechaza; se editan las habitaciones.
- **Reserva por habitacion completa**: en hoteles, `Reserva.tipoHabitacion` indica la capacidad de la habitacion y `cantidadPersonas` cuantas personas la ocupan. La disponibilidad se cuenta por tipo y fecha; no se asigna un numero de habitacion.
- **Precio por tipo y snapshot**: cada tipo tiene su precio por noche y la reserva guarda el precio usado, para que un cambio posterior no altere el total historico.
- **Logica mixta**: los hoteles usan disponibilidad por habitaciones; parroquias y espacios siguen usando capacidad por personas.
- **Menu del asistente sin accesos duplicados**: cada opcion abre una pantalla distinta (agenda, mis inscripciones, busqueda de alojamientos y reservas). Las acciones de reservar, consultar y cancelar viven dentro de `PanelReservas`, que ademas muestra el historial de reservas canceladas; ese historial es de solo lectura y se apoya en `GestorReservas.reservasDeAsistente`.
- **Identidad de cuentas por nombre normalizado**: `Usuario.equals` y `hashCode` usan el nombre de usuario sin espacios externos y en minusculas. Por eso `Main`, `PersistenciaJSON` y `GestorUsuarios` trabajan con `LinkedHashSet<Usuario>` y un `cuentas.json` con nombres equivalentes se rechaza al cargar.
- **Colecciones de solo lectura hacia afuera**: `Asistente.getEventos()` y `GestorUsuarios.getUsuarios()` devuelven copias no modificables, y `GestorEventos.getEventos()` devuelve una copia. `GestorReservas.getReservas()` sigue devolviendo la lista interna.
- **Concurrencia acotada**: `GestorEventos`, `GestorUsuarios` y los metodos de `Asistente` estan sincronizados, y `PersistenciaJSON.escribirArchivo` serializa las escrituras. `PanelAgenda` ejecuta la inscripcion en un `SwingWorker`; `GestorReservas`, `GestorAlojamientos` y `GestorNotificaciones` no estan sincronizados y la aplicacion esta pensada para una sesion local.

---

## Limitaciones conocidas

- Las claves se guardan en **texto plano** exclusivamente por la limitacion academica del TPO. No es una practica segura para produccion.
- El parser JSON es un parser sencillo que soporta el subconjunto de JSON usado por la aplicacion (objetos, arreglos, cadenas, numeros, booleanos y `null`). No soporta features avanzadas.
- La ubicacion de los archivos es relativa a la raiz del proyecto: hay que ejecutar el programa desde esa carpeta.
- Si falta `cuentas.json`, `eventos.json` o `reservas.json` la aplicacion no arranca: muestra un aviso con todos los faltantes y la carpeta esperada. Un archivo existente pero vacio sí se acepta y se carga como lista vacia.
- El horario de fin de cada evento es **estimado**: el modelo no maneja duraciones reales ni eventos que crucen la medianoche.
- La capacidad de un evento se controla por cantidad de asistentes inscriptos; no hay lista de espera. La desinscripcion libera el lugar.
- La interfaz grafica no fue probada de forma manual exhaustiva (clic por clic); sí se verifico que compila y que todos los paneles se construyen y navegan sin errores.
- La regla de cancelacion con 24 hs usa el reloj del sistema; en los datos de ejemplo (noviembre de 2026) las reservas son cancelables mientras falte mas de un dia para el ingreso.
- El precio se muestra y se guarda como snapshot, pero **no se modela un pago** ni se cobra desde la aplicacion.
- Las notificaciones son internas de la aplicacion: se guardan en `notificaciones.json` y se muestran en una ventana Swing; no se envian por correo ni push. `NotificadorReservas` sigue escribiendo en consola como traza.
- `notificaciones.json` no tiene purga automatica; las notificaciones se acumulan hasta que se borra el archivo manualmente.
- La sincronizacion cubre las inscripciones, las cuentas y la escritura de archivos, pero no todas las operaciones: `GestorReservas`, `GestorAlojamientos` y `GestorNotificaciones` no son thread-safe. La aplicacion esta pensada para una sesion local.
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

El 30/10 se detecto que el fuente habia quedado en una version anterior y se reintegro la logica del 28/10 conservando el diseno actual de los paneles. Se recompilo desde cero y se corrio una verificacion nueva sobre una copia de los datos: **48/48 OK** (modelo, servicios, Factory, Observer y round-trip de persistencia), **18/18 OK** de notificaciones y **smoke test OK** de todos los paneles. Tambien se migraron los datos al esquema nuevo conservando la cuenta local `Santino` y la reserva 5, y se reincorporo `hotelcentro` como id 11.

**Notificaciones**
- Alta de evento avisada a todos los usuarios.
- Modificacion de evento avisada solo a los asistentes inscriptos.
- Reserva creada y cancelada avisada al asistente y al alojamiento.
- Persistencia en `notificaciones.json`.
- Contador de no leidas y marcado como leidas.
- Listener de interfaz notificado por cada aviso nuevo.

El 31/10 se unifico el menu del asistente (un acceso por pantalla) y se agrego el historial de reservas canceladas en `PanelReservas`. Compila limpio y se verifico `GestorReservas.reservasDeAsistente` sobre los datos de ejemplo: Pedro (id 4) obtiene su reserva activa y la cancelada, ordenadas por ingreso descendente. La prueba manual de la interfaz sigue pendiente.

El 10/11 se agregaron colecciones (`Set`), E/S con buffers, sincronizacion y consultas polimorficas. Se recompilo y se corrio una verificacion nueva sobre una copia de los datos: **prueba de concurrencia** (dos hilos intentaron inscribir a Pedro en el evento 102: hubo 1 exito y 1 rechazo, y quedo una sola inscripcion persistida), **round-trip de `cuentas.json`** (11 cuentas, orden conservado y `eventos: [102]` para Pedro) y **smoke test OK** de los paneles. Compila con el *note* de `unchecked` de `PersistenciaJSON`.

El 23/11 se agrego la capacidad maxima de eventos (`0` = sin limite). Se corrio una verificacion externa de **14/14 OK**: capacidad guardada, segundo inscripto rechazado por cupo, cambio a `0` habilitando nuevas inscripciones, reduccion por debajo de los inscriptos rechazada, capacidad igual a los inscriptos permitida, capacidad negativa rechazada, round-trip de `eventos.json`, archivo viejo sin el campo cargado como `0` y datos de ejemplo cargados con capacidad `0`. Ademas, una prueba de concurrencia con dos hilos y capacidad 1 obtuvo 1 exito y 1 rechazo, con una sola inscripcion persistida.

### Que quedo sin verificar

- La interaccion manual completa de la interfaz (clics, ingreso de datos y mensajes de dialogo).
