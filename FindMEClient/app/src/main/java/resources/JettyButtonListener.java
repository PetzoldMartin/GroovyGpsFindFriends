package resources;

import android.os.AsyncTask;
import android.view.View;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.Servlet;

/**
 * Created by Tobias on 02.12.2015.
 */
public class JettyButtonListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        new StartJettyServerAsyncTask().execute();
    }

    private class StartJettyServerAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            java.net.InetSocketAddress addresse = new java.net.InetSocketAddress("localhost", 8088);
            Server server = new Server(addresse);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            ResourceConfig config = new ResourceConfig().register(resources.Rest.class);
            ServletContainer container = new ServletContainer(config);
            ServletHolder holder = new ServletHolder((Servlet) container);
            holder.setInitOrder(0);
            holder.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, resources.Rest.class.getCanonicalName());

            context.addServlet(holder, "/*");
            server.setHandler(context);

            try {
                server.start();
                server.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
