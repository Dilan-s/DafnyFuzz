[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

# DafnyFuzz: A black-box generation-based fuzzer for Dafny

DafnyFuzz is a fuzzer built for the [Dafny](https://dafny.org/) programming language with the purpose of fuzzing the Dafny compiler. This repository contains the code for generation and infomration on how to use and install.

# Table of contents

- [Getting Started](#getting-started)
- [Main usage](#usage)
- [Making changes](#making-changes)

# Getting started

To get started with DafnyFuzz, the easiest way is to make use of the project with IntelliJ. The alternative is 

### IntelliJ

Clone the repository, and then open in IntelliJ, clicking on the "pom.xml" file to handle any maven imports required.

Use any of the Main files as required to generate programs.

### Command line

To compile the files in to the folder out/, specifying one of more of the main programs as required:

```shell
javac -cp src/main/java/ -d ./out/ src/main/java/Main/<MAIN_PROGRAM>.java
```

The folder created can then be used as a class path:

```shell
java -cp out/ Main.<MAIN_PROGRAM> <ARG1> <ARG2> ...
```

# Usage

The seed argument for all programs is optional.

### Generate Program

This main produces all possible outputs, being the base program, minimized program, incorrect verification programs and metamorphic programs.

```shell
java -cp out/ Main.GenerateProgram $seed
```

### Base Program

This main produces the base program, which is the AST representation outputted in the Dafny syntax.

```shell
java -cp out/ Main.BaseProgram $seed
```

### Expected Program Generation

This main produces the base program, which is the AST representation outputted in the Dafny syntax, as well as the expected output of the program as calculated by the value tracking.

```shell
java -cp out/ Main.ExpectedProgram $seed
```

### Metamorphic Program Generation

This main produces variations of the program, which are all equivalent through metamorphic transformations.

```shell
java -cp out/ Main.MetamorphicProgram $seed
```

### Minimized Program Generation

This main produces the base program, and a variation of the program containing no dead code.

```shell
java -cp out/ Main.MinimizedProgram $seed
```

### Verification Program Generation

This main produces the minimized program for correct validation, and variations of the program the verifier should reject.

```shell
java -cp out/ Main.VerificationProgram $seed
```

# Making changes

### Statement

To add a new statement, create the statement class, ensuring that it ```extends BaseStatement```, in the package AST.Statements and then add the generation code required to the RandomStatementGenerator, including adding the probability to the ```generateStatement``` method.

To view the meaning of each method, see the ```Statement``` interface.

### Expressions

To add a new expression, create the expression class, ensuring that it ```extends BaseExpression```, in the package AST.Expressions and then add the generation code required to the RandomExpressionGenerator, including adding the probability to the ```generateExpression``` method.

To view the meaning of each method, see the ```Expression``` interface.

### Types

To add a new type, create the type class, ensuring that it ```implements Type```, in the package AST.SymbolTable.Types and then add the generation code required to the RandomTypeGenerator, including adding the probability to the ```generateType``` method.

To view the meaning of each method, see the ```Type``` interface.

