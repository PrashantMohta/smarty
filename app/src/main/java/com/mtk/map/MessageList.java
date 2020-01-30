package com.mtk.map;

import java.util.ArrayList;

public class MessageList {
    private ArrayList<MessageListItem> mMessageItems;
    private boolean mNewMessage;
    private int mSize;

    public MessageList() {
        reset();
    }

    synchronized void reset() {
        if (this.mMessageItems == null) {
            this.mMessageItems = new ArrayList();
        } else {
            this.mMessageItems.clear();
        }
        this.mSize = 0;
        this.mNewMessage = false;
    }

    public synchronized boolean addSize(int size) {
        this.mSize += size;
        return true;
    }

    public synchronized boolean setNewMessage() {
        if (!this.mNewMessage) {
            this.mNewMessage = true;
        }
        return true;
    }

    public synchronized boolean addMessageItem(MessageListItem item) {
        if (item != null) {
            this.mMessageItems.add(item);
        }
        return true;
    }

    public synchronized MessageListItem[] generateMessageItemArray() {
        return (MessageListItem[]) this.mMessageItems.toArray(new MessageListItem[this.mMessageItems.size()]);
    }

    public int getCurrentSize() {
        return this.mSize;
    }
}
