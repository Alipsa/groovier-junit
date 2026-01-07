# groovier-junit5

A Groovy Extension Module that makes JUnit 5 assertions more Groovy-friendly by adding improved BigDecimal comparison support.

## Overview

This library extends JUnit 5's `Assertions` class with BigDecimal-aware assertion methods that use numerical comparison instead of strict equality. This solves the common problem where `new BigDecimal("5.0")` and `new BigDecimal("5.00")` are not considered equal by the standard `assertEquals` method, even though they represent the same numerical value.

## Features

- **Scale-insensitive BigDecimal comparison**: Compares BigDecimals using `compareTo()` instead of `equals()`
- **Seamless integration**: Works automatically via Groovy's Extension Module mechanism
- **Multiple overloads**: Supports `assertEquals(BigDecimal, BigDecimal)` and `assertEquals(Number, BigDecimal)`
- **JUnit 5 compatible**: Extends the standard `org.junit.jupiter.api.Assertions` class

## Installation

### Gradle

Add the following dependency to your `build.gradle`:

```groovy
dependencies {
    testImplementation 'se.alipsa.groovy:groovier-junit5:0.1.0'
}
```

For Kotlin DSL (`build.gradle.kts`):

```kotlin
dependencies {
    testImplementation("se.alipsa.groovy:groovier-junit5:0.1.0")
}
```

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>se.alipsa.groovy</groupId>
    <artifactId>groovier-junit5</artifactId>
    <version>0.1.0</version>
    <scope>test</scope>
</dependency>
```

## Usage

Once the library is on your classpath, the extension methods are automatically available. No additional configuration is needed.

### Examples

```groovy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MyTest {

    @Test
    void testBigDecimalComparison() {
        // These assertions now pass, comparing numerical values instead of scale
        Assertions.assertEquals(5.0, 5.00G)
        Assertions.assertEquals(new BigDecimal("5.0"), new BigDecimal("5.00"))

        // Works with different Number types
        Assertions.assertEquals(5, 5.00G)
        Assertions.assertEquals(5.0d, 5.00G)
        Assertions.assertEquals(5L, 5.00G)
        Assertions.assertEquals(5G, 5.00G)
    }

    @Test
    void testWithCustomMessage() {
        // Custom failure messages are supported
        Assertions.assertEquals(100, 100.00G, "Account balance mismatch")
    }
}
```

### How It Works

The extension uses Groovy's Extension Module mechanism to add static methods to JUnit's `Assertions` class. When you call `Assertions.assertEquals()` with BigDecimal arguments, Groovy automatically dispatches to the enhanced version that uses `compareTo()` for comparison.

**Standard JUnit behavior (without this extension):**
```groovy
assertEquals(new BigDecimal("5.0"), new BigDecimal("5.00"))  // FAILS - different scale
```

**With groovier-junit5:**
```groovy
assertEquals(new BigDecimal("5.0"), new BigDecimal("5.00"))  // PASSES - numerical equality
```

## Requirements

- **Java**: 17 or higher
- **Groovy**: any 4.x or 5.x version should work
- **JUnit 5**: any 6.x version should work (probably earlier versions too)

## Building from Source

```bash
git clone https://github.com/Alipsa/groovier-junit5.git
cd groovier-junit5
./gradlew build
```

## Documentation

Generate GroovyDoc documentation:

```bash
./gradlew antGroovydoc
```

The documentation will be available in `build/groovydocant/`.

## License

This project is licensed under the [MIT License](LICENSE).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Author

Per Nyfelt

## Links

- [GitHub Repository](https://github.com/Alipsa/groovier-junit5)
- [Issue Tracker](https://github.com/Alipsa/groovier-junit5/issues)
