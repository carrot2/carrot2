package com.chilang.carrot.filter.cluster.rough;



public interface Snippet {

    int getInternalId();

    void setInternalId(int id);

    String getId();

    String getTitle();

    String getUrl();

    String getDescription();

    void setTitle(String title);

    void setUrl(String url);

    void setDescription(String snippet);

}
