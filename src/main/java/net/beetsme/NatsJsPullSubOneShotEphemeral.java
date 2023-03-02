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

package net.beetsme;

import io.nats.client.*;
import io.nats.client.api.ConsumerConfiguration;

import java.time.Duration;

/**
 * This example will demonstrate basic use of a pull subscription leveraging OneShot pattern
 * which combines NoWait with an expiry, telling the server to return immediately if messages to deliver
 * but hold the request until messages to deliver or expiry (whichever comes first).
 */
public class NatsJsPullSubOneShotEphemeral {
    static final String usageString =
        "\nUsage: java -cp <classpath> NatsJsPullSubOneShotEphemeral [-s server] [-strm stream] [-sub subject]"
            + "\n\nDefault Values:"
            + "\n   [-strm] expires-in-stream"
            + "\n   [-sub]  expires-in-subject"
            + "\n\nUse tls:// or opentls:// to require tls, via the Default SSLContext\n"
            + "\nSet the environment variable NATS_NKEY to use challenge response authentication by setting a file containing your private key.\n"
            + "\nSet the environment variable NATS_CREDS to use JWT/NKey authentication by setting a file containing your user creds.\n"
            + "\nUse the URL in the -s server parameter for user/pass/token authentication.\n";

    public static void main(String[] args) {
        ExampleArgs exArgs = ExampleArgs.builder("Pull Subscription using primitive Batch With OneShot - Ephemeral", args, usageString)
            .defaultStream("expires-in-stream")
            .defaultSubject("expires-in-subject")
            .build();

        try (Connection nc = Nats.connect(ExampleUtils.createExampleOptions(exArgs.server))) {

            // Create our JetStream context.
            JetStream js = nc.jetStream();

            // Build our consumer configuration and subscription options.
            // make sure the ack wait is sufficient to handle the reading and processing of the batch.
            ConsumerConfiguration cc = ConsumerConfiguration.builder()
                    .ackWait(Duration.ofMillis(2500))
                    .build();

            // Build our subscription options.
            PullSubscribeOptions pullOptions = PullSubscribeOptions.builder()
                    .configuration(cc)
                    .build();

            JetStreamSubscription sub = js.subscribe(exArgs.subject, pullOptions);
            nc.flush(Duration.ofSeconds(1));

            // Key "OneShot" pattern (nowait + expiry)
            int oneShotWait = 300; // seconds for batch request to live with no pending messages
            while (true) {
                sub.pullNoWait(10, Duration.ofSeconds(oneShotWait));
                Message m = sub.nextMessage(Duration.ofSeconds(oneShotWait+1)); // first message
                while (m != null) {
                    System.out.println(m);
                    m.ack();
                    m = sub.nextMessage(Duration.ofMillis(50)); // other messages should already be on the client
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
