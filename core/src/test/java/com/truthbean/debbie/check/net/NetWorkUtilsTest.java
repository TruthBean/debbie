package com.truthbean.debbie.check.net;

import com.truthbean.debbie.net.NetWorkUtils;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.List;

class NetWorkUtilsTest {

    private void printLocalAddress(InetAddress localAddress) {
        if (localAddress != null) {
            System.out.println(localAddress);
            System.out.println(localAddress.getCanonicalHostName());
            System.out.println(localAddress.getHostAddress());
            System.out.println(localAddress.getHostName());
        }
    }

    @Test
    void getLocalIpv4Address() {
        InetAddress localAddress = NetWorkUtils.getLocalIpv4Address();
        printLocalAddress(localAddress);
    }

    @Test
    void getLocalIpv6Address() {
        InetAddress localAddress = NetWorkUtils.getLocalIpv6Address();
        printLocalAddress(localAddress);
    }

    @Test
    void getLocalHost() {
        String localHost = NetWorkUtils.getLocalHost();
        System.out.println(localHost);
    }

    @Test
    void getLocalIpv4Address0() {
        InetAddress localAddress0 = NetWorkUtils.getLocalIpv4Address0();
        printLocalAddress(localAddress0);
    }

    @Test
    void getAllLocalAddress() {
        List<InetAddress> allLocalAddress = NetWorkUtils.getAllLocalAddress();
        for (InetAddress localAddress : allLocalAddress) {
            printLocalAddress(localAddress);
            System.out.println("--------------------------------------------------");
        }
    }

    @Test
    void getMacAddress() {
        InetAddress localAddress0 = NetWorkUtils.getLocalIpv4Address0();
        String macAddress = NetWorkUtils.getMacAddress(localAddress0);
        System.out.println(macAddress);
    }

    @Test
    void getSubnetMask() {
        InetAddress localAddress0 = NetWorkUtils.getLocalIpv4Address0();
        String subnetMask = NetWorkUtils.getSubnetMask(localAddress0);
        System.out.println(subnetMask);
    }

    @Test
    void calcSubnetAddress() {
    }
}