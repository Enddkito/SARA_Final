# SARA — Sistema de Análisis de Rendimiento Académico

<p align="center">
  <img src="SARA_Banner.png" alt="Banner SARA" width="100%">
</p>

[![Build](https://img.shields.io/github/actions/workflow/status/Enddkito/SARA_Final/ci.yml?branch=main&style=for-the-badge)](https://github.com/Enddkito/SARA_Final/actions)
[![License](https://img.shields.io/github/license/Enddkito/SARA_Final?color=green&style=for-the-badge)](https://github.com/Enddkito/SARA_Final/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://github.com/Enddkito/SARA_Final)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge&logo=javafx)](https://github.com/Enddkito/SARA_Final)

SARA es una aplicación de escritorio modular para centralizar, procesar y proyectar métricas de rendimiento estudiantil y docente. Provee una interfaz gráfica desarrollada en JavaFX para la carga y gestión de calificaciones, simulación de promedios y un motor predictivo que ayuda a detectar necesidades de tutoría.

## 📌 Tabla de contenidos
- [Características](#-características)
- [Requisitos](#-requisitos)
- [Instalación y ejecución](#-instalación-y-ejecución)
- [Formato de persistencia (CSV)](#-formato-de-persistencia-csv)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Arquitectura y diagramas UML](#-arquitectura-y-diagramas-uml)
- [Contribuir](#-contribuir)
- [Licencia](#-licencia)
- [Contacto](#-contacto)

---

## 🚀 Características

- Panel del docente: carga y edición de aportes bimestrales (pruebas, presenciales, exámenes, talleres y deberes) con guardado en persistencia.
- Panel del estudiante: vista dinámica de asignaturas y matrículas según el usuario autenticado.
- Simulador de notas (RAM): sección independiente que permite proyectar promedios sin alterar datos reales.
- Asistente predictivo: análisis de notas en escalas de 20 y 40 puntos con generación de alertas automatizadas (tutorías, supletorios, seguimiento).
- Gestión de tutorías: calendarización, seguimiento y exportación automática de reportes de acompañamiento pedagógico.
- Validaciones y manejo de excepciones robusto para entradas numéricas y operaciones de I/O.

---

## 🛠️ Requisitos

- Java SE 17 (JDK 17)
- JavaFX 21 (o la versión compatible que tengas instalada)
- Maven o Gradle (según el sistema de construcción usado por el proyecto)
- IntelliJ IDEA (recomendado) u otro IDE con soporte JavaFX

---

## ⚙️ Instalación y ejecución

1. Clona el repositorio:

   git clone https://github.com/Enddkito/SARA_Final.git
   cd SARA_Final

2. Desde el IDE (recomendado):
   - Abre el proyecto en IntelliJ IDEA.
   - Configura JDK 17 como SDK del proyecto.
   - Ejecuta la clase `com.example.sara_ap.HelloApplication` (punto de entrada JavaFX).

3. Uso con Maven (si aplica):

   mvn clean package
   java -jar target/sara-app.jar

4. Uso con Gradle (si aplica):

   ./gradlew build
   ./gradlew run

Notas sobre JavaFX y module-path:
- Si el proyecto no está empaquetado como fat-jar, es posible que necesites pasar el module-path con los módulos JavaFX en las opciones de la JVM:

  java --module-path /ruta/a/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/sara-app.jar

- Recomiendo usar los plugins de JavaFX para Maven/Gradle (e.g., org.openjfx:javafx-maven-plugin) para simplificar la ejecución.

---

## 🗂️ Formato de persistencia de datos (CSV)

Documenta a continuación el esquema de CSV que utiliza la aplicación. Añade ejemplos reales si los tienes.

- users.csv (credenciales y roles)
  - id,role,email,hashed_password,full_name
  - Ejemplo: 1,student,juan.perez@uni.edu,$2a$10$...,Juan Pérez

- courses.csv
  - course_id,code,name,credits
  - Ejemplo: 1,INF101,Introducción a la Informática,3

- enrollments.csv
  - enrollment_id,student_id,course_id,period,group
  - Ejemplo: 1,1,1,2026-1,A

- grades.csv
  - grade_id,enrollment_id,bimestre,type,score,max_score,date,notes
  - Ejemplo: 1,1,1,Parcial,18,20,2026-05-10,"Buen desempeño"

Buenas prácticas y validaciones aplicadas por la app:
- Sanitización de saltos de línea y espacios en blanco en campos de texto.
- Validación numérica (rango de score entre 0 y max_score).
- Manejo de errores por filas corruptas: se registran y omiten sin detener la carga completa.

---

## 📂 Estructura del proyecto

```text
src/
└── main/
    ├── java/
    │   └── com/example/sara_ap/
    │       ├── HelloApplication.java      # Punto de entrada de la aplicación (JavaFX)
    │       ├── controller/                # Controladores para las vistas FXML
    │       │   ├── HelloController.java   # Controlador del login y roles
    │       │   ├── StudentController.java # Controlador del panel del estudiante
    │       │   └── TeacherController.java # Controlador del panel del docente
    │       ├── domain/                    # Entidades del dominio (POO)
    │       │   ├── Course.java
    │       │   ├── Enrollment.java
    │       │   ├── Professor.java
    │       │   └── Student.java
    │       └── services/                  # Lógica de negocio
    │           ├── PredictiveService.java
    │           ├── SimulationService.java
    │           └── TutoriaDocumentService.java
    └── resources/                         # Recursos (FXML, imágenes, estilos)
        └── com/example/sara_ap/
            ├── hello-view.fxml
            ├── login-view.fxml
            ├── student-view.fxml
            └── teacher-view.fxml
```

---

## 🏗️ Arquitectura y diagramas UML

- El proyecto se organiza en capas: Controller (UI), Services (lógica), Domain (entidades) e Infrastructure (persistencia CSV).
- Si dispones de diagramas UML o de arquitectura, añádelos en `/docs/` o en `/docs/uml/` y enlázalos aquí (por ejemplo `docs/architecture.png`).

---

## ✅ Cómo contribuir

1. Abre un issue describiendo la mejora o bug.
2. Crea una rama con nombre `feature/xxx` o `fix/xxx` desde `main`.
3. Realiza commits claros y atómicos.
4. Abre un Pull Request describiendo los cambios y adjunta capturas si aplica.

Sugerencia: añade un archivo `CONTRIBUTING.md` y `CODE_OF_CONDUCT.md` para facilitar aportes externos.

---

## 📄 Licencia

Este proyecto utiliza la licencia que se encuentra en el archivo `LICENSE` del repositorio.

---

## ✉️ Contacto

- Autor: Enddkito — https://github.com/Enddkito
- Para soporte o preguntas, usa Issues en este repositorio.
