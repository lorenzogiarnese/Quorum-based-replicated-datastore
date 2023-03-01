package it.polimi.ds.node;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface NodeInterface extends Remote {
     boolean put(int key, int value) throws RemoteException;
     Integer get(int key) throws RemoteException;

     void handleAbortPut(int k, int v) throws RemoteException;
     void handleCommitPut(int k, int v) throws RemoteException;
     boolean vote(int k, int v) throws RemoteException;

}
