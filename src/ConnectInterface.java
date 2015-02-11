/**
 * SimpleTime.java
 * Definition of the SimplTime interface.
 * @version 1.0
 */
//Import needed packages

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ConnectInterface extends Remote {
//    boolean createUser(String login, String password);
//
//    boolean loginUser(String login, String password);
//
//    boolean createDocument(String name, String author, String text);
//
//    boolean updateDocumentText(String text, String name, String author);
//
//    String getDocumentText(String name, String author);
//
//    boolean setDocumentUserAccess(String documentName, String author, String login, boolean exclusiveAccess);
//
//    public String[][] getAllDocsFromUser(String login);
//
//    boolean setWriter(String fullname, String masterLogin);
//
//    boolean writeStatus(String docname, String masterLogin);
//
//    public ArrayList<String> getWriters();
//
//    public ArrayList<String> getUsingDocuments();




    boolean createUser(String login, String password) throws RemoteException;
    boolean checkUser(String login, String password) throws RemoteException;
    boolean createDocument(String name, String masterLogin, String text) throws RemoteException;
    String getDocument(String name, String masterLogin) throws RemoteException;
    ArrayList<String> getAllDocsFromUser(String masterLogin) throws RemoteException;
    boolean updateDocument(String text, String name, String masterLogin) throws RemoteException;
    boolean setWriter(String fullname, String masterLogin) throws RemoteException;
    boolean writeStatus(String docname, String masterLogin) throws RemoteException;
    ArrayList<String> getDocumentInfo(String clearDocName)throws RemoteException;
}
