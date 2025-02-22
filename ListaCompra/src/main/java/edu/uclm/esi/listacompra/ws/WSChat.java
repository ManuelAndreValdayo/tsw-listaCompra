package edu.uclm.esi.listacompra.ws;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.nimbusds.jose.shaded.json.JSONValue;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSChat extends TextWebSocketHandler{
	private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WSChat.class);

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.sessions.put(session.getId(), session);
		JSONObject jso = new JSONObject();
		jso.put("tipo", "llegandoUsuario");
		jso.put("contenido", session.getId());
		System.out.println(session.getId());
		this.difundir(jso);
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message)
	throws Exception {
		if(message.getPayload().startsWith("idLista_")) {
			String cadena[] = message.getPayload().split("_");
			Integer idLista = Integer.parseInt(cadena[1]);
			session.sendMessage(new TextMessage("Mensaje recibido"));
			JSONObject jso = new JSONObject();
			jso.put("idLista",idLista);
			this.difundir(jso);			
		}
		System.out.println(message.getPayload());
	}
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
	}
	
	private void difundir(JSONObject jso) throws IOException{
		TextMessage message = new TextMessage(jso.toString());
		for (WebSocketSession target : this.sessions.values()) {
			target.sendMessage(message);
		}
	}
	
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Error de transporte: {}", exception.getMessage(), exception);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.sessions.remove(session.getId()); // Eliminar la sesión del mapa
        System.out.println("Sesión cerrada: " + session.getId());
    }

}
