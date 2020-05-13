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

package com.hillert.gnss.demo.services.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import com.hillert.gnss.demo.config.SpringIntegrationConfig.NmeaMessageGateway;
import com.hillert.gnss.demo.model.RemoteGnssDevice;
import com.hillert.gnss.demo.services.ConnectionType;
import com.hillert.gnss.demo.services.ConnectorService;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* Implementation of the {@link ConnectorService} for serial connections (e.g. via USB).
*
* @author Gunnar Hillert
*
*/
@Service
@ConditionalOnProperty(name = "demo.settings.type", havingValue = "SERIAL")
public class SerialConnectorService implements ConnectorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SerialConnectorService.class);

	@Autowired
	private NmeaMessageGateway nmeaMessageGateway;

	/**
	 * Starts the discovery for available serial devices.
	 *
	 */
	@Override
	public List<RemoteGnssDevice> discoverAndGetDevices() {

		final List<CommPortIdentifier> commPortIdentifiers = SerialConnectorService.getSerialPortIdentifiers();

		final List<RemoteGnssDevice> devices = commPortIdentifiers.stream().map(portIdentifier -> {
			RemoteGnssDevice s = new RemoteGnssDevice(portIdentifier.getName(), portIdentifier.getName());
			return s;
		}).collect(Collectors.toList());

		LOGGER.info("Finished device discovery.");

		return devices;
	}

	@Override
	public void subscribeToData(String serialPortId) {

		final CommPortIdentifier id;
		try {
			id = CommPortIdentifier.getPortIdentifier(serialPortId);
		}
		catch (NoSuchPortException e) {
			throw new IllegalStateException("The requested Port does not exist.", e);
		}
		final CommPort commPort;
		try {
			commPort = id.open(SerialConnectorService.class.getSimpleName(), 5000);
		}
		catch (PortInUseException e) {
			throw new IllegalStateException("The port requested is currently in use.", e);
		}
		commPort.disableReceiveTimeout();
		try {
			commPort.enableReceiveThreshold(1);
		}
		catch (UnsupportedCommOperationException e) {
			throw new IllegalStateException("Unable to set receive threshold.", e);
		}

		LOGGER.info("Opened serial port: " + serialPortId);

		final InputStream is;

		try {
			is = commPort.getInputStream();
		}
		catch (IOException e) {
			throw new IllegalStateException("Unable to get the input stream from the serial port.", e);
		}

		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

		while (true) {
			try {
				final String lineRead = bufferedReader.readLine();
				if (StringUtils.hasText(lineRead) && lineRead.startsWith("$")) {
					this.nmeaMessageGateway.send(lineRead);
				}
			}
			catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

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
