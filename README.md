# Todo App REST API

## Overview
This project implements a REST API for a Todo application using **Spring Boot**. The API provides essential functionalities for user authentication and todo management, ensuring that users can manage their tasks securely and efficiently.

## Postman Documentation
https://documenter.getpostman.com/view/39279974/2sAY4xBMrm

## Technologies Used
- **Spring Boot**: The core framework for building the API.
- **Spring Security**: For authentication and authorization using JWT (JSON Web Tokens).
- **MySQL**: Database for storing user and todo data, accessed using Spring JPA.
- **JUnit**: For testing various functionalities, with at least five unit tests implemented.

## Features
### User Authentication
- **Register**: Users can create new accounts using their email and password.
- **Login**: Users can authenticate with their email and password to gain access to their todo items.

### Todo Management
- **Create**: Users can create new todo items.
- **Read**: Users can retrieve their own todo items.
- **Update**: Users can modify existing todo items.
- **Delete**: Users can remove todo items they no longer need.

### Authorization
- **Private Data Access**: Users can only access and modify their own todo items, ensuring privacy.
- **Security Measures**: Implemented to protect user data and secure endpoints.

### Additional Considerations
- **Error Handling**: Informative error messages for invalid requests or unauthorized actions.
- **Pagination**: Improves performance and user experience for large todo lists by implementing pagination.
- **Sorting**: Users can sort their todo items by criteria such as due date and priority.
- **Search Functionality**: Allows users to search for todo items based on keywords or other attributes.
- **Completion Status Tracking**: Keeps track of the completion status of todo items.
- **Logging**: Utilizes logging to monitor and trace errors effectively.

## Running the Application
To run this application, ensure you have Java and MySQL installed. Follow these steps:

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Configure your database settings in the `application.properties` file.
4. Run the application using your preferred method (e.g., through an IDE like IntelliJ or via command line).

```bash
./mvnw spring-boot:run

