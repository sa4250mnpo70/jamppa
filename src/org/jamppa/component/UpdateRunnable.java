/*
 * Copyright 2012 buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jamppa.component;

import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.db.ComponentDataSource;
import org.xmpp.packet.Message;

/**
 * @author Abmar
 *
 */
public abstract class UpdateRunnable implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(UpdateRunnable.class);
	
	private final PacketSender packetSender;
	private final String action;
	private final Properties properties;
	private final ComponentDataSource dataSource;

	public UpdateRunnable(String action, ComponentDataSource dataSource, 
			PacketSender packetSender, Properties properties) {
		this.action = action;
		this.dataSource = dataSource;
		this.packetSender = packetSender;
		this.properties = properties;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Set<String> subscribers = getSubscribers();
		for (String subscriberJid : subscribers) {
			sendPacket(subscriberJid);
		}
	}

	/**
	 * @param dataSource2
	 * @return
	 */
	protected abstract Set<String> getSubscribers();

	/**
	 * @param subscriberJid
	 * @param event
	 */
	private void sendPacket(String subscriberJid) {
		Message message = new Message();
		message.setTo(subscriberJid);
		
		Element notifyEl = message.getElement().addElement("notify", getNamespace());
		notifyEl.add(getXML());
		Element actionEl = notifyEl.addElement("action");
		actionEl.setText(action);
		LOGGER.debug("Notify update: " + message.toXML());
		packetSender.sendPacket(message);
	}

	/**
	 * @return
	 */
	protected abstract String getNamespace();

	/**
	 * @return
	 */
	protected abstract Element getXML();
	
	/**
	 * @return the dataSource
	 */
	public ComponentDataSource getDataSource() {
		return dataSource;
	}
	
	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}
}
