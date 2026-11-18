package com.luwq;

/**
 * 雪花算法（Snowflake）分布式 ID 生成器。
 *
 * <p>生成的 long 型 ID 由以下部分组成（从左到右）：</p>
 * <pre>
 *  1 位符号位  |  41 位时间戳  |  5 位数据中心 ID  |  5 位机器 ID  |  12 位序列号
 * </pre>
 * <ul>
 *     <li>符号位：固定为 0，保证生成的 ID 为正数</li>
 *     <li>时间戳：当前时间与 {@link #EPOCH} 的差值，单位毫秒，可使用约 69 年</li>
 *     <li>数据中心 ID + 机器 ID：共 10 位，支持最多 1024 个节点</li>
 *     <li>序列号：同一毫秒内自增，最多 4096 个 ID</li>
 * </ul>
 *
 * <p>本类通过 synchronized 保证线程安全；当检测到时钟回拨时直接抛出异常，
 * 以避免产生重复 ID。</p>
 */
public class Snowflake {

    /** 起始时间戳：2024-01-01 00:00:00 UTC，建议在上线前固定，不要改动 */
    public static final long EPOCH = 1704067200000L;

    private static final long SEQUENCE_BITS = 12L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long workerId;
    private final long datacenterId;

    /** 当前毫秒内的序列号 */
    private long sequence = 0L;
    /** 上一次生成 ID 的时间戳 */
    private long lastTimestamp = -1L;

    public Snowflake() {
        this(0, 0);
    }

    public Snowflake(long workerId) {
        this(workerId, 0);
    }

    /**
     * @param workerId     机器 ID，取值范围 [0, 31]
     * @param datacenterId 数据中心 ID，取值范围 [0, 31]
     */
    public Snowflake(long workerId, long datacenterId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("workerId must be between 0 and " + MAX_WORKER_ID);
        }
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException("datacenterId must be between 0 and " + MAX_DATACENTER_ID);
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 生成下一个全局唯一 ID。
     *
     * @return long 型雪花 ID
     * @throws IllegalStateException 系统时钟发生回拨时抛出
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(String.format(
                    "Clock moved backwards, refusing to generate id. lastTimestamp: %d, current: %d",
                    lastTimestamp, timestamp));
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /** 生成字符串形式的 ID，方便直接作为数据库主键或业务编号使用 */
    public String nextIdStr() {
        return Long.toString(nextId());
    }

    /** 阻塞等待，直到系统时间超过上一次生成 ID 的时间戳 */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }
}
