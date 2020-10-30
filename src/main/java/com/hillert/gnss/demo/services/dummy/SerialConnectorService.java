/*
 * Copyright 2020 Gunnar Hillert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hillert.gnss.demo.services.dummy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.hillert.gnss.demo.model.RemoteGnssDevice;
import com.hillert.gnss.demo.services.AbstractConnectorService;
import com.hillert.gnss.demo.services.ConnectionType;
import com.hillert.gnss.demo.services.ConnectorService;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

/**
* Implementation of the {@link ConnectorService} for serial connections (e.g. via USB).
*
* @author Gunnar Hillert
*
*/
@Service
@ConditionalOnProperty(name = "demo.settings.type", havingValue = "DUMMY")
public class SerialConnectorService extends AbstractConnectorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SerialConnectorService.class);

	/**
	 * Starts the discovery for available serial devices.
	 *
	 */
	@Override
	public List<RemoteGnssDevice> discoverAndGetDevices() {

		final List<CommPortIdentifier> commPortIdentifiers = SerialConnectorService.getSerialPortIdentifiers();

		final List<RemoteGnssDevice> devices = new ArrayList<>();

		LOGGER.info("Finished device discovery.");

		return devices;
	}

	@Override
	public void subscribeToData(String serialPortId) {

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
	}

	@Override
	public ConnectionType getType() {
		return ConnectionType.SERIAL;
	}

	@SuppressWarnings("unchecked")
	private static List<CommPortIdentifier> getSerialPortIdentifiers() {
		final Enumeration<CommPortIdentifier> ids = CommPortIdentifier.getPortIdentifiers();
		return Collections.list(ids);
	}

}
