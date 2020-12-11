package com.cet.eem.model.base;

/**
 * @author ACloud
 * @title: MoveToOtherDTO
 * @description: TODO
 * @date 2019/4/1919:30
 */
public class MoveToOtherDTO {
    private ModelIdPairDTO toBeOut;
    private ModelIdPairDTO previous;
    private ModelIdPairDTO following;

    @Override
    public String toString() {
        return "MoveToOtherDTO{" +
                "theObject=" + toBeOut +
                ", previous=" + previous +
                ", following=" + following +
                '}';
    }

    public ModelIdPairDTO getToBeOut() {
        return toBeOut;
    }

    public void setToBeOut(ModelIdPairDTO toBeOut) {
        this.toBeOut = toBeOut;
    }

    public ModelIdPairDTO getPrevious() {
        return previous;
    }

    public void setPrevious(ModelIdPairDTO previous) {
        this.previous = previous;
    }

    public ModelIdPairDTO getFollowing() {
        return following;
    }

    public void setFollowing(ModelIdPairDTO following) {
        this.following = following;
    }
}
