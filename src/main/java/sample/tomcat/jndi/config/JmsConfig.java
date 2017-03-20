package sample.tomcat.jndi.config;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.naming.ContextBindings;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DestinationResolutionException;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.web.context.WebApplicationContext;

import javax.jms.*;
import javax.naming.NamingException;
import java.lang.IllegalStateException;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(WebApplicationContext applicationContext) throws NamingException {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());

        // Case 1. JndiDestinationResolver - does not work, as it does an JNDI lookup in separate thread which Tomcat's JNDI
        // treats as a different App context.
//        factory.setDestinationResolver(new JndiDestinationResolver());

        // Case 2. Potential workaround that binds the classloader of the Boot App to Tomcat's App context and
        // its JNDI classes. Then all classes having this classloader (or ) would have been able to do JNDI lookups.
        // BUT Spring Boot in TomcatEmbeddedServletContainer unbinds this very classloader right after bootstrap finishes.
        // So even if we bind it here, it will get unbound by Spring Boot and we are back to Case 1.
//        Context context = getContext(applicationContext);
//        ContextBindings.bindClassLoader(context, context.getNamingToken(), getClass().getClassLoader());
//        factory.setDestinationResolver(new JndiDestinationResolver());

        // Case 3. Working workaround - EmbeddedTomcatJndiDestinationResolver that binds Tomcat's App context and its
        // JNDI to the thread where the lookup happens before delegating to JndiDestinationResolver.
        // While the workaround works fine for jmsListenerContainerFactory, any other threads that are not spawned
        // by Tomcat and not affected by this fix (e.g. in a legacy code that can't be changed, which might be common when
        // migrating old modular apps), would not be able to do JNDI lookups.
        Context context = getContext(applicationContext);
        factory.setDestinationResolver(new EmbeddedTomcatJndiDestinationResolver(context));

        factory.setConcurrency("3-10");
        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory() throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:comp/env/jms/ConnectionFactory");
        bean.setResourceRef(true);
        bean.afterPropertiesSet();
        return (ConnectionFactory) bean.getObject();
    }

    @Bean
    public Queue myQueue() throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("jms/topic/MyQueue");
        bean.setResourceRef(true);
        bean.afterPropertiesSet();
        return (Queue) bean.getObject();
    }

    private static class EmbeddedTomcatJndiDestinationResolver extends JndiDestinationResolver {
        private Context context;

        public EmbeddedTomcatJndiDestinationResolver(Context context) {
            super();
            this.context = context;
            setResourceRef(true);
        }

        @Override
        public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain) throws JMSException {
            try {
                ContextBindings.bindThread(context, context.getNamingToken());
                return super.resolveDestinationName(session, destinationName, pubSubDomain);
            } catch (NamingException e) {
                throw new DestinationResolutionException("Destination [" + destinationName + "] not found in JNDI", e);
            }
        }
    }

    private Context getContext(WebApplicationContext applicationContext) {
        EmbeddedWebApplicationContext ewac = (EmbeddedWebApplicationContext) applicationContext;
        TomcatEmbeddedServletContainer tomcatContainer = (TomcatEmbeddedServletContainer) ewac.getEmbeddedServletContainer();
        for (Container child : tomcatContainer.getTomcat().getHost().findChildren()) {
            if (child instanceof Context) {
                return (Context) child;
            }
        }
        throw new IllegalStateException("The host does not contain a Context");
    }

}
