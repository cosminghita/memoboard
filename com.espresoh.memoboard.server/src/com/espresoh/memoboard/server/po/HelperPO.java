package com.espresoh.memoboard.server.po;

import java.util.HashSet;
import java.util.Set;

import com.espresoh.memoboard.server.model.Memo;
import com.espresoh.memoboard.server.model.User;
import com.espresoh.memoboard.server.session.UserSession;

public class HelperPO
{

	public static User toUser(UserPO userPO)
	{
		User user = new User();
		user.setEmail(userPO.getEmail());
		user.setName(userPO.getName());
		user.setAvatar(userPO.getAvatar());
		
		return user;
	}
	
	
	public static Memo toMemo(MemoPO memoPO)
	{
		Memo memo = new Memo();
		memo.setContent(memoPO.getContent());
		memo.setTitle(memoPO.getTitle());
		
		Set<User> users = new HashSet<>();
		if (memoPO.getTargetUsers() != null)
			for (Integer targetUserId : memoPO.getTargetUsers())
				users.add(getUserForId(targetUserId, memoPO.getOwnerId()));
		
		memo.setTargetUsers(users);
		
		memo.setRequiresConfirmation(memoPO.isRequiresConfirmation());
		memo.setDueDate(memoPO.getDueDate());
		memo.setOwner(getUserForId(memoPO.getOwnerId(), memoPO.getOwnerId()));
		return memo;
	}


	private static User getUserForId(int userId, int ownerId)
	{
		return UserSession.getEntityManager(ownerId).getUser(userId);
	}

	


}
