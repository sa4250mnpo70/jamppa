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
package org.jamppa.component.handler;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.jamppa.component.PacketSender;
import org.jamppa.component.db.ComponentDataSource;

/**
 * Generic QueryHandler. 
 * QueryHandler implementations should extend this class.
 * 
 * @see QueryHandler
 *  
 */
public abstract class AbstractQueryHandler implements QueryHandler {

	private final String namespace;
	private final Logger logger;
	private Properties properties;
	private ComponentDataSource dataSource;
	private PacketSender packetSender;
	
	/**
	 * Creates a QueryHandler for a given namespace 
	 * @param namespace
	 */
	public AbstractQueryHandler(String namespace) {
		this.namespace = namespace;
		this.logger = Logger.getLogger(getClass());
	}
	
	@Override
	public String getNamespace() {
		return namespace;
	}
	
	protected Logger getLogger() {
		return logger;
	}
	
	protected Properties getProperties() {
		return properties;
	}
	
	/**
	 * @return the dataSource
	 */
	public ComponentDataSource getDataSource() {
		return dataSource;
	}
	
	/**
	 * @return the packetSender
	 */
	public PacketSender getPacketSender() {
		return packetSender;
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(ComponentDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * @param packetSender the packetSender to set
	 */
	public void setPacketSender(PacketSender packetSender) {
		this.packetSender = packetSender;
	}
}
