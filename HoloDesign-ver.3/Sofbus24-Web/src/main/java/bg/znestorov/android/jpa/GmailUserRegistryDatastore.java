package bg.znestorov.android.jpa;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import bg.znestorov.android.entity.GmailUser;
import bg.znestorov.android.pojo.AppRole;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class GmailUserRegistryDatastore implements GmailUserRegistry {

	private static final String GMAIL_USER_ENTITY = "Gmail Users";
	private static final String GMAIL_USER_NICKNAME = "Nickname";
	private static final String GMAIL_USER_EMAIL = "Email";
	private static final String GMAIL_USER_AUTHORITIES = "Authorities";

	public GmailUser findGmailUser(String gmailId) {

		Key key = KeyFactory.createKey(GMAIL_USER_ENTITY, gmailId);
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		try {
			Entity user = datastore.get(key);

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
					(String) user.getProperty(GMAIL_USER_EMAIL), roles);

			return gmailUser;

		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	public Boolean registerGmailUser(GmailUser newUser) {

		Key key = KeyFactory.createKey(GMAIL_USER_ENTITY, newUser.getGmailId());

		Entity user = new Entity(key);
		user.setProperty(GMAIL_USER_NICKNAME, newUser.getNickname());
		user.setProperty(GMAIL_USER_EMAIL, newUser.getEmail());

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

}