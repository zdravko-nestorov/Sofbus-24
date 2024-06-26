package bg.znestorov.sofbus24.apidb.entity;

import java.util.Objects;

public class Station {

    private String lon;
    private String code;
    private String publicNameEN;
    private String id;
    private String skgtId;
    private String lat;
    private String publicName;
    private boolean metro;

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPublicNameEN() {
        return publicNameEN;
    }

    public void setPublicNameEN(String publicNameEN) {
        this.publicNameEN = publicNameEN;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkgtId() {
        return skgtId;
    }

    public void setSkgtId(String skgtId) {
        this.skgtId = skgtId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public boolean isMetro() {
        return metro;
    }

    public void setMetro(boolean metro) {
        this.metro = metro;
    }

    public String getLabel() {
        return this.publicName + " (" + this.code + ")";
    }

    public VehicleType getType() {

        if (!this.metro) {
            return VehicleType.BTT;
        } else {
            return VehicleType.METRO1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(code, station.code)
            && Objects.equals(id, station.id)
            && Objects.equals(skgtId, station.skgtId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, id);
    }

    @Override
    public String toString() {
        return "Station{" +
                "lon='" + lon + '\'' +
                ", code='" + code + '\'' +
                ", publicNameEN='" + publicNameEN + '\'' +
                ", id='" + id + '\'' +
                ", skgtId='" + skgtId + '\'' +
                ", lat='" + lat + '\'' +
                ", publicName='" + publicName + '\'' +
                ", metro=" + metro +
                '}';
    }

}
