package org.drack.hackathon.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.net.URI;
import java.util.Map;

public final class Links {

    @JsonProperty("_links")
    private Map<String, URI> links;

    public Map<String, URI> getLinks() {
        return links;
    }

    public void setLinks(Map<String, URI> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("links", links)
                .toString();
    }
}