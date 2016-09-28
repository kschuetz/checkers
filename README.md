# Checkers

- A 100% client-side implementation of Checkers that runs in modern web browsers.
- Graphics and animations are rendered as SVG elements in the browser DOM.
- Supports Human vs. Computer, Human vs. Human (hot seat), and Computer vs. Computer games.
- Written in Scala.js.

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


## Acknowledgements

Thanks to the authors and contributors of the following high-quality projects that were used in the creation of this game:

- [Scala.js](https://www.scala-js.org) - Scala to JavaScript transpiler.
- [scalajs-react](https://github.com/japgolly/scalajs-react) - An incredibly flexible Scala.js binding to React.
- [scalajs-spa-tutorial](https://github.com/ochrons/scalajs-spa-tutorial) - This is more than a tutorial -- it is a very good foundation for building non-trivial Play/Scala.js projects.

## License

Copyright 2016 Kevin Schuetz

[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)