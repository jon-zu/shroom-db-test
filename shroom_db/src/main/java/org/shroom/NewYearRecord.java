package org.shroom;

public class NewYearRecord {
    public int id;

    public int senderId;
    public String senderName;
    public boolean senderDiscardCard;

    public int receiverId;
    public String receiverName;
    public boolean receiverDiscardCard;
    public boolean receiverReceivedCard;

    public String stringContent;
    public long dateSent = 0;
    public long dateReceived = 0;

    public NewYearRecord(int senderid, String sender, int receiverid, String receiver, String message) {
        this.id = -1;
        
        this.senderId = senderid;
        this.senderName = sender;
        this.senderDiscardCard = false;
        
        this.receiverId = receiverid;
        this.receiverName = receiver;
        this.receiverDiscardCard = false;
        this.receiverReceivedCard = false;
        
        this.stringContent = message;
        
        this.dateSent = System.currentTimeMillis();
        this.dateReceived = 0;
    }
}
