package io.github.ztkmkoo.dss.core.actor.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import io.github.ztkmkoo.dss.core.message.rest.DssRestMasterActorCommand;
import io.github.ztkmkoo.dss.core.message.rest.DssRestMasterActorCommandRequest;
import io.github.ztkmkoo.dss.core.message.security.DssAuthenticationCommand;
import io.github.ztkmkoo.dss.core.message.security.DssAuthenticationCommandRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class DssRestAuthenticationActor {
	
	private String userID;
	private String userPassword;
	
	public static Behavior<DssAuthenticationCommand> create(Map<String, String> userList) {
        return Behaviors.setup(context -> new DssRestAuthenticationActor(context, userList).dssRestAuthenticationActor());
    }
	
	private final ActorContext<DssAuthenticationCommand> context;
	private final Map<String, String> userList;
    
    private DssRestAuthenticationActor(ActorContext<DssAuthenticationCommand> context, Map<String, String> userList) {
        this.context = context;
        this.userList = userList;
    }
    
    private Behavior<DssAuthenticationCommand> dssRestAuthenticationActor() {
        return Behaviors
                .receive(DssAuthenticationCommand.class)
                .onMessage(DssAuthenticationCommandRequest.class, this::handlingDssAuthenticationCommandRequest)
                .build();
    }
    
    private Behavior<DssAuthenticationCommand> handlingDssAuthenticationCommandRequest(DssAuthenticationCommandRequest request) {
    	
    	context.getLog().info("DssAuthenticationCommandRequest: {}", request);
    	
    	return Behaviors.same();
    }
    
    private String createToken() {
    	Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("data", "testing jwt");

        Long expiredTime = 1000 * 60L * 60L * 2L;

        Date ext = new Date();
        ext.setTime(ext.getTime() + expiredTime);
     
        String jwt = Jwts.builder()
                .setHeader(headers) 
                .setClaims(payloads) 
                .setSubject("user")
                .setExpiration(ext)
                .signWith(SignatureAlgorithm.HS256, userID.getBytes()) 
                .compact(); 

        return jwt;
    }
    
    
    
    
    
}