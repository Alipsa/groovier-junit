package test.alipsa.groovy.junit5

import org.codehaus.groovy.tools.RootLoader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryListener
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.LauncherExecutionRequest
import org.junit.platform.launcher.LauncherInterceptor
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestPlan
import se.alipsa.groovy.junit5.GroovyClassLoaderLauncherInterceptor

class GroovyClassLoaderLauncherInterceptorTest {

  @Test
  void interceptorIsNoopByDefault() {
    Map<String, String> originalProperties = captureProperties()
    ClassLoader originalContextLoader = Thread.currentThread().contextClassLoader
    try {
      clearProperties()
      def interceptor = new GroovyClassLoaderLauncherInterceptor()
      def launcher = new RecordingLauncher()

      Launcher result = interceptor.intercept({ launcher } as LauncherInterceptor.Invocation<Launcher>)

      Assertions.assertSame(launcher, result)
    } finally {
      restoreProperties(originalProperties)
      Thread.currentThread().contextClassLoader = originalContextLoader
    }
  }

  @Test
  void contextModeWrapsLauncherAndRestoresContextClassLoader() {
    Map<String, String> originalProperties = captureProperties()
    ClassLoader originalContextLoader = Thread.currentThread().contextClassLoader
    URLClassLoader testContextLoader = new URLClassLoader(new URL[0], null)
    try {
      clearProperties()
      System.setProperty(GroovyClassLoaderLauncherInterceptor.ENABLE_PROPERTY, 'true')
      System.setProperty(GroovyClassLoaderLauncherInterceptor.MODE_PROPERTY, 'context')
      Thread.currentThread().contextClassLoader = testContextLoader

      def interceptor = new GroovyClassLoaderLauncherInterceptor()
      def launcher = new RecordingLauncher()

      Launcher wrapped = interceptor.intercept({ launcher } as LauncherInterceptor.Invocation<Launcher>)
      Assertions.assertNotSame(launcher, wrapped)

      wrapped.discover(null)

      Assertions.assertNotNull(launcher.discoverContextClassLoader)
      Assertions.assertEquals('groovy.lang.GroovyClassLoader', launcher.discoverContextClassLoader.class.name)
      Assertions.assertSame(testContextLoader, Thread.currentThread().contextClassLoader)
    } finally {
      restoreProperties(originalProperties)
      Thread.currentThread().contextClassLoader = originalContextLoader
      testContextLoader.close()
    }
  }

  @Test
  void rootModeIsNoopWhenNoRootLoaderExists() {
    Map<String, String> originalProperties = captureProperties()
    ClassLoader originalContextLoader = Thread.currentThread().contextClassLoader
    URLClassLoader testContextLoader = new URLClassLoader(new URL[0], null)
    try {
      clearProperties()
      System.setProperty(GroovyClassLoaderLauncherInterceptor.ENABLE_PROPERTY, 'true')
      System.setProperty(GroovyClassLoaderLauncherInterceptor.MODE_PROPERTY, 'root')
      Thread.currentThread().contextClassLoader = testContextLoader

      Assumptions.assumeFalse(rootLoaderExistsInHierarchy(ClassLoader.getSystemClassLoader()))
      Assumptions.assumeFalse(rootLoaderExistsInHierarchy(this.class.classLoader))

      def interceptor = new GroovyClassLoaderLauncherInterceptor()
      def launcher = new RecordingLauncher()

      Launcher result = interceptor.intercept({ launcher } as LauncherInterceptor.Invocation<Launcher>)

      Assertions.assertSame(launcher, result)
    } finally {
      restoreProperties(originalProperties)
      Thread.currentThread().contextClassLoader = originalContextLoader
      testContextLoader.close()
    }
  }

