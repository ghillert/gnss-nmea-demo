package com.hillert.gnss.demo.store;

import com.hillert.gnss.demo.model.GnssProvider;
import com.hillert.gnss.demo.model.SignalId;

public class SatelliteKey {
	private final GnssProvider gnssProvider;
	private final SignalId signalId;

	public SatelliteKey(GnssProvider gnssProvider, SignalId signalId) {
		super();
		this.gnssProvider = gnssProvider;
		this.signalId = signalId;
	}

	public GnssProvider getGnssProvider() {
		return gnssProvider;
	}

	public SignalId getSignalId() {
		return signalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gnssProvider == null) ? 0 : gnssProvider.hashCode());
		result = prime * result + ((signalId == null) ? 0 : signalId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SatelliteKey other = (SatelliteKey) obj;
		if (gnssProvider != other.gnssProvider)
			return false;
		if (signalId != other.signalId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("SatelliteKey [%s_%s]", gnssProvider.getId(), signalId);
	}

}