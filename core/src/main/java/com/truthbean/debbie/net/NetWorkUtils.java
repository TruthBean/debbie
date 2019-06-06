package com.truthbean.debbie.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-31 17:45
 */
public class NetWorkUtils {
    public static final String LOCAL_ADDRESS = "localhost";
    public static final String LOCAL_IP = "127.0.0.1";

    public static final String ANY_IP = "0.0.0.0";

    private static final Pattern IPV4_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && !ANY_IP.equals(name)
                && !LOCAL_IP.equals(name)
                && IPV4_PATTERN.matcher(name).matches());
    }

    private static volatile InetAddress LOCAL_ADDRESS_CACHE = null;

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS_CACHE != null) {
            return LOCAL_ADDRESS_CACHE;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS_CACHE = localAddress;
        return localAddress;
    }

    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCAL_IP : address.getHostAddress();
    }

    private static InetAddress getLocalAddress0() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress address = addresses.nextElement();
                            if (isValidAddress(address)) {
                                return address;
                            }
                        } catch (Throwable e) {
                            LOGGER.warn("Failed to retriving ip address, " + e.getMessage(), e);
                        }
                    }
                } catch (Throwable e) {
                    LOGGER.warn("Failed to retriving ip address, " + e.getMessage(), e);
                }
            }
        } catch (Throwable e) {
            LOGGER.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        LOGGER.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NetWorkUtils.class);
}
