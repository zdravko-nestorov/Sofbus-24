package bg.znestorov.android.jpa;

import java.util.List;

import bg.znestorov.android.entity.GmailUser;

public interface GmailUserRegistry {

	/**
	 * Find a user via its Gmail id (received by the system on login)
	 * 
	 * @param gmailId
	 *            the Gmail id of the user
	 * @return a GmailUser profile with all properties inside
	 */
	GmailUser findGmailUser(String gmailId);

	/**
	 * Get all Gmail users that has tried to open the GCM system for
	 * notifications
	 * 
	 * @return a list with all GmailUser with all their properties inside
	 */
	List<GmailUser> findAllGmailUsers();

	/**
	 * Register the Gmail user that has tried to open the GCM system for
	 * notifications
	 * 
	 * @param newUser
	 *            a Gmail user with all properties inside
	 * @return if the user is successfully registered
	 */
	Boolean registerGmailUser(GmailUser newUser);

	/**
	 * Update the Gmail user that has tried to open the GCM system for
	 * notifications with his/her last visit date
	 * 
	 * @param newUser
	 *            a Gmail user with all properties inside
	 * @return if the user is successfully registered
	 */
	Boolean updateGmailUser(GmailUser newUser, String lastOnlineDate);

	/**
	 * Remove a Gmail user that has tried to open the GCM system for
	 * notifications via its id
	 * 
	 * @param userId
	 *            the Gmail id of the user
	 * @return if the user is successfully removed
	 */
	Boolean removeGmailUser(String userId);

}