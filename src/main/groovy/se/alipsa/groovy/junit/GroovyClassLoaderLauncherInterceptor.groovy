package se.alipsa.groovy.junit

import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryListener
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.LauncherExecutionRequest
import org.junit.platform.launcher.LauncherInterceptor
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestPlan

/**
 * Optional JUnit Platform launcher interceptor that switches the thread context classloader
 * during launcher operations to a Groovy-friendly classloader.
 *
 * <p>The interceptor is disabled by default and must be enabled explicitly with:
 * {@code -Dgroovier.junit5.classloader.enabled=true}.</p>
 */
class GroovyClassLoaderLauncherInterceptor implements LauncherInterceptor {

  static final String ENABLE_PROPERTY = 'groovier.junit5.classloader.enabled'
  static final String MODE_PROPERTY = 'groovier.junit5.classloader.mode'
  static final String DEBUG_PROPERTY = 'groovier.junit5.classloader.debug'
  static final String MODE_CONTEXT = 'context'
  static final String MODE_ROOT = 'root'

  @Override
  def <T> T intercept(LauncherInterceptor.Invocation<T> invocation) {
    T launcherCandidate = invocation.proceed()
    if (!isEnabled() || !(launcherCandidate instanceof Launcher)) {
      return launcherCandidate
    }

    ClassLoader targetClassLoader = resolveTargetClassLoader()
    if (targetClassLoader == null) {
      debug("No suitable classloader found, skipping classloader wrapping")
      return launcherCandidate
    }
    debug("Using ${targetClassLoader.class.name} with mode=${resolvedMode()}")

    return (T) new ContextClassLoaderLauncher((Launcher) launcherCandidate, targetClassLoader)
  }

  @Override
  void close() {
    // Nothing to close
  }

  private boolean isEnabled() {
    Boolean.parseBoolean(System.getProperty(ENABLE_PROPERTY, 'false'))
  }

  private String resolvedMode() {
    String mode = System.getProperty(MODE_PROPERTY, MODE_CONTEXT)
    mode = mode == null ? MODE_CONTEXT : mode.trim().toLowerCase(Locale.ROOT)
    mode == MODE_ROOT ? MODE_ROOT : MODE_CONTEXT
  }

  private ClassLoader resolveTargetClassLoader() {
    String mode = resolvedMode()
    if (mode == MODE_ROOT) {
      ClassLoader rootLoader = findRootLoader(Thread.currentThread().contextClassLoader)
          ?: findRootLoader(ClassLoader.getSystemClassLoader())
          ?: findRootLoader(this.class.classLoader)
      if (rootLoader == null) {
        debug('mode=root requested but no RootLoader found in classloader hierarchies')
      }
      return rootLoader
    }

    ClassLoader contextLoader = Thread.currentThread().contextClassLoader
    if (isGroovyGrabTarget(contextLoader)) {
      return contextLoader
    }

    ClassLoader systemLoader = ClassLoader.getSystemClassLoader()
    if (isGroovyGrabTarget(systemLoader)) {
      return systemLoader
    }

    ClassLoader interceptorLoader = this.class.classLoader
    if (isGroovyGrabTarget(interceptorLoader)) {
      return interceptorLoader
    }

    ClassLoader parent = contextLoader ?: systemLoader ?: interceptorLoader
    ClassLoader groovyLoader = createGroovyClassLoader(parent)
    if (groovyLoader == null) {
      debug('Could not create GroovyClassLoader, leaving launcher unchanged')
    }
    return groovyLoader
  }

  private static ClassLoader findRootLoader(ClassLoader classLoader) {
    ClassLoader current = classLoader
    while (current != null) {
      if (current.class.name == 'org.codehaus.groovy.tools.RootLoader') {
        return current
      }
      current = current.parent
    }
    return null
  }

  private static boolean isGroovyGrabTarget(ClassLoader classLoader) {
    if (classLoader == null) {
      return false
    }
    Class currentClass = classLoader.class
    while (currentClass != null) {
      if (currentClass.name == 'groovy.lang.GroovyClassLoader'
          || currentClass.name == 'org.codehaus.groovy.tools.RootLoader') {
        return true
      }
      currentClass = currentClass.superclass
    }
    return false
  }

  private static ClassLoader createGroovyClassLoader(ClassLoader parent) {
    try {
      Class groovyClassLoaderClass = Class.forName('groovy.lang.GroovyClassLoader')
      return (ClassLoader) groovyClassLoaderClass.getConstructor(ClassLoader).newInstance(parent)
    } catch (Throwable ignored) {
      return null
    }
  }

  private void debug(String message) {
    if (Boolean.parseBoolean(System.getProperty(DEBUG_PROPERTY, 'false'))) {
      System.err.println("[groovier-junit5] ${message}")
    }
  }

  private static final class ContextClassLoaderLauncher implements Launcher {

    private final Launcher launcherDelegate
    private final ClassLoader targetClassLoader

    private ContextClassLoaderLauncher(Launcher delegate, ClassLoader targetClassLoader) {
      this.launcherDelegate = delegate
      this.targetClassLoader = targetClassLoader
    }

    @Override
    void registerLauncherDiscoveryListeners(LauncherDiscoveryListener... listeners) {
      launcherDelegate.registerLauncherDiscoveryListeners(listeners)
    }

    @Override
    void registerTestExecutionListeners(TestExecutionListener... listeners) {
      launcherDelegate.registerTestExecutionListeners(listeners)
    }

    @Override
    TestPlan discover(LauncherDiscoveryRequest request) {
      withContext { this.launcherDelegate.discover(request) }
    }

    @Override
    void execute(LauncherDiscoveryRequest request, TestExecutionListener... listeners) {
      withContextVoid { this.launcherDelegate.execute(request, listeners) }
    }

    @Override
    void execute(TestPlan testPlan, TestExecutionListener... listeners) {
      withContextVoid { this.launcherDelegate.execute(testPlan, listeners) }
    }

    @Override
    void execute(LauncherExecutionRequest request) {
      withContextVoid { this.launcherDelegate.execute(request) }
    }

    private <T> T withContext(Closure<T> action) {
      Thread thread = Thread.currentThread()
      ClassLoader previous = thread.contextClassLoader
      thread.contextClassLoader = targetClassLoader
      try {
        return action.call()
      } finally {
        thread.contextClassLoader = previous
      }
    }

    private void withContextVoid(Closure<Void> action) {
      withContext {
        action.call()
        null
      }
    }
  }
}
