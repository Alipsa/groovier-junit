package test.alipsa.groovy.junit5

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AssertionsExtensionTest {

  @Test
  void testBigDecimalEquals() {
    Assertions.assertEquals(5.0, 5.00G)
    Assertions.assertNotEquals(null, 5.00G)
    Assertions.assertNotEquals(Double.NaN, 5.00G)
  }

  @Test
  void testNumberEquals() {
    Assertions.assertEquals(5.0d, 5.00G)
    Assertions.assertEquals(5G, 5.00G)
  }
}
