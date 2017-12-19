package com.bookkeeping.model;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.utilities.ControllerUtilities;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chandanm on 9/22/16.
 */
public abstract class ModelObj {
    private String uniqueKey;
    private String createdBy;
    private String _id = ObjectId.get().toHexString();
    private String creationDate;
    private String creationTime;
    private String updateDate;
    private String updatedBy;
    private String photo;


    public ModelObj() {
        SimpleDateFormat dt = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM_DD);
        SimpleDateFormat dtFull = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM_DD_FULL);
        Date date = new Date();
        this.creationDate = dt.format(date);
        this.creationTime = dtFull.format(date);
        this.setCreatedBy(Constants.ROOT_USER);
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
