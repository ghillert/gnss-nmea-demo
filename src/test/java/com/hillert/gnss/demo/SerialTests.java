package com.hillert.gnss.demo;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

class SerialTests {

	@Test
	void test() throws InterruptedException, NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException {
		Thread thread = new Thread(SerialTests::printPortIdentifiers);
		thread.start();
		Thread.sleep(2000L);
		//String PORT = "/dev/tty.usbmodem1422401";
		String PORT = "COM4";
		CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(PORT);
		CommPort port2 = id.open(SerialTests.class.getSimpleName(), 5000);
		System.out.println("Opened: " + PORT);
		InputStream is = port2.getInputStream();
		port2.disableReceiveTimeout();
		port2.enableReceiveThreshold(1);
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

		while (true) {
			try {
				final String lineRead = bufferedReader.readLine();
				System.out.println(lineRead);
			}
			catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}


		//LOCK.lock();
	}

	@SuppressWarnings("unchecked")
	private static void printPortIdentifiers() {
		Enumeration<CommPortIdentifier> ids = CommPortIdentifier.getPortIdentifiers();
		System.out.println("--- Port Identifiers ---");
		while (ids.hasMoreElements()) {
			System.out.println("name: " + ids.nextElement().getName());
		}
	}
}
