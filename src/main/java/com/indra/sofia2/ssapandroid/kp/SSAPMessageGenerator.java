/*******************************************************************************
 * Copyright 2013-15 Indra Sistemas S.A.
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

package com.indra.sofia2.ssapandroid.kp;

import java.util.Map;

import com.indra.sofia2.ssapandroid.kp.exceptions.NotJoinedException;
import com.indra.sofia2.ssapandroid.kp.exceptions.SQLSentenceNotAllowedForThisOperationException;
import com.indra.sofia2.ssapandroid.ssap.SSAPMessage;
import com.indra.sofia2.ssapandroid.ssap.SSAPMessageDirection;
import com.indra.sofia2.ssapandroid.ssap.SSAPMessageTypes;
import com.indra.sofia2.ssapandroid.ssap.SSAPQueryType;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyJoinTokenMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyJoinUserAndPasswordMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyOperationMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyQueryMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyQueryWithParamMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodySubscribeMessage;
import com.indra.sofia2.ssapandroid.ssap.body.SSAPBodyUnsubscribeMessage;


public class SSAPMessageGenerator {

	private static SSAPMessageGenerator me = new SSAPMessageGenerator();

	public static SSAPMessageGenerator getInstance() {
		return me;
	}

	/**
	 * Constructor
	 */
	public SSAPMessageGenerator() {
	}

	/**
	 * Genera un mensaje JOIN con autenticación basada en usuario y password
	 * @param usuario
	 * @param password
	 * @param instance
	 * @param sessionKey
	 * @return
	 */
	public SSAPMessage generateJoinMessage(String usuario, String password, String instance, String sessionKey) {
		SSAPMessage mensaje = new SSAPMessage();
		SSAPBodyJoinUserAndPasswordMessage body = new SSAPBodyJoinUserAndPasswordMessage();
		body.setPassword(password);
		body.setUser(usuario);
		body.setInstance(instance);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.JOIN);
		mensaje.setSessionKey(sessionKey);
		return mensaje;
	}

	/**
	 * Genera un mensaje JOIN con autenticación basada en token
	 * @param token
	 * @param instance
	 * @return
	 */
	public SSAPMessage generateJoinByTokenMessage(String token, String instance){
		SSAPMessage mensaje = new SSAPMessage();
		SSAPBodyJoinTokenMessage body = new SSAPBodyJoinTokenMessage();
		body.setToken(token);
		body.setInstance(instance);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.JOIN);
		return mensaje;
	}
	
	/**
	 * Genera un mensaje JOIN con autenticación basada en token
	 * @param token
	 * @param instance
	 * @param sessionKey
	 * @return
	 */
	public SSAPMessage generateJoinByTokenMessage(String token, String instance, String sessionKey){
		SSAPMessage mensaje = new SSAPMessage();
		SSAPBodyJoinTokenMessage body = new SSAPBodyJoinTokenMessage();
		body.setToken(token);
		body.setInstance(instance);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.JOIN);
		mensaje.setSessionKey(sessionKey);
		return mensaje;
	}	

	/**
	 * genera un mensaje LEAVE para cerrar la sesión 
	 * @param sessionKey
	 * @return
	 */
	public SSAPMessage generateLeaveMessage(String sessionKey) {
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.LEAVE);
		return mensaje;
	}

	/**
	 * genera un mensaje INSERT de tipo nativo
	 * @param sessionKey
	 * @param ontologia
	 * @param datos
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateInsertMessage(String sessionKey, String ontologia, String datos) {
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodyOperationMessage body = new SSAPBodyOperationMessage();
		body.setData(datos);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.INSERT);
		mensaje.setOntology(ontologia);
		return mensaje;
	}

	/**
	 * genera un mensaje de INSERT del tipo indicado en el argumento queryType
	 * @param sessionKey
	 * @param ontologia
	 * @param datos
	 * @param queryType
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateInsertMessage(String sessionKey, String ontologia, String datos, SSAPQueryType queryType) throws SQLSentenceNotAllowedForThisOperationException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodyOperationMessage body = new SSAPBodyOperationMessage();
		if(isInsert(datos, queryType)){
			if(queryType==SSAPQueryType.SQLLIKE){
				body.setQuery(datos);
			}else{
				body.setData(datos);
			}
		}else{
			throw new SQLSentenceNotAllowedForThisOperationException (new Exception("ERROR - Expected insert values"));
		}
		body.setQueryType(queryType);
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setBody(body.toJson());
		mensaje.setMessageType(SSAPMessageTypes.INSERT);
		mensaje.setOntology(ontologia);
		return mensaje;
	}

	private boolean isInsert(String datos, SSAPQueryType queryType){
		if(queryType != null && datos.length()>0){
			switch(queryType){
			case SQLLIKE:			
			case NATIVE:
				return datos.toUpperCase().contains("INSERT");
			default:
				return false;
			}
		}else {
			return datos.length() > 0;
		}
	}

	/**
	 * Genera un mensaje UPDATE de tipo nativo
	 * @param sessionKey
	 * @param ontologia
	 * @param datos
	 * @param query
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateUpdateMessage(String sessionKey, String ontologia, String datos, String query)throws NotJoinedException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodyOperationMessage body = new SSAPBodyOperationMessage();
		body.setData(datos);
		body.setQuery(query);
		body.setQueryType(SSAPQueryType.NATIVE);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.UPDATE);
		mensaje.setOntology(ontologia);
		return mensaje;
	}



	/**
	 * Genera un mensaje UPDATE del tipo indicado en el argumento queryType
	 * @param sessionKey
	 * @param ontologia
	 * @param datos
	 * @param query
	 * @param queryType
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateUpdateMessage(String sessionKey, String ontologia, String datos, String query, SSAPQueryType queryType)throws SQLSentenceNotAllowedForThisOperationException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodyOperationMessage body = new SSAPBodyOperationMessage();
		
		if ( isUpdate(query, queryType)){
			body.setQuery(query);
		}else{
			throw new SQLSentenceNotAllowedForThisOperationException(new Exception("ERROR - Expected update query"));
		}
		body.setData(datos);
		body.setQueryType(queryType);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.UPDATE);
		mensaje.setOntology(ontologia);
		return mensaje;
	}

	/**
	 * Genera un mensaje REMOVE de tipo nativo
	 * @param sessionKey
	 * @param ontologia
	 * @param query
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateRemoveMessage(String sessionKey, String ontologia, String query)throws NotJoinedException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodyOperationMessage body = new SSAPBodyOperationMessage();
		body.setQuery(query);
		body.setQueryType(SSAPQueryType.NATIVE);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.DELETE);
		mensaje.setOntology(ontologia);
		return mensaje;
	}

	/**
	 * Genera un mensaje REMOVE del tipo indicado en el argumento queryType
	 * @param sessionKey
	 * @param ontologia
	 * @param query
	 * @param queryType
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateRemoveMessage(String sessionKey, String ontologia, String query, SSAPQueryType queryType)throws SQLSentenceNotAllowedForThisOperationException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodyOperationMessage body = new SSAPBodyOperationMessage();
		if(isRemove(query, queryType)){
			body.setQuery(query);
		}else{
			throw new SQLSentenceNotAllowedForThisOperationException(new Exception("Error - statement no expected"));
		}
		body.setQueryType(queryType);
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setBody(body.toJson());
		mensaje.setMessageType(SSAPMessageTypes.DELETE);
		mensaje.setOntology(ontologia);
		return mensaje;
	}


	/**
	 * Genera un mensaje QUERY de tipo nativo
	 * @param sessionKey
	 * @param idQuery
	 * @param queryType
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateQueryMessage(String sessionKey, String ontologia, String query) throws NotJoinedException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodyQueryMessage body = new SSAPBodyQueryMessage();
		body.setQuery(query);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.QUERY);
		mensaje.setOntology(ontologia);
		return mensaje;
	}

	/**
	 * Genera un mensaje QUERY del tipo pasado por parámetros
	 * @param sessionKey
	 * @param idQuery
	 * @param queryType
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateQueryMessage(String sessionKey, String ontologia, String query, SSAPQueryType queryType) throws SQLSentenceNotAllowedForThisOperationException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodyQueryMessage body = new SSAPBodyQueryMessage();
		if(isQuery(query, queryType)){
			body.setQuery(query);
		}else{
			throw new SQLSentenceNotAllowedForThisOperationException(new Exception("ERROR - statement no expected"));
		}
		body.setQueryType(queryType);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.QUERY);
		mensaje.setOntology(ontologia);
		return mensaje;
	}

	/**
	 * Genera un mensaje QUERY para queries predefinidas en el SIB
	 * @param sessionKey
	 * @param idQuery
	 * @param queryType
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateQueryMessage (String sessionKey, String idQuery) throws NotJoinedException{
		SSAPMessage message = new SSAPMessage();
		message.setSessionKey(sessionKey);
		SSAPBodyQueryMessage body = new SSAPBodyQueryMessage();
		body.setQuery(idQuery);
		body.setQueryType(SSAPQueryType.SIB_DEFINED);
		message.setBody(body.toJson());
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.setMessageType(SSAPMessageTypes.QUERY);
		return message;
	}

	/**
	 * Genera mensaje QUERY para queries predefinidas en el SIB y permite pasar parametros a la query
	 * @param sessionKey
	 * @param idQuery
	 * @param params
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateQueryMessageWithParam (String sessionKey, String idQuery, Map<String,String> params) throws NotJoinedException{
		SSAPMessage message = new SSAPMessage();
		message.setSessionKey(sessionKey);
		//Segun el tipo de query que llege se creara un body diferente
		//para el tipo SIB_DIFINED pueden venir parametros y es necesario utilizar SSAPBodyQueryWithParamMessage en lugar de SSAPQueryMessage
		if(idQuery.length()>0 && params != null){
			SSAPBodyQueryWithParamMessage body = new SSAPBodyQueryWithParamMessage();
			if(isQuery(idQuery, SSAPQueryType.SIB_DEFINED)){
				body.setQuery(idQuery);
			}else{
				throw new SQLSentenceNotAllowedForThisOperationException(new Exception("ERROR - statement no expected"));
			}
			body.setQueryType(SSAPQueryType.SIB_DEFINED);
			body.setQueryParams(params);
			message.setBody(body.toJson());
		}else{
			SSAPBodyQueryMessage body = new SSAPBodyQueryMessage();
			if(isQuery(idQuery, SSAPQueryType.SIB_DEFINED)){
				body.setQuery(idQuery);
			}else{
				throw new SQLSentenceNotAllowedForThisOperationException(new Exception("ERROR - statement no expected"));
			}
			body.setQueryType(SSAPQueryType.SIB_DEFINED);
			message.setBody(body.toJson());

		}
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.setMessageType(SSAPMessageTypes.QUERY);
		return message;
	}

	/**
	 * Genera un mensaje SUBSCRIBE del tipo nativo
	 * @param sessionKey
	 * @param idQuery
	 * @param queryType
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateSubscribeMessage(String sessionKey, String ontologia, int msRefresh, String query)throws NotJoinedException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodySubscribeMessage body = new SSAPBodySubscribeMessage();
		body.setQuery(query);
		body.setMsRefresh(msRefresh);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.SUBSCRIBE);
		mensaje.setOntology(ontologia);
		return mensaje;
	}

	/**
	 * Genera un mensaje SUBSCRIBE de tipo pasado por parametros
	 * @param sessionKey
	 * @param idQuery
	 * @param queryType
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateSubscribeMessage(String sessionKey, String ontologia, int msRefresh, String query, SSAPQueryType queryType)throws SQLSentenceNotAllowedForThisOperationException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodySubscribeMessage body = new SSAPBodySubscribeMessage();


		body.setQueryType(queryType);

		if(isQuery(query, queryType)){
			body.setQuery(query);
		}else{
			throw new SQLSentenceNotAllowedForThisOperationException(new Exception("ERROR - statement no expected"));
		}

		body.setQuery(query);


		body.setMsRefresh(msRefresh);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.SUBSCRIBE);
		mensaje.setOntology(ontologia);
		return mensaje;
	}




	/**
	 * Genera un mensaje UNSUBSCRIBE
	 * @param sessionKey
	 * @param ontologia
	 * @param idSuscripcion
	 * @return
	 * @throws NotJoinedException
	 */
	public SSAPMessage generateUnsubscribeMessage(String sessionKey, String ontologia, String idSuscripcion)throws NotJoinedException{
		SSAPMessage mensaje = new SSAPMessage();
		mensaje.setSessionKey(sessionKey);
		SSAPBodyUnsubscribeMessage body = new SSAPBodyUnsubscribeMessage();
		body.setIdSuscripcion(idSuscripcion);
		mensaje.setBody(body.toJson());
		mensaje.setDirection(SSAPMessageDirection.REQUEST);
		mensaje.setMessageType(SSAPMessageTypes.UNSUBSCRIBE);
		mensaje.setOntology(ontologia);
		return mensaje;
	}


	private boolean isUpdate(String query, SSAPQueryType queryType){
		if(queryType != null && query.length()>0){
			switch (queryType){
			case SQLLIKE:
			case NATIVE:
				return query.toUpperCase().contains("UPDATE");
			default:
				return false;
			}
		}else{
			return query.length() > 0;
		}
	}

	private boolean isRemove(String query, SSAPQueryType queryType){
		if(queryType !=null && query.length()>0){
			switch(queryType){
			case SQLLIKE:
				return query.toUpperCase().contains("DELETE ");
			case NATIVE:
				return query.toUpperCase().contains("REMOVE");
			default:
				return false;
			}
		}else{
			return query.length() > 0;
		}
	}

	private boolean isQuery(String query, SSAPQueryType queryType){
		if(queryType !=null && query.length()>0){
			switch(queryType){
			case SQLLIKE:
			case BDH:
				return query.toUpperCase().contains("SELECT ")||query.toUpperCase().contains("INSERT ")||query.toUpperCase().contains("UPDATE ")||query.toUpperCase().contains("DELETE ");
			case NATIVE:
				return query.toUpperCase().contains("FIND") || query.toUpperCase().startsWith("db.");
			case SIB_DEFINED:
				return true;
			
			default:
				return false;
			}
		}else{
			return query.length() > 0;
		}
	}

}
