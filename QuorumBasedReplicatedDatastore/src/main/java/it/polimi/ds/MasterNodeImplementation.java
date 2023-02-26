package it.polimi.ds;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static java.lang.System.exit;

public class MasterNodeImplementation extends UnicastRemoteObject implements MasterNodeInterface{
    private List<NodeInterface> nodes;
    private Integer writeQuorum;
    private Integer readQuorum;
    public MasterNodeImplementation() throws RemoteException {
        super();
        this.nodes = new ArrayList<>();
    }

    public MasterNodeImplementation(Integer writeQuorum, Integer readQuorum) throws RemoteException {
        this();
        this.writeQuorum = writeQuorum;
        this.readQuorum = readQuorum;
    }
    public boolean handlePutRequest(int key, int value) throws RemoteException {
        System.out.println("Handling client request put("+key+","+value+")");

        //distribute put request to nodes
        for(NodeInterface nodeInterface : nodes) {
            nodeInterface.put(key, value);
        }

        if (countNodes(key,value) < writeQuorum){
            System.out.println("Majority not reached!");
            System.out.println("Aborting request put("+key+","+value+")");
            for (NodeInterface nodeInterface : nodes)
                nodeInterface.handleAbortPut(key,value);
           return false;
        }
        else {
            System.out.println("Majority reached!");
            System.out.println("Committing request put(" + key + "," + value + ")");
            for (NodeInterface nodeInterface : nodes)
                nodeInterface.handleCommitPut(key, value);
            return true;
        }

    }

    /*
    This method counts the number of nodes that have written the key,value tuple as last operation
     */
    public Integer countNodes(int key, int value) throws RemoteException {
        int i=0;
        for(NodeInterface nodeInterface : nodes) {
            if(nodeInterface.vote(key,value)){
                i++;
            }
        }
        return i;
    }

    public Integer handleGetRequest(int key) throws RemoteException {
        System.out.println("Handling client request get("+key+")");
        List<Integer> values = new ArrayList<>();
        for(NodeInterface nodeInterface: nodes) {
            try{
                //TODO controlla
                Integer value = nodeInterface.get(key);
                if(value != null){
                    values.add(value);
                }
            }catch(RemoteException e){
                //replica is down, ignore
            }
        }

        if(values.size()< readQuorum) {
            throw new RemoteException("Quorum not met!");
        }

        Integer value = values.get(0);
        for(int i=1; i<values.size(); i++) {
            if(!(value == values.get(i))){
                //inconsistent values, initiate read-repair protocol
                for(NodeInterface nodeInterface: nodes) {
                    try{
                        nodeInterface.put(key,value);
                    }catch(RemoteException e){
                        //replica is down, ignore
                    }
                }
                throw new RemoteException("Inconsistent values!");
            }
        }

        return value;

    }

    public void registerNode(NodeInterface node) throws RemoteException {
        nodes.add(node);
    }

    public void nodeDisconnection(NodeInterface nodeInterface) throws RemoteException {
        System.err.println("Received disconnection from a node");
        nodes.remove(nodeInterface);
        System.err.println("Connected nodes are now: " + nodes.size());
    }

    public Integer getReadQuorum() {
        return readQuorum;
    }

    public void setReadQuorum(Integer readQuorum) {
        this.readQuorum = readQuorum;
    }

    public Integer getWriteQuorum() {
        return writeQuorum;
    }

    public void setWriteQuorum(Integer writeQuorum) {
        this.writeQuorum = writeQuorum;
    }

    private static void masterNodeConfiguration(MasterNodeImplementation masterNodeImplementation){
        Scanner input = new Scanner(System.in);
        int pick;
        do{
            System.out.println("CURRENT READ QUORUM: "+masterNodeImplementation.getReadQuorum()+"" +
                    "\nCURRENT WRITE QUORUM: "+masterNodeImplementation.getWriteQuorum());
            System.out.println("At anytime, press 1 to change quorums, 0 to exit");
            pick = Integer.parseInt(input.nextLine());
            switch (pick){
                case 1:
                    int r,w;
                    try{
                        System.out.println("Please insert desired read quorum:");
                        r = Integer.parseInt(input.nextLine());
                        System.out.println("Please insert desired write quorum:");
                        w = Integer.parseInt(input.nextLine());
                        masterNodeImplementation.setReadQuorum(r);
                        masterNodeImplementation.setWriteQuorum(w);
                    }catch(NumberFormatException e){
                        System.out.println("Quorums must be numbers");
                    }
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
            }
        }while(pick != 0);
        exit(0);
    }

}
