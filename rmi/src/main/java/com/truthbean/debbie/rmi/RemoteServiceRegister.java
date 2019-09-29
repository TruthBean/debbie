package com.truthbean.debbie.rmi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

public class RemoteServiceRegister {

    private final int rmiBindPort;
    private final String rmiBindAddress;

    private final Registry registry;

    public RemoteServiceRegister(int rmiBindPort) {
        this.rmiBindPort = rmiBindPort;
        this.rmiBindAddress = "localhost";
        registry = register(false, "192.168.1.198", 8088);
    }

    public RemoteServiceRegister(String rmiBindAddress, int rmiBindPort) {
        this.rmiBindPort = rmiBindPort;
        this.rmiBindAddress = rmiBindAddress;
        registry = register(false, "192.168.1.198", 8088);
    }

    public Registry register(boolean rmiRegistrySSL, String rmiBindAddress, int registryPort) {

        // Prevent an attacker guessing the RMI object ID
        System.setProperty("java.rmi.server.randomIDs", "true");

        RMIClientSocketFactory registryCsf = null;
        RMIServerSocketFactory registrySsf = null;

        RMIClientSocketFactory serverCsf = null;
        RMIServerSocketFactory serverSsf = null;

        // Configure registry socket factories
        if (rmiRegistrySSL) {
            /*registryCsf = new SslRMIClientSocketFactory();
            if (rmiBindAddress == null) {
                registrySsf = new SslRMIServerSocketFactory(sslContext,
                    getEnabledCiphers(), getEnabledProtocols(),
                    getCertificateVerification() == CertificateVerification.REQUIRED);
            } else {
                registrySsf = new SslRmiServerBindSocketFactory(sslContext,
                    getEnabledCiphers(), getEnabledProtocols(),
                    getCertificateVerification() == CertificateVerification.REQUIRED,
                    rmiBindAddress);
            }*/
        } else {
            if (rmiBindAddress != null) {
                registrySsf = new RmiServerBindSocketFactory(rmiBindAddress);
            }
        }

        // Create the RMI registry
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(registryPort, registryCsf, registrySsf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (registry == null) {
            try {
                registry = LocateRegistry.createRegistry(registryPort);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }

        return registry;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void bind(String serviceName, Remote service) {
        bind(registry, serviceName, service);
    }

    public void bind(Registry registry, String serviceName, Remote service) {
        System.out.println("registry ...");

        boolean serviceBind = false;
        try {
            String[] list = registry.list();
            if (list != null && list.length > 0) {
                for (String s : list) {
                    if (serviceName.equals(s)) {
                        serviceBind = true;
                        break;
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (serviceBind) {
            try {
                Remote lookup = registry.lookup(serviceName);
                System.out.println(lookup);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        } else {
            //将服务绑定命名
            try {
                registry.bind(serviceName, service);

                String[] list = registry.list();
                for (String s : list) {
                    System.out.println("bind server " + s);
                }

            } catch (RemoteException | AlreadyBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static class RmiServerBindSocketFactory implements RMIServerSocketFactory {

        private final InetAddress bindAddress;

        public RmiServerBindSocketFactory(String address) {
            InetAddress bindAddress = null;
            try {
                bindAddress = InetAddress.getByName(address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.bindAddress = bindAddress;
        }

        @Override
        public ServerSocket createServerSocket(int port) throws IOException {
            return new ServerSocket(port, 0, bindAddress);
        }
    }
}
