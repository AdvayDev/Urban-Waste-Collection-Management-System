# Worker Management Module

The **Worker Management Module** is designed to manage and schedule workers efficiently. This document provides an overview of the project, including how to clone the repository, set up the database, and get started with the project.

---

## **Features**
- Creating worker profiles (Scheduler or Sanitary Worker.)
- Manage worker's personal information, status, and assignment information.
- Integration with a database for storing and retrieving relevant data.
- APIs to interact with the module for seamless integration with other systems.

---

## **Table of Contents**
1. [Getting Started](#getting-started)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Database Configuration](#database-configuration)
5. [Usage](#usage)
6. [Project Structure](#project-structure)
7. [Contributing](#contributing)
8. [License](#license)

---

## **Getting Started**

To clone the Worker Management Module repository and set it up on your local machine, follow the steps below:

### **1. Open a Terminal**
   Open a terminal or command prompt on your computer where Git is installed.

### **2. Run the Git Clone Command**
   Use the following command to clone the repository to your local machine:
   ```bash
   git clone https://github.com/AdvayDev/worker-management.git
   ```

### **3. Navigate to the Cloned Directory**
   After cloning, move into the project directory by running:
   ```bash
   cd worker.management
   ```

### **4. Verify the Repository**
   Ensure the repository is cloned correctly by listing the files in the directory:
   - **Mac/Linux**:
     ```bash
     ls
     ```
   - **Windows**:
     ```bash
     dir
     ```

   You should see the project files, including:
   ```
   src/
   pom.xml
   README.md
   ```

---

Once you’ve done this, you can proceed to follow the [Installation](#installation) and [Database Configuration](#database-configuration) sections to set up and run the project locally.

---

## **Prerequisites**

Ensure you have the following installed on your system:
- **Java 21** (as the project uses Java SDK 21)
- **Maven** (to manage project dependencies)
- **MySQL Database** (or another DBMS if configured differently)
- A modern IDE like IntelliJ IDEA for development.

---

## **Installation**

### **Run the Application**
1. Build the project using Maven:
   ```bash
   mvn clean install
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

---

## **Database Configuration**

### **1. Database Overview**

You can find the database schema in the `db/schema.sql` file.


---

## **Usage**

## **API Endpoints**

The following REST API endpoints are available in the Pickup Scheduling Module. These endpoints allow you to create, delete, retrieve, and list pickups.

---

### **Base URL**
All endpoints are prefixed with:
http://localhost:8086/wastewise/admin/workers
http://localhost:8086/wastewise/admin/worker-assignments

---

### **Endpoints**

#### **1. Create a New Worker**
**Description**: Creates a new worker profile.  
**Method**: `POST`  
**URL**:
/wastewise/admin/workers

**Request Body**:
```json
{
  "name": "name",
  "contactNumber": "9876543210",
  "contactEmail": "name@eample.com",
  "roleId": "003",
  "workerStatus": "AVAILABLE"
}
```

**Response**:
- **201 Created**: Returns the ID of the newly created pickup.  
  Example Response:
  ```json
  "Created worker with id W0XX"
  ```

---
...
### **Notes**
- Replace `{id}` in the URL with the actual ID of the pickup you want to access.
- Ensure the application is running locally or on the specified host/port before accessing these endpoints.
- For detailed API testing, tools like **Postman**, **cURL**, or **Swagger** can be used.