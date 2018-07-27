package com.nabenik.jmovies.dao;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author tuxtor
 */
@Stateless
public class OmdbMovieDao {
    
    @Inject @LookupMovie
    Event<String> lookupMovie;
    
    @Resource
    ManagedExecutorService threadPool;
    
    @Inject
    @ConfigProperty(name = "omdb.key", defaultValue = "999")
    String omdbKey;
    
    String omdbBaseUrl;
    
    @PostConstruct
    public void init(){
        omdbBaseUrl = "http://www.omdbapi.com/?apikey=" + omdbKey;
    }
    
    
    public CompletionStage<String> findActors(String imdb){
        
        lookupMovie.fireAsync(imdb, NotificationOptions.ofExecutor(threadPool));
        
        String requestUrl = omdbBaseUrl + "&i=" + imdb;
        
        //Intentar buscar los detalles
        //Parametrizamos el cliente
        WebTarget target = ClientBuilder.newBuilder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build()
                .target(requestUrl);

        CompletionStage<String> future = target.request()
                .accept(MediaType.APPLICATION_JSON)
                .rx()
                .get(String.class);
        return future;
    }
    
    
}
