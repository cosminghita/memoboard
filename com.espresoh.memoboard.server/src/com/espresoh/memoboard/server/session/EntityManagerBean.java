package com.espresoh.memoboard.server.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import com.espresoh.memoboard.server.model.BaseEntity;
import com.espresoh.memoboard.server.model.Group;
import com.espresoh.memoboard.server.model.Memo;
import com.espresoh.memoboard.server.model.MemoAction;
import com.espresoh.memoboard.server.model.User;
import com.gncsoft.util.GzipUtil;

/**
 * Se creeaza pentru fiecare sesiune de utilizator.
 * 
 * @author cghita
 * 
 */
public class EntityManagerBean {

	// ====================== 2. Instance Fields =============================

	private ConnectionDetails connectionDetails;
	private User userLogged;
	
	private Date lastPing;

	@PersistenceUnit(unitName="memoboardPU")
	private EntityManager entityManager;


	// ==================== 4. Constructors ====================

	public EntityManagerBean()
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("memoboardPU");
		entityManager = emf.createEntityManager();
		
		lastPing = new Date();
	}


	public EntityManager getEntityManager()
	{
		return entityManager;
	}


	public ConnectionDetails getConnectionDetails()
	{
		return connectionDetails;
	}


	public Date getLastPing()
	{
		return lastPing;
	}
	
	public void setLastPing(Date lastPing)
	{
		this.lastPing = lastPing;
	}
	
	public User getUserLogged()
	{
		return userLogged;
	}

	public void setUserLogged(final User userLogged)
	{
		this.userLogged = userLogged;
	}


	// ==================== Basic actions ====================

	public GzipUtil<BaseEntity> saveCompressed(final GzipUtil<? extends BaseEntity> zip) throws MBException
	{

		if (zip != null)
		{
			final BaseEntity saved = save(zip.getValue());
			return new GzipUtil<BaseEntity>(saved);
		}

		return new GzipUtil<BaseEntity>();
	}


	public <T extends BaseEntity> T save(final T entity) throws MBException
	{

		// System.out.println("[" + getUserLogged().getUsername() + " - " + DateManipulate.formatDateTimeRO(Calendar.getInstance()) + "] SAVE: " +
		// entity.toString());

		// if(BeanManager.getInstance().getUtilizator().isGuest())
		// throw new BaseException("Utilizatorul nu are drepturi de modificare!!!", null);

		if (!entity.isSavable())
			throw new MBException("Salvarea nu poate fi efectuata (NOT SAVABLE!)!", null);

		if (!entity.isValid())
			throw new MBException("Date incomplete! Salvarea nu poate fi efectuata!", null);

		// if (entity instanceof Dosar && !BeanManager.getInstance().getUtilizator().canEdit((Dosar) entity))
		// throw new BaseException("Nu aveti drepturi de modificare asupra acestui dosar!", null);
		//
		// if (entity instanceof Osim && !BeanManager.getInstance().getUtilizator().canEditOSIM())
		// throw new BaseException("Nu aveti drepturi de modificare in OSIM!", null);

		try
		{
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();

			entityManager.getTransaction().begin();
			final T ent = entityManager.merge(entity);
			entityManager.getTransaction().commit();

			return ent;

		} finally
		{
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();

		}
	}


	public void saveFast(final BaseEntity entity) throws MBException
	{
		save(entity);
	}


	public void saveFastCompressed(final GzipUtil<? extends BaseEntity> zip) throws MBException
	{
		save(zip.getValue());
	}


	public void delete(final BaseEntity entity) throws MBException
	{
		// if (entity instanceof Dosar && !BeanManager.getInstance().getUtilizator().canEdit((Dosar) entity))
		// throw new BaseException("Nu aveti drepturi de modificare asupra acestui dosar!", null);

		try
		{
			final BaseEntity e = entityManager.find(entity.getClass(), entity.getId());
			entityManager.getTransaction().begin();
			entityManager.remove(e);
			entityManager.getTransaction().commit();

		} catch (final Exception e)
		{
			throw new MBException("Stergerea nu a fost posibila! ATENTIE: stergerea din baza de date nu poate fi efectuata daca are referinte!", e);
		} finally
		{
			
			if(entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().rollback();
			}
			
		}
	}


	// ==================== CREDENTIALS ====================

	public User getUser(int userId)
	{
		return entityManager.find(User.class, userId);
		
//		try
//		{
//			final Query query = entityManager.createQuery("SELECT u FROM User AS u WHERE u.id=:userId");
//			query.setParameter("userId", userId);
//			
//			return (User) query.getSingleResult();
//		} catch (final NoResultException e)
//		{
//			// e.printStackTrace();
//		}
//		return null;
//	
	}


	public User getUser(final String email)
	{
		try
		{
			final Query query = entityManager.createQuery("SELECT u FROM User AS u WHERE u.email=:email");
			query.setParameter("email", email);
			
			return (User) query.getSingleResult();
		} catch (final NoResultException e)
		{
			// e.printStackTrace();
		}
		return null;
	}


	public List<User> getUsers()
	{
		final Query query = entityManager.createQuery("SELECT u FROM User AS u ORDER BY u.name");
		return query.getResultList();
	}
	
	public List<Group> getGroups()
	{
		final Query query = entityManager.createQuery("SELECT g FROM Group AS g ORDER BY g.name");
		return query.getResultList();
	}

	
	public Memo getMemo(int memoId)
	{
		return entityManager.find(Memo.class, memoId);
	}

	/**
	 * The active memos in which the user is added.
	 * 
	 * @param userId
	 * @return
	 */
	public List<Memo> getActiveMemos(int userId)
	{
		final Query query = entityManager.createQuery("SELECT m FROM Memo AS m JOIN m.targetUsers as u "
															+ "WHERE u.id=:userId AND m NOT IN (SELECT a.memo FROM MemoAction AS a WHERE a.user.id=:userId) ORDER BY m.startDate DESC");
		query.setParameter("userId", userId);
		
		return calculateProgress(query.getResultList());
	}

	public List<Memo> getMyMemos(int ownerId)
	{
//		final Query query = entityManager.createQuery("SELECT u FROM Memo AS u WHERE u.owner.id=:ownerId");
//		query.setParameter("ownerId", userId);
		
		final Query query = entityManager.createQuery("SELECT m FROM Memo AS m "
				+ "WHERE m.owner.id=:ownerId AND m NOT IN (SELECT a.memo FROM MemoAction AS a WHERE a.user.id=:ownerId) ORDER BY m.startDate DESC");
		query.setParameter("ownerId", ownerId);

		return calculateProgress(query.getResultList());
	}
	
	private List<Memo> calculateProgress(List<Memo> memoList)
	{
		for (Memo memo : memoList)
		{
			List<User> confirmedUsers = new ArrayList<>();
			for (MemoAction action : getProgress(memo.getId()) )
				confirmedUsers.add(action.getUser());
			
			memo.setConfirmedUsers(confirmedUsers);
		}
		 return memoList;
	}

	public List<MemoAction> getProgress(int memoId)
	{
		final Query query = entityManager.createQuery("SELECT a FROM MemoAction AS a WHERE a.memo.id=:memoId");
		query.setParameter("memoId", memoId);
		return query.getResultList();
	}

	

}
