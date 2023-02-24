package it.polimi.ds;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public class Node implements Remote {

    Map<Integer, Collection<Integer>> historyDB = new HashMap<>();
    Map<Integer, Integer> lastWrite = new HashMap<>();
    Map<Integer,Integer> committedValues = new HashMap<>();

    public synchronized void handlePutRequest(int k, int v) throws RemoteException {
        Collection<Integer> tempValues = historyDB.get(k);
        if(tempValues == null)
            tempValues = new ArrayList<>();
        if(!tempValues.contains(v))
            tempValues.add(v);
        historyDB.put(k, tempValues);
        lastWrite.put(k, v);

    }

    public synchronized void handleAbortPutRequest(int k, int v) throws RemoteException{
        System.out.println("Aborted key "+ k + "value "+v);
        Collection<Integer> tempValues = historyDB.remove(k);
        if(tempValues == null || tempValues.isEmpty())
            return;
        tempValues.remove(v);
        if(!tempValues.isEmpty())
            historyDB.put(k, tempValues);
        if(v == lastWrite.get(k))
            lastWrite.remove(k);
    }

    public synchronized void commitPut(int k, int v) throws RemoteException {
        System.out.println("Committed key "+k+" value "+v);
        historyDB.remove(k);
        lastWrite.remove(k);
        committedValues.put(k, v);
    }

    public synchronized Integer handleGetRequest(int k) throws RemoteException {
        return committedValues.get(k);
    }

    public synchronized Map<Integer, Integer> getCommitedValues() throws RemoteException {
        return new HashMap<>(committedValues);
    }

    public synchronized void printValues() throws RemoteException {
        for(Integer key : committedValues.keySet()) {
            System.out.println("value (" + key + ", " + committedValues.get(key) + ")");
        }
    }




}
