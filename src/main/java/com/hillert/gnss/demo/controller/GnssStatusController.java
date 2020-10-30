package com.hillert.gnss.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hillert.gnss.demo.model.GnssStatus;
import com.hillert.gnss.demo.store.GnssStatusStore;

@RestController
@RequestMapping(path = "/gnss-status")
public class GnssStatusController {

	@Autowired
	private GnssStatusStore gnssStatusStore;

	@GetMapping
	public GnssStatus getGnssStatus() {
		return gnssStatusStore.getGnssStatus();
	}
}
