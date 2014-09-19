package bg.znestorov.sofbus24.entity;

import java.util.Arrays;

/**
 * Notification entity class
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class NotificationEntity {

	private int id;
	private String name;
	private String[] information;
	private boolean isActive;

	public NotificationEntity(String[] information) {
		this.name = information[7];
		this.information = information;
		this.isActive = true;
	}

	public NotificationEntity(String[] information, boolean isActive) {
		this.name = information[7];
		this.information = information;
		this.isActive = isActive;
	}

	public NotificationEntity(int id, String name, String[] information,
			boolean isActive) {
		this.id = id;
		this.name = name;
		this.information = information;
		this.isActive = isActive;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getInformation() {
		return information;
	}

	public void setInformation(String[] information) {
		this.information = information;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tid: " + id + "\n\tname: " + name
				+ "\n\tinformation: " + Arrays.toString(information)
				+ "\n\tisActive: " + isActive + "\n}";
	}

}