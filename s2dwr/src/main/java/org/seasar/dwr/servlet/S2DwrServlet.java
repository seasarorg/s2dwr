package org.seasar.dwr.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.Container;
import org.directwebremoting.WebContextFactory.WebContextBuilder;
import org.directwebremoting.dwrp.HtmlCallMarshaller;
import org.directwebremoting.dwrp.PlainCallMarshaller;
import org.directwebremoting.extend.ServerLoadMonitor;
import org.directwebremoting.impl.ContainerUtil;
import org.directwebremoting.impl.DefaultContainer;
import org.directwebremoting.impl.StartupUtil;
import org.directwebremoting.servlet.DwrServlet;
import org.directwebremoting.servlet.UrlProcessor;
import org.directwebremoting.util.ServletLoggingOutput;
import org.seasar.dwr.impl.S2HtmlCallMarshaller;
import org.seasar.dwr.impl.S2PlainCallMarshaller;

public class S2DwrServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog(DwrServlet.class);

    private DefaultContainer container;

    private WebContextBuilder webContextBuilder;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        ServletContext servletContext = servletConfig.getServletContext();

        try {
            // setupLogging() only needed for servlet logging if commons-logging
            // is unavailable
            // logStartup() just outputs some version numbers
            StartupUtil.setupLogging(servletConfig, this);
            StartupUtil.logStartup(servletConfig);

            // create and setup a DefaultContainer
            container = ContainerUtil.createDefaultContainer(servletConfig);
            // ContainerUtil.setupDefaultContainer(container, servletConfig);
            ContainerUtil.setupDefaults(container, servletConfig);
            container.addParameter(HtmlCallMarshaller.class.getName(),
                    S2HtmlCallMarshaller.class.getName());
            container.addParameter(PlainCallMarshaller.class.getName(),
                    S2PlainCallMarshaller.class.getName());
            ContainerUtil.setupFromServletConfig(container, servletConfig);
            container.setupFinished();

            webContextBuilder = StartupUtil.initWebContext(servletConfig,
                    servletContext, container);
            StartupUtil.initServerContext(servletConfig, servletContext,
                    container);

            ContainerUtil.prepareForWebContextFilter(servletContext,
                    servletConfig, container, webContextBuilder, this);
            ContainerUtil.configureContainerFully(container, servletConfig);
            ContainerUtil.publishContainer(container, servletConfig);
        } catch (ExceptionInInitializerError ex) {
            log.fatal("ExceptionInInitializerError. Nested exception:", ex
                    .getException());
            throw new ServletException(ex);
        } catch (Exception ex) {
            log.fatal("DwrServlet.init() failed", ex);
            throw new ServletException(ex);
        } finally {
            if (webContextBuilder != null) {
                webContextBuilder.unset();
            }

            ServletLoggingOutput.unsetExecutionContext();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
        shutdown();
        super.destroy();
    }

    /**
     * Kill all comet polls.
     * <p>
     * Technically a servlet engine ought to call this only when all the threads
     * are already removed, however at least Tomcat doesn't do this properly (it
     * waits for a while and then calls destroy anyway).
     * <p>
     * It would be good if we could get {@link #destroy()} to call this method
     * however destroy() is only called once all threads are done so it's too
     * late.
     */
    public void shutdown() {
        ServerLoadMonitor monitor = (ServerLoadMonitor) container
                .getBean(ServerLoadMonitor.class.getName());
        monitor.shutdown();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doPost(req, resp);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        try {
            webContextBuilder.set(request, response, getServletConfig(),
                    getServletContext(), container);
            ServletLoggingOutput.setExecutionContext(this);

            UrlProcessor processor = (UrlProcessor) container
                    .getBean(UrlProcessor.class.getName());
            processor.handle(request, response);
        } finally {
            webContextBuilder.unset();
            ServletLoggingOutput.unsetExecutionContext();
        }
    }

    /**
     * Accessor for the DWR IoC container.
     * 
     * @return DWR's IoC container
     */
    public Container getContainer() {
        return container;
    }

}
