/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.net;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-31 17:45
 */
public class NetWorkUtils {
    public static final String LOCAL_ADDRESS = "localhost";
    public static final String LOCAL_IPV4 = "127.0.0.1";
    public static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";

    public static final String ANY_IPV4 = "0.0.0.0";
    public static final String ANY_IPV6 = "0:0:0:0:0:0:0:0";

    public static final Pattern IPV4_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    public static final Pattern IPV6_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    protected NetWorkUtils() {
    }

    public static boolean isValidIpv4Address(InetAddress address) {
        if (address == null || address.isLoopbackAddress() || address instanceof Inet6Address) {
            return false;
        }

        LOGGER.trace(() -> address.getClass().getName());
        LOGGER.trace(address::toString);

        String name = address.getHostAddress();
        return (name != null
            && !ANY_IPV4.equals(name)
            && !LOCAL_IPV4.equals(name)
                // 防止继承 InetAddress 重写 方法
            && IPV4_PATTERN.matcher(name).matches());
    }

    public static boolean isValidIpv6Address(InetAddress address) {
        if (address == null || address.isLoopbackAddress() || address instanceof Inet4Address) {
            return false;
        }

        LOGGER.trace(() -> address.getClass().getName());
        LOGGER.trace(address::toString);

        String name = address.getHostAddress();
        return (name != null
            && !ANY_IPV6.equals(name)
            && !LOCAL_IPV6.equals(name)
            && IPV6_PATTERN.matcher(name).matches());
    }

    private static volatile Inet4Address LOCAL_IPV4_ADDRESS_CACHE = null;
    private static volatile Inet6Address LOCAL_IPV6_ADDRESS_CACHE = null;

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    public static Inet4Address getLocalIpv4Address() {
        if (LOCAL_IPV4_ADDRESS_CACHE != null) {
            return LOCAL_IPV4_ADDRESS_CACHE;
        }
        Inet4Address localAddress = getLocalIpv4Address0();
        if (localAddress == null) {
            try {
                LOGGER.error("Could not get local host ip address, will use 127.0.0.1 instead.");
                return (Inet4Address) InetAddress.getByName(LOCAL_IPV4);
            } catch (UnknownHostException e) {
                LOGGER.error("Could not get 127.0.0.1 address, may network driver error.", e);
            }
        } else {
            LOCAL_IPV4_ADDRESS_CACHE = localAddress;
        }
        return localAddress;
    }

    public static Inet6Address getLocalIpv6Address() {
        if (LOCAL_IPV6_ADDRESS_CACHE != null) {
            return LOCAL_IPV6_ADDRESS_CACHE;
        }
        Inet6Address localAddress = getLocalIpv6Address0();
        if (localAddress == null) {
            try {
                LOGGER.error("Could not get local host ip address, will use 0:0:0:0:0:0:0:1 instead.");
                return (Inet6Address) InetAddress.getByName(LOCAL_IPV6);
            } catch (UnknownHostException e) {
                LOGGER.error("Could not get 0:0:0:0:0:0:0:1 address, may network driver error.");
            }
        } else {
            LOCAL_IPV6_ADDRESS_CACHE = localAddress;
        }
        return localAddress;
    }

    public static String getLocalHost() {
        InetAddress address = getLocalIpv4Address();
        return address == null ? LOCAL_IPV4 : address.getHostAddress();
    }

    public static Inet4Address getLocalIpv4Address0() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress address = addresses.nextElement();
                            if (isValidIpv4Address(address) && address instanceof Inet4Address) {
                                return (Inet4Address) address;
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
        return null;
    }

