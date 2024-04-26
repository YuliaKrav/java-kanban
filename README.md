# Java Kanban: Task Tracker Backend

## Description
Java Kanban is a backend system for a task tracker, enabling the management of both short-term and long-term tasks. The application facilitates the creation, update, deletion, and tracking of tasks through a web interface, ensuring efficient workflow management.

## Architecture
The system architecture includes several components, each responsible for different aspects of the application's functionality:

### Task Types
- **Task**: A simple individual task.
- **Subtask**: A component of an epic, representing a smaller, manageable part of a larger task.
- **Epic**: A composite task made up of several subtasks, designed to be tackled over a longer period.

### Task Management
- Tasks are managed through simple HTTP requests which enable interaction with tasks and access to their historical data.

### Data Storage Options
- **InMemoryTaskManager**: Utilizes system memory to temporarily store task data.
- **FileBackedTaskManager**: Uses .csv files to persist task data, enabling data recovery and historical tracking.
- **HttpTaskManager**: Operates with a remote server to ensure scalability and remote accessibility.

### KV Server and Client
- **KV Server**: Implements a key-value storage model to ensure data persistence across sessions. Supports operations such as registration, data storage, and data retrieval, secured by unique access tokens.
- **KV Client**: Communicates with the KV Server to perform data operations like saving and loading tasks, handled through HTTP requests. This setup is crucial for maintaining data consistency and managing task states efficiently.

### Server and Client Communication
- Data exchange between the client and server is carried out via HTTP.

## Usage
To interact with the system, users can make HTTP requests to perform CRUD operations on tasks, view task history, and manage task data via the provided interfaces.
