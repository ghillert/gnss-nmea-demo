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

import java.util.List;

import javax.bluetooth.RemoteDevice;

import com.hillert.gnss.demo.model.LocalDeviceInformation;

/**
*
* @author Gunnar Hillert
*
*/
public interface BluetoothService {

	LocalDeviceInformation getLocalDeviceInformation();

	void discoverBluetoothDevices();

	void subscribeToData(String id);

	void disconnect();

	List<RemoteDevice> getDiscoveredBluetoothDevices();

	List<String> getDiscoveredBluetoothServices();

}
