package bg.znestorov.android.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import bg.znestorov.android.entity.GmailUser;
import bg.znestorov.android.pojo.AppRole;
import bg.znestorov.android.utils.Utils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

public class GmailUserRegistryDatastore implements GmailUserRegistry {

	private static final String GMAIL_USER_ENTITY = "Gmail Users";
	private static final String GMAIL_USER_NICKNAME = "Nickname";
	private static final String GMAIL_USER_EMAIL = "Email";
	private static final String GMAIL_USER_AUTHORITIES = "Authorities";
	private static final String GMAIL_USER_REGISTRATION_DATE = "Registration Date";
	private static final String GMAIL_USER_LAST_ONLINE_DATE = "Last Online";

	@Override
	public GmailUser findGmailUser(String gmailId) {

		Key key = KeyFactory.createKey(GMAIL_USER_ENTITY, gmailId);
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		try {
			Entity user = datastore.get(key);

			return getGmailUserFromEntity(user);
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	@Override
	public List<GmailUser> findAllGmailUsers() {

		List<GmailUser> gmailUsers = new ArrayList<GmailUser>();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(GMAIL_USER_ENTITY).addSort(
				GMAIL_USER_REGISTRATION_DATE, SortDirection.ASCENDING);
		for (Entity user : datastore.prepare(query).asIterable()) {
			gmailUsers.add(getGmailUserFromEntity(user));
		}

		return gmailUsers;
	}

	@Override
	public Boolean registerGmailUser(GmailUser newUser) {

		return updateGmailUser(newUser, newUser.getLastOnlineDate());
	}

	@Override
	public Boolean updateGmailUser(GmailUser newUser) {

		return updateGmailUser(newUser, Utils.getCurrentDateTime());
	}

	@Override
	public Boolean updateGmailUser(GmailUser newUser, String lastOnlineDate) {

		Key key = KeyFactory.createKey(GMAIL_USER_ENTITY, newUser.getGmailId());

		Entity user = new Entity(key);
		user.setProperty(GMAIL_USER_NICKNAME, newUser.getNickname());
		user.setProperty(GMAIL_USER_EMAIL, newUser.getEmail());
		user.setProperty(GMAIL_USER_REGISTRATION_DATE,
				newUser.getRegistrationDate());
		user.setProperty(GMAIL_USER_LAST_ONLINE_DATE, lastOnlineDate);

		Collection<? extends GrantedAuthority> roles = newUser.getAuthorities();
		long binaryAuthorities = 0;
		for (GrantedAuthority role : roles) {
			binaryAuthorities |= 1 << ((AppRole) role).getBit();
		}

		user.setUnindexedProperty(GMAIL_USER_AUTHORITIES, binaryAuthorities);

		try {
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			datastore.put(user);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Boolean removeGmailUser(String userId) {

		Key key = KeyFactory.createKey(GMAIL_USER_ENTITY, userId);

		try {
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			datastore.delete(key);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get a Gmail user from a datastore entity object
	 * 
	 * @param user
	 *            the datastore entity object
	 * @return the GmailUser object
	 */
	private GmailUser getGmailUserFromEntity(Entity user) {

		long binaryAuthorities = (Long) user
				.getProperty(GMAIL_USER_AUTHORITIES);
		Set<AppRole> roles = EnumSet.noneOf(AppRole.class);

		for (AppRole r : AppRole.values()) {
			if ((binaryAuthorities & (1 << r.getBit())) != 0) {
				roles.add(r);
			}
		}

		GmailUser gmailUser = new GmailUser(user.getKey().getName(),
				(String) user.getProperty(GMAIL_USER_NICKNAME),
				(String) user.getProperty(GMAIL_USER_EMAIL), roles,
				(String) user.getProperty(GMAIL_USER_REGISTRATION_DATE),
				(String) user.getProperty(GMAIL_USER_LAST_ONLINE_DATE));

		return gmailUser;
	}

}