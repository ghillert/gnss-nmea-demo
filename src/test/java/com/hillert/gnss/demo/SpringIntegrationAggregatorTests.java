package com.hillert.gnss.demo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.IntegrationManagementConfigurer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.management.MessageChannelMetrics;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

import com.hillert.gnss.demo.config.SpringIntegrationConfig;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GSVSentence;
import net.sf.marineapi.nmea.sentence.Sentence;

@SpringBootTest(classes = SpringIntegrationConfig.class)
class SpringIntegrationAggregatorTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringIntegrationAggregatorTests.class);

	@Autowired
	@Qualifier("rawNmeaInput")
	MessageChannel rawNmeaInput;

//	@Autowired
//	@Qualifier("destination")
//	QueueChannel destination;

	@Autowired
	@Qualifier("gsvChannel")
	QueueChannel gsvChannel;

	@Autowired
	IntegrationManagementConfigurer metrics;

	@Test
	void testRoutingSentences() throws IOException, InterruptedException {
		final String data = getTestData();
		data.lines().forEach(l -> {

			Message<String> m = MessageBuilder.withPayload(l)
					.build();
			rawNmeaInput.send(m);
			System.out.println(">>>>");
		});
		Thread.sleep(30000);
		Assertions.assertEquals(18, gsvChannel.getQueueSize());
	}

	//@Test
	void testAggregationOfGSVSentences() throws IOException {
		final String data = getTestData();
		data.lines().forEach(l -> {

			final SentenceFactory sf = SentenceFactory.getInstance();
			final Sentence sentence = (Sentence) sf.createParser(l);
			final GSVSentence gsvSentence = (GSVSentence) sentence;

			Message<String> m = MessageBuilder.withPayload(l)
					.setHeader("correlationKey", gsvSentence.getTalkerId() + "_" + gsvSentence.getSentenceCount())
					.setHeader("talkerId", gsvSentence.getTalkerId())
					.setHeader("sentenceIndex", gsvSentence.getSentenceIndex())
					.setHeader("numberOfSentences", gsvSentence.getSentenceCount())
					.build();
			rawNmeaInput.send(m);
		});
		Assertions.assertEquals(18, metrics.getChannelMetrics("input").getSendCount());
		//Assertions.assertEquals(8, destination.getQueueSize(), "Expected 7 messages in the destination channel");
	}

	private String getTestData() {
		final Resource resource = new ClassPathResource("gsv-test-data.txt");

		try (final InputStream inputStream = resource.getInputStream()){
			byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
			final String data = new String(bdata, StandardCharsets.UTF_8);
			return data;
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
