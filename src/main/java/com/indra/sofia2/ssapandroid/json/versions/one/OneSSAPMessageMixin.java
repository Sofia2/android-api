package com.indra.sofia2.ssapandroid.json.versions.one;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class OneSSAPMessageMixin<T> {
	@JsonIgnore public abstract String getBodyLegacy();
	@JsonProperty("body") public abstract T getBodyAsObject();
	@JsonIgnore public abstract void setBodyAsObject(T bodyObject);	
	@JsonIgnore public abstract JsonNode getBodyAsJsonObject();	
	@JsonIgnore public abstract String getBodyAsJsonString();	
	@JsonIgnore public abstract String getBody();
	
}
