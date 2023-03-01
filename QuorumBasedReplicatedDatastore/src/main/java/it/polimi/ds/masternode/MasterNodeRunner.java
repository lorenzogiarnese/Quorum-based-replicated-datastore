package it.polimi.ds.masternode;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MasterNodeRunner {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {

        try{
            MasterNodeImplementation masterNode = new MasterNodeImplementation();
            Registry registry = LocateRegistry.createRegistry(1099);
            UnicastRemoteObject.exportObject(masterNode,0);
            registry.bind("masternode", masterNode);
            System.out.println("Masternode bound");
        }catch(Exception e) {
            System.err.println("Error during masternode registration"+ e.getMessage());
            e.printStackTrace();
        }

    }
}
