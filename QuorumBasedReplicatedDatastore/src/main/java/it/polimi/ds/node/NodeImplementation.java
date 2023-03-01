package it.polimi.ds.node;

import it.polimi.ds.masternode.MasterNodeImplementation;

import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class NodeImplementation extends UnicastRemoteObject implements NodeInterface {
    private final MasterNodeImplementation masterNode;
    private String ipAddress;

    public NodeImplementation(String masterNodeIpAddress) throws RemoteException, NotBoundException {
        super();
        this.ipAddress = getIpAddress();
        Registry registry = LocateRegistry.getRegistry(masterNodeIpAddress);
        masterNode = (MasterNodeImplementation) registry.lookup("masternode");
        masterNode.addNode(ipAddress);
    }

    Map<Integer, Collection<Integer>> historyDB = new HashMap<>();
    Map<Integer, Integer> lastWrite = new HashMap<>();
    Map<Integer,Integer> committedValues = new HashMap<>();


    public synchronized boolean put(int k, int v) throws RemoteException {
        Collection<Integer> tempValues = historyDB.get(k);
        if(tempValues == null)
            tempValues = new ArrayList<>();
        if(!tempValues.contains(v))
            tempValues.add(v);
        historyDB.put(k, tempValues);
        lastWrite.put(k, v);
        return true;

    }

    @Override
    public synchronized void handleAbortPut(int k, int v) throws RemoteException{
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

    @Override
    public synchronized void handleCommitPut(int k, int v) throws RemoteException {
        System.out.println("Committed key "+ k +" value "+v);
        historyDB.remove(k);
        lastWrite.remove(k);
        committedValues.put(k, v);
    }

    public boolean vote(int k, int v) throws RemoteException {
        return historyDB.containsKey(k) && historyDB.get(k).contains(v)
                && lastWrite.containsKey(k) && lastWrite.get(k) == v;
    }

    public synchronized Integer get(int k) throws RemoteException {
        return committedValues.get(k);
    }


    public synchronized Map<Integer, Integer> getCommitedValues() throws RemoteException {
        return new HashMap<>(committedValues);
    }

    private String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.err.println("Error getting IP address:");
            e.printStackTrace();
            return null;
        }
    }

    public synchronized void printValues() throws RemoteException {
        for(Integer key : committedValues.keySet()) {
            System.out.println("value: (" + key + ", " + committedValues.get(key) + ")");
        }
    }



}
