package org.knowhow.mwa.view.mustache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.servlet.ServletContext;

import org.knowhow.mwa.view.ModelContribution;
import org.knowhow.mwa.view.ModernView;
import org.knowhow.mwa.view.ModernViewResolver;
import org.springframework.web.servlet.ViewResolver;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

/**
 * A Mustache's {@link ViewResolver view resolver}.
 *
 * @author edgar.espina
 * @since 0.1
 */
public class MustacheViewResolver extends ModernViewResolver {

  /**
   * The default content type.
   */
  private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";

  /**
   * The default view prefix.
   */
  private static final String DEFAULT_PREFIX = "/";

  /**
   * The default view suffix.
   */
  private static final String DEFAULT_SUFFIX = ".html";

  /**
   * The mustache factory.
   */
  private MustacheFactory mustacheFactory;

  /**
   * The encoding charset.
   */
  private Charset encoding = Charset.forName("UTF-8");

  /**
   * Creates a new {@link MustacheViewResolver}.
   *
   * @param viewClass The mustache view class. Required.
   * @param contributions The model contributions. Cannot be null.
   */
  public MustacheViewResolver(final Class<? extends MustacheView> viewClass,
      final ModelContribution... contributions) {
    super(contributions);
    setViewClass(viewClass);
    setContentType(DEFAULT_CONTENT_TYPE);
    setPrefix(DEFAULT_PREFIX);
    setSuffix(DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link MustacheViewResolver}.
   *
   * @param contributions The model contributions. Cannot be null.
   */
  public MustacheViewResolver(final ModelContribution... contributions) {
    this(MustacheView.class, contributions);
  }

  /**
   * Configure a new {@link MustacheView}. {@inheritDoc}
   */
  @Override
  protected void buildView(final ModernView view) throws Exception {
    Reader reader = null;
    try {
      String resourceName = view.getUrl();
      reader = read(resourceName);
      Mustache mustache = mustacheFactory.compile(reader, resourceName);
      ((MustacheView) view).setMustache(mustache);
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  /**
   * Configure Mustache. {@inheritDoc}
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    mustacheFactory = new DefaultMustacheFactory() {
      @Override
      public Reader getReader(final String resourceName) {
        return read(resourceName);
      }
    };
  }

  /**
   * Read the resource at the given path.
   *
   * @param path The resource's path.
   * @return The reader.
   */
  private Reader read(String path) {
    /** Build a mustache template. */
    final ServletContext servletContext = getServletContext();
    // Fix for Jetty:
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    return new BufferedReader(new InputStreamReader(
        servletContext.getResourceAsStream(path), encoding));
  }

  /**
   * Returns the mustache template engine.
   *
   * @return The mustache template engine.
   */
  public MustacheFactory getMustacheFactory() {
    return mustacheFactory;
  }

  /**
   * Set the charset encoding. Default is: UTF-8.
   *
   * @param encoding The charset encoding.
   */
  public void setEncoding(final String encoding) {
    this.encoding = Charset.forName(encoding);
  }

  /**
   * The required view class.
   *
   * @return The required view class.
   */
  @Override
  protected Class<?> requiredViewClass() {
    return MustacheView.class;
  }

}
