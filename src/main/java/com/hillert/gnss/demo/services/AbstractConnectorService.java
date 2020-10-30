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

import org.opengis.metadata.identification.CharacterSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hillert.gnss.demo.config.SpringIntegrationConfig.NmeaMessageGateway;

/**
* See {@link ConnectorService}.
*
* @author Gunnar Hillert
*
*/
@Service
@ConditionalOnProperty(name = "demo.settings.type", havingValue = "BLUETOOTH")
public abstract class AbstractConnectorService implements ConnectorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConnectorService.class);

	@Autowired
	private NmeaMessageGateway nmeaMessageGateway;

	public void extractMessages(InputStream is) {

//		while(!Thread.interrupted()) {
//						try {
////							Thread.sleep(1000);
////							System.out.println("Running, press CTRL-C to stop..");
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//		}
		try (
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, CharacterSet.ISO_8859_1.toCharset()))
			) {
			while(!Thread.interrupted()) {
				try {
					if(!bufferedReader.ready()) {
						try {
							Thread.sleep(300);
							continue;
						}
						catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}
				catch (IOException e) {
					throw new IllegalStateException(e);
				}

				try {
					final String lineRead = bufferedReader.readLine();
//					if (lineRead.matches("^...GSV.*")) {
//						System.out.println(lineRead);
//					}
					if (StringUtils.hasText(lineRead) && lineRead.startsWith("$")) {
						this.nmeaMessageGateway.send(lineRead);
					}
				}
				catch (IOException e) {
					throw new IllegalStateException(e);
				}

				try {
					Thread.sleep(28);
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
