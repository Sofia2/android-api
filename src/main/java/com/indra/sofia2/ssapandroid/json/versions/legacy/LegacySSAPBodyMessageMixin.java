package com.indra.sofia2.ssapandroid.json.versions.legacy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JsonNode;
import com.indra.sofia2.ssapandroid.ssap.SSAPVersion;

@JsonTypeInfo(use=Id.NONE)
public abstract class LegacySSAPBodyMessageMixin {
	@JsonIgnore public abstract SSAPVersion getVersion();
	@JsonIgnore public abstract JsonNode getDataJson();
	@JsonIgnore public abstract String getDataAsJsonString();
	@JsonIgnore public abstract JsonNode getDataAsJsonObject();
	
}
