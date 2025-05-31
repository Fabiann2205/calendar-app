# calendar-app

## Installation

### Command Line:

1. Clone the repository:
   ```bash
   git clone 
   ```

2. Navigate to the project directory:
   ```bash
   cd calendar-app
   ```

3. Install the project:
   ```bash
   mvn install
   ```

4. Run the application using the Runner:
   ```bash
   cd runner;
   mvn exec:java
   ```

### Using IntelliJ IDEA:

1. Open the project in IntelliJ IDEA.
2. There should be two run configurations available:
    - `calendar-app [Install]`: Installs the application.
    - `runner [exec:java]`: Runs the application.
    - Run these configurations in the given order from the UI.

### Using VS Code:

1. Open the project in VS Code.
2. Install the Java Extension Pack if not already installed.
3. Run the task `calendar-app [Install]` to install the application (from command palette).
4. There is a launch configuration (`calendar-app [exec:java]`)  for running the application.

## Change Database
To change the used database, simply uncomment the wanted database in the Main.java (in Runner). Comment out the line of the previous used database.
