package com.hillert.gnss.demo.store;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.hillert.gnss.demo.model.GnssStatus;
import com.hillert.gnss.demo.model.Satellite;

@Component
public class GnssStatusStore {

	private final GnssStatus gnssStatus = new GnssStatus();

	public GnssStatus getGnssStatus() {
		return gnssStatus;
	}

}
