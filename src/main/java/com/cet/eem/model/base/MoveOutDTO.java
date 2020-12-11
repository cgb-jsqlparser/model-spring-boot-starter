package com.cet.eem.model.base;

/**
 * @author ACloud
 * @title: MoveOutDTO
 * @description: TODO
 * @date 2019/4/1919:29
 */
public class MoveOutDTO {
    private ModelIdPairDTO parent;
    private ModelIdPairDTO toBeOut;

    @Override
    public String toString() {
        return "MoveOutDTO{" +
                "parent=" + parent +
                ", theObject=" + toBeOut +
                '}';
    }

    public ModelIdPairDTO getParent() {
        return parent;
    }

    public void setParent(ModelIdPairDTO parent) {
        this.parent = parent;
    }

    public ModelIdPairDTO getToBeOut() {
        return toBeOut;
    }

    public void setToBeOut(ModelIdPairDTO toBeOut) {
        this.toBeOut = toBeOut;
    }
}
