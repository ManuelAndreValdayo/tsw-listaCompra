package edu.uclm.esi.listacompra.ws;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
	    System.out.println(request.getHeaders().getFirst("listId"));
	    String listId = request.getHeaders().getFirst("listId");
	    attributes.put("listId", listId); // Almacena idLista como atributo
	    return super.beforeHandshake(request, response, wsHandler, attributes);
	}
    
}