  @Test
  void rootModeUsesRootLoaderWhenAvailable() {
    Map<String, String> originalProperties = captureProperties()
    ClassLoader originalContextLoader = Thread.currentThread().contextClassLoader
    URLClassLoader baseLoader = new URLClassLoader(new URL[0], null)
    RootLoader rootLoader = new RootLoader(baseLoader)
    try {
      clearProperties()
      System.setProperty(GroovyClassLoaderLauncherInterceptor.ENABLE_PROPERTY, 'true')
      System.setProperty(GroovyClassLoaderLauncherInterceptor.MODE_PROPERTY, 'root')
      Thread.currentThread().contextClassLoader = rootLoader

      def interceptor = new GroovyClassLoaderLauncherInterceptor()
      def launcher = new RecordingLauncher()

      Launcher wrapped = interceptor.intercept({ launcher } as LauncherInterceptor.Invocation<Launcher>)
      Assertions.assertNotSame(launcher, wrapped)

      wrapped.discover(null)

      Assertions.assertSame(rootLoader, launcher.discoverContextClassLoader)
      Assertions.assertSame(rootLoader, Thread.currentThread().contextClassLoader)
    } finally {
      restoreProperties(originalProperties)
      Thread.currentThread().contextClassLoader = originalContextLoader
      baseLoader.close()
    }
  }

  @Test
  void serviceRegistrationExists() {
    URL serviceUrl = this.class.classLoader.getResource('META-INF/services/org.junit.platform.launcher.LauncherInterceptor')

    Assertions.assertNotNull(serviceUrl)
    String content = serviceUrl.text
    Assertions.assertTrue(content.contains('se.alipsa.groovy.junit5.GroovyClassLoaderLauncherInterceptor'))
  }

  private static Map<String, String> captureProperties() {
    [
        (GroovyClassLoaderLauncherInterceptor.ENABLE_PROPERTY): System.getProperty(GroovyClassLoaderLauncherInterceptor.ENABLE_PROPERTY),
        (GroovyClassLoaderLauncherInterceptor.MODE_PROPERTY): System.getProperty(GroovyClassLoaderLauncherInterceptor.MODE_PROPERTY),
        (GroovyClassLoaderLauncherInterceptor.DEBUG_PROPERTY): System.getProperty(GroovyClassLoaderLauncherInterceptor.DEBUG_PROPERTY),
    ]
  }

  private static void restoreProperties(Map<String, String> properties) {
    properties.each { key, value ->
      if (value == null) {
        System.clearProperty(key)
      } else {
        System.setProperty(key, value)
      }
    }
  }

  private static void clearProperties() {
    System.clearProperty(GroovyClassLoaderLauncherInterceptor.ENABLE_PROPERTY)
    System.clearProperty(GroovyClassLoaderLauncherInterceptor.MODE_PROPERTY)
    System.clearProperty(GroovyClassLoaderLauncherInterceptor.DEBUG_PROPERTY)
  }

  private static boolean rootLoaderExistsInHierarchy(ClassLoader classLoader) {
    ClassLoader current = classLoader
    while (current != null) {
      if (current.class.name == RootLoader.name) {
        return true
      }
      current = current.parent
    }
    false
  }

  private static class RecordingLauncher implements Launcher {

    ClassLoader discoverContextClassLoader
    ClassLoader executeDiscoveryRequestContextClassLoader
    ClassLoader executeTestPlanContextClassLoader
    ClassLoader executeExecutionRequestContextClassLoader

    @Override
    void registerLauncherDiscoveryListeners(LauncherDiscoveryListener... listeners) {
    }

    @Override
    void registerTestExecutionListeners(TestExecutionListener... listeners) {
    }

    @Override
    TestPlan discover(LauncherDiscoveryRequest request) {
      discoverContextClassLoader = Thread.currentThread().contextClassLoader
      return null
    }

    @Override
    void execute(LauncherDiscoveryRequest request, TestExecutionListener... listeners) {
      executeDiscoveryRequestContextClassLoader = Thread.currentThread().contextClassLoader
    }

    @Override
    void execute(TestPlan testPlan, TestExecutionListener... listeners) {
      executeTestPlanContextClassLoader = Thread.currentThread().contextClassLoader
    }

    @Override
    void execute(LauncherExecutionRequest request) {
      executeExecutionRequestContextClassLoader = Thread.currentThread().contextClassLoader
    }
  }
}
