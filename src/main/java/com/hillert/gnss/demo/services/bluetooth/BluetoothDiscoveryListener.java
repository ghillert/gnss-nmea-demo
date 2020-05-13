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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import com.hillert.gnss.demo.model.RemoteGnssDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* {@link DiscoveryListener} implementation that handles device discovery and
* service discovery events.
*
* @author Gunnar Hillert
*
*/
public class BluetoothDiscoveryListener implements DiscoveryListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(BluetoothDiscoveryListener.class);

	private CountDownLatch countDownLatch = new CountDownLatch(1);

	private final Set<RemoteDevice> discoveredBluetoothDevices = ConcurrentHashMap.newKeySet();
	private final Set<RemoteGnssDevice> remoteDeviceServices = ConcurrentHashMap.newKeySet();

	public BluetoothDiscoveryListener() {
	}

	@Override
	public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
		final String address = remoteDevice.getBluetoothAddress();
		try {
			final String name = remoteDevice.getFriendlyName(false);
			LOGGER.info("New device discovered: [{}  - {}].", name, address);
		}
		catch (IOException e) {
			throw new IllegalStateException(String.format("The remote device (address: %s) can not be contacted "
					+ "or the remote device could not provide its name.", address), e);
		}
		this.discoveredBluetoothDevices.add(remoteDevice);
	}

	@Override
	public void inquiryCompleted(int discType) {
		final String deviceDiscoveryStatus;

		switch (discType) {
			case DiscoveryListener.INQUIRY_COMPLETED:
				deviceDiscoveryStatus = "completed";
				break;
			case DiscoveryListener.INQUIRY_ERROR:
				deviceDiscoveryStatus = "error";
				break;
			case DiscoveryListener.INQUIRY_TERMINATED:
				deviceDiscoveryStatus = "terminated";
				break;
			default:
				throw new IllegalStateException(String.format("Discovery status code %s is note supported.", discType));
		}

		LOGGER.info("Device discovery completed with status {}.", deviceDiscoveryStatus);
		this.countDownLatch.countDown();
	}

	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] services) {
		for (int i = 0; i < services.length; i++) {
			final String connectionUrl = services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			if (connectionUrl == null) {
				LOGGER.warn("Retrieved connectionUrl is null. Ignoring.");
				continue;
			}

			final DataElement serviceName = services[i].getAttributeValue(0x0100);
			if (serviceName != null) {
				LOGGER.info("service {} found with connectionUrl {}.", serviceName.getValue(), connectionUrl);
			}
			else {
				LOGGER.info("service found with connectionUrl {}.", connectionUrl);
			}
			try {
				this.remoteDeviceServices.add(new RemoteGnssDevice(services[i].getHostDevice().getFriendlyName(false), connectionUrl));
			}
			catch (IOException e) {
				throw new IllegalStateException("The remote device can not be "
						+ "contacted or the remote device could not provide its name.", e);
			}
		}
	}

	@Override
	public void serviceSearchCompleted(int transID, int respCode) {
		final String serviceSearchStatus;

		switch (respCode) {
			case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
				serviceSearchStatus = "completed";
				break;
			case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
				serviceSearchStatus = "remote device not reachable";
				break;
			case DiscoveryListener.SERVICE_SEARCH_ERROR:
				serviceSearchStatus = "error";
				break;
			case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
				serviceSearchStatus = "no records";
				break;
			case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
				serviceSearchStatus = "terminated";
				break;
			default:
				throw new IllegalStateException(String.format("Service search response code %s is note supported.", respCode));
		}

		LOGGER.info("Service search completed with status '{}'.", serviceSearchStatus);
		this.countDownLatch.countDown();
	}

	public CountDownLatch getCountDownLatch() {
		return this.countDownLatch;
	}

	public void resetCountDownLatch() {
		this.countDownLatch = new CountDownLatch(1);
	}

	public Set<RemoteDevice> getDiscoveredBluetoothDevices() {
		return this.discoveredBluetoothDevices;
	}

	public Set<RemoteGnssDevice> getRemoteDeviceServices() {
		return this.remoteDeviceServices;
	}

}
