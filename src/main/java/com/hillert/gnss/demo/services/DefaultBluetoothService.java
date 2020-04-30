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

package com.hillert.gnss.demo.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.hillert.gnss.demo.config.SpringIntegrationConfig.NmeaMessageGateway;
import com.hillert.gnss.demo.model.LocalDeviceInformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
*
* @author Gunnar Hillert
*
*/
@Service
public class DefaultBluetoothService implements BluetoothService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBluetoothService.class);

	@Autowired
	private NmeaMessageGateway nmeaMessageGateway;

	private List<RemoteDevice> discoveredBluetoothDevices = new ArrayList<>();

	private List<String> discoveredBluetoothServices = new ArrayList<>();

	/**
	 * Starts the Bluetooth devices discovery. Close-by devices are printed in
	 * console.
	 *
	 */
	@Override
	public void discoverBluetoothDevices() {

		final DiscoveryAgent agent;
		try {
			agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
		}
		catch (BluetoothStateException e) {
			throw new IllegalStateException(e);
		}
		//agent.retrieveDevices()
		LOGGER.info("Starting device discovery...");
		final BluetoothDiscoveryListener listener = new BluetoothDiscoveryListener(
				this.discoveredBluetoothDevices,
				this.discoveredBluetoothServices);
		try {
			agent.startInquiry(DiscoveryAgent.GIAC, listener);
		}
		catch (BluetoothStateException e) {
			throw new IllegalStateException(e);
		}

		try {
			listener.getCountDownLatch().await();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (RemoteDevice device : this.discoveredBluetoothDevices) {
			final String deviceName;
			try {
				deviceName = device.getFriendlyName(false);
			} catch (IOException e) {
				throw new IllegalStateException("Remote device can not be contacted or the remote " +
						"device could not provide its name.", e);
			}
			try {
				UUID[] uuid = new UUID[1];
				uuid[0] = new UUID(BluetoothServiceClass.SERIAL_PORT.getId());
				agent.searchServices(null, uuid, device, listener);
			}
			catch (BluetoothStateException e) {
				throw new IllegalStateException("The Bluetooth services search failed.", e);
			}

			try {
				listener.getCountDownLatch().await();
			}
			catch (InterruptedException e) {
				LOGGER.warn("The discovery of Bluetooth devices got interrupted.");
			}

			LOGGER.info("The search for Bluetooth services for device {} finished.", deviceName);
		}

		LOGGER.info("Finished device discovery.");

	}

	@Override
	public void subscribeToData(String address) {
		final StreamConnection connection;
		try {
			connection = (StreamConnection)  Connector.open(address);
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}

		if (connection == null) {
			System.err.println("Could not open connection to address: " + address);
			System.exit(1);
		}

		final InputStream is;
		try {
			is = connection.openInputStream();
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}

		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

		while (true) {
			try {
				String lineRead = bufferedReader.readLine();
				this.nmeaMessageGateway.send(lineRead);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
	}

	@Override
	public LocalDeviceInformation getLocalDeviceInformation() {
		final LocalDevice local;
		try {
			local = LocalDevice.getLocalDevice();
		}
		catch (BluetoothStateException e) {
			throw new IllegalStateException(e);
		}

		return new LocalDeviceInformation(local.getFriendlyName(), local.getBluetoothAddress());

	}

	@Override
	public List<RemoteDevice> getDiscoveredBluetoothDevices() {
		return this.discoveredBluetoothDevices;
	}

	@Override
	public List<String> getDiscoveredBluetoothServices() {
		return this.discoveredBluetoothServices;
	}
}
