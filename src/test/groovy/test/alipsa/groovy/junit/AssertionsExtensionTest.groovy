package test.alipsa.groovy.junit

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError

@CompileStatic
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

  @Test
  void testNumberEqualsWithDelta() {
    Assertions.assertEquals(5.0d, 5.04G, 0.05G)
  }

  @Test
  void testNumberEqualsWithDeltaFailsOnOneSidedNull() {
    Assertions.assertThrows(AssertionFailedError) {
      Assertions.assertEquals(null, 5.00G, 0.05G)
    }
    Assertions.assertThrows(AssertionFailedError) {
      Assertions.assertEquals(5.0d, null, 0.05G)
    }
  }

  @Test
  void testGstringAndString() {
    String a = 'Hello World'
    GString b = "${a}"
    Assertions.assertEquals(a, b)
    Assertions.assertEquals(b, a)
    Assertions.assertEquals(a, b, "String should equalG String")
    Assertions.assertEquals(b, a, "GString should equal String")
  }
}
