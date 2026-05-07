package test.alipsa.groovy.junit

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError

class AssertionsExtensionTest {

  @Test
  @CompileDynamic
  void testBigDecimalEquals() {
    Assertions.assertEquals(5.0, 5.00G)
    Assertions.assertNotEquals(null, 5.00G)
    Assertions.assertNotEquals(Double.NaN, 5.00G)
  }

  @Test
  @CompileStatic
  void testBigDecimalEqualsStatic() {
    Assertions.assertEquals(5.0, 5.00G)
    Assertions.assertNotEquals(null, 5.00G)
    Assertions.assertNotEquals(Double.NaN, 5.00G)
  }

  @Test
  @CompileDynamic
  void testNumberEquals() {
    Assertions.assertEquals(5.0d, 5.00G)
    Assertions.assertEquals(5G, 5.00G)
  }

  @Test
  @CompileStatic
  void testNumberEqualsStatic() {
    Assertions.assertEquals(5.0d, 5.00G)
    Assertions.assertEquals(5G, 5.00G)
    Object o = new BigDecimal("5")
    Assertions.assertEquals(o, 5.00G)
    Assertions.assertEquals(5.00G, o)
  }

  @Test
  @CompileDynamic
  void testNumberEqualsWithDelta() {
    Assertions.assertEquals(5.0d, 5.04G, 0.05G)
  }

  @Test
  @CompileStatic
  void testNumberEqualsWithDeltaStatic() {
    Assertions.assertEquals(5.0d, 5.04G, 0.05G)
  }

  @Test
  @CompileDynamic
  void testNumberEqualsWithDeltaFailsOnOneSidedNull() {
    Assertions.assertThrows(AssertionFailedError) {
      Assertions.assertEquals(null, 5.00G, 0.05G)
    }
    Assertions.assertThrows(AssertionFailedError) {
      Assertions.assertEquals(5.0d, null, 0.05G)
    }
  }

  @Test
  @CompileStatic
  void testNumberEqualsWithDeltaFailsOnOneSidedNullStatic() {
    Assertions.assertThrows(AssertionFailedError) {
      Assertions.assertEquals(null, 5.00G, 0.05G)
    }
    Assertions.assertThrows(AssertionFailedError) {
      Assertions.assertEquals(5.0d, null, 0.05G)
    }
  }


  @Test
  @CompileDynamic
  void testObjectEqualsBigDecimal() {
    def value = 5.00
    Assertions.assertEquals(value, 5.00G)
    Assertions.assertEquals(5.00G, value)
    Assertions.assertNotEquals(null, value)
    Assertions.assertNotEquals(value, null)
  }

  @Test
  @CompileStatic
  void testObjectEqualsBigDecimalStatic() {
    def value = 5.00
    Assertions.assertEquals(value, 5.00G)
    Assertions.assertEquals(5.00G, value)
    Assertions.assertNotEquals(null, value)
    Assertions.assertNotEquals(value, null)
  }

  @Test
  @CompileDynamic
  void testGstringAndString() {
    String a = 'Hello World'
    GString b = "${a}"
    Assertions.assertEquals(a, b)
    Assertions.assertEquals(b, a)
    Assertions.assertEquals(a, b, "String should equalG String")
    Assertions.assertEquals(b, a, "GString should equal String")
  }

  @Test
  @CompileStatic
  void testGstringAndStringStatic() {
    String a = 'Hello World'
    GString b = "${a}"
    Assertions.assertEquals(a, b)
    Assertions.assertEquals(b, a)
    Assertions.assertEquals(a, b, "String should equalG String")
    Assertions.assertEquals(b, a, "GString should equal String")
  }
}
