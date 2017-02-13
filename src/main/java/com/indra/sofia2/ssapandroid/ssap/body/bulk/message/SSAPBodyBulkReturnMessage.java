/*******************************************************************************
 * Copyright 2013-16 Indra Sistemas S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 ******************************************************************************/
package com.indra.sofia2.ssapandroid.ssap.body.bulk.message;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.indra.sofia2.ssapandroid.json.JSON;
import com.indra.sofia2.ssapandroid.ssap.exceptions.SSAPMessageDeserializationError;

public class SSAPBodyBulkReturnMessage {

	private SSAPBulkOperationSummary insertSummary;
	private SSAPBulkOperationSummary updateSummary;
	private SSAPBulkOperationSummary deleteSummary;

	public SSAPBulkOperationSummary getInsertSummary() {
		return insertSummary;
	}

	public void setInsertSummary(SSAPBulkOperationSummary insertSummary) {
		this.insertSummary = insertSummary;
	}

	public SSAPBulkOperationSummary getUpdateSummary() {
		return updateSummary;
	}

	public void setUpdateSummary(SSAPBulkOperationSummary updateSummary) {
		this.updateSummary = updateSummary;
	}

	public SSAPBulkOperationSummary getDeleteSummary() {
		return deleteSummary;
	}

	public void setDeleteSummary(SSAPBulkOperationSummary deleteSummary) {
		this.deleteSummary = deleteSummary;
	}

	public String toJson() {
		try {
			return JSON.serialize(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJsonArray(Collection<SSAPBodyBulkReturnMessage> collection) {
		try {
			return JSON.serializeCollection(collection);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static SSAPBodyBulkReturnMessage fromJsonToSSAPBodyBulkReturnMessage(String json) {
		try {
			StringBuffer sb = new StringBuffer();
			Pattern pattern = Pattern.compile("(\\{\\\"_id\\\":\\{\\\"\\$oid\\\"):\\\"(\\w*)\\\"(\\}\\})");
			Matcher matcher = pattern.matcher(json);
			
			
			while(matcher.find()) {
				String g2 = matcher.group(2);
				matcher.appendReplacement(sb, Matcher.quoteReplacement("\"{\\\"_id\\\":ObjectId(\\\""+g2+"\\\")}\""));
			}			
			matcher.appendTail(sb);
			
			json = sb.toString();
			json = json.replaceAll(	"\\\\*\"objectIds\\\\*\":\\[\\{\\\\*\"_ids\\\\*\"", "\\\\\\\"objectIds\\\\\\\":\\[\\{\\\\\\\"_ids\\\\\\\"");
			// \\*\"objectIds\\\":\[\{\\\"_ids\\*\"
			System.out.println(json);
			
			
			
			
			
			
			return JSON.deserialize(json, SSAPBodyBulkReturnMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}

	}

	public static Collection<SSAPBodyBulkReturnMessage> fromJsonArrayToSSAPBodyBulkReturnMessage(String json) {
		try {
			return JSON.deserializeCollection(json, SSAPBodyBulkReturnMessage.class);
		} catch (IOException e) {
			throw new SSAPMessageDeserializationError(e);
		}
	}

}
