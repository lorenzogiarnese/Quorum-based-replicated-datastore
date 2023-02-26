package it.polimi.ds;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterNodeInterface extends Remote {
     boolean handlePutRequest(int key, int value) throws RemoteException, InterruptedException;
     Integer handleGetRequest(int key) throws RemoteException;


}
