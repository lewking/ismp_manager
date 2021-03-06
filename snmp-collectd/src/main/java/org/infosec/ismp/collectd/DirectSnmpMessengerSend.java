package org.infosec.ismp.collectd;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;


public class DirectSnmpMessengerSend {
	private JmsTemplate jmsTemplate;
	private Destination destination;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public void springSend(final Serializable object)
		throws Exception {
		jmsTemplate.send(destination, new MessageCreator(){
			@Override
			public Message createMessage(Session session) throws JMSException {
//				ObjectMessage msg = session.createObjectMessage(object);
				DirectSnmpModel snmpmodel = (DirectSnmpModel)object;
				MapMessage msg =session.createMapMessage();
				msg.setString("uuid", snmpmodel.getUuid());
				msg.setInt("port", snmpmodel.getPort());
				msg.setInt("version", snmpmodel.getVersion());
				msg.setString("address", snmpmodel.getAddress());
				msg.setString("community", snmpmodel.getCommunity());
				msg.setString("status", snmpmodel.getStatus());
				msg.setString("checkTime",format.format(new Date()));
				return msg;
			}
			
		});
	}	
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public void setDestination( Destination destination) {
		this.destination = destination;
	}
}
