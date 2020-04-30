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

package com.hillert.gnss.demo.support;

import java.util.List;
import java.util.Scanner;

import javax.bluetooth.RemoteDevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hillert.gnss.demo.services.BluetoothService;

/**
*
* @author Gunnar Hillert
*
*/
@Component
@Profile("!test")
public class AppStartRunner implements ApplicationRunner {

	@Autowired
	private BluetoothService bluetoothService;

	@Autowired
	DemoSettings demoSettings;

	private static final Logger LOG =
	LoggerFactory.getLogger(AppStartRunner.class);

	private static int counter;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOG.info("Application started with option names : {}",
		args.getOptionNames());
		LOG.info("Increment counter");
		counter++;

		final String bluetoothAddressToUse;

		if (StringUtils.hasText(this.demoSettings.getBluetoothAddress())) {
			LOG.info("Using pre-configured bluetooth device: " + this.demoSettings.getBluetoothAddress());
			bluetoothAddressToUse = this.demoSettings.getBluetoothAddress();
		}
		else {
			LOG.info("Bluetooth device not pre-configurred. Starting discovery…");
			this.bluetoothService.discoverBluetoothDevices();
			List<RemoteDevice> discoveredBluetoothDevices = this.bluetoothService.getDiscoveredBluetoothDevices();
			List<String> discoveredBluetoothServices = this.bluetoothService.getDiscoveredBluetoothServices();

			for (RemoteDevice rd : discoveredBluetoothDevices) {
				System.out.println(rd.getFriendlyName(false) + " : " + rd.getBluetoothAddress());
			}

			for (String serviceUrl : discoveredBluetoothServices) {
				System.out.println(serviceUrl);
			}

			System.out.println("Which Service do you like to use?");

			try(Scanner in = new Scanner(System.in)) {
				int selectedOption = in.nextInt();
				System.out.println("You entered: " + selectedOption);
				bluetoothAddressToUse = discoveredBluetoothServices.get(selectedOption);
			}

		}
		this.bluetoothService.subscribeToData(bluetoothAddressToUse);
	}
}
