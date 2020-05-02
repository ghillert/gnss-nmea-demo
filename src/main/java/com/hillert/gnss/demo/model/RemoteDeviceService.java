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

import javax.bluetooth.RemoteDevice;

import org.springframework.util.Assert;

/**
 * A remote bluetooth device may technically provide multiple services. As such,
 * this class will hold the {@link RemoteDevice} and the corresponding service-specific
 * connection url.
 *
 * @author Gunnar Hillert
 *
 */
public class RemoteDeviceService implements Comparable<RemoteDeviceService> {

	private final RemoteDevice remoteDevice;
	private final String connectionUrl;

	public RemoteDeviceService(RemoteDevice remoteDevice, String connectionUrl) {
		super();
		Assert.notNull(remoteDevice, "The remoteDevice cannot be null.");
		Assert.hasText(connectionUrl, "connectionUrl cannot be empty.");
		this.remoteDevice = remoteDevice;
		this.connectionUrl = connectionUrl;
	}

	public RemoteDevice getRemoteDevice() {
		return this.remoteDevice;
	}

	/**
	 * REturns the connection Url of the service.
	 * @return Connection Url of the service. Never null.
	 */
	public String getConnectionUrl() {
		return this.connectionUrl;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RemoteDeviceService [remoteDevice=");
		builder.append(this.remoteDevice);
		builder.append(", connectionUrl=");
		builder.append(this.connectionUrl);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(RemoteDeviceService remoteDeviceService) {
		return this.getConnectionUrl().compareTo(remoteDeviceService.getConnectionUrl());
	}

}
