package it.polimi.ds;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface NodeInterface extends Remote {
     void put(int key, int value) throws RemoteException;
     Integer get(int key) throws RemoteException;

     void handleAbortPut(int k, int v) throws RemoteException;
     void handleCommitPut(int k, int v) throws RemoteException;
     boolean vote(int k, int v) throws RemoteException;

     Map<Integer, Integer> getCommitedValues() throws RemoteException;
}
