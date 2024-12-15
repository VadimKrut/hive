package com.store.crypto.model;

public record DecodedIdData(long timestamp, long datacenterId, long workerId, long sequence) {
    public DecodedIdData decodeId(long id) {
        long sequenceBits = 12L;
        long workerIdBits = 5L;
        long datacenterIdBits = 5L;
        long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        long datacenterIdShift = sequenceBits + workerIdBits;
        long epoch = 1609459200000L;
        long timestamp = (id >> timestampLeftShift) + epoch;
        long datacenterId = (id >> datacenterIdShift) & ~(-1L << datacenterIdBits);
        long workerId = (id >> sequenceBits) & ~(-1L << workerIdBits);
        long sequence = id & ~(-1L << sequenceBits);
        return new DecodedIdData(timestamp, datacenterId, workerId, sequence);
    }
}