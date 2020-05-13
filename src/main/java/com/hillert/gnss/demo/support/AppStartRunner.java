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

import com.hillert.gnss.demo.model.RemoteGnssDevice;
import com.hillert.gnss.demo.services.ConnectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
* {@link ApplicationRunner} that checks whether a Bluetooth Url is specified as
* part of the {@link DemoSettings}. If not specified, will perform Bluetooth discovery.
*
* @author Gunnar Hillert
*
*/
@Component
@Profile("!test")
public class AppStartRunner implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppStartRunner.class);

	@Autowired
	private ConnectorService connectorService;

	@Autowired
	DemoSettings demoSettings;

	private static final Logger LOG =
	LoggerFactory.getLogger(AppStartRunner.class);

	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOG.info("Application started with option names : {}", args.getOptionNames());

		final String connectorAddressToUse;

		if (StringUtils.hasText(this.demoSettings.getId())) {
			LOG.info("Using pre-configured {} device: {}.",
				this.connectorService.getType(), this.demoSettings.getId());
			connectorAddressToUse = this.demoSettings.getId();
		}
		else {
			LOG.info("{} device not pre-configurred. Starting discovery...", this.connectorService.getType());
			final List<RemoteGnssDevice> discoveredRemoteDeviceService = this.connectorService.discoverAndGetDevices();

			if (discoveredRemoteDeviceService.isEmpty()) {
				LOGGER.warn("No {} devices/services found. Exiting...", this.connectorService.getType());
				System.exit(1);
			}

			int option = 0;
			for (RemoteGnssDevice remoteDeviceService : discoveredRemoteDeviceService) {
				System.out.println("Option " + option + " : " +
					remoteDeviceService.getRemoteDeviceLabel() +
					" - connection id: " + remoteDeviceService.getConnectionId());
				option++;
			}

			System.out.println("Which " + this.connectorService.getType() + " Service do you like to use? Please enter a " +
					"numeric option:");

			try (Scanner in = new Scanner(System.in)) {
				int selectedOption = in.nextInt();
				System.out.println("You entered: " + selectedOption);
				connectorAddressToUse = discoveredRemoteDeviceService.get(selectedOption).getConnectionId();
			}

		}
		this.connectorService.subscribeToData(connectorAddressToUse);
	}
}
