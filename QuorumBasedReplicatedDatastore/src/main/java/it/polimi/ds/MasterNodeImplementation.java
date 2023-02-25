package it.polimi.ds;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MasterNodeImplementation extends UnicastRemoteObject implements MasterNodeInterface{
    private List<NodeInterface> nodes;
    public MasterNodeImplementation() throws RemoteException {
        super();
        nodes = new ArrayList<>();
    }
    public boolean handlePutRequest(int key, int value) throws RemoteException {
        //TODO
        return true;

    }

    public Integer handleGetRequest(int key) throws RemoteException {
        //TODO
        return null;
    }

    public boolean proposeCommit(int k, int v) throws RemoteException {
        int votesFor = 0;
        int votesAgainst = 0;

        //call the vote method on each node and count votes
        for(NodeInterface node : nodes) {
            try{
                boolean vote = node.vote(k,v);
                if(vote) {
                    votesFor++;
                }
                else{
                    votesAgainst++;
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        //determine whether the proposed commit is accepted or not
        if(votesFor > votesAgainst) {
            return true;
        }else {
            return false;
        }
    }

    public void registerNode(NodeInterface node) throws RemoteException {
        nodes.add(node);
    }

    public void nodeDisconnection(NodeInterface nodeInterface) throws RemoteException {
        System.err.println("Received disconnection from a node");
        nodes.remove(nodeInterface);
        System.err.println("Connected nodes are now: " + nodes.size());
    }
}
