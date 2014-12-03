package com.garethahealy.activemq.client.poc.services;

import com.garethahealy.activemq.client.poc.callbacks.DefaultCallbackHandler;
import com.garethahealy.activemq.client.poc.config.AmqBrokerConfiguration;
import com.garethahealy.activemq.client.poc.config.RetryConfiguration;
import com.garethahealy.activemq.client.poc.mocked.producers.CallbackableRetryableAmqProducer;
import com.garethahealy.activemq.client.poc.producers.Producer;
import com.garethahealy.activemq.client.poc.resolvers.ConnectionFactoryResolver;
import com.garethahealy.activemq.client.poc.resolvers.PooledAmqConnectionFactoryResolver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageServiceBrokerDownAfterQueueCreatedTest extends BaseBroker {

        private Producer getRetryableAmqProducerWithDownBroker() {
                RetryConfiguration retryConfiguration = new RetryConfiguration();
                AmqBrokerConfiguration amqBrokerConfiguration = new AmqBrokerConfiguration();
                ConnectionFactoryResolver connectionFactoryResolver = new PooledAmqConnectionFactoryResolver(amqBrokerConfiguration);


                DefaultCallbackHandler defaultCallbackHandler = new DefaultCallbackHandler() {
                        @Override
                        public void createQueue() {
                                try {
                                        stopBroker();
                                }catch (Exception ex) {
                                }
                        }
                };

                return new CallbackableRetryableAmqProducer(defaultCallbackHandler, retryConfiguration, amqBrokerConfiguration, connectionFactoryResolver);
        }

        @Before
        public void startBroker() throws Exception {
                super.startBroker();
        }

        @After
        public void stopBroker() throws Exception {
                super.stopBroker();
        }

        @Test
        public void handlesDownBroker() {
                Producer producer = getRetryableAmqProducerWithDownBroker();

                MessageService messageService = new MessageService(producer);
                boolean hasSent = messageService.sendMessagesToQueue("gareth", "healy");

                Assert.assertFalse("hasSent", hasSent);
        }
}