package com.indra.sofia2.ssapandroid.json.versions.one;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class OneSSAPBodyMessageMixin {
	//@JsonIgnore public abstract SSAPVersion getVersion();
	@JsonProperty("data") public abstract JsonNode getDataJson();
	@JsonSetter("data") public abstract void setDataJson(JsonNode node);
	@JsonIgnore public abstract String getDataAsJsonString();
	@JsonIgnore public abstract JsonNode getDataAsJsonObject();
	//@JsonIgnore public abstract String getData();
}
