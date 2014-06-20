package com.espresoh.memoboard.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.espresoh.memoboard.server.po.UserPO;
import com.espresoh.memoboard.server.session.EntityManagerBean;
import com.espresoh.memoboard.server.session.MBException;
import com.espresoh.memoboard.server.session.UserSession;
import com.google.gson.Gson;

/**
 * This is used to register or sign in an user.
 * 
 * At each login call it will receive the name and email. If it does not exist in database will be added.
 * 
 * The user id will be returned to be used for later calls.
 * 
 * The UI will handle the user session timeout.
 * 
 * @author cghita
 * 
 */
@Path("/login")
public class LoginRegister
{

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String getUserByEmail(String jsonUserPO)
	{
		System.out.println(jsonUserPO);
		
		EntityManagerBean entityManager;
		try
		{
			entityManager = UserSession.getEntityManager(new Gson().fromJson(jsonUserPO, UserPO.class));
			return new Gson().toJson(entityManager.getUserLogged());
			
//			return Response.ok(String.valueOf(entityManager.getUserLogged().getId())).build();

		} catch (MBException e)
		{
			e.printStackTrace();
			return null;
//			return Response.status(Response.Status.FORBIDDEN).build();
		}
	}

	@GET
	@Path("/users/{userId}/")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUsers(@PathParam("userId") int userId)
	{
		EntityManagerBean entityManager = UserSession.getEntityManager(userId);
		return new Gson().toJson(entityManager.getUsers());
	}

	@GET
	@Path("/groups/{userId}/")
	@Produces(MediaType.TEXT_PLAIN)
	public String getGroups(@PathParam("userId") int userId)
	{
		EntityManagerBean entityManager = UserSession.getEntityManager(userId);
		return new Gson().toJson(entityManager.getGroups());
	}


//	@Path("/hello")
//	@GET
//	@Produces(MediaType.TEXT_PLAIN)
//	public String sayPlainTextHello()
//	{
//		EntityManagerBean manager = new EntityManagerBean();
//
//		return "Hello Jersey";
//	}

}