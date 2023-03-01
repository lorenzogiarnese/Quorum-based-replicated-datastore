package it.polimi.ds.node;

import it.polimi.ds.masternode.MasterNodeImplementation;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class NodeRunner {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            MasterNodeImplementation stub = (MasterNodeImplementation) registry.lookup("masternode");
            String hostname = args[0];
            stub.addNode(hostname);
            System.out.println("Node " + hostname + " added");
        } catch (Exception e) {
            System.err.println("Node exception:");
            e.printStackTrace();
        }
    }

}
