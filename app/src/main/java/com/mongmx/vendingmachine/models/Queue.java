package com.mongmx.vendingmachine.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Queue {

    private int id;
    private int total;

    @SerializedName("order_items")
    private List<QueueItem> queueItems;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<QueueItem> getQueueItems() {
        return queueItems;
    }

    public void setQueueItems(List<QueueItem> queueItems) {
        this.queueItems = queueItems;
    }

}
