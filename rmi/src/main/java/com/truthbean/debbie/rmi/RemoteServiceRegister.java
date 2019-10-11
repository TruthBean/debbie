package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.proxy.ProxyInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
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

    private final BeanFactoryHandler handler;

    public RemoteServiceRegister(BeanFactoryHandler handler, int rmiBindPort) {
        this.handler = handler;
        this.rmiBindPort = rmiBindPort;
        this.rmiBindAddress = "localhost";
        registry = register(false, rmiBindAddress, rmiBindPort);
    }

    public RemoteServiceRegister(BeanFactoryHandler handler, String rmiBindAddress, int rmiBindPort) {
        this.handler = handler;
        this.rmiBindPort = rmiBindPort;
        this.rmiBindAddress = rmiBindAddress;
        registry = register(false, rmiBindAddress, rmiBindPort);
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
            LOGGER.info("Create the RMI registry with port: " + registryPort);
        } catch (RemoteException e) {
            LOGGER.error("", e);
        }

        if (registry == null) {
            try {
                registry = LocateRegistry.createRegistry(registryPort);
                LOGGER.info("Create the RMI registry with port: " + registryPort);
            } catch (RemoteException e) {
                LOGGER.error("", e);
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

    public <S, SI extends S> void bind(Class<S> serviceClass) {
        try {
            DebbieBeanInfo<S> beanInfo = handler.getBeanInfo(serviceClass);
            S bean = beanInfo.getBean();
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(bean);

            if (invocationHandler instanceof ProxyInvocationHandler) {
                ProxyInvocationHandler<SI> proxyInvocationHandler = (ProxyInvocationHandler) invocationHandler;
                SI realService = proxyInvocationHandler.getRealTarget();

                RemoteServiceProxy<S> remoteServiceProxy = new RemoteServiceProxy<>();
                remoteServiceProxy.setService(realService);

                bind(beanInfo.getServiceName(), remoteServiceProxy);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void bind(Registry registry, String serviceName, Remote service) {
        LOGGER.trace("register (" + registry + ") bind " + serviceName + " to " + service);

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
            LOGGER.error("", e);
        }

        if (serviceBind) {
            try {
                Remote lookup = registry.lookup(serviceName);
                LOGGER.debug(lookup.toString());
            } catch (RemoteException | NotBoundException e) {
                LOGGER.error("", e);
            }
        } else {
            // 将服务绑定命名
            try {
                registry.bind(serviceName, service);

                String[] list = registry.list();
                for (String s : list) {
                    LOGGER.debug("bind server " + s);
                }

            } catch (RemoteException | AlreadyBoundException e) {
                LOGGER.error("", e);
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
                LOGGER.error("", e);
            }
            this.bindAddress = bindAddress;
        }

        @Override
        public ServerSocket createServerSocket(int port) throws IOException {
            return new ServerSocket(port, 0, bindAddress);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteServiceRegister.class);
}
