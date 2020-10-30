package com.hillert.gnss.demo.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hillert.gnss.demo.model.GnssProvider;
import com.hillert.gnss.demo.model.Satellite;
import com.hillert.gnss.demo.model.SignalId;
import com.hillert.gnss.demo.store.SatelliteKey;
import com.hillert.gnss.demo.store.SatelliteStore;

@RestController
@RequestMapping(path = "/satellites")
public class SatelliteController {

	@Autowired
	private SatelliteStore satelliteStore;

	@GetMapping
	public Map<GnssProvider, Set<Satellite>> getSatellites() {
		return satelliteStore.getSatellites();
	}
}
