package jetty.test

import java.net.InetSocketAddress

import javax.servlet.Servlet

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.servlet.ServletContainer

InetSocketAddress addr = new InetSocketAddress("localhost", 8080)
Server server = new Server(addr)
ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS)
context.setContextPath("/")

ResourceConfig config = new ResourceConfig().register(Rest.class)
ServletContainer container = new ServletContainer(config)
ServletHolder holder = new ServletHolder((Servlet) container)
holder.setInitOrder(0)
holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "jetty.test")

context.addServlet(holder, "/*")
server.setHandler(context)
server.start()
server.join()
