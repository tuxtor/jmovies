package com.nabenik.jmovies.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.nabenik.jmovies.dao.MovieDao;
import com.nabenik.jmovies.dao.OmdbMovieDao;
import com.nabenik.jmovies.model.Movie;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.JsonbBuilder;

@RequestScoped
@Path("/movies")
@Produces("application/json")
@Consumes("application/json")
public class MovieEndpoint {
	
	@EJB
	MovieDao movieDao;
	
	@POST
	public Response create(Movie movie) {
		movieDao.create(movie); 
		return Response
				.created(UriBuilder.fromResource(MovieEndpoint.class).path(String.valueOf(movie.getId())).build())
				.build();
	}
	
	@GET
	@Path("/{id:[0-9][0-9]*}")
	public Response findById(@PathParam("id") final Long id) {
		Movie movie = movieDao.findById(id);
		if (movie == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(movie).build();
	}
	
	@GET
	public List<Movie> listAll(@QueryParam("start") final Integer startPosition,
			@QueryParam("max") final Integer maxResult) {
		final List<Movie> movies = movieDao.listAll(startPosition, maxResult);
		return movies;
	}
	
	@PUT
	@Path("/{id:[0-9][0-9]*}")
	public Response update(@PathParam("id") Long id, Movie movie) {
		movie = movieDao.update(movie);
		return Response.ok(movie).build();
	}
	
	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") final Long id) {
		movieDao.deleteById(id);
		return Response.noContent().build();
	}
        
        @GET
	@Path("/with-actors/{id:[0-9][0-9]*}")
	public Response findWithActors(@PathParam("id") final Long id) {
		Movie movie = movieDao.findById(id);
		if (movie == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
                
		return Response.ok(createMovieWithActor(movie)).build();
	}
        
        private String createMovieWithActor(Movie movie){
            
            //To json
            String result = JsonbBuilder.create().toJson(movie);
             
            JsonReader jsonReader = Json.createReader(new StringReader(result));
            JsonObject jobj = jsonReader.readObject();       
            
            //Json-p Patch
            jobj = Json.createPatchBuilder()
	      .add("/actores", "mafalda")
	      .build()
	      .apply(jobj);
            
            return jobj.toString();
        }

}
