package uk.ac.open.kmi.ptAnywhere;

import com.cisco.pt.ipc.sim.Network;
import com.cisco.pt.ipc.sim.CiscoDevice;
import com.cisco.pt.ipc.ui.IPC;
import com.cisco.pt.ipc.IPCFactory;
import com.cisco.pt.ptmp.ConnectionNegotiationProperties;
import com.cisco.pt.ptmp.PacketTracerSession;
import com.cisco.pt.ptmp.PacketTracerSessionFactory;
import com.cisco.pt.ptmp.impl.PacketTracerSessionFactoryImpl;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class allows you to check when a Packet Tracer session is up and
 * able to answer IPC requests.
 *
 * Also, it can be used to measure the average response time of the instance
 * for a predefine request.
 *
 * Note that this class depends on the ptipc library.
 * I cannot provide a version of it as its intellectual property belongs to Cisco.
 */
public class PTChecker extends PacketTracerClient {

    final static int defaultWaitTime = 5000;
    final static int retryMiliseconds = 500;

    public PTChecker(String host, int port) {
        super(host, port);
    }

    protected long waitUntilPTResponds(int maxWaitingMillis, String file, String deviceName, boolean debug) throws Exception {
        int remainingMs = maxWaitingMillis;
        final long init = System.currentTimeMillis();
        boolean alreadyOpened = file==null;
        long ret = -1;
        while (remainingMs>0 && ret==-1) {
            final long initLoop = System.currentTimeMillis();
            try {
                final IPC ipc = getIPC();
                if (!alreadyOpened) {
                    ipc.appWindow().setVisible(true);
                    // I open a new file before opening another to make sure that
                    // I'm not testing other file opened before (e.g., if the
                    // file opening fails, the already opened file might answer).
                    ipc.appWindow().fileNew(false);
                    if (file.startsWith("http://")) {
                        ipc.appWindow().fileOpenFromURL(file);
                    } else {
                        ipc.appWindow().fileOpen(file);
                    }
                    ipc.appWindow().setVisible(false);
                    ipc.appWindow().showMinimized();
                    alreadyOpened = true;
                }
                if (alreadyOpened) {
                    // Measure time only after the appropriate file has been opened.
                    final Network network = this.ipcFactory.network(ipc);
                    // Checking ends only when the device is found (if it was specified).
                    if (deviceName!=null) {
                        final CiscoDevice dev = (CiscoDevice) network.getDevice(deviceName);
                        if (dev!=null) {
                          ret = System.currentTimeMillis() - init;  // elapsed
                        }
                    } else {
                        ret = System.currentTimeMillis() - init;  // elapsed
                    }
                }
            } catch(Error|Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
            } finally {
                if (ret==-1) {
                  final long elapsedLoop = System.currentTimeMillis() - initLoop;
                  // We substract the time already elapsed in this loop and
                  // the time that it will need to wait for a new retry.
                  remainingMs -= (elapsedLoop + PTChecker.retryMiliseconds);
                  if (remainingMs>0) {
                    // We wait only if we can make another attempt
                    // (i.e. wait for the retry time) within the remaining time.
                    Thread.sleep(PTChecker.retryMiliseconds);
                  }
                }
            }
        }
        return ret;  // In miliseconds
    }

    protected long getAverageResponseTime(int repetitions) {
        // TODO
        return 0;  // In miliseconds
    }

    public static void main(String[] args) throws Exception {
        if (args.length<2) {
            System.out.println("usage: java PTChecker hostname port [timeout] [file] [deviceToFind] [--debug]\n");
            System.out.println("Checks the time needed to contact a PacketTracer instance.\n");
            System.out.println("\thostname\tstring with the name of the Packet Tracer instance host.");
            System.out.println("\tport    \tan integer for the port number of the Packet Tracer instance.");
            System.out.println("\ttimeout    \t(optional, default: " + PTChecker.defaultWaitTime +
                                            ") number of milliseconds that the program will retry connections.");
            System.out.println("\tfile    \t(optional) file to be opened");
            System.out.println("\tdeviceToFind \t(optional) a device which should be found in the PT instance.");
            System.out.println("\t--debug    \t(optional) Show debug logs. This argument should always be the last one.");
        } else {
            Logger logger = Logger.getLogger("com.cisco.pt");

            // Now set its level. Normally you do not need to set the
            // level of a logger programmatically. This is usually done
            // in configuration files.
            logger.setLevel(Level.OFF);

            int waitTime = PTChecker.defaultWaitTime;
            String file = null;
            String deviceName = null;
            boolean debug = false;
            if (args.length>=3) {
                // Check if the last argument is the debug flag.
                if (args[args.length-1].equals("--debug")) {
                    debug = true;
                }
                if (!debug || args.length>3) {
                    waitTime = Integer.parseInt(args[2]);
                }
                if (args.length>=4) {
                    if (!debug || args.length>4) {
                        file = args[3];
                    }
                    if (args.length>=5) {
                        if (!debug || args.length>5) {
                            deviceName = args[4];
                        }
                    }
                }
            }
            final PTChecker checker = new PTChecker(args[0], Integer.parseInt(args[1]));
            System.out.println( checker.waitUntilPTResponds(waitTime, file, deviceName, debug) );

            checker.stop();
            //checker.getAverageResponseTime(100);
        }
    }
}

abstract class PacketTracerClient {
    protected PacketTracerSession packetTracerSession;
  	protected IPCFactory ipcFactory;
    final protected String hostName; // "localhost";
  	final protected int port;

    public PacketTracerClient(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public void start() throws Exception {
        final PacketTracerSessionFactory sessionFactory = PacketTracerSessionFactoryImpl.getInstance();
        this.packetTracerSession = createSession(sessionFactory);
        this.ipcFactory = new IPCFactory(this.packetTracerSession);
    }

    public IPC getIPC() throws Exception {
        if (this.ipcFactory==null) start();
        return this.ipcFactory.getIPC();
    }

    public void stop() throws Exception {
        if (this.packetTracerSession!=null)
            this.packetTracerSession.close();
    }

  	protected PacketTracerSession createSession(PacketTracerSessionFactory sessionFactory) throws Exception {
       return createDefaultSession(sessionFactory);
  	}

  	protected PacketTracerSession createDefaultSession(PacketTracerSessionFactory sessionFactory) throws Exception {
        return sessionFactory.openSession(this.hostName, this.port);
  	}

  	protected PacketTracerSession createSession(PacketTracerSessionFactory sessionFactory, ConnectionNegotiationProperties negotiationProperties) throws Exception {
        return sessionFactory.openSession(this.hostName, this.port, negotiationProperties);
  	}
}
