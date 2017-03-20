package sample.tomcat.jndi.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class SampleJmsService {

    private static Log LOGGER = LogFactory.getLog(SampleJmsService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue myQueue;

    public void sendMessage(final String message) {
        jmsTemplate.send(myQueue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
    }

    @JmsListener(destination = "jms/topic/MyQueue")
    public void processMessage(String message) {
        LOGGER.info("MBP got a message with text [" + message + "]");
    }

}
