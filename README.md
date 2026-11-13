 # Reading Room – Library Management System

**Reading Room** is a Java-based web application developed to simplify library operations, including book management, member management, book borrowing, returns, and fine calculation.

The application follows the **Model–View–Controller (MVC)** architecture and uses Oracle Database for data storage.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend programming |
| Java Servlets | Request handling and application logic |
| JSP | Dynamic web pages |
| JSTL | JSP tag library |
| JDBC | Database connectivity |
| Oracle Database | Database management |
| HTML5 | Web page structure |
| CSS3 | Styling and responsive design |
| JavaScript | Frontend interactions |
| Apache Tomcat 9 | Web application server |
| Maven | Project and dependency management |
| Eclipse IDE | Development environment |

## Features

### Administrator / Librarian
- Secure administrator login
- Admin dashboard
- Add, edit and delete books
- Manage library members
- Issue books to members
- Process book returns
- Calculate overdue fines
- View and manage loan records

### Library Members
- Member registration and login
- Browse the book catalogue
- Search for books
- Borrow available books
- View borrowed books and due dates
- Track borrowing history
- Change account password

### Additional Features
- 14-day borrowing period
- Maximum of 5 active loans per member
- Members with overdue books cannot borrow additional books
- Late return fine of ₹5 per day
- Dark mode and light mode
- Theme palette selection (Ocean, Emerald, Violet, Rose and Mono)

## Project Structure

```text
lib-oracle/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/library/
│       │       ├── model/
│       │       ├── dao/
│       │       ├── web/
│       │       └── util/
│       │
│       ├── resources/
│       │   ├── db.properties
│       │   └── db.properties.example
│       │
│       └── webapp/
│           ├── index.jsp
│           ├── css/
│           ├── js/
│           └── WEB-INF/
│               ├── web.xml
│               ├── lib/
│               └── jsp/
│
├── sql/
│   ├── 1_create_user.sql
│   └── 2_schema.sql
│
├── pom.xml
├── README.md
└── .gitignore
```

## Requirements

Before running the application, ensure that the following software is installed:

- JDK 11 or compatible version
- Eclipse IDE
- Apache Tomcat 9
- Oracle Database
- Maven
- Compatible Oracle JDBC driver

## Installation and Setup

### Step 1: Clone the Repository

Clone the GitHub repository:

```bash
git clone https://github.com/YOUR_USERNAME/Reading-Room-Library-Management-System.git
```

Open the extracted project folder in Eclipse.

### Step 2: Configure Oracle Database

1. Start Oracle Database.
2. Open SQL*Plus or Run SQL Command Line.
3. Execute the SQL scripts available in the `sql` folder.

Example:

```sql
connect system
@path_to_project/sql/1_create_user.sql

connect library/YOUR_DATABASE_PASSWORD
@path_to_project/sql/2_schema.sql
```

Replace the database password and file paths with your local configuration.

**Note:** If the database schema already exists, do not execute the schema creation script again without checking its contents. The script may drop existing tables and data.

### Step 3: Configure Database Connection

Navigate to:

```text
src/main/resources/
```

Copy:

```text
db.properties.example
```

Rename the copied file to:

```text
db.properties
```

Open `db.properties` and update the database connection details:

```properties
db.driver=oracle.jdbc.OracleDriver
db.url=jdbc:oracle:thin:@localhost:1521:XE
db.user=YOUR_DATABASE_USERNAME
db.password=YOUR_DATABASE_PASSWORD

loan.days=14
fine.per.day=5
max.loans=5

show.errors=false
```

Update the URL, username and password according to your Oracle Database installation.

**Important:** The actual `db.properties` file contains local database credentials and is excluded from GitHub using `.gitignore`. Never upload your actual database passwords.

### Step 4: Add Oracle JDBC Driver

The Oracle JDBC driver is required to establish database connectivity.

1. Obtain a compatible Oracle JDBC driver for your Java and Oracle Database versions.
2. Place the driver JAR inside:

```text
src/main/webapp/WEB-INF/lib/
```

3. Ensure the driver is available to the application when deployed.

The Oracle JDBC driver is not included in the public repository.

### Step 5: Import the Project into Eclipse

1. Open Eclipse IDE.
2. Select **File → Import**.
3. Choose **Maven → Existing Maven Projects**.
4. Browse to the project folder.
5. Select the `pom.xml` file.
6. Click Finish.

### Step 6: Configure Apache Tomcat

1. Open **Window → Preferences**.
2. Navigate to **Server → Runtime Environments**.
3. Click Add.
4. Select Apache Tomcat v9.0.
5. Browse to your Tomcat installation directory.
6. Finish the configuration.

### Step 7: Run the Application

