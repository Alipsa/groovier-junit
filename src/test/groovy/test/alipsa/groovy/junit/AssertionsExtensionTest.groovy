package test.alipsa.groovy.junit

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.GStringImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
  void testGstringAndString() {
    String a = 'Hello World'
    GString b = "${a}"
    Assertions.assertEquals(a, b)
    Assertions.assertEquals(b, a)
  }
}
