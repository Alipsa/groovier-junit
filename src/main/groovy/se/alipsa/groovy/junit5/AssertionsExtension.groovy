package se.alipsa.groovy.junit5

import org.junit.jupiter.api.Assertions

class AssertionsExtension {

  /**
   * Extension for Assertions.assertEquals(BigDecimal, BigDecimal)
   * The first parameter (Assertions self) tells Groovy to attach this
   * static method to the Assertions class.
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
   * Extension for Assertions.assertEquals(Number, BigDecimal)
   * Handles cases like assertEquals(100, myBigDecimal)
   */
  static void assertEquals(Assertions self, Number expected, BigDecimal actual, String message = null) {
    // Delegate to the method above
    assertEquals(self, expected as BigDecimal, actual, message)
  }
}
