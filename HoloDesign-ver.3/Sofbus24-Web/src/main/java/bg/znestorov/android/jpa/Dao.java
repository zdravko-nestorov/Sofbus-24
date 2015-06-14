package bg.znestorov.android.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import bg.znestorov.android.entity.Registration;

public enum Dao {
	INSTANCE;

	@SuppressWarnings("unchecked")
	public List<Registration> listRegistrations() {

		List<Registration> registrations;

		// Read the existing entries
		EntityManager em = EMFService.get().createEntityManager();
		Query query = em.createQuery("SELECT r FROM Registration r");

		try {
			registrations = query.getResultList();
		} catch (Exception e) {
			registrations = new ArrayList<Registration>();
		}

		return registrations;
	}

	public List<String> listRegistrationIds() {

		List<String> registrationIds = new ArrayList<String>();

		List<Registration> registrations = listRegistrations();
		if (registrations.size() > 0) {
			for (Registration registration : registrations) {
				registrationIds.add(registration.getRegId());
			}
		}

		return registrationIds;
	}

	public boolean addRegistration(String regId, String deviceModel,
			String deviceOsVersion) {

		boolean isSuccessfullyAdded = false;

		if (getRegistration(regId) == null) {

			synchronized (this) {
				Registration registration = new Registration();
				registration.setRegId(regId);
				registration.setDeviceModel(deviceModel);
				registration.setDeviceOsVersion(deviceOsVersion);

				EntityManager em = EMFService.get().createEntityManager();
				EntityTransaction tx = em.getTransaction();
				try {
					tx.begin();
					em.persist(registration);

					// It is important to flush the object (to save it to the
					// DB) before commit the transaction
					em.flush();
					tx.commit();

					isSuccessfullyAdded = true;
				} catch (Exception e) {
					if (tx.isActive()) {
						tx.rollback();
					}
				} finally {
					em.close();
				}
			}
		}

		return isSuccessfullyAdded;
	}

	@SuppressWarnings("unchecked")
	public Registration getRegistration(String regId) {

		EntityManager em = EMFService.get().createEntityManager();
		Query query = em
				.createQuery("SELECT r FROM Registration r WHERE r.regId = :regId");
		query.setParameter("regId", regId);

		Registration registration;
		try {
			List<Registration> registrationsList = query.getResultList();
			registration = registrationsList != null
					&& registrationsList.size() > 0 ? registrationsList.get(0)
					: null;
		} catch (Exception e) {
			registration = null;
		}

		return registration;
	}

}