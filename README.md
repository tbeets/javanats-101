# javanats-101

The basics of the NATS Java SDK

## Build
```bash
mvn package
```

## Execute
```bash
# NatsPub
java -cp /home/todd/.m2/repository/io/nats/jnats/2.16.8/jnats-2.16.8.jar:target/javanats-101-1.0-SNAPSHOT.jar net.beetsme.NatsPub -s "nats://UserA1:s3cr3t@localhost:4222" foo "hello java"
```

## Resources

* [NATS Java SDK](https://github.com/nats-io/nats.java)
* [Java NATS Examples](https://github.com/nats-io/nats.java/blob/main/src/examples/java/io/nats/examples/README.md)