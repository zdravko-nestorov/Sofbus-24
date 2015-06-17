package bg.znestorov.android.jpa;

import java.util.List;

import bg.znestorov.android.entity.PhoneUser;

public interface PhoneUserRegistry {

	/**
	 * Finds a user that has registered his/her device to receive GCM
	 * notifications
	 * 
	 * @param regId
	 *            the registration id of the user, received when its devices has
	 *            registered on GCM
	 * @return a PhoneUser with all properties inside
	 */
	PhoneUser findPhoneUser(String regId);

	/**
	 * Get all users that has registered their devices to receive GCM
	 * notifications
	 * 
	 * @return a list with all PhoneUsers with all their properties inside
	 */
	List<PhoneUser> findAllPhoneUsers();

	/**
	 * Get all users' registration ids that has registered their devices to
	 * receive GCM notifications
	 * 
	 * @return a list with all PhoneUsers registration ids
	 */
	List<String> findAllPhoneUserRegistrationIds();

	/**
	 * Insert a user into the datastore that has already registered his/her
	 * device to receive GCM notifications
	 * 
	 * @param newUser
	 *            a user with all properties inside
	 * @return if the user is successfully registered
	 */
	Boolean registerPhoneUser(PhoneUser newUser);

	/**
	 * Update the user that has already registered his/her device to receive GCM
	 * notifications with his/her last push notification date
	 * 
	 * @param newUser
	 *            a Gmail user with all properties inside
	 * @return if the user is successfully registered
	 */
	Boolean updatePhoneUser(PhoneUser newUser, String lastPushNotificationDate);

	/**
	 * Remove a user from the datastore that has already registered his/her
	 * device to receive GCM notifications via its registration ID
	 * 
	 * @param userId
	 *            the registration id of the user
	 * @return if the user is successfully removed
	 */
	Boolean removePhoneUser(String userId);

}
