package se.alipsa.groovy.junit5

import org.junit.jupiter.api.Assertions

/**
 * Groovy extension module that adds improved assertion methods to JUnit 5's {@link Assertions} class.
 * <p>
 * This extension provides {@link BigDecimal}-aware equality assertions that use numerical comparison
 * instead of strict equality, allowing values with different scales to be considered equal
 * (e.g., {@code 5.0} equals {@code 5.00}).
 * <p>
 * The extension methods are automatically available when this library is on the classpath,
 * thanks to Groovy's Extension Module mechanism.
 *
 * @author Per Nyfelt
 * @since 0.1.0
 */
class AssertionsExtension {

  /**
   * Asserts that two {@link BigDecimal} values are numerically equal, ignoring scale differences.
   * <p>
   * Unlike the standard {@link Assertions#assertEquals(Object, Object)} which compares BigDecimals
   * using {@code equals()} (scale-sensitive), this method uses {@code compareTo()} to perform
   * a numerical comparison. This means {@code 5.0} and {@code 5.00} are considered equal.
   * <p>
   * Example:
   * <pre>
   * Assertions.assertEquals(new BigDecimal("5.0"), new BigDecimal("5.00"))  // passes
   * Assertions.assertEquals(5.0, 5.00G)  // passes (using Groovy BigDecimal literal)
   * </pre>
   *
   * @param self the {@link Assertions} class (implicit parameter for Groovy extension methods)
   * @param expected the expected {@link BigDecimal} value
   * @param actual the actual {@link BigDecimal} value
   * @param message optional custom failure message (defaults to "Expected X but was Y")
   * @throws org.opentest4j.AssertionFailedError if the values are not numerically equal or if either is null (when the other is not)
   */
  static void assertEquals(Assertions self, BigDecimal expected, BigDecimal actual, String message = null) {
    if (expected == null && actual == null) return

    def msg = message ?: "Expected ${expected} but was ${actual}"

    if (expected == null || actual == null) {
      Assertions.fail(msg)
    }

    // Use compareTo to ignore scale (2.00 == 2.0)
    if (expected.compareTo(actual) != 0) {
      Assertions.fail(msg)
    }
  }

  /**
   * Asserts that a {@link Number} and a {@link BigDecimal} are numerically equal, ignoring scale differences.
   * <p>
   * This convenience method handles cases where the expected value is any {@link Number} type
   * (Integer, Long, Double, etc.) and the actual value is a {@link BigDecimal}. The Number is
   * converted to BigDecimal before comparison.
   * <p>
   * Example:
   * <pre>
   * Assertions.assertEquals(5, new BigDecimal("5.00"))  // passes
   * Assertions.assertEquals(5.0d, 5.00G)  // passes
   * Assertions.assertEquals(100L, 100.00G)  // passes
   * </pre>
   *
   * @param self the {@link Assertions} class (implicit parameter for Groovy extension methods)
   * @param expected the expected {@link Number} value
   * @param actual the actual {@link BigDecimal} value
   * @param message optional custom failure message
   * @throws org.opentest4j.AssertionFailedError if the values are not numerically equal
   * @see #assertEquals(Assertions, BigDecimal, BigDecimal, String)
   */
  static void assertEquals(Assertions self, Number expected, BigDecimal actual, String message = null) {
    // Delegate to the method above
    assertEquals(self, expected as BigDecimal, actual, message)
  }
}
