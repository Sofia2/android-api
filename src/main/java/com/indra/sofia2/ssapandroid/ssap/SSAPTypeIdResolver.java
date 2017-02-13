package com.indra.sofia2.ssapandroid.ssap;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class SSAPTypeIdResolver implements TypeIdResolver{

	private JavaType mBaseType;
	
	@Override
	public void init(JavaType baseType) {
		 mBaseType = baseType;
		
	}

	@Override
	public String idFromValue(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String idFromValueAndType(Object value, Class<?> suggestedType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String idFromBaseType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JavaType typeFromId(DatabindContext context, String id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescForKnownTypeIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Id getMechanism() {
		return Id.CUSTOM;
	}

}
