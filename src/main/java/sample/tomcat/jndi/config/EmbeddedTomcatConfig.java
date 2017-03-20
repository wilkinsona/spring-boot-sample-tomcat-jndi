package sample.tomcat.jndi.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.jndi.JNDIReferenceFactory;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * This would be tuned off (probably using profiles) if this app would need need to be build as WAR and
 * deployed to an external Servlet or Application Container with pre-configured JNDI resources.
 * In that case all JNDI names and resources looked up by them would remain the same for this app's new
 * code as well as legacy code.
 */
@Configuration
public class EmbeddedTomcatConfig {

	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatFactory() {
		return new TomcatEmbeddedServletContainerFactory() {

			@Override
			protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatEmbeddedServletContainer(tomcat);
			}

			@Override
			protected void postProcessContext(Context context) {
                context.getNamingResources().addResource(createDataSource());
				context.getNamingResources().addResource(createJmsConnectionFactory());
				context.getNamingResources().addResource(createJmsQueue());
				context.getNamingResources().addResource(createJmsTopic());
			}

            private ContextResource createDataSource() {
                ContextResource resource = new ContextResource();
                resource.setName("jdbc/myDataSource");
                resource.setType(DataSource.class.getName());
                resource.setProperty("driverClassName", "your.db.Driver");
                resource.setProperty("url", "jdbc:yourDb");
                return resource;
            }

            /**
             * To quickly add some JMS using ActiveMQ roughly based on http://activemq.apache.org/tomcat.html
             *
             * <Resource  name=”jms/ConnectionFactory” auth=”Container” type=”org.apache.activemq.ActiveMQConnectionFactory”
             * description=”JMS Connection Factory” factory=”org.apache.activemq.jndi.JNDIReferenceFactory”
             * brokerURL=”tcp://localhost:61616″ brokerName=”ActiveMQBroker” useEmbeddedBroker=”false”/>
             */
			private ContextResource createJmsConnectionFactory() {
				ContextResource resource = new ContextResource();
				resource.setName("jms/ConnectionFactory");
				resource.setAuth("Container");
				resource.setType(ActiveMQConnectionFactory.class.getName());
				resource.setDescription("JMS Connection Factory");
				resource.setProperty("factory", JNDIReferenceFactory.class.getName());
				resource.setProperty("brokerName", "ActiveMQBroker");
//				resource.setProperty("useEmbeddedBroker", "false");
//				resource.setProperty("brokerURL", "tcp://localhost:61616");
				resource.setProperty("useEmbeddedBroker", "true");
				resource.setProperty("brokerURL", "vm://localhost?broker.persistent=false");
                return resource;
			}

            /**
             * <Resource name=”jms/topic/MyTopic” auth=”Container” type=”org.apache.activemq.command.ActiveMQTopic”
             * factory=”org.apache.activemq.jndi.JNDIReferenceFactory” physicalName=”APP.JMS.TOPIC”/>
             */
            private ContextResource createJmsTopic() {
                ContextResource resource = new ContextResource();
                resource.setName("jms/topic/MyTopic");
                resource.setAuth("Container");
                resource.setType(ActiveMQTopic.class.getName());
                resource.setProperty("factory", JNDIReferenceFactory.class.getName());
                resource.setProperty("physicalName", "APP.JMS.TOPIC");
                return resource;
            }

			/**
			 * <Resource name=”jms/queue/MyQueue” auth=”Container” type=”org.apache.activemq.command.ActiveMQQueue”
			 * factory=”org.apache.activemq.jndi.JNDIReferenceFactory” physicalName=” APP.JMS.QUEUE “/>
			 */
			private ContextResource createJmsQueue() {
				ContextResource resource = new ContextResource();
				resource.setName("jms/topic/MyQueue");
				resource.setAuth("Container");
				resource.setType(ActiveMQQueue.class.getName());
				resource.setProperty("factory", JNDIReferenceFactory.class.getName());
				resource.setProperty("physicalName", "APP.JMS.QUEUE");
				return resource;
			}

		};
	}

}
