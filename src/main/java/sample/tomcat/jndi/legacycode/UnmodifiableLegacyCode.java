package sample.tomcat.jndi.legacycode;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Let's assume this is a code in a provided library that is really needed to be reused
 * and can't be reimplemented at the moment. We might not have access to sources
 * or can't change it for any other reasons.
 */
public class UnmodifiableLegacyCode {
    private static String JNDI_QUEUE_NAME = "java:comp/env/jms/queue/MyQueue";

    /**
     * This method will do successful JNDI lookup only if current thread or classloader is
     * bound to Tomcat's App context and its JNDI.
     */
    public static Queue getMessageQueue() throws NamingException {
        return (Queue) new InitialContext().lookup(JNDI_QUEUE_NAME);
    }

}
