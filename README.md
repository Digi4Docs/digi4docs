# Digi4Docs
## The Project
The for Digi4Docs is a tool what can help schools to document students' digital skills. This software is a simple prototype that was developed as part of my master's thesis. The software provides the following functionalities:

### Security / Login
The application is password protected. Each user receives a personal access with e-mail and password. Everyone can change his own profile data. Furthermore, a forgotten password function is available.

### Administration
Administrators have the most rights in the software. The following functionalities are available.

* System configuration
* User management
* Management of courses, modules and related tasks
* All functionalities of a teacher
* Statistics

#### Configuration
The settings contain two different areas. On the one hand, general system settings, and on the other hand, the management of subjects and assigned teachers.

##### System settings
This area allows you to store information that is necessary for the operation of the software. This includes:

* An e-mail account for sending system e-mails
* An official operator of the software
* A link to the imprint of the operator

#### Subjects
Subjects as well as assigned teachers are required in the software to allow students to document their digital skills. The students select in predefined tasks in which subject and with which teacher these were worked on. The configuration of the teaching subjects contains the following functionalities:

* Creating, editing and deleting of any subjects
* Assignment of existing teachers to the subjects

#### User management
All users stored in the system are managed via the user administration. The following functionalities are available:

* Create, edit, delete and deactivate users
* Import of many users in CSV format
* Viewing courses attended by users who are students
* Download course certificates of course participants

#### Course management
Digi4Docs allows students to document their participation in various courses. Courses consist of different modules and tasks. The individual tasks must be demonstrably completed by the students. The completion is confirmed by the responsible teachers. The course management allows the definition of any courses. The following functions are available:

* Create, edit and delete any courses
* Create, edit and delete any modules for a course
* Modules can be nested in submodules to any depth
* Create, edit and delete any number of tasks for a module
* Activate and deactivate individual tasks, modules or entire courses

#### Statistics
In the Statistics section, summed data for selected courses and time periods can be exported in CSV format. The data provides information about the degree of fulfillment of the individual courses.

### Teachers
#### Tasks
Teachers have the possibility to validate tasks that have been completed by students. This is done via the task list. The following functions are available:

* View list of own tasks
* Edit tasks and add a comment
* Resolve tasks or return them to the student
* View list of already resolved tasks

#### Tasks for admins
Admins have the following functions in addition to teachers:

* View tasks of all teachers
* View resolved tasks of all teachers
* Change general data (teacher, subject) of individual tasks
* Admins cannot resolve tasks for other teachers

#### Dashboard
The dashboard is the home page for all users. Teachers and admins can see on the dashboard if there are tasks to be completed.

### Students
Students can use Digi4Docs to document their skills in certain predefined courses and have them confirmed by their teachers. In addition, a certificate is created for each course attended.

#### Dashboard
The Dashboard is the entry page for students. They will see the following things on the dashboard:

* List of all available courses
* List of own attended courses including progress indicator
* List of assignments which have been returned by teachers and need to be revised again

#### Courses and tasks
Students can use the dashboard to view individual courses and their associated modules and assignments. Any available task can be started. Once the first task is started in a course, the course is automatically considered attended. The following functions are available to students:

* View all courses, modules and tasks
* Work on tasks in which the subject, teacher, time of execution and a solution to the task are specified
* Transmit ready-made tasks to teachers
* View the status of tasks 
* View the progress of individual modules and the complete course
* View the current certificate for each course
* Reprocess and submit returned tasks

## Technical information
Digi4Docs is a prototype, which was implemented as a simple application in Spring Boot and with MySQL.

### Run the application
1. Install a local database 
2. Setup the database information in `src/main/resources/application.properties` (or create a application-dev.properties file)
3. Run spring boot in terminal: `./mvnw spring-boot:run`
   * Database will be automatically setup on first run
4. Add config entries to database table `config` for:
   * mail.host (e.g. `smtp.gmail.com`)
   * mail.port (e.g. `587`)
   * mail.sender (e.g. `test@example.com`)
   * mail.user (e.g. `test@example.com`)
   * mail.password (e.g. `12345`)
5. Add an initial user to database table `user` with at least the following information: 
   * `INSERT INTO digi4docs.user (email, firstname, lastname, is_active) VALUES ('test@example.com', 'Jan', 'Doe', TRUE)`

6. Open the project in your browser: `localhost:8080`
7. Use the option to send a new password by `password forgotten`
8. Add a admin role to the database table `user_role` for the new user:
   * `INSERT INTO digi4docs.user_role (role, user) VALUES('ADMIN', 1)`
9. Re-login with your user: now you should see all available menu items

### Adjust the styling
The program uses SASS to compile style files. To translate the edited SASS files into CSS, run the following command in the console:

* For development environment: `npm run sass-dev`
* For production environment: `npm run sass-prod`

### Deploy

* Create `src/main/resources/application-prod.properties` with specific settings
* Run `.\mvnw clean install -Denv=prod`
* Run `.\mvnw package`
* Add as ROOT.war to webapps folder of Tomcat

## Credits
Digi4Docs uses some freely available resources and libraries. Thanks for this go to:

### Images
* Home page image: @kreatikar - https://pixabay.com/de/illustrations/online-bildung-lernprogramm-3412473/
* Background image: @stux - https://pixabay.com/de/illustrations/aquarell-farbverlauf-maltechnik-1325656/
* Dashboard image: @kreatikar - https://pixabay.com/de/illustrations/online-netz-statistiken-daten-3539412/

### Libaries
* CSS framework: Bootstrap - https://getbootstrap.com/
* Datatables: 
  * jQuery - https://jquery.com/
  * DataTables (SpryMedia Ltd.) -  https://datatables.net/
* WYSIWYG editor: TinyMCE - https://www.tiny.cloud/
* Icon picker: AppoloDev - https://github.com/AppoloDev/icon-picker
* Circular progress bar: tomik23 - https://github.com/tomik23/circular-progress-bar
* Favicon generator: https://realfavicongenerator.net/
* Fonts:
  * Fontawesome: https://fontawesome.com/
  * Bootstrap Icons: https://icons.getbootstrap.com/
  * Google Font "Barrio": https://fonts.google.com/specimen/Barrio