    public static Inet6Address getLocalIpv6Address0() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress address = addresses.nextElement();
                            if (isValidIpv6Address(address) && address instanceof Inet6Address) {
                                return (Inet6Address) address;
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

    public static List<InetAddress> getAllLocalAddress() {
        List<InetAddress> result = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress address = addresses.nextElement();
                            result.add(address);
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
        return result;
    }

    public static List<InetAddress> getAllIpv4LocalAddress() {
        LOGGER.trace("before getAllIpv4LocalAddress");
        List<InetAddress> result = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress address = addresses.nextElement();
                            if (isValidIpv4Address(address)) {
                                result.add(address);
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
        LOGGER.trace("after getAllIpv4LocalAddress");
        return result;
    }

    public static String getMacAddress(InetAddress ipAddress) {
        LOGGER.trace("before getMacAddress");
        String result = null;
        // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
        byte[] mac = new byte[0];
        try {
            mac = NetworkInterface.getByInetAddress(ipAddress).getHardwareAddress();
        } catch (SocketException e) {
            LOGGER.error("getByInetAddress error", e);
        }
        if (mac != null) {
            // 下面代码是把mac地址拼装成String
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append(":");
                }
                // mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            // 把字符串所有小写字母改为大写成为正规的mac地址并返回
            result = sb.toString().toUpperCase();
        }
        LOGGER.trace("after getMacAddress");
        return result;
    }

    public static Map<String, String> getIpv4AndMac() {
        Map<String, String> map = new HashMap<>();
        List<InetAddress> allIpv4LocalAddress = getAllIpv4LocalAddress();
        for (InetAddress ipv4LocalAddress : allIpv4LocalAddress) {
            String macAddress = getMacAddress(ipv4LocalAddress);
            map.put(ipv4LocalAddress.getHostAddress(), macAddress);
        }
        return map;
    }

    public static Map<String, String> getRealIpAndMac() {
        Map<String, String> map = new HashMap<>();

        Map<String, String> ipAndMac = new HashMap<>();
        List<InetAddress> allIpv4LocalAddress = getAllIpv4LocalAddress();
        for (InetAddress ipv4LocalAddress : allIpv4LocalAddress) {
            String macAddress = getMacAddress(ipv4LocalAddress);
            String ip = ipv4LocalAddress.getHostAddress();
            if (ip.startsWith("169.254")) {
                LOGGER.debug("内部私有地址: " + ip);
                continue;
            }
            ipAndMac.put(ipv4LocalAddress.getHostAddress(), macAddress);
        }

        ipAndMac.forEach((ip, mac) -> {
             if (mac.startsWith("00:15:5D")) {
                LOGGER.debug("Hyper-V虚拟Mac地址: " + mac);
            } else if (mac.startsWith("00:50:56") || mac.startsWith("00:0c:29")) {
                 LOGGER.debug("vmware虚拟Mac地址: " + mac);
            } else if (mac.startsWith("08:00:27")) {
                 LOGGER.debug("virtualbox虚拟Mac地址: " + mac);
            } else {
                map.put(ip, mac);
            }
        });

        return map;
    }

    public static InetAddress getOneRealIp() {
        Map<InetAddress, String> map = new HashMap<>();

        Map<InetAddress, String> ipAndMac = new HashMap<>();
        List<InetAddress> allIpv4LocalAddress = getAllIpv4LocalAddress();
        for (InetAddress ipv4LocalAddress : allIpv4LocalAddress) {
            String macAddress = getMacAddress(ipv4LocalAddress);
            String ip = ipv4LocalAddress.getHostAddress();
            if (ip.startsWith("169.254")) {
                LOGGER.debug("内部私有地址: " + ip);
                continue;
            }
            ipAndMac.put(ipv4LocalAddress, macAddress);
        }

        ipAndMac.forEach((ip, mac) -> {
            if (mac.startsWith("00:15:5D")) {
                LOGGER.debug("Hyper-V虚拟Mac地址: " + mac);
            } else if (mac.startsWith("00:50:56") || mac.startsWith("00:0c:29")) {
                LOGGER.debug("vmware虚拟Mac地址: " + mac);
            } else if (mac.startsWith("08:00:27")) {
                LOGGER.debug("virtualbox虚拟Mac地址: " + mac);
            } else {
                map.put(ip, mac);
            }
        });

        for (Map.Entry<InetAddress, String> entry : map.entrySet()) {
            InetAddress ip = entry.getKey();
            String mac = entry.getValue();
            return ip;
        }
        return getLocalIpv4Address();
    }

    public static String getSubnetMask(InetAddress ipAddress) {
        try {
            // 获取此网络接口的全部或部分 InterfaceAddresses
            List<InterfaceAddress> list = NetworkInterface.getByInetAddress(ipAddress).getInterfaceAddresses();
            // 所组成的列表
            if (!list.isEmpty()) {
                int mask;
                if (list.size() == 1) {
                    mask = list.get(0).getNetworkPrefixLength();
                } else {
                    // 0 ipv6
                    // 1 ipv4
                    mask = list.get(1).getNetworkPrefixLength();
                }
                final int finalMask = mask;
                LOGGER.debug(() -> "network prefix length: " + finalMask);
                // 子网掩码的二进制1的个数
                StringBuilder maskStr = new StringBuilder();
                int[] maskIp = new int[4];
                for (int i = 0; i < maskIp.length; i++) {
                    maskIp[i] = (mask >= 8) ? 255 : (mask > 0 ? calcLastBitOfSubnetMask(mask) : 0);
                    mask -= 8;
                    maskStr.append(maskIp[i]);
                    if (i < maskIp.length - 1) {
                        maskStr.append(".");
                    }
                }
                LOGGER.debug(() -> "SubnetMask: " + maskStr);
                return maskStr.toString();
            }
        } catch (Exception e) {
            LOGGER.error("get subnet mask error", e);
        }
        return "";
    }

    private static int calcLastBitOfSubnetMask(int length) {
        int r = 0;
        for (int i = 1; i <= length; i++) {
            r += 2 << (7 - i);
        }
        return r;
    }

    public static String calcSubnetAddress(String ip, String mask) {
        StringBuilder result = new StringBuilder();
        try {
            // calc sub-net IP
            InetAddress ipAddress = InetAddress.getByName(ip);
            InetAddress maskAddress = InetAddress.getByName(mask);

            byte[] ipRaw = ipAddress.getAddress();
            byte[] maskRaw = maskAddress.getAddress();

            int unsignedByteFilter = 0x000000ff;
            int[] resultRaw = new int[ipRaw.length];
            for (int i = 0; i < resultRaw.length; i++) {
                resultRaw[i] = (ipRaw[i] & maskRaw[i] & unsignedByteFilter);
            }

            // make result string
            result.append(resultRaw[0]);
            for (int i = 1; i < resultRaw.length; i++) {
                result.append(".").append(resultRaw[i]);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public static InetAddress strToAddr(String ipv4) {
        var bytes = ipv4ToBytes(ipv4);
        try {
            return Inet4Address.getByAddress(bytes);
        } catch (UnknownHostException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static byte[] ipv4ToBytes(String ipv4) {
        byte[] bytes = new byte[4];
        var parts = ipv4.split("\\.");
        int i = 0;
        for (String part : parts) {
            bytes[i++] = (byte) Integer.parseInt(part);
        }
        return bytes;
    }

    /**
     * 把字符串IP转换成long
     *
     * @param ipStr 字符串IP
     * @return IP对应的long值
     */
    public static long ipv4ToLong(String ipStr) {
        String[] ip = ipStr.split("\\.");
        return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16)
                + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
    }

    /**
     * 把IP的long值转换成字符串
     *
     * @param ipLong IP的long值
     * @return long值对应的字符串
     */
    public static String longToIpv4(long ipLong) {
        return (ipLong >>> 24) + "." +
                ((ipLong >>> 16) & 0xFF) + "." +
                ((ipLong >>> 8) & 0xFF) + "." +
                (ipLong & 0xFF);
    }

    private static boolean isPrivateIp(String ipv4) {
        String prefix = "169.254.";
        return ipv4.startsWith(prefix);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NetWorkUtils.class);
}
