package bg.znestorov.android.jpa;

import java.util.ArrayList;
import java.util.List;

import bg.znestorov.android.entity.PhoneUser;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class PhoneUserRegistryDatastore implements PhoneUserRegistry {

	private static final String PHONE_USER_ENTITY = "Smartphone Users";
	private static final String PHONE_USER_DEVICE_MODEL = "Smartphone Model";
	private static final String PHONE_USER_DEVICE_OS_VERSION = "Android Version";
	private static final String PHONE_USER_TIMESTAMP = "Timestamp";

	@Override
	public PhoneUser findPhoneUser(String regId) {

		Key key = KeyFactory.createKey(PHONE_USER_ENTITY, regId);
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		try {
			Entity user = datastore.get(key);
			PhoneUser phoneUser = new PhoneUser(user.getKey().getName(),
					(String) user.getProperty(PHONE_USER_DEVICE_MODEL),
					(String) user.getProperty(PHONE_USER_DEVICE_OS_VERSION),
					(String) user.getProperty(PHONE_USER_TIMESTAMP));

			return phoneUser;
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	@Override
	public List<PhoneUser> findAllPhoneUsers() {

		List<PhoneUser> phoneUsers = new ArrayList<PhoneUser>();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(PHONE_USER_ENTITY);
		for (Entity user : datastore.prepare(query).asIterable()) {
			PhoneUser phoneUser = new PhoneUser(user.getKey().getName(),
					(String) user.getProperty(PHONE_USER_DEVICE_MODEL),
					(String) user.getProperty(PHONE_USER_DEVICE_OS_VERSION),
					(String) user.getProperty(PHONE_USER_TIMESTAMP));
			phoneUsers.add(phoneUser);
		}

		return phoneUsers;
	}

	@Override
	public List<String> findAllPhoneUserRegistrationIds() {

		List<String> registrationIds = new ArrayList<String>();

		List<PhoneUser> phoneUsers = findAllPhoneUsers();
		if (phoneUsers.size() > 0) {
			for (PhoneUser phoneUser : phoneUsers) {
				registrationIds.add(phoneUser.getRegId());
			}
		}

		return registrationIds;
	}

	@Override
	public Boolean registerPhoneUser(PhoneUser newUser) {

		Key key = KeyFactory.createKey(PHONE_USER_ENTITY, newUser.getRegId());

		Entity user = new Entity(key);
		user.setProperty(PHONE_USER_DEVICE_MODEL, newUser.getDeviceModel());
		user.setProperty(PHONE_USER_DEVICE_OS_VERSION,
				newUser.getDeviceOsVersion());
		user.setProperty(PHONE_USER_TIMESTAMP, newUser.getTimestamp());

		try {
			if (findPhoneUser(newUser.getRegId()) == null) {
				DatastoreService datastore = DatastoreServiceFactory
						.getDatastoreService();
				datastore.put(user);

				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Boolean removePhoneUser(String userId) {

		Key key = KeyFactory.createKey(PHONE_USER_ENTITY, userId);

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