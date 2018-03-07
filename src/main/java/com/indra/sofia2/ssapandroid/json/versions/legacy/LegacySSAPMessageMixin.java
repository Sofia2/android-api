package com.indra.sofia2.ssapandroid.json.versions.legacy;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JsonNode;
import com.indra.sofia2.ssapandroid.ssap.SSAPVersion;

@JsonTypeInfo(use=Id.NONE)
public abstract class LegacySSAPMessageMixin<T> {
	@JsonIgnore public abstract SSAPVersion getVersion();
	@JsonIgnore public abstract String getBody();
	@JsonIgnore public abstract T getBodyAsObject();
	@JsonIgnore public abstract void setBodyAsObject(T bodyObject);	
	@JsonIgnore public abstract JsonNode getBodyAsJsonObject();	
	@JsonIgnore public abstract String getBodyAsJsonString();	
	@JsonProperty("body") public abstract String getBodyLegacy();
}
