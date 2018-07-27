package com.nabenik.jmovies.dao;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Named;

/**
 *
 * @author tuxtor
 */
@Named
public class OmdbMovieObserver {
    
    public void logMovieLookup(@ObservesAsync String imdb){
        System.out.println("Notificando async");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(OmdbMovieObserver.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Buscando " + imdb);
    }
    
}
