/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.web.templates;

import com.google.common.base.Charsets;
import com.typesafe.config.ConfigValue;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import sirius.kernel.Sirius;
import sirius.kernel.async.CallContext;
import sirius.kernel.commons.Context;
import sirius.kernel.commons.Strings;
import sirius.kernel.di.GlobalContext;
import sirius.kernel.di.Lifecycle;
import sirius.kernel.di.std.Parts;
import sirius.kernel.di.std.PriorityParts;
import sirius.kernel.di.std.Register;
import sirius.kernel.health.Exceptions;
import sirius.kernel.health.HandledException;
import sirius.kernel.health.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Content generator which generates output based on templates.
 * <p>
 * In contrast to the web server and its handlers Velocity is used here as template engine. This is because
 * these templates are easier to write as they don't need andy type information. as these templates are less
 * frequently executed the lower performance does not matter. The language reference of velocity, which is one of the
 * most commonly used language for templates can be found here:
 * http://velocity.apache.org/engine/devel/vtl-reference-guide.html
 * </p>
 * <p>
 * The template sources are loaded via {@link Resolver#resolve(String)}. If no resolver is available or none of the
 * available ones can load the template, it is tried to load the template from the classpath.
 * </p>
 * <p>
 * To extend the built in velocity engine, macro libraries can be enumerated in the system config under
 * <b>content.velocity-libraries</b> (For examples see component-web.conf). Also {@link ContentContextExtender} can
 * be implemented and registered in order to provider default variables within the execution context.
 * </p>
 * <p>
 * Specific output types are generated by {@link ContentHandler} implementations. Those are either picked by the file
 * name of the template, or by setting {@link Generator#handler(String)}. So if a file ends with <b>.pdf.vm</b> it is
 * first evaluated by velocity (expecting to generate XHTML) and then rendered to a PDF by flying saucer.
 * Alternatively the handler type <b>pdf-vm</b> can be set to ensure that this handler is picked.
 * </p>
 *
 * @author Andreas Haufler (aha@scireum.de)
 * @since 2014/02
 */
@Register(classes = {Content.class, Lifecycle.class})
public class Content implements Lifecycle {

    /**
     * If a specific output encoding is required (other than the system encoding - most definitely UTF-8) a variable
     * using this key can be supplied to the generator, specifying the name of the encoding to use.
     * <p>
     * If possible however it is preferable to use {@link Generator#encoding(String)} to set the encoding.
     * </p>
     */
    public static final String ENCODING = "encoding";

    /*
     * Logger used by the content generator framework
     */
    protected static Log LOG = Log.get("content-generator");

    /*
     * Contains all implementations of ContentHandler sorted by getPriority ascending
     */
    @PriorityParts(ContentHandler.class)
    private Collection<ContentHandler> handlers;

    /*
     * Contains all implementations of ContentContextExtender
     */
    @Parts(ContentContextExtender.class)
    private Collection<ContentContextExtender> extenders;

    @sirius.kernel.di.std.Context
    private GlobalContext ctx;

    /**
     * Used to generate content by either evaluating a template or directly supplied template code.
     * <p>
     * This uses a builder like pattern (a.k.a. fluent API) and requires to either call {@link #generate()} or
     * {@link #generateTo(java.io.OutputStream)} to finally generate the content.
     * </p>
     */
    public class Generator {

        private String templateName;
        private String templateCode;
        private String handlerType;
        private Context context = Context.create();
        private String encoding;

        /**
         * Applies the context to the generator.
         * <p>
         * This will join the given context with the one previously set (Or the system context). All values with
         * the same name will be overwritten using the values in the given context.
         * </p>
         *
         * @param ctx the context to be applied to the one already present
         * @return the generator itself for fluent API calls
         */
        public Generator applyContext(Context ctx) {
            context.putAll(ctx);
            return this;
        }

        /**
         * Adds a variable with the given name (key) and value to the internal context.
         * <p>
         * If a value with the same key was already defined, it will be overwritten.
         * </p>
         *
         * @param key   the name of the variable to set
         * @param value the value of the variable to set
         * @return the generator itself for fluent API calls
         */
        public Generator put(String key, Object value) {
            context.put(key, value);
            return this;
        }

        /**
         * Sets the output encoding which is used to generate the output files.
         *
         * @param encoding the encoding to use for output files
         * @return the generator itself for fluent API calls
         */
        public Generator encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        /**
         * Determines which template file should be used.
         * <p>
         * The content is resolved by calling {@link Resolver#resolve(String)} or by a classpath lookup if
         * all resolvers fail.
         * </p>
         *
         * @param templateName the name of the template to use
         * @return the generator itself for fluent API calls
         */
        public Generator useTemplate(String templateName) {
            this.templateName = templateName;
            return this;
        }

        /**
         * Sets the template code to be used directly as string.
         * <p>
         * Most probably this will be velocity code. Once a direct code is set, the template specified by
         * {@link #useTemplate(String)} will be ignored.
         * </p>
         *
         * @param templateCode the template code to evaluate
         * @return the generator itself for fluent API calls
         */
        public Generator direct(String templateCode) {
            this.templateCode = templateCode;
            return this;
        }

        /**
         * Specifies which {@link ContentHandler} is used to generate the content.
         * <p>
         * Most of the time, the content handler is auto-detected using the file name of the template. An example
         * would be <b>.pdf.vm</b> which will force the {@link VelocityPDFContentHandler} to generate a PDF file
         * using the template. However, by using <code>generator.handler("pdf-vm")</code> it can be ensured, that
         * this handler is picked, without relying on the file name.
         * </p>
         *
         * @param handlerType the name of the handler type to use. Constants can be found by looking at the
         *                    {@link Register} annotations of the implementing classes of {@link ContentHandler}.
         * @return the generator itself for fluent API calls
         */
        public Generator handler(String handlerType) {
            this.handlerType = handlerType;
            return this;
        }

        /**
         * Calls the appropriate {@link ContentHandler} to generate the output which is written into the given
         * output stream.
         *
         * @param out the output stream to which the generated content is written
         */
        public void generateTo(OutputStream out) {
            if (Strings.isFilled(templateName)) {
                CallContext.getCurrent().addToMDC("content-generator-template", templateName);
            }
            try {
                try {
                    if (Strings.isFilled(handlerType)) {
                        ContentHandler handler = ctx.findPart(handlerType, ContentHandler.class);
                        if (!handler.generate(this, out)) {

                        }
                    }
                    for (ContentHandler handler : handlers) {
                        if (handler.generate(this, out)) {
                            return;
                        }
                    }
                } catch (HandledException e) {
                    throw e;
                } catch (Throwable e) {
                    throw Exceptions.handle()
                                    .error(e)
                                    .to(LOG)
                                    .withSystemErrorMessage("Error applying template '%s': %s (%s)",
                                                            Strings.isEmpty(templateName) ? templateCode : templateName)
                                    .handle();
                }
            } finally {
                CallContext.getCurrent().removeFromMDC("content-generator-template");
            }
        }

        /**
         * Invokes the appropriate {@link ContentHandler} and returns the generated content handler as string.
         * <p>
         * Most probably the input will be a velocity template which generates readable text (which also might be
         * XML or HTML).
         * </p>
         *
         * @return the generated string contents
         */
        public String generate() {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            generateTo(out);
            return new String(out.toByteArray(), Charsets.UTF_8);
        }

        /**
         * Can be used by a {@link ContentHandler} to obtain a preset templateName.
         *
         * @return the templateName which was previously set or <tt>null</tt> if no template name was given
         */
        public String getTemplateName() {
            return templateName;
        }

        /**
         * Can be used by a {@link ContentHandler} to obtain a preset templateCode.
         *
         * @return the templateCode which was previously set or <tt>null</tt> if no template code was given
         */
        public String getTemplateCode() {
            return templateCode;
        }

        /**
         * Can be used by a {@link ContentHandler} to access the context which contains all previously set variables.
         *
         * @return the previously set context containing all applied variables.
         */
        public Context getContext() {
            return context;
        }

        /**
         * Can be used by a {@link ContentHandler} to determine the file ending of the selected template. This is
         * used to select which content handler is actually used to generate the output.
         *
         * @param extension the expected file extension
         * @return <tt>true</tt> if the given template ends with the given extensions, <tt>false</tt> otherwise.
         *         If the templateName is <tt>null</tt>, this method always returns <tt>false</tt>.
         */
        public boolean isTemplateEndsWith(String extension) {
            return Strings.isFilled(templateName) && templateName.endsWith(extension);
        }

        /**
         * Can be used by a {@link ContentHandler} to determine the effective encoding used for the generated output.
         * This is either set via {@link #encoding(String)} or by placing a variable named {@link #ENCODING} in the
         * context or it is the default encoding used by the JVM (most probably UTF-8).
         *
         * @return the effective encoding used to generate the output
         */
        public String getEncoding() {
            if (Strings.isFilled(encoding)) {
                return encoding;
            }
            if (context.containsKey(ENCODING)) {
                return (String) context.get(ENCODING);
            }
            return Charsets.UTF_8.name();
        }

        /**
         * Contains the handler type. This can be used by a {@link ContentHandler} to skip all filename checks and
         * always generate its output.
         *
         * @return the handlerType previously set using {@link #handler(String)} or <tt>null</tt> if no handler type
         *         was set.
         */
        public String getHandlerType() {
            return handlerType;
        }

        /**
         * Uses the {@link Resolver} implementations or the classloader to load the template as input stream.
         *
         * @return the contents of the template as stream or <tt>null</tt> if the template cannot be resolved
         */
        public InputStream getTemplate() {
            return VelocityResourceLoader.INSTANCE.getResourceStream(templateName);
        }
    }

    /**
     * Creates a new generator which can be used to generate a template based output.
     *
     * @return a new {@link Generator} which can be used to generate output
     */
    public Generator generator() {
        Generator result = new Generator();
        for (ContentContextExtender extender : extenders) {
            extender.extend(result.getContext());
        }

        return result;
    }

    @Override
    public void started() {
        // We wait with starting and configuring Velocity up until now as we need the
        // system config to be ready and populated...
        try {
            Velocity.setProperty("sirius.resource.loader.class", VelocityResourceLoader.class.getName());
            Velocity.setProperty(RuntimeConstants.RESOURCE_MANAGER_CACHE_CLASS, VelocityResourceCache.class.getName());
            Velocity.setProperty(Velocity.RESOURCE_LOADER, "sirius");
            StringBuilder libraryPath = new StringBuilder();
            for (Map.Entry<String, ConfigValue> e : Sirius.getConfig()
                                                          .getConfig("content.velocity-libraries")
                                                          .entrySet()) {
                libraryPath.append(e.getValue().unwrapped());
                libraryPath.append(",");
            }
            Velocity.setProperty(Velocity.VM_LIBRARY, libraryPath.toString());
            Velocity.setProperty(Velocity.SET_NULL_ALLOWED, Boolean.TRUE);
            Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                                 "org.apache.velocity.runtime.log.Log4JLogChute");
            Velocity.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
            Velocity.init();
        } catch (Throwable e) {
            Exceptions.handle(LOG, e);
        }
    }

    @Override
    public void stopped() {
        // Not used
    }

    @Override
    public void awaitTermination() {
        // Not necessary
    }

    @Override
    public String getName() {
        return "content-generator";
    }
}
