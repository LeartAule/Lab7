package Utils.OtherUtils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class PortForwarder {

    public static String forwardAnyPort() {  /// Add forwardPort(port)
        String host = "se.ifmo.ru";
        int port = 2222;

        String user = "s284702";
        String pass = "tfo348";

        String listenerHost = "localhost";
        int listenerPort = 50000;

        String listeningHost = "pg";
        int listeningPort = 5432;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(pass);
            session.setConfig("StrictHostKeyChecking", "no");

//			System.out.println("Establishing Connection...");
            session.connect();

            session.setPortForwardingL(listenerPort, listeningHost, listeningPort);
//			System.out.println(listenerHost + ":" + listenerPort + " -> " + listeningHost + ":" + listeningPort);
        } catch (Exception  e) {
            //e.printStackTrace();
        }

        return "jdbc:postgresql://" + listenerHost + ":" + listenerPort + "/studs";
    }


}