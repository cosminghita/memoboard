package com.espresoh.memoboard.server.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.espresoh.memoboard.server.model.Memo;
import com.espresoh.memoboard.server.model.User;
import com.espresoh.memoboard.server.po.HelperPO;
import com.espresoh.memoboard.server.po.UserPO;

/**
 * Caches the entity manager for each user.
 * 
 * @author cghita
 *
 */
public class UserSession
{

	// ==================== 1. Static Fields ========================

	/**
	 * This can be modified by many users at same time.
	 */
	private static Map<Integer, EntityManagerBean> userIdSessionMap = new HashMap<>();
	

	// ==================== 3. Static Methods ====================

	public static synchronized EntityManagerBean getEntityManager(Integer userId)
	{
		//TODO: check if the user id is in the map, otherwise try to get it from DB. If not found in db throw an exception:
		EntityManagerBean entityManagerBean = userIdSessionMap.get(userId);
		if (entityManagerBean == null)
		{
			entityManagerBean = new EntityManagerBean();
			User user = entityManagerBean.getUser(userId);
			entityManagerBean.setUserLogged(user);
			
			
			userIdSessionMap.put(user.getId(), entityManagerBean);


		}
		return entityManagerBean;
	}
	
	
	
	/**
	 * Gives back the entity manager with the user based on email. If the user is not in db, he will be created.
	 * 
	 * @param email
	 * @param name
	 * @return
	 * @throws MBException
	 */
	public static synchronized EntityManagerBean getEntityManager(UserPO userPO) throws MBException
	{
		EntityManagerBean manager = new EntityManagerBean();

		User user = manager.getUser(userPO.getEmail());

		if (user == null)
		{
			HashSet<User> targetUsers = new HashSet<>(manager.getUsers());

			user = manager.save(HelperPO.toUser(userPO));
			
			
			//Notify all users that new user has registered:
			Memo notificationNewUser = new Memo();
			notificationNewUser.setTargetUsers(targetUsers);
			notificationNewUser.setOwner(user);
			notificationNewUser.setTitle("Welcome to MemoBoard");
			notificationNewUser.setContent("Hello to everyboby. I just joined MemoBoard!");
			
			manager.save(notificationNewUser);
			
		}
		//Make sure the name is up to date
		else if (!userPO.getName().equals(user.getName()) )
		{
			user.setName(userPO.getName());
			user = manager.save(user);

		} else if (userPO.getAvatar() != null && !userPO.getAvatar().equals(user.getAvatar()) )
		{
			user.setAvatar(userPO.getAvatar());
			user = manager.save(user);
		}


		manager.setUserLogged(user);

		userIdSessionMap.put(user.getId(), manager);

		return manager;
	}
	
}
