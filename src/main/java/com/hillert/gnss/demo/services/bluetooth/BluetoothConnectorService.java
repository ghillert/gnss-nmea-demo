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

package com.hillert.gnss.demo.services.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.hillert.gnss.demo.model.RemoteGnssDevice;
import com.hillert.gnss.demo.services.AbstractConnectorService;
import com.hillert.gnss.demo.services.ConnectionType;
import com.hillert.gnss.demo.services.ConnectorService;

/**
* See {@link ConnectorService}.
*
* @author Gunnar Hillert
*
*/
@Service
@ConditionalOnProperty(name = "demo.settings.type", havingValue = "BLUETOOTH")
public class BluetoothConnectorService extends AbstractConnectorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BluetoothConnectorService.class);

	private SortedSet<RemoteGnssDevice> discoveredRemoteDeviceServices = new ConcurrentSkipListSet<>();

	private StreamConnection streamConnection;

	/**
	 * Starts the Bluetooth devices discovery. Close-by devices are printed in
	 * console.
	 *
	 */
	@Override
	public List<RemoteGnssDevice> discoverAndGetDevices() {

		final LocalDevice localDevice = this.getLocalDeviceInformation();

		final DiscoveryAgent agent = localDevice.getDiscoveryAgent();

		LOGGER.info("Starting device discovery using local device {} ({}) …", localDevice.getFriendlyName(), localDevice.getBluetoothAddress());

		final BluetoothDiscoveryListener listener = new BluetoothDiscoveryListener();

		try {
			agent.startInquiry(DiscoveryAgent.GIAC, listener);
		}
		catch (BluetoothStateException e) {
			throw new IllegalStateException("Discovery cannot be started due to "
					+ "other operations that are being performed by the device", e);
		}

		try {
			listener.getCountDownLatch().await();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.warn("The discovery of Bluetooth devices got interrupted.");
		}

		final UUID[] bluetoothServiceClassuUuids = new UUID[1];
		bluetoothServiceClassuUuids[0] = new UUID(BluetoothServiceClass.SERIAL_PORT.getId());

		for (RemoteDevice device : listener.getDiscoveredBluetoothDevices()) {

			final String deviceName;
			try {
				deviceName = device.getFriendlyName(false);
			}
			catch (IOException e) {
				throw new IllegalStateException("Remote device can not be contacted or the remote " +
						"device could not provide its name.", e);
			}
			try {
				LOGGER.info("Trying to discover the following service(s): '{}' for device '{}'.", bluetoothServiceClassuUuids, deviceName);
				listener.resetCountDownLatch();
				agent.searchServices(null, bluetoothServiceClassuUuids, device, listener);
			}
			catch (BluetoothStateException e) {
				throw new IllegalStateException("The Bluetooth services search failed.", e);
			}

			try {
				listener.getCountDownLatch().await();
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.warn("The discovery of Bluetooth services got interrupted.");
			}

			LOGGER.info("The search for Bluetooth services for device {} finished.", deviceName);
		}

		for (RemoteGnssDevice remoteDeviceService : listener.getRemoteDeviceServices()) {
			this.discoveredRemoteDeviceServices.add(remoteDeviceService);
		}

		LOGGER.info("Finished device discovery.");

		return new ArrayList<RemoteGnssDevice>(this.discoveredRemoteDeviceServices);
	}

	@Override
	public void subscribeToData(String address) {

		try {
			this.streamConnection = (StreamConnection) Connector.open(address);

			try (
				final InputStream is = streamConnection.openInputStream();
				) {
				super.extractMessages(is);
			}
		}
		catch (IOException e) {
			throw new IllegalStateException("Unable to open Bluetooth coonnection to address:" + address, e);
		}
		finally {
			if (this.streamConnection != null) {
				try {
					this.streamConnection.close();
				}
				catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	@Override
	public void disconnect() {
		if (this.streamConnection != null) {
			try {
				this.streamConnection.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private LocalDevice getLocalDeviceInformation() {
		final LocalDevice localDevice;
		try {
			localDevice = LocalDevice.getLocalDevice();
		}
		catch (BluetoothStateException e) {
			throw new IllegalStateException("The Bluetooth system could not be initialized.", e);
		}

		return localDevice;
	}

	@Override
	public ConnectionType getType() {
		return ConnectionType.BLUETOOTH;
	}
}
