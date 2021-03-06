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

import org.jamppa.component.PacketSender;
import org.jamppa.component.db.ComponentDataSource;
import org.xmpp.packet.IQ;

/**
 * Handle content queries (iq gets) to this component.
 * 
 */
public interface QueryHandler {

	IQ handle(IQ query);
	
	String getNamespace();
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(ComponentDataSource dataSource);
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties);
	
	
	/**
	 * @param packetSender the packetSender to set
	 */
	public void setPacketSender(PacketSender packetSender);
}
