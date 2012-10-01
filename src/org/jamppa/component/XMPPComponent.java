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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jamppa.component.db.ComponentDataSource;
import org.jamppa.component.handler.QueryHandler;
import org.jamppa.component.utils.ConfigurationUtils;
import org.jamppa.component.utils.XMPPUtils;
import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.AbstractComponent;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;

/**
 * @author Abmar
 *
 */
public class XMPPComponent extends AbstractComponent implements PacketSender  {

	private static final Logger LOGGER = Logger.getLogger(XMPPComponent.class);
	
	private final Map<String, QueryHandler> queryGetHandlers = new HashMap<String, QueryHandler>();
	private final Map<String, QueryHandler> querySetHandlers = new HashMap<String, QueryHandler>();
	
	private final Properties configuration;
	private ComponentDataSource dataSource;
	
	private String description;
	private String name;
	private String discoInfoIdentityCategory;
	private String discoInfoIdentityCategoryType;
	
	/**
	 * @param configuration
	 */
	public XMPPComponent(Properties configuration) {
		this.configuration = configuration;
		this.dataSource = new ComponentDataSource(configuration);
	}

	public XMPPComponent(String configurationFilePath) {
		this(ConfigurationUtils.loadConfiguration(configurationFilePath));
	}
	
	public void addSetHandler(QueryHandler queryHandler) {
		queryHandler.setDataSource(dataSource);
		queryHandler.setProperties(configuration);
		queryHandler.setPacketSender(this);
		querySetHandlers.put(queryHandler.getNamespace(), queryHandler);
	}
	
	public void addGetHandler(QueryHandler queryHandler) {
		queryHandler.setDataSource(dataSource);
		queryHandler.setProperties(configuration);
		queryHandler.setPacketSender(this);
		queryGetHandlers.put(queryHandler.getNamespace(), queryHandler);
	}

	/* (non-Javadoc)
	 * @see org.xmpp.component.AbstractComponent#handleIQSet(org.xmpp.packet.IQ)
	 */
	@Override
	protected IQ handleIQSet(IQ iq) throws Exception {
		return handle(iq, querySetHandlers);
	}
	
	@Override
	protected IQ handleIQGet(IQ iq) throws Exception {
		return handle(iq, queryGetHandlers);
	}

	private IQ handle(IQ iq, Map<String, QueryHandler> handlers) {
		Element queryElement = iq.getElement().element("query");
		if (queryElement == null) {
			return XMPPUtils.error(iq, "IQ does not contain query element.", 
					LOGGER);
		}
		
		Namespace namespace = queryElement.getNamespace();
		
		QueryHandler queryHandler = handlers.get(namespace.getURI());
		if (queryHandler == null) {
			return XMPPUtils.error(iq, "QueryHandler not found for namespace: " + namespace, 
					LOGGER);
		}
		
		return queryHandler.handle(iq);
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/* (non-Javadoc)
	 * @see org.xmpp.component.AbstractComponent#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
	
	/* (non-Javadoc)
	 * @see org.xmpp.component.AbstractComponent#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override 
	protected String discoInfoIdentityCategory() {
		return discoInfoIdentityCategory;
	}
	
	/**
	 * @param discoInfoIdentityCategory the discoInfoIdentityCategory to set
	 */
	public void setDiscoInfoIdentityCategory(String discoInfoIdentityCategory) {
		this.discoInfoIdentityCategory = discoInfoIdentityCategory;
	}

	@Override 
	protected String discoInfoIdentityCategoryType() {
		return discoInfoIdentityCategoryType;
	}
	
	/**
	 * @param discoInfoIdentityCategoryType the discoInfoIdentityCategoryType to set
	 */
	public void setDiscoInfoIdentityCategoryType(
			String discoInfoIdentityCategoryType) {
		this.discoInfoIdentityCategoryType = discoInfoIdentityCategoryType;
	}

	/* (non-Javadoc)
	 * @see uk.co.rappidcars.PacketSender#sendPacket(org.jivesoftware.smack.packet.Packet)
	 */
	@Override
	public void sendPacket(Packet packet) {
		send(packet);
	}
	
	/**
	 * 
	 */
	public void run() {
		
		LOGGER.debug("Initializing XMPP component...");
		
		ExternalComponentManager componentManager = new ExternalComponentManager(
				configuration.getProperty("xmpp.host"),
				Integer.valueOf(configuration.getProperty("xmpp.port")));
		
		String subdomain = configuration.getProperty("xmpp.subdomain");
		componentManager.setSecretKey(subdomain, 
				configuration.getProperty("xmpp.secretkey"));
		
		try {
			componentManager.addComponent(subdomain, this);
		} catch (ComponentException e) {
			LOGGER.fatal("Component could not be started.", e);
		}
		
		LOGGER.debug("XMPP component initialized.");
		
		Runnable componentRunnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						LOGGER.fatal("Main loop.", e);
					}
				}
			}
		};
		
		Thread t = new Thread(componentRunnable, "xmpp-hanging-thread");
		t.start();
	}
}
