package net.beetsme;

// Copyright 2020 The NATS Authors
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


import io.nats.client.*;
import io.nats.client.api.ConsumerConfiguration;

import java.time.Duration;
import java.util.List;

/**
 * This example will demonstrate basic use of a pull subscription of:
 * fetch pull: <code>fetch(int batchSize, Duration or millis maxWait)</code>,
 */
public class NatsJsPullSubFetchEphemeral {
    static final String usageString =
            "\nUsage: java -cp <classpath> NatsJsPullSubFetchEphemeral [-s server] [-strm stream] [-sub subject]"
                    + "\n\nDefault Values:"
                    + "\n   [-strm] fetch-stream"
                    + "\n   [-sub]  fetch-subject"
                    + "\n\nUse tls:// or opentls:// to require tls, via the Default SSLContext\n"
                    + "\nSet the environment variable NATS_NKEY to use challenge response authentication by setting a file containing your private key.\n"
                    + "\nSet the environment variable NATS_CREDS to use JWT/NKey authentication by setting a file containing your user creds.\n"
                    + "\nUse the URL in the -s server parameter for user/pass/token authentication.\n";

    public static void main(String[] args) {
        ExampleArgs exArgs = ExampleArgs.builder("Pull Subscription using macro Fetch - Ephemeral", args, usageString)
                .build();

        try (Connection nc = Nats.connect(ExampleUtils.createExampleOptions(exArgs.server))) {

            // Create our JetStream context.
            JetStream js = nc.jetStream();

            // Build our consumer configuration and subscription options.
            // make sure the ack wait is sufficient to handle the reading and processing of the batch.
            ConsumerConfiguration cc = ConsumerConfiguration.builder()
                    .ackWait(Duration.ofMillis(2500))
                    .build();

            PullSubscribeOptions pullOptions = PullSubscribeOptions.builder()
                    .configuration(cc)
                    .build();

            JetStreamSubscription sub = js.subscribe(exArgs.subject, pullOptions);
            nc.flush(Duration.ofSeconds(1));

            // application decides size of message batch and expiry of each batch request
            // application user decides whether to loop forever, or a number of batch requests
            int maxBatchRequests = 5;
            int batchRequest = 1;
            while (batchRequest <= maxBatchRequests) {
                List<Message> list = sub.fetch(10, Duration.ofSeconds(1));
                for (Message m : list) {
                    System.out.println("batch:" + batchRequest + ". " + m);
                    m.ack();
                }
                batchRequest++;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

