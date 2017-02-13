package com.indra.sofia2.ssapandroid.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.indra.sofia2.ssapandroid.json.versions.legacy.LegacySSAPBodyMessageMixin;
import com.indra.sofia2.ssapandroid.json.versions.legacy.LegacySSAPMessageMixin;
import com.indra.sofia2.ssapandroid.json.versions.one.OneSSAPBodyMessageMixin;
import com.indra.sofia2.ssapandroid.json.versions.one.OneSSAPMessageMixin;
import com.indra.sofia2.ssapandroid.ssap.SSAPMessage;
import com.indra.sofia2.ssapandroid.ssap.SSAPVersion;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyMessage;

public class SSAPJsonModule extends Module {
	private static final String NAME = "JAKCSON_MODULE";
	private static final VersionUtil VERSION_UTIL = new VersionUtil() {};
	private SSAPVersion ssap_version;
	
	private static final long serialVersionUID = 1L;
	
	public SSAPJsonModule() {
		this(SSAPVersion.ONE);
	}
	public SSAPJsonModule(SSAPVersion ssap_version) {
		super();
		this.ssap_version = ssap_version;
	}
	
	@Override
	public String getModuleName() {
		return NAME;
	}

	@Override
	public Version version() {
		return VERSION_UTIL.version();
	}

	@Override
	public void setupModule(SetupContext context) {
		
		if(ssap_version.equals(SSAPVersion.LEGACY)) {
			context.setMixInAnnotations(SSAPMessage.class, LegacySSAPMessageMixin.class);
			context.setMixInAnnotations(SSAPBodyMessage.class, LegacySSAPBodyMessageMixin.class);
		}
		else {
			context.setMixInAnnotations(SSAPMessage.class, OneSSAPMessageMixin.class);
			context.setMixInAnnotations(SSAPBodyMessage.class, OneSSAPBodyMessageMixin.class);
		}
	}
}
