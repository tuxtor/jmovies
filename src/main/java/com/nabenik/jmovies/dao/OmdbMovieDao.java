package com.nabenik.jmovies.dao;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author tuxtor
 */
@Stateless
public class OmdbMovieDao {
    
    @Inject @LookupMovie
    Event<String> lookupMovie;

    
    private static final String OMDB_KEY = "380e56c9";
    private static final String OMDB_BASE_URL = "http://www.omdbapi.com/?apikey=" + OMDB_KEY;
    
    public CompletionStage<String> findActors(String imdb){
        
        lookupMovie.fire(imdb);
        
        String requestUrl = OMDB_BASE_URL + "&i=" + imdb;
        
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
