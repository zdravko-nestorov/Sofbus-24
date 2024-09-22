package bg.znestorov.sofbus24.apidb.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Station implements Comparable<Station> {

  private int id;
  private String title;
  private String name;
  private String code;
  private String[] position;
  private int type;
  @SerializedName("ext_id")
  private String extId;

  // -------------------------- //
  // SOFBUS 24 DATABASE FIELDS  //
  // -------------------------- //
  public int getSofbusNumber() {
    return Integer.parseInt(code) + (getSofbusType() == StationType.METRO ? 100000 : 0);
  }

  public String getSofbusName() {
    return name;
  }

  public String getSofbusLatitude() {
    return position[0];
  }

  public String getSofbusLongitude() {
    return position[1];
  }

  public StationType getSofbusType() {
    return StationType.getStationType(type);
  }

  // -------------------------- //
  // SGKT DATABASE FIELDS       //
  // -------------------------- //
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String[] getPosition() {
    return position;
  }

  public void setPosition(String[] position) {
    this.position = position;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getExtId() {
    return extId;
  }

  public void setExtId(String extId) {
    this.extId = extId;
  }

  @Override
  public int compareTo(Station station) {
    return
        // FIRST compare by the station type
        Comparator.comparing(Station::getSofbusType)
            // SECOND compare by the station code
            .thenComparing(Station::getSofbusNumber)
            // LAST compare the station hashes
            .compare(this, station);
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
    return Objects.equals(id, station.id)
        && Objects.equals(code, station.code)
        && Objects.equals(type, station.type)
        && Objects.equals(extId, station.extId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code, type, extId);
  }

  @Override
  public String toString() {
    return "Station{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", name='" + name + '\'' +
        ", code='" + code + '\'' +
        ", position=" + Arrays.toString(position) +
        ", type=" + type +
        ", extId='" + extId + '\'' +
        '}';
  }
}
