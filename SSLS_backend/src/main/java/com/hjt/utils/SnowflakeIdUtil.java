package com.hjt.utils;


public class SnowflakeIdUtil{
    private final long epoch = 1622764800000L; // 自定义起始时间戳，如需修改，请根据实际情况调整
    private final long workerIdBits = 5L;
    private final long dataCenterIdBits = 5L;
    private final long sequenceBits = 12L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
    private final long workerIdShift = sequenceBits;
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    private final long timestampShift = sequenceBits + workerIdBits + dataCenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long dataCenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdUtil(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Invalid worker ID");
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException("Invalid data center ID");
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }
//加上synchronized关键字的函数意味着在任意时刻只有一个线程可以执行该函数。这种同步机制有助于解决多线程环境下的并发访问问题和数据竞争条件。
    public synchronized long generateId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID.");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - epoch) << timestampShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long waitNextMillis(long currentTimestamp) {
        long millis = System.currentTimeMillis();
        while (millis <= currentTimestamp) {
            millis = System.currentTimeMillis();
        }
        return millis;
    }  public static void main(String[] args) {
        SnowflakeIdUtil idGenerator = new SnowflakeIdUtil(1, 1); // 传入workerId和dataCenterId
        long uniqueId = idGenerator.generateId();

        System.out.println(  String.valueOf(uniqueId).length());
        System.out.println("Generated Unique ID: " + uniqueId);
    }
}
