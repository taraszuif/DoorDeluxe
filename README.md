# DoorDeluxe

DoorDeluxe is a Spring Boot application that serves as an online store for selling doors via Internet.

## Prerequisites

Before running this application, ensure you have the following:

- Java 21
- IntelliJ IDEA (recommended)
- Gradle (managed by IntelliJ)
- A relational database (PostgreSQL)
- A Google Cloud account for OAuth credentials

## Installation Steps

1. Clone the Project:

   In IntelliJ IDEA, go to  
   `File -> New -> Project from Version Control`  
   and paste the repository URL to clone the project.

2. Sync Gradle:

   After cloning, click "Sync all Gradle projects" to resolve dependencies and generate required files.

3. Configure the Database:

   Open `src/main/resources/application.properties` and configure the following to match your environment:

   ```properties
   spring.datasource.url=jdbc:your_database_url
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password
   ```

4. Set Up Google OAuth:

   Replace the OAuth configuration in `application.properties` with your own Google credentials:

   ```properties
   spring.security.oauth2.client.registration.google.client-id=your_client_id
   spring.security.oauth2.client.registration.google.client-secret=your_client_secret
   ```

5. Configure Java and Run Configuration:

   - Ensure your project SDK is set to Java 21
   - Create a new Run Configuration:
     - Type: Spring Boot
     - Main class: `me.zuif.doordeluxe.DoorDeluxeApplication`

6. Initial Application Run:

   Update the following settings before the first run:

   ```properties
   spring.jpa.defer-datasource-initialization=true
   spring.flyway.enabled=false
   ```

   Now run the project once. Wait until it starts successfully, then stop the application.

7. Enable Flyway Migrations:

   After the first run, update these properties again:

   ```properties
   spring.jpa.defer-datasource-initialization=false
   spring.flyway.enabled=true
   ```

   Now you can launch the application and use it normally(localhost:8060 by default)

## Available Users

The application comes with the following pre-defined users:

| Email              | Password |
|-------------------|----------|
| director@gmail.com| rootroot |
| manager@gmail.com | rootroot |

## Known Issues

- Port is already in use: Make sure no other application is using the same port.
- Incorrect Java version: Ensure Java 21 is selected as your SDK.
- Invalid database config: Double-check your DB credentials and connection string.
- Misconfigured application.properties: Ensure all required parameters are updated as described above.

## System Testing Note

Some system tests require the application to be running. Make sure to start the app before executing these specific tests.
