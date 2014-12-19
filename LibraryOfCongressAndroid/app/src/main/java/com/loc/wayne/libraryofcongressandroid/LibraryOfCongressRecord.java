package com.loc.wayne.libraryofcongressandroid;

import org.json.JSONObject;

/**
 * Created by wayne on 12/17/14.
 */
public class LibraryOfCongressRecord {

    private Integer index;
    private String title;
    private String creator;
    private String createdPublishedDate;
    private String imageUrl;

    static LibraryOfCongressRecord locRecordFromJSONObject(JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        final LibraryOfCongressRecord record = new LibraryOfCongressRecord();
        final int index = jsonObject.optInt("index", -1);
        final String title = jsonObject.optString("title", "NA");
        final String creator = jsonObject.optString("creator", "NA");
        final String createdPublishedDate = jsonObject.optString("created_published_date", "NA");
        final String imageUrl = jsonObject.optJSONObject("image").optString("full");
        record.setIndex(index);
        record.setTitle(title);
        record.setCreator(creator);
        record.setCreatedPublishedDate(createdPublishedDate);
        record.setImageUrl(imageUrl);
        return record;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatedPublishedDate() {
        return createdPublishedDate;
    }

    public void setCreatedPublishedDate(String createdPublishedDate) {
        this.createdPublishedDate = createdPublishedDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
