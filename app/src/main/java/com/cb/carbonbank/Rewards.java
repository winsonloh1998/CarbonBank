package com.cb.carbonbank;

public class Rewards {
    private String rewardID;
    private String rewardTitle;
    private String rewardDesc;
    private String rewardType;
    private String rewardImage;
    private int ccRequired;

    public Rewards() {
    }

    public Rewards(String rewardID, String rewardTitle, String rewardDesc, String rewardType, String rewardImage, int ccRequired) {
        this.rewardID = rewardID;
        this.rewardTitle = rewardTitle;
        this.rewardDesc = rewardDesc;
        this.rewardType = rewardType;
        this.rewardImage = rewardImage;
        this.ccRequired = ccRequired;
    }

    public Rewards(String rewardTitle, String rewardImage, int ccRequired) {
        this.rewardTitle = rewardTitle;
        this.rewardImage = rewardImage;
        this.ccRequired = ccRequired;
    }

    public String getRewardID() {
        return rewardID;
    }

    public void setRewardID(String rewardID) {
        this.rewardID = rewardID;
    }

    public String getRewardTitle() {
        return rewardTitle;
    }

    public void setRewardTitle(String rewardTitle) {
        this.rewardTitle = rewardTitle;
    }

    public String getRewardDesc() {
        return rewardDesc;
    }

    public void setRewardDesc(String rewardDesc) {
        this.rewardDesc = rewardDesc;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public String getRewardImage() {
        return rewardImage;
    }

    public void setRewardImage(String rewardImage) {
        this.rewardImage = rewardImage;
    }

    public int getCcRequired() {
        return ccRequired;
    }

    public void setCcRequired(int ccRequired) {
        this.ccRequired = ccRequired;
    }
}
