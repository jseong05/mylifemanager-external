package com.mlm.external;

import com.google.gson.annotations.SerializedName;

public class Expense {
    private String id;
    private String memo;
    private int money;
    private boolean isMinus;
    private boolean isWaste;
    private String category; // "식비", "교통", "쇼핑", "문화", "기타"

    @SerializedName("timestamp")
    private long timestamp;

    public Expense(String id, String memo, int money, boolean isMinus, boolean isWaste) {
        this(id, memo, money, isMinus, isWaste, System.currentTimeMillis(), "기타");
    }

    public Expense(String id, String memo, int money, boolean isMinus, boolean isWaste, String category) {
        this(id, memo, money, isMinus, isWaste, System.currentTimeMillis(), category);
    }

    public Expense(String id, String memo, int money, boolean isMinus, boolean isWaste, long timestamp) {
        this(id, memo, money, isMinus, isWaste, timestamp, "기타");
    }

    public Expense(String id, String memo, int money, boolean isMinus, boolean isWaste, long timestamp, String category) {
        this.id = id;
        this.memo = memo;
        this.money = money;
        this.isMinus = isMinus;
        this.isWaste = isWaste;
        this.timestamp = (timestamp == 0) ? System.currentTimeMillis() : timestamp;
        this.category = (category == null || category.isEmpty()) ? "기타" : category;
    }

    public String getId() { return id; }
    public String getMemo() { return memo; }
    public int getMoney() { return money; }
    public boolean isMinus() { return isMinus; }
    public boolean isWaste() { return isWaste; }
    public String getCategory() { return (category == null || category.isEmpty()) ? "기타" : category; }
    public long getTimestamp() {
        return timestamp;
    }
}
