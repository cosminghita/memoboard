package com.espresoh.memoboard.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.espresoh.memoboard.server.model.Memo;
import com.espresoh.memoboard.server.model.MemoAction;
import com.espresoh.memoboard.server.model.MemoAction.MemoActionType;
import com.espresoh.memoboard.server.model.User;
import com.espresoh.memoboard.server.po.HelperPO;
import com.espresoh.memoboard.server.po.MemoPO;
import com.espresoh.memoboard.server.session.EntityManagerBean;
import com.espresoh.memoboard.server.session.MBException;
import com.espresoh.memoboard.server.session.UserSession;
import com.gncsoft.util.DateManipulate;
import com.gncsoft.util.mail.SendMailGmail;
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
@Path("/memoboard")
public class MemoboardService
{

	/**
	 * Active memos in which the user is added.
	 * 
	 * @param userId
	 * @return
	 */
	@GET
	@Path("/activememos/{userId}/")
	@Produces(MediaType.TEXT_PLAIN)
	public String getActiveMemos(@PathParam("userId") int userId)
	{
		EntityManagerBean entityManager = UserSession.getEntityManager(userId);
		return new Gson().toJson(entityManager.getActiveMemos(userId));
	}

	/**
	 * Memos that user created.
	 * 
	 * @param userId
	 * @return
	 */
	@GET
	@Path("/mymemos/{ownerId}/")
	@Produces(MediaType.TEXT_PLAIN)
	public String getMyMemos(@PathParam("ownerId") int ownerId)
	{
		EntityManagerBean entityManager = UserSession.getEntityManager(ownerId);
		return new Gson().toJson(entityManager.getMyMemos(ownerId));
	}

	/**
	 * Memos that user created.
	 * 
	 * @param userId
	 * @return
	 */
	@PUT
	@Path("/addmemo/{userId}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addMemo(@PathParam("userId") int userId, String jsonMemoPO)
	{
		EntityManagerBean entityManager = UserSession.getEntityManager(userId);
		
		try
		{
			MemoPO fromJson = new Gson().fromJson(jsonMemoPO, MemoPO.class);
			fromJson.setOwnerId(userId);
			final Memo savedMemo = entityManager.save(HelperPO.toMemo(fromJson));
			
			//Do not block the call while the emails are sent:
			new Thread(new Runnable() {
				
				@Override
				public void run()
				{
					System.out.println("Sending emails for: " + savedMemo.getContent());
					for (User user : savedMemo.getTargetUsers())
					{
						StringBuilder s = new StringBuilder();
						s.append("New Memo.");
						if (savedMemo.getDueDate() != null)
							s.append(" Due Date: ").append(DateManipulate.formatDate(savedMemo.getDueDate()));
						
						new SendMailGmail(user.getEmail(), s.toString(), savedMemo.getContent(), null).send();
					}
				}
			}).start();

		} catch (Exception e)
		{
			e.printStackTrace();
			return  Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		return Response.ok().build();
	}

	@PUT
	@Path("/savememo/{userId}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response sameMemo(@PathParam("userId") int userId, String jsonMemo)
	{
		EntityManagerBean entityManager = UserSession.getEntityManager(userId);
		
		try
		{
			final Memo fromJson = new Gson().fromJson(jsonMemo, Memo.class);
			final Memo savedMemo = entityManager.save(fromJson);

			//Do not block the call while the emails are sent:
			new Thread(new Runnable() {

				@Override
				public void run()
				{
					System.out.println("Sending emails for: " + savedMemo.getContent());
					for (User user : savedMemo.getTargetUsers())
					{
						StringBuilder s = new StringBuilder();
						if (fromJson.isNewBean())
							s.append("New Memo.");
						else
							s.append("Modified Memo.");

						if (savedMemo.getDueDate() != null)
							s.append(" Due Date: ").append(DateManipulate.formatDate(savedMemo.getDueDate()));

						new SendMailGmail(user.getEmail(), s.toString(), savedMemo.getContent(), null).send();
					}
				}
			}).start();

		} catch (Exception e)
		{
			e.printStackTrace();
			return  Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		return Response.ok().build();
	}

	@PUT
	@Path("/dismiss/{userId}/{memoId}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response dismiss(@PathParam("userId") int userId, @PathParam("memoId") int memoId)
	{
		EntityManagerBean entityManager = UserSession.getEntityManager(userId);
		
		try
		{
			Memo memo = entityManager.getMemo(memoId);
			MemoActionType type = memo.isRequiresConfirmation() ? MemoActionType.CONFIRMED : MemoActionType.DISMISSED;
			MemoAction dismissMemoAction = new MemoAction();
			dismissMemoAction.setType(type);
			dismissMemoAction.setUser(entityManager.getUserLogged());
			dismissMemoAction.setMemo(memo);

			entityManager.save(dismissMemoAction);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			return  Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		return Response.ok().build();
	}

//	@PUT
//	@Path("/confirm/{userId}/{memoId}/")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.TEXT_PLAIN)
//	public Response confirm(@PathParam("userId") int userId, @PathParam("memoId") int memoId)
//	{
//		EntityManagerBean entityManager = UserSession.getEntityManager(userId);
//		
//		try
//		{
//			MemoAction dismissMemoAction = new MemoAction();
//			dismissMemoAction.setType(MemoActionType.CONFIRMED);
//			dismissMemoAction.setUser(entityManager.getUserLogged());
//			dismissMemoAction.setMemo(entityManager.getMemo(memoId));
//
//			entityManager.save(dismissMemoAction);
//			
//		} catch (MBException e)
//		{
//			e.printStackTrace();
//			return  Response.status(Response.Status.BAD_REQUEST).build();
//		}
//		
//		return Response.ok().build();
//	}


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