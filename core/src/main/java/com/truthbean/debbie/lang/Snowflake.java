package com.truthbean.debbie.lang;

/**
 * Twitter_Snowflake
 * SnowFlake的结构如下(每部分用-分开):
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 1位标识，sign long类型，最高位是符号位，正数是0，负数是1，id一般是正数，最高位是0
 * 41位时间戳差值(毫秒级)，41位时间戳差值是存储时间戳的差值（当前时间戳 - 开始时间戳)；41位的时间截，可以使用69年，年T = (1L &lt;&lt; 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * 10位的数据机器位，可以部署在1024个节点，包括5位dataCenterId和5位workerId
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号
 *
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右
 *
 * 雪花算法强依赖机器时钟，如果机器上时钟回拨，会导致发号重复或者服务会处于不可用状态。
 * 如果恰巧回退前生成过一些ID，而时间回退后，生成的ID就有可能重复。
 * 官方对于此并没有给出解决方案，而是简单的抛错处理，这样会造成在时间被追回之前的这段时间服务不可用
 *
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 */
public class Snowflake {

    private long workerId;
    private long dataCenterId;
    private long sequence = 0L;

    /**
     * 开始时间戳
     * Tue Feb 01 00:00:00 CST 2000
     */
    private final long startTimestamp = 949334400892L;

    /**
     * 节点ID长度
     */
    private final long workerIdBits = 5L;

    /**
     * 数据中心ID长度
     */
    private final long dataCenterIdBits = 5L;

    /**
     * 最大支持机器节点数0~31，一共32个
     */
    private final long maxWorkerId = ~(-1L << workerIdBits);

    /**
     * 最大支持数据中心节点数0~31，一共32个
     */
    private final long maxDataCenterId = ~(-1L << dataCenterIdBits);

    /**
     * 序列号12位
     */
    private final long sequenceBits = 12L;

    /**
     * 机器节点左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据中心节点左移17位
     */
    private final long dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间毫秒数左移22位
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /**
     * 最大为4095
     */
    private final long sequenceMask = ~(-1L << sequenceBits);

    private long lastTimestamp = -1L;

    private static class SnowflakeHolder {
        private static final Snowflake INSTANCE = new Snowflake();
    }

    public static Snowflake getInstance() {
        return SnowflakeHolder.INSTANCE;
    }

    public Snowflake() {
        this(0L, 0L);
    }

    public Snowflake(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenter id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    public synchronized long next() {
        // 获取当前毫秒数
        long timestamp = timeGen();
        // 如果服务器时间有问题(时钟后退) 报错。
        if (timestamp < lastTimestamp) {
            String message = String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp);
            throw new RuntimeException(message);
        }
        // 如果上次生成时间和当前时间相同,在同一毫秒内
        if (lastTimestamp == timestamp) {
            //sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
            sequence = (sequence + 1) & sequenceMask;
            // 判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
            if (sequence == 0) {
                //自旋等待到下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加
            sequence = 0L;
        }
        lastTimestamp = timestamp;

        // 最后按照规则拼出ID。
        // 000000000000000000000000000000000000000000  00000            00000       000000000000
        // time                                      datacenterId      workerId     sequence
        // return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
        //        | (workerId << workerIdShift) | sequence;
        return ((timestamp - startTimestamp) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
