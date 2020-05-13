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

package com.hillert.gnss.demo.model;

import org.springframework.util.Assert;

/**
 * A remote bluetooth device may technically provide multiple services. As such,
 * this class will hold the {@link RemoteGnssDevice} and the corresponding service-specific
 * connection url.
 *
 * @author Gunnar Hillert
 *
 */
public class RemoteGnssDevice implements Comparable<RemoteGnssDevice> {

	private final String remoteDeviceLabel;
	private final String connectionId;

	public RemoteGnssDevice(String remoteDeviceLabel, String connectionId) {
		super();
		Assert.hasText(remoteDeviceLabel, "The remoteDeviceLabel cannot be empty.");
		Assert.hasText(connectionId, "connectionId cannot be empty.");
		this.remoteDeviceLabel = remoteDeviceLabel;
		this.connectionId = connectionId;
	}

	public String getRemoteDeviceLabel() {
		return this.remoteDeviceLabel;
	}

	/**
	 * REturns the connection Url of the service.
	 * @return Connection Url of the service. Never null.
	 */
	public String getConnectionId() {
		return this.connectionId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RemoteDevice [remoteDeviceLabel=");
		builder.append(this.remoteDeviceLabel);
		builder.append(", connectionId=");
		builder.append(this.connectionId);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(RemoteGnssDevice remoteDevice) {
		return this.remoteDeviceLabel.compareTo(remoteDevice.getRemoteDeviceLabel());
	}

}
