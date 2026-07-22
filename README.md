# CNM - Centro de Nivelacion

Aplicacion de escritorio JavaFX para la gestion de tareas dirigidas de un
centro de nivelacion. Proyecto Final de Programacion Orientada a Objetos -
Escuela de Formacion de Tecnologos, EPN (2026-A).

## Integrantes
- Mateo Josue Rodriguez Quevedo


## 1. Que resuelve el sistema
Centraliza la gestion de deberes ("tareas") entre profesores y estudiantes:
el profesor publica tareas, el estudiante sube su entrega, y el profesor
la califica. Un administrador gestiona las cuentas de profesores y
estudiantes, y consulta reportes generales.

## 2. Roles

| Rol | Que puede hacer |
|---|---|
| **Administrador** | CRUD de cuentas de Profesores y Estudiantes, ver reportes/estadisticas, configuracion basica del sistema. Ve Tareas y Entregas en solo lectura. |
| **Profesor** | CRUD completo de sus propias Tareas (con archivo adjunto), consulta y **califica** las Entregas de sus tareas, edita su propio perfil. |
| **Estudiante** | Consulta las Tareas disponibles (solo lectura), CRUD de sus propias Entregas (sube archivo), edita su propio perfil. |

## 3. Arquitectura y paquetes

```
model/      Persona (abstracta) -> Usuario -> Administrador / Profesor / Estudiante
            Tarea, Entrega  (clases de la tematica, sin herencia de Persona)
dao/        ICRUD<T> (interfaz) implementada por AdministradorDAO, ProfesorDAO,
            EstudianteDAO, TareaDAO y EntregaDAO. UsuarioDAO no implementa
            ICRUD porque su responsabilidad es autenticacion (login/registro),
            no un CRUD de un recurso.
controller/ Un controlador por pantalla. VistaConUsuario es la interfaz que
            implementan las pantallas que necesitan saber quien inicio sesion.
view/       Los .fxml. dashboard.fxml es UNICO y se reutiliza para los 3 roles.
db/         Conexion (Singleton) hacia PostgreSQL en la nube.
util/       PasswordUtil (hash SHA-256) y Navegador (cambio de escena).
app/        Main (JavaFX Application) y Launcher (para el .exe).
```

### Decision de diseno importante
Tu proyecto ya tenia archivos `ProfesorController`/`ProfesorDAO` y
`EstudianteController`/`EstudianteDAO` ademas de `TareaController` y
`EntregaController`. Para no duplicar pantallas (Tareas y Entregas ya
cubren lo que hace cada rol con esos recursos), se decidio que:
- `TareaController` / `EntregaController` = las pantallas de CRUD de la
  tematica, compartidas y adaptadas por rol.
- `AdministradorController` = pantalla de "Gestion de usuarios" (con
  pestañas de Reportes y Configuracion) que usa el Administrador.
- `ProfesorController` / `EstudianteController` = pantalla **"Mi Perfil"**
  para que cada rol edite sus propios datos y contrasena.

Esto es exactamente lo que deberias poder explicar en la defensa si te
preguntan por que existen esos archivos.

## 4. Seguridad: credenciales de la base de datos
**Las credenciales NUNCA se escriben en el codigo.** `db/Conexion.java` las
lee desde `src/main/resources/db.properties`, que esta en `.gitignore` y
por lo tanto NO se sube a GitHub.

Pasos:
1. Copia `src/main/resources/db.properties.example`
2. Renombra la copia a `db.properties` (misma carpeta)
3. Reemplaza los valores por los datos reales de tu base de datos en la nube

Agrega esta linea a tu `.gitignore` si no esta ya:
```
db.properties
```

> Importante: si ya compartiste una contrasena de base de datos en algun
> chat o repositorio, cambiala (reset) desde el panel de tu proveedor
> (Neon, Supabase, etc.) antes de la entrega.

## 5. Puesta en marcha

1. Ejecuta `database/script.sql` completo contra tu base PostgreSQL en la nube.
2. Configura `db.properties` (paso anterior).
3. Abre el proyecto en IntelliJ (Maven lo reconoce automaticamente).
4. Ejecuta con `mvn javafx:run` o desde el boton Run de `Main.java`.

### Usuarios de prueba (ya insertados por el script SQL)
Contrasena para los tres: `123456`

| Rol | Correo |
|---|---|
| Administrador | admin@cnm.edu.ec |
| Profesor | laura.torres@cnm.edu.ec |
| Estudiante | kevin.salazar@cnm.edu.ec |

## 6. Generar el ejecutable .exe

```bash
mvn clean package
```
Esto genera `target/ProyectoFinalPOO-CNM-1.0.0.jar` (jar con todas las
dependencias, gracias a maven-shade-plugin), usando `app.Launcher` como
clase principal para evitar el error "JavaFX runtime components are
missing" al hacer doble clic.

Luego, con **Launch4j**:
1. Output file: `dist/CNM.exe`
2. Jar: el jar generado en `target/`
3. Main class: ya viene definida en el manifest del jar (no hace falta
   repetirla en Launch4j)
4. JRE: minimum version 21
5. Genera el .exe y coloca la copia final en la carpeta `/dist` del repositorio

Si `mvn package` marca un error relacionado con `module-info`, es un
problema conocido al mezclar JavaFX modular con jars "gordos" — cuéntame
el error exacto y lo resolvemos.

## 7. Sugerencia de commits (minimo 6 que pide la rubrica)
1. `Estructura inicial del proyecto`
2. `Modelo y herencia: Persona, Usuario, Administrador, Profesor, Estudiante`
3. `Conexion a base de datos y script SQL`
4. `Login, registro y dashboard reutilizable por rol`
5. `CRUD de Tareas y Entregas con validaciones`
6. `CSS y diseño aplicado por rol`
7. `Modulo de reportes y configuracion`
8. `Ejecutable .exe generado`

## 8. Pilares de POO aplicados (para la defensa)
- **Encapsulamiento**: todos los atributos de `model/` son privados, acceso solo por get/set.
- **Herencia**: `Persona` (abstracta) -> `Usuario` -> `Administrador` / `Profesor` / `Estudiante`.
- **Polimorfismo**: `getDescripcionRol()` se comporta distinto en cada subclase; `UsuarioDAO.mapearUsuario()` devuelve el subtipo correcto segun el rol guardado en la BD.
- **Abstraccion**: interfaz `ICRUD<T>` (implementada por 5 DAO distintos) e interfaz `VistaConUsuario` (implementada por los controladores que necesitan el usuario logueado).
