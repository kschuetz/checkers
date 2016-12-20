# Checkers

**[Play it here](http://kschuetz.github.io/checkers)**

- A 100% client-side implementation of Checkers that runs in modern web browsers.
- Graphics and animations are rendered as SVG elements in the browser DOM.
- Supports Human vs. Computer, Human vs. Human (hot seat), and Computer vs. Computer games.
- Written in Scala.js.

## Rules

- Jumps are compulsory.  As long as you have an opportunity to capture, you must take it.  
- If a piece can continue to jump once it has jumped, it must do so in the same turn.
- If there is more than one alternative for capture, you may choose which one to take.  It need not be the capture that takes the most pieces.
- When a checker is kinged, the turn automatically ends, even if the king can continue to jump. 
- If an exact board position is repeated a third time, the game automatically ends in a draw.
- If 50 moves have taken place (for both players) since the last capture or advancement of a regular checker, the game ends in a draw.

## Known issues

- Some of the SVG elements in the user interface do not render correctly in iOS Safari.


## Build instructions

### Prerequisites

- JDK 1.8+
- [SBT](http://www.scala-sbt.org "SBT")

### Development

`sbt devServer` - Runs the development server (a Play application) on port 9000.  While the development server is running, you can browse to http://localhost:9000 to run the game in development mode.  (It will take several minutes the first time it is run to compile all of the source files).

To run the development server on a different port (say 8765), use `sbt "devServer 8765"`.

### Release

`sbt release` - Builds the finished product, and copies it and all assets into the `dist` directory.

## Subprojects

### Production

- **client** - Most of the application code resides here
- **macros** - Since Scala requires separation compilation of macros, all macro definitions go here

### Development

- **server** - The development server (a small Play application)
- **shared** - Code that is employed by both the client and server projects
- **benchmarks** - Self-explanatory

## Implementation notes

- Most user interface components, including the game elements and the user interface chrome, are composed of SVG elements.  The exception is the new game dialog, which has some HTML 5 elements as well.  [React](https://facebook.github.io/react/) is used throughout the user interface.
- The board is represented internally using three 32-bit integers: one for dark pieces, one for light pieces, and one for kings.  Thanks to [this article](http://www.3dkingdoms.com/checkers/bitboards.htm) for the idea.
- Computer players use alpha-beta search, combined with iterative deepening.  An endgame tablebase is not employed (yet), as this game is client-side only, and introducing a tablebase would significantly increase resources required of the browser.
- Everything is done in a single thread.  No web workers are used.  While the computer player is thinking, it is given time slices from the event loop until the computation is complete.
- The difficulty level of a computer player determines, among other things, the maximum number of evaluations per turn the computer player is allowed before having to yield a move.  While slower computers will take longer to calculate a move, they will ultimately arrive at the same conclusion on a faster computer, all else being equal.
- The computer doesn't always attempt for a perfect play.  Lower level computer players have a higher chance of making a artificial blunder.
- In some parts of the search tree, move order is shuffled (for moves other than the move the computer already thinks is best).  This causes the computer to examine the same tree in a different way, so it may come to a different conclusion in situations where each alternatives' value is similar.  This is done to introduce some variety into the computer's play, in the hopes of making it more or less as effective without being as robotic.
- The computer can be rushed by the user. Shortly after being rushed, it will yield the move it thinks is the best from what it has seen so far.  This is possible to due the computer player keeping track of the "principal variation" (what is thought of as the best sequence of moves), which evolves through the search process.
- As an optimization, macros are used extensively throughout the project.  The implementations of these macros are simple constants or bit-wise expressions.  Since they are used in the same manner as C preprocessor macros, by convention they all have uppercase names (e.g. `DARK`, `LIGHT`, `SHIFTNW`).


## Acknowledgements

Thanks to the authors and contributors of the following high-quality projects that were used in the creation of this game:

- [Scala.js](https://www.scala-js.org) - Scala to JavaScript transpiler.
- [React](https://facebook.github.io/react/) 
- [scalajs-react](https://github.com/japgolly/scalajs-react) - An incredibly flexible Scala.js binding to React.
- [scalajs-spa-tutorial](https://github.com/ochrons/scalajs-spa-tutorial) - This is more than a tutorial -- it is a very good foundation for building non-trivial Play/Scala.js projects.

## License

Copyright 2016 Kevin Schuetz

[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)
