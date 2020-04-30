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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
*
* @author Gunnar Hillert
*
*/
public class BluetoothDiscoveryListener implements DiscoveryListener {

	private final CountDownLatch countDownLatch = new CountDownLatch(1);

	private final List<RemoteDevice> getDiscoveredBluetoothDevices;
	private final List<String> getDiscoveredBluetoothServices;

	public BluetoothDiscoveryListener(
			List<RemoteDevice> getDiscoveredBluetoothDevices,
			List<String> discoveredBluetoothServices) {
		this.getDiscoveredBluetoothDevices = getDiscoveredBluetoothDevices;
		this.getDiscoveredBluetoothServices = discoveredBluetoothServices;
	}

	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] services) {
		for (int i = 0; i < services.length; i++) {
				String url = services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
				if (url == null) {
					continue;
				}

				DataElement serviceName = services[i].getAttributeValue(0x0100);
				if (serviceName != null) {
					System.out.println("service " + serviceName.getValue() + " found " + url);
				}
				else {
					System.out.println("service found " + url);
				}
				this.getDiscoveredBluetoothServices.add(url);
		}
	}

	@Override
	public void serviceSearchCompleted(int transID, int respCode) {
		this.countDownLatch.countDown();
	}

	@Override
	public void inquiryCompleted(int discType) {
		// Logs the end of the device discovery.
		System.out.println("Device discovery completed!");
		this.countDownLatch.countDown();
	}

	@Override
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		// Logs the device discovered.
		String address = btDevice.getBluetoothAddress();
		try {
			String name = btDevice.getFriendlyName(false);
			System.out.println("New device discovered: [" + address + " - " + name + "]");
		}
		catch (IOException e) {
			System.err.println("Error while retrieving name for device [" + address + "]");
			e.printStackTrace();
		}
		this.getDiscoveredBluetoothDevices.add(btDevice);
	}

	public CountDownLatch getCountDownLatch() {
		return this.countDownLatch;
	}
}
