# groovier-junit

A Groovy Extension Module that makes JUnit 5 and 6 assertions more Groovy-friendly by adding BigDecimal-aware comparisons and `GString`/`String` `assertEquals` support.

## Overview

This library extends JUnit's `Assertions` class with
- BigDecimal-aware assertion methods.
  Use numerical comparison instead of strict equality. This solves the common problem where `new BigDecimal("5.0")` and `new BigDecimal("5.00")` are not considered equal by the standard `assertEquals` method, even though they represent the same numerical value.
- `assertEquals` overloads for `String` and `GString` (added in `v0.3.0`).
  This is especially useful in `@CompileStatic` code, where JUnit can otherwise get confused by Groovy's `String` and `GString` types.
- Optional Groovy-friendly classloader support for JUnit Platform launching (for example when using `@Grab`/`Grape` at runtime)

## Features

- **Scale-insensitive BigDecimal comparison**: Compares BigDecimals using `compareTo()` instead of `equals()`
- **`GString`/`String` compatibility**: Compares interpolated Groovy strings and plain Java strings in `assertEquals`
- **Seamless integration**: Works automatically via Groovy's Extension Module mechanism
- **Multiple overloads**: Supports BigDecimal equality, BigDecimal delta assertions, and `String`/`GString` combinations
- **JUnit 5 compatible**: Extends the standard `org.junit.jupiter.api.Assertions` class

## Installation

### Gradle

Add the following dependency to your `build.gradle`:

```groovy
dependencies {
    testImplementation 'se.alipsa.groovy:groovier-junit:0.3.0'
}
```

For Kotlin DSL (`build.gradle.kts`):

```kotlin
dependencies {
    testImplementation("se.alipsa.groovy:groovier-junit:0.3.0")
}
```

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>se.alipsa.groovy</groupId>
    <artifactId>groovier-junit</artifactId>
    <version>0.3.0</version>
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

    @Test
    void testBigDecimalWithDelta() {
        Assertions.assertEquals(10, 10.04G, 0.05G)
    }

    @Test
    void testStringAndGStringComparison() {
        String expected = 'Hello World'
        GString actual = "${expected}"

        Assertions.assertEquals(expected, actual)
        Assertions.assertEquals(actual, expected, "Interpolated text should match")
    }
}
```

The `String`/`GString` overloads are mainly relevant in statically compiled Groovy, where method selection can otherwise fail even though the values are textually equal.

## Optional: Groovy-Friendly Test ClassLoader (Opt-In)

The assertion extensions work with zero extra setup.

If you also want Groovy-friendly classloader behavior for JUnit Platform launching (for example when using `@Grab`/`Grape` at runtime), enable the optional launcher interceptor.

### System properties

- `groovier.junit.classloader.enabled` (default: `false`)
- `groovier.junit.classloader.mode` (optional: `context` or `root`, default: `context`)
- `groovier.junit.classloader.debug` (optional: `true` to print debug logs)

### Gradle (JUnit Platform)

```groovy
test {
  useJUnitPlatform()

  // Enable JUnit launcher interceptor auto-detection
  systemProperty 'junit.platform.launcher.interceptors.enabled', 'true'

  // Opt in to groovier-junit classloader support
  systemProperty 'groovier.junit.classloader.enabled', 'true'
  systemProperty 'groovier.junit.classloader.mode', 'context'
}
```

For `@GrabConfig(systemClassLoader=true)`, the JVM must start with `RootLoader` as system classloader:

```groovy
test {
  useJUnitPlatform()
  systemProperty 'junit.platform.launcher.interceptors.enabled', 'true'
  systemProperty 'groovier.junit.classloader.enabled', 'true'
  systemProperty 'groovier.junit.classloader.mode', 'root'
  jvmArgs '-Djava.system.class.loader=org.codehaus.groovy.tools.RootLoader'
}
```

### Maven (Surefire/Failsafe)

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.5.4</version>
  <configuration>
    <systemPropertyVariables>
      <junit.platform.launcher.interceptors.enabled>true</junit.platform.launcher.interceptors.enabled>
      <groovier.junit.classloader.enabled>true</groovier.junit.classloader.enabled>
      <groovier.junit.classloader.mode>context</groovier.junit.classloader.mode>
    </systemPropertyVariables>
  </configuration>
</plugin>
```

For `@GrabConfig(systemClassLoader=true)`, add:

```xml
<argLine>-Djava.system.class.loader=org.codehaus.groovy.tools.RootLoader</argLine>
```

### Notes

- This feature is opt-in and does not affect users who only want assertion extensions.
- The interceptor can switch context classloader during launcher operations.
- It cannot retroactively replace the JVM system classloader after startup.

### How It Works

The extension uses Groovy's Extension Module mechanism to add static methods to JUnit's `Assertions` class. When you call `Assertions.assertEquals()`, Groovy automatically dispatches to the matching enhanced overload for BigDecimal-aware comparison or `String`/`GString` conversion.

**Standard JUnit behavior (without this extension):**
```groovy
assertEquals(new BigDecimal("5.0"), new BigDecimal("5.00"))  // FAILS - different scale
```

**With groovier-junit:**
```groovy
assertEquals(new BigDecimal("5.0"), new BigDecimal("5.00"))  // PASSES - numerical equality
assertEquals('Hello World', "${'Hello World'}")              // PASSES - GString converted to String
```

## Requirements

- **Java**: 17 or higher
- **Groovy**: any 4.x or 5.x version should work
- **JUnit**: any 6.x version should work (probably earlier versions too)

## Building from Source

```bash
git clone https://github.com/Alipsa/groovier-junit.git
cd groovier-junit
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

- [GitHub Repository](https://github.com/Alipsa/groovier-junit)
- [Issue Tracker](https://github.com/Alipsa/groovier-junit/issues)