1. Right-click the project in Eclipse.
2. Select **Run As → Run on Server**.
3. Select Apache Tomcat 9.
4. Finish and wait for the server to start.

Open the application in your browser:

```text
http://localhost:8080/library/
```

Use the administrator account configured for your local installation. Change any initial administrator password immediately after first login.

## Application Usage

### Adding Books

1. Log in with an administrator or librarian account.
2. Open **Manage Books**.
3. Enter the book title, author, category, ISBN and number of copies.
4. Click Add Book.
5. Use Edit or Delete to modify existing book records.

### Borrowing Books

1. Log in using a member account.
2. Open the Catalogue.
3. Search for an available book.
4. Click Borrow.
5. View the borrowed book and due date under My Books.

### Returning Books

1. Log in as the librarian.
2. Open the Loans section.
3. Find the relevant loan record.
4. Click Return.
5. Calculate and collect any applicable overdue fine.

## Security Features

The application implements several security measures:

- **Password Security:** Salted PBKDF2 password hashing with 65,536 iterations.
- **SQL Injection Prevention:** PreparedStatements are used for database queries.
- **Cross-Site Scripting (XSS):** JSTL output escaping and Content Security Policy.
- **Cross-Site Request Forgery (CSRF):** Session-based security tokens for POST requests.
- **Brute-Force Protection:** Login lockout after five unsuccessful attempts.
- **Session Security:** Session renewal during login and password changes.
- **Access Control:** Filters protect administrator routes.
- **Private JSP Pages:** JSP files are stored under WEB-INF.
- **Transaction Management:** SELECT FOR UPDATE helps prevent simultaneous issuing of the same book copy.
- **Cache Protection:** No-cache headers help prevent access to private pages after logout.

## Troubleshooting

| Problem | Possible Solution |
|---|---|
| The request failed | Check whether Oracle Database is running and verify database configuration. |
| ClassNotFoundException: oracle.jdbc.OracleDriver | Check whether the compatible JDBC driver is present in WEB-INF/lib. |
| ORA-12505 / ORA-12514 | Verify the Oracle SID or service name in db.properties. |
| ORA-01017 | Verify the database username and password. |
| ORA-01882 | Check Oracle JDBC timezone configuration. |
| ORA-28040 | Check compatibility between the Oracle JDBC driver and database version. |
| ORA-65096 | Connect to the appropriate pluggable database before creating a user, if applicable. |
| HTTP 404 | Verify the Tomcat version and application context path. |

For detailed error information, inspect the Eclipse Console and Tomcat logs.

## Version History

### Version 3
- Added a Borrow button to the member catalogue.
- Redesigned the user interface.
- Added a clean white navigation bar.
- Introduced a split-screen login interface.
- Added book cards with coloured covers.
- Added status badges and dashboard icons.
- Introduced theme palette selection.
- Added dark mode support.

### Version 2
- Introduced a redesigned midnight-indigo and amber theme.
- Added dark mode and light mode.
- Improved logout security using POST requests and CSRF tokens.
- Added login attempt restrictions.
- Improved session handling and password security.
- Added input validation.
- Improved Oracle database error handling.
- Added change password functionality.
- Prevented members with overdue books from borrowing additional books.
- Introduced a maximum loan limit.

## Database Implementation

The application uses Oracle Database instead of MySQL.

Major database changes include:

- `AUTO_INCREMENT` replaced with Oracle sequences.
- `ENUM` replaced with `VARCHAR2` and CHECK constraints.
- Case-insensitive search implemented using `UPPER()`.
- Oracle-specific error codes handled appropriately.
- JDBC used for database connectivity.

Database scripts:

```text
sql/
├── 1_create_user.sql
└── 2_schema.sql
```

## Important Application URLs

| Page | URL |
|---|---|
| Login | `/login` |
| Registration | `/register` |
| Home | `/home` |
| Catalogue | `/books` |
| My Books | `/my-books` |
| Account | `/account` |
| Admin Dashboard | `/admin/dashboard` |
| Manage Books | `/admin/books` |
| Manage Loans | `/admin/loans` |
| Manage Members | `/admin/members` |

## Academic Project Information

**Project Name:** Reading Room – Library Management System

**Subject:** Full Stack Development – II (FSD-II)

**Project Type:** Java Web Application

**Architecture:** Model–View–Controller (MVC)

**Database:** Oracle Database

**Development Environment:** Eclipse IDE and Apache Tomcat 9

Developed as part of an academic project to demonstrate full-stack web application development, database connectivity, server-side programming and secure library management.


## Project Demonstration Video

Watch the complete demonstration of our Reading Room Library Management System:

[Click here to watch the project demo](https://drive.google.com/file/d/1L26EnIoEuoEGzSklajkbi806_5_eClaj/view?usp=sharing)


## License

This project is developed for academic and educational purposes.

