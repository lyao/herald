package org.church.volyn.entities;

import org.church.volyn.App;
import org.church.volyn.utils.ResourcesUtils;

public class Category {
    private long id;
    private String mValidationName;
    private String mTitle;
    private String mResourceID;
    private int mOrder;

    public Category() {
    }

    public Category(String validationName, String resourceID, int order) {
        this.mValidationName = validationName;
        this.mResourceID = resourceID;
        this.mOrder = order;
    }

    public long getId() {
        return id;
    }

    public Category setId(long id) {
        this.id = id;
        return this;
    }

    public String getValidationName() {
        return mValidationName;
    }

    public void setValidationName(String validationName) {
        this.mValidationName = validationName;
    }

    public String getTitle() {
        return ResourcesUtils.getStringByName(App.getContext(), mResourceID);
       // return mValidationName;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getResourceID() {
        return mResourceID;
    }

    public void setResourceID(String resourceID) {
        this.mResourceID = resourceID;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        this.mOrder = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return id == category.id;

    }

}
