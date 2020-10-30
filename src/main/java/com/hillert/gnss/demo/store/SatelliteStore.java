package com.hillert.gnss.demo.store;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.hillert.gnss.demo.model.GnssProvider;
import com.hillert.gnss.demo.model.Satellite;

@Component
public class SatelliteStore {

	private final Map<GnssProvider, Set<Satellite>> satellites = new ConcurrentHashMap<>(0);

	private volatile boolean processed = false;

	private volatile Set<GnssProvider> processedGnssProviders = ConcurrentHashMap.newKeySet();

	public Map<GnssProvider, Set<Satellite>> getSatellites() {
		return satellites;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public Set<GnssProvider> getProcessedGnssProviders() {
		return processedGnssProviders;
	}

	public void setProcessedGnssProviders(Set<GnssProvider> processedGnssProviders) {
		this.processedGnssProviders = processedGnssProviders;
	}

	public synchronized void cleanupIfNecessary() {
		if (processed) {
			return;
		}
		satellites.entrySet().removeIf(entry -> {
			boolean remove = !processedGnssProviders.contains(entry.getKey());
			if (remove) {
				System.out.println("remove " + entry.getKey());
			}
			return remove;
		});
		processedGnssProviders.clear();
		processed = true;
	}

}
