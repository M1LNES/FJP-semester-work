# Ligma Compiler

**Authors:** Milan Janoch and Jakub Pavlíček  
**Course:** KIV/FJP
**Year:** 2024  
**Project Type:** Semester Assignment

## About the Project

The goal of this project was to develop a compiler for the **Ligma programming language** (Language Sigma) that translates source code into **PL/0 instruction set**. The project includes detailed testing, an extensible grammar, and logging functionality during compilation.

## Features of Ligma Language

### Basic (Mandatory) Features

- **Variable definitions:** Support for `int` and `boolean`.
- **Constants:** Immutable integer constants.
- **Arithmetic and Logical Operations:** `+`, `-`, `*`, `/`, `AND`, `OR`, `NOT`, comparison operators, and parentheses.
- **Loops:** Basic `while` loop.
- **Conditions:** `if` statements without `else`.
- **Functions:** Definitions and calls for reusable code.

### Extended Features

- Additional loops: `do-while`, `for`, and `repeat-until`.
- `else` branch for conditions.
- `boolean` type with logical operations.
- Multiple assignment: e.g., `a = b = c = d = 3`.
- Functions with parameters passed by value and return values.

### Custom Extensions

- Exponentiation (`^`) for non-negative integers.
- Nested comments: single-line (`//`) and multi-line (`/* */`).

## How to Use

### Prerequisites

- **Java** version 23 or later.
- **Maven** version 3.9.9.

### Compilation and Execution

1. Navigate to the `ligma` directory and run the script:
   - On Linux/MacOS: `./run.sh`
   - On Windows: `run.bat`
2. The script will:

   - Compile the project and run tests.
   - Generate the executable JAR file: `ligma.jar`.

3. To execute the compiler:
   ```bash
   java -jar ligma.jar <input-file> <output-file>
   ```
   - `<input-file>`: Path to the Ligma source code.
   - `<output-file>`: Path to the generated PL/0 instructions.

### Example Usage

```bash
java -jar ligma.jar ligma-example.txt output-pl0.txt
```

## Project Structure

- **Grammar**: Defined in `Ligma.g4`, includes lexing and parsing rules.
- **Main Source Files**:
  - `App.java`: Entry point.
  - `ir/`: Intermediate representation for statements and expressions.
  - `visitor/`: Tree traversal logic for the parse tree.
  - `generator/`: PL/0 instruction generation.

## Testing

Automated tests are implemented for:

- Lexical analysis.
- Syntax analysis.
- Semantic analysis.

Tests are executed automatically during compilation. Test files are located in:

- `src/test/resources/lexical`
- `src/test/resources/syntax`
- `src/test/resources/semantic`

## Resources

For testing PL/0 instructions, use the online interpreter:  
[PL/0 Interpreter](https://home.zcu.cz/~lipka/fjp/pl0/)

## Conclusion

The Ligma Compiler project demonstrates the implementation of a custom programming language and compiler with PL/0 as the target instruction set. It features a modular design, automatic testing, and extensibility. Logs and test results ensure robust validation of functionality.
