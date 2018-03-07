package com.indra.sofia2.ssapandroid.ssap.body;

import java.util.ArrayList;
import java.util.Collection;

import com.indra.sofia2.ssapandroid.ssap.body.bulk.message.SSAPBodyBulkItem;

public class SSAPBodyBulkMessage extends SSAPBodyMessage {

	private Collection<SSAPBodyBulkItem> items = new ArrayList<SSAPBodyBulkItem>();
	
	public Collection<SSAPBodyBulkItem> getItems() {
		return items;
	}

	public void setItems(Collection<SSAPBodyBulkItem> items) {
		this.items = items;
	}

	
	
	

}
