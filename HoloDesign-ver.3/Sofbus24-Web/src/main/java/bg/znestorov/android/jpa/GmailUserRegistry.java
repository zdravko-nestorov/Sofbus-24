package bg.znestorov.android.jpa;

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
	 * Register the Gmail user that has tried to open the GCM system for
	 * notifications
	 * 
	 * @param newUser
	 *            a Gmail user with all properties inside
	 * @return if the user is successfully registered
	 */
	Boolean registerGmailUser(GmailUser newUser);

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