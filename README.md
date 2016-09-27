# Build instructions

## Prerequisites

- JDK 1.8+
- [SBT](http://www.scala-sbt.org "SBT")

## Development

`sbt devServer` - Runs the development server (a Play application) on port 9000.  While the development server is running, you can browse to http://localhost:9000 to run the game in development mode.  (It will take several minutes the first time it is run to compile all of the source files).

To run the development server on a different port (say 8765), use `sbt "devServer 8765"`.

## Release

`sbt release` - Builds the finished product, and copies it and all assets into the `dist` directory.

# Subprojects

## Production

- client
- macros

## Development

- server
- shared
- benchmarks


# Acknowledgements

- [scalajs](https://www.scala-js.org)
- [scalajs-react](https://github.com/japgolly/scalajs-react)
- [scalajs-spa-tutorial](https://github.com/ochrons/scalajs-spa-tutorial)
