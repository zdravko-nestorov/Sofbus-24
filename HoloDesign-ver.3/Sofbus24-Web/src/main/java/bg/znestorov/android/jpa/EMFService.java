package bg.znestorov.android.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMFService {

	private static final EntityManagerFactory emfInstance = Persistence
			.createEntityManagerFactory("gae-sofbus");

	private EMFService() {
	}

	public static EntityManagerFactory get() {
		return emfInstance;
	}
}