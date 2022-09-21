package com.kafka.deadletterqueue.kafka.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class DataTopicConsumer {

    private static Logger logger = LoggerFactory.getLogger(DataTopicConsumer.class);

    @KafkaListener(topics = "data.topic")
//    @RetryableTopic(
//            attempts = "4",
//            backoff = @Backoff(delay = 1000, maxDelay = 1200, multiplier = 2.0),
//            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
//    )
    //using auto create false to get control over topic creation as default topic have default number of partition and replication factors
    //data.topic-dlt
    //data.topic-retry-0
    //data.topic-retry-1
    //data.topic-retry-2
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, maxDelay = 1200, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            autoCreateTopics = "false"
    )
    public void consumeTopicData(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws InterruptedException {
        if(message.contains("Error")){
            logger.info(" Error from : {}, data : {} ", topic ,message);
            throw new InterruptedException("Logically interrupted !!!");
        }
        logger.info(" consuming from : {}, data : {} ", topic ,message);
    }

    // can be used to handle the dlt errors in the service itself
    @DltHandler
    public void dlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        logger.info(" Error from : {}, data : {} ", topic ,message);
    }

}
