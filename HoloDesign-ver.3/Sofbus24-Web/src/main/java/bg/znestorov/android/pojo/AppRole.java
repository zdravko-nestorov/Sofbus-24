package bg.znestorov.android.pojo;

import org.springframework.security.core.GrantedAuthority;

public enum AppRole implements GrantedAuthority {

	ADMIN(0), USER(1);

	private int bit;

	AppRole(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}

	public void setBit(int bit) {
		this.bit = bit;
	}

	public String getAuthority() {
		return toString();
	}

}