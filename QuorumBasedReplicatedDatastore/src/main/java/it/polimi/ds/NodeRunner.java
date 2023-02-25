package it.polimi.ds;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class NodeRunner {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        try{
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            MasterNodeImplementation masterNode = (MasterNodeImplementation) registry.lookup("masternode");
            NodeImplementation node = new NodeImplementation(masterNode);
            NodeInterface stub = (NodeInterface) UnicastRemoteObject.exportObject(node,0);
            masterNode.registerNode(stub);
            System.out.println("Node ready");
        }catch(Exception e){
            System.err.println("Error during node registration"+ e.getMessage());
            e.printStackTrace();
        }

    }
}
