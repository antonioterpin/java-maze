# The Maze Game

This project was a fun game I developed to play with my niece when I was learning Java in high school. It generates a random maze of the desired size (it works quite nicely!) and makes the two players challenge each other on who finishes first. If stuck, it computes the solution and show it to the players.

![Maze game preview](media/maze-run.gif)
![Maze game solution](media/maze-run.gif)

## Features
| Feature              | Description                                                                                                   |
|----------------------|---------------------------------------------------------------------------------------------------------------|
| Buffered Rendering   | Utilizes off-screen buffering to ensure smooth animation and prevent flickering.                   |
| Multithreading       | Implements separate threads for the players and and for the GUI updates, ensuring a responsive user experience.|
| Interrupts set and reset       | Implements some sort of interrupts handling to keep track of players moves and halt them when needed.|
| Synchronization      | Employs semaphores for making sure that the evaluation of who reaches the goal first is fair.    |
| Maze generation and solution      | Generates random mazes of the chosen size (default, 10), and solves them. Both are done with a (randomized) DFS.        |
| User Interaction     | Provides interactive controls.                               |


## Getting Started
### Prerequisites
Ensure you have the following installed:
- Java Runtime Environment (JRE)
- Java Development Kit (JDK)

### Installation
Clone the repository or download the source code:
```bash
git clone git@github.com:antonioterpin/java-maze.git
```
Compile the Java bytecode:
```bash
javac -sourcepath src -d out MazeGame.java
```
Run the app:
```bash
java -classpath out MazeGame N
```
where ```N``` is the desired size of the maze.

### Usage
After starting the application, you need to insert the two players names and then use the following buttons on the interface:

| Button | Functionality                                     |
|---------|-------------------------------------------------|
| Start   | Begin the game.                      |
| Draw solution | Draw the solution to the maze.             |
| Exit    | Close the application.                          |

## Limitations
1. Some of the features that were implemented at the time are not working now and should be fixed. There are TODOs in the code, I should get at them eventually.
2. There is quite a bunch of overly code that could be cut.
3. The graphical interface is simple and might not run smoothly on all systems.

## Contributing
This was a fun project to learn some stuff in Java, and while it is not actively seeking contributions, feedback and suggestions are always welcome.
