package com.example

import com.example.SimpleProducerKT.logger
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Properties

object SimpleProducerKT {
    // bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic alter.test --from-beginning
    val logger: Logger = LoggerFactory.getLogger(SimpleProducerKT::class.java)
    const val TOPIC_NAME = "alter.test"
    const val BOOTSTRAP_SERVERS = "my-kafka:9092"
}
fun main() {
    val configs = Properties()
    configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = SimpleProducerKT.BOOTSTRAP_SERVERS
    configs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
    configs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
    val producer = KafkaProducer<String, String>(configs)
    val messageValue = "testMessage"
    val record = ProducerRecord<String, String>(SimpleProducerKT.TOPIC_NAME, messageValue)
    producer.send(record)
    logger.info("{}", record)
    producer.flush()
    producer.close()
}
