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

package com.hillert.gnss.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.http.config.EnableIntegrationGraphController;

import com.hillert.gnss.demo.integration.GgaService;
import com.hillert.gnss.demo.integration.GsaService;
import com.hillert.gnss.demo.integration.GsvService;
import com.hillert.gnss.demo.integration.UbxService;
import com.hillert.gnss.demo.store.GnssStatusStore;
import com.hillert.gnss.demo.store.SatelliteStore;

/**
 * Spring Integration specific configuration.
 *
 * @author Gunnar Hillert
 *
 */
@Configuration()
@EnableIntegration
@ImportResource(locations = {"classpath:integration-context.xml"})
@EnableIntegrationGraphController
public class SpringIntegrationConfig {

	@MessagingGateway(defaultRequestChannel = "rawNmeaInput")
	public interface NmeaMessageGateway {
		void send(String data);
	}

	@Bean
	public GsvService gsvService(SatelliteStore satelliteStore, GnssStatusStore gnssStatusStore) {
		return new GsvService(satelliteStore, gnssStatusStore);
	}

	@Bean
	public GsaService gsaService(GnssStatusStore gnssStatusStore) {
		return new GsaService(gnssStatusStore);
	}

	@Bean
	public GgaService ggaService(GnssStatusStore gnssStatusStore) {
		return new GgaService(gnssStatusStore);
	}

	@Bean
	public UbxService ubxService(GnssStatusStore gnssStatusStore) {
		return new UbxService(gnssStatusStore);
	}
}
