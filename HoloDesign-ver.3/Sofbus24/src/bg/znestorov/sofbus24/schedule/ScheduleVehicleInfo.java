package bg.znestorov.sofbus24.schedule;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.PublicTransportStationEntity;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.main.History;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.publictransport.ChooseDirectionDialog;
import bg.znestorov.sofbus24.publictransport.RetrievePublicTransportDirection;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;

/**
 * Class used to retrieve info for a SKGT vehicle (used in Schedule tab (home
 * screen) and History activity)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ScheduleVehicleInfo {

	private Activity context;
	private Object callerInstance;

	private StationsDataSource stationsDatasource;
	private String language;

	public ScheduleVehicleInfo(Activity context, Object callerInstance) {

		this.context = context;
		this.callerInstance = callerInstance;

		this.stationsDatasource = new StationsDataSource(context);
		this.language = LanguageChange.getUserLocale(context);
	}

	/**
	 * Retieve an information about the selected vehicle
	 * 
	 * @param vehicle
	 *            the selected vehicle
	 * @param vehicleTitle
	 *            the vehicle title in format: "xxxxx �xxx"
	 */
	public void onListItemClick(VehicleEntity vehicle, String vehicleTitle) {

		String vehicleNumber = vehicle.getNumber();
		if ("1-��".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity1TB(vehicle));
		} else if ("5-��".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity5TM(vehicle));
		} else if ("6-�".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity6A(vehicle));
		} else if ("10-��".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity10TM(vehicle));
		} else if ("11-��".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity11TM(vehicle));
		} else if ("12-�".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity12A(vehicle));
		} else if ("44-�".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity44B(vehicle));
		} else {
			proceedStandardCase(vehicle, vehicleTitle);
		}
	}

	/**
	 * Retieve an information about the selected vehicle
	 * 
	 * @param scheduleStationAdapter
	 *            the ScheduleVehicleAdapter
	 * @param position
	 *            the position of the selected vehicle
	 */
	public void onListItemClick(ScheduleVehicleAdapter scheduleStationAdapter,
			int position) {

		VehicleEntity vehicle = (VehicleEntity) scheduleStationAdapter
				.getItem(position);
		String rowCaption = scheduleStationAdapter.getVehicleCaption(context,
				vehicle);

		onListItemClick(vehicle, rowCaption);
	}

	/**
	 * Create a direction entity in case the vehicle is a bus with number 1-TB
	 * 
	 * @param vehicle
	 *            the selected vehicle with number 1-TB
	 * @return a direction entity for this tram
	 */
	private DirectionsEntity createDirectionEntity1TB(VehicleEntity vehicle) {
		ArrayList<String> vt = new ArrayList<String>();
		ArrayList<String> lid = new ArrayList<String>();
		ArrayList<String> rid = new ArrayList<String>();

		ArrayList<String> directionsNames = new ArrayList<String>();
		ArrayList<StationEntity> stationsList;
		ArrayList<ArrayList<StationEntity>> directionsList = new ArrayList<ArrayList<StationEntity>>();

		// Direction 1
		vt.add("1");
		lid.add("190");
		rid.add("2238");
		directionsNames
				.add(translateString("���� ������� ������� - ���. �����"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1256", translateString("���� ������� �������"), "28584"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0324", translateString("���. ������� ����� �����"), "3531"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2105", translateString("��. �����"), "3536"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2171", translateString("��.��.��.����� � �������"), "11885"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2111", translateString("��. ��������"), "11895"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0283", translateString("���. ��. ������������"), "14666"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1295", translateString("��.����� ��������"), "14648"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1296", translateString("��.����� ��������"), "22636"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0366", translateString("���. �����"), "22656"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("1");
		lid.add("190");
		rid.add("2237");
		directionsNames
				.add(translateString("���. ����� - ���� ������� �������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0366", translateString("���. �����"), "22656"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0355", translateString("���. ����� ���������"), "22668"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"22680", translateString("������� �������"), "6300"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1300", translateString("��. ����� ��������"), "14649"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0284", translateString("���. ��. ������������"), "14667"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2114", translateString("��. ��������"), "11892"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2170", translateString("��.��.��. ����� � �������"), "11882"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2102", translateString("��. �����"), "28563"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0339", translateString("���. ���. �.��������"), "28580"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2596", translateString("���� ������� �������"), "28583"));
		directionsList.add(stationsList);

		return new DirectionsEntity(vehicle, 0, 0, vt, lid, rid,
				directionsNames, directionsList);
	}

	/**
	 * TODO: Create a direction entity in case the vehicle is a bus with number
	 * 5-TM
	 * 
	 * @param vehicle
	 *            the selected vehicle with number 5-TM
	 * @return a direction entity for this tram
	 */
	private DirectionsEntity createDirectionEntity5TM(VehicleEntity vehicle) {
		ArrayList<String> vt = new ArrayList<String>();
		ArrayList<String> lid = new ArrayList<String>();
		ArrayList<String> rid = new ArrayList<String>();

		ArrayList<String> directionsNames = new ArrayList<String>();
		ArrayList<StationEntity> stationsList;
		ArrayList<ArrayList<StationEntity>> directionsList = new ArrayList<ArrayList<StationEntity>>();

		// Direction 1
		vt.add("");
		lid.add("");
		rid.add("");
		directionsNames.add(translateString(""));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS, "",
				translateString(""), ""));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("");
		lid.add("");
		rid.add("");
		directionsNames.add(translateString(""));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS, "",
				translateString(""), ""));
		directionsList.add(stationsList);

		return new DirectionsEntity(vehicle, 0, 0, vt, lid, rid,
				directionsNames, directionsList);
	}

	/**
	 * Create a direction entity in case the vehicle is a tram with number 6-A
	 * 
	 * @param vehicle
	 *            the selected vehicle with number 6-A
	 * @return a direction entity for this tram
	 */
	private DirectionsEntity createDirectionEntity6A(VehicleEntity vehicle) {
		ArrayList<String> vt = new ArrayList<String>();
		ArrayList<String> lid = new ArrayList<String>();
		ArrayList<String> rid = new ArrayList<String>();

		ArrayList<String> directionsNames = new ArrayList<String>();
		ArrayList<StationEntity> stationsList;
		ArrayList<ArrayList<StationEntity>> directionsList = new ArrayList<ArrayList<StationEntity>>();

		// Direction 1
		vt.add("0");
		lid.add("49");
		rid.add("1316");
		directionsNames
				.add(translateString("�. �. �����-2 - ���� ����� �����"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0679", translateString("�. �. �����-2"), "4405"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0013", translateString("140 ���"), "4479"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0872", translateString("��. �����"), "4467"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0059", translateString("����������� �����"), "4902"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1710", translateString("��������� �-� �����"), "4447"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0593", translateString("�. �. ��������-1"), "4356"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1838", translateString("��. ���� �����"), "4346"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0334", translateString("���. ������ ����"), "5379"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0222", translateString("���. 458 �.�. ������� 4"), "5397"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0019", translateString("24-�� ���"), "5385"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0002", translateString("102-�� ��"), "5398"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1901", translateString("��. ���. ������ �����"), "5391"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"2575", translateString("���. �����"), "5400"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0540", translateString("����� ���� �������"), "3996"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0374", translateString("����� ���� �������"), "3993"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0477", translateString("���� ����� �����"), "3982"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("0");
		lid.add("49");
		rid.add("1317");
		directionsNames.add(translateString("���� ����� ����� - �.�.����� 2"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0476", translateString("���� ����� �����"), "3986"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0375", translateString("����� ���� �������"), "4088"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0547", translateString("���.�����"), "4005"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"2574", translateString("���.���.������ �����"), "5358"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"2578", translateString("102-�� ��"), "5399"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0018", translateString("24-�� ���"), "5364"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0221", translateString("��. 458 �.�. ������� 4"), "5396"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"2576", translateString("���.������ ����"), "5371"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"2577", translateString("��.���� �����"), "4349"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0592", translateString("�.�. ��������"), "4416"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1709", translateString("��������� �������� �����"), "4448"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0060", translateString("����������� �����"), "4458"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0873", translateString("��.�����"), "4468"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0012", translateString("140 ���"), "4478"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0680", translateString("�.�.����� 2"), "4403"));
		directionsList.add(stationsList);

		return new DirectionsEntity(vehicle, 0, 0, vt, lid, rid,
				directionsNames, directionsList);
	}

	/**
	 * Create a direction entity in case the vehicle is a bus with number 10-TM
	 * 
	 * @param vehicle
	 *            the selected vehicle with number 10-TM
	 * @return a direction entity for this bus
	 */
	private DirectionsEntity createDirectionEntity10TM(VehicleEntity vehicle) {
		ArrayList<String> vt = new ArrayList<String>();
		ArrayList<String> lid = new ArrayList<String>();
		ArrayList<String> rid = new ArrayList<String>();

		ArrayList<String> directionsNames = new ArrayList<String>();
		ArrayList<StationEntity> stationsList;
		ArrayList<ArrayList<StationEntity>> directionsList = new ArrayList<ArrayList<StationEntity>>();

		// Direction 1
		vt.add("1");
		lid.add("145");
		rid.add("1910");
		directionsNames
				.add(translateString("����������� ���������� - ����� ������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0064", translateString("����������� ����������"), "13095"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0909", translateString("��.����������"), "13085"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2654", translateString("��.����������"), "17308"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0342", translateString("���.������ ��������"), "17294"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2039", translateString("��.��������"), "25748"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0923", translateString("��������� ����� ���������"), "26999"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2330", translateString("����� �����"), "18120"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0397", translateString("����� ������"), "18115"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("1");
		lid.add("145");
		rid.add("1032");
		directionsNames
				.add(translateString("����� ������ - ����������� ����������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0397", translateString("����� ������"), "18115"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1322", translateString("����� �����"), "25739"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0922", translateString("��������� ����� ���������"), "25756"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2038", translateString("��.��������"), "25747"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0343", translateString("���.������ ��������"), "25751"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2655", translateString("��.����������"), "17311"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0912", translateString("��.����������"), "13084"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0064", translateString("����������� ����������"), "13095"));
		directionsList.add(stationsList);

		return new DirectionsEntity(vehicle, 0, 0, vt, lid, rid,
				directionsNames, directionsList);
	}

	/**
	 * Create a direction entity in case the vehicle is a bus with number 11-TM
	 * 
	 * @param vehicle
	 *            the selected vehicle with number 11-TM
	 * @return a direction entity for this bus
	 */
	private DirectionsEntity createDirectionEntity11TM(VehicleEntity vehicle) {
		ArrayList<String> vt = new ArrayList<String>();
		ArrayList<String> lid = new ArrayList<String>();
		ArrayList<String> rid = new ArrayList<String>();

		ArrayList<String> directionsNames = new ArrayList<String>();
		ArrayList<StationEntity> stationsList;
		ArrayList<ArrayList<StationEntity>> directionsList = new ArrayList<ArrayList<StationEntity>>();

		// Direction 1
		vt.add("1");
		lid.add("208");
		rid.add("1149");
		directionsNames
				.add(translateString("������ ��������� ���� - ����������� �������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1328", translateString("������ ��������� ����"), "15026"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1326", translateString("��.����������"), "15029"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0382", translateString("���.������ �����"), "2844"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2081", translateString("��. ����������"), "2854"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2101", translateString("��. �����"), "16896"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0323", translateString("���. ������� ����� �����"), "16899"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1114", translateString("������ �������"), "6463"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0539", translateString("����� ���� �������"), "16545"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2674", translateString("���.�����"), "16110"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0727", translateString("��� �������"), "16104"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0745", translateString("������� ��"), "16098"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0754", translateString("������ ����� �������"), "16090"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1634", translateString("������ ��������"), "16993"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0938", translateString("������� ��. �������"), "17011"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0058", translateString("����������� �������"), "7868"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("1");
		lid.add("208");
		rid.add("1150");
		directionsNames
				.add(translateString("����������� ������� - ������ ��������� ����"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0058", translateString("����������� �������"), "7868"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0939", translateString("����������� �������"), "17008"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1633", translateString("������ ��������"), "16992"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0755", translateString("������ ����� �������"), "16091"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0744", translateString("������� ��"), "16099"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0728", translateString("��� �������"), "16105"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0546", translateString("���.�����"), "28841"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0542", translateString("����� ���� �������"), "16542"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"2079", translateString("��. ����������"), "6608"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1328", translateString("������ ��������� ����"), "15026"));
		directionsList.add(stationsList);

		return new DirectionsEntity(vehicle, 0, 0, vt, lid, rid,
				directionsNames, directionsList);
	}

	/**
	 * Create a direction entity in case the vehicle is a bus with number 12-A
	 * 
	 * @param vehicle
	 *            the selected vehicle with number 12-A
	 * @return a direction entity for this bus
	 */
	private DirectionsEntity createDirectionEntity12A(VehicleEntity vehicle) {
		ArrayList<String> vt = new ArrayList<String>();
		ArrayList<String> lid = new ArrayList<String>();
		ArrayList<String> rid = new ArrayList<String>();

		ArrayList<String> directionsNames = new ArrayList<String>();
		ArrayList<StationEntity> stationsList;
		ArrayList<ArrayList<StationEntity>> directionsList = new ArrayList<ArrayList<StationEntity>>();

		// Direction 1
		vt.add("0");
		lid.add("52");
		rid.add("607");
		directionsNames.add(translateString("��.������� - ���� ����� �����"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0848", translateString("��.�������"), "4145"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1988", translateString("��.����� �������"), "4135"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1916", translateString("��.�������"), "4123"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1635", translateString("������ ��������"), "4101"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0755", translateString("������ ����� �������"), "4036"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0744", translateString("������� ��"), "4030"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0728", translateString("��� �������"), "4022"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0546", translateString("���.�����"), "4008"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0540", translateString("����� ���� �������"), "3996"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0374", translateString("����� ���� �������"), "3993"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0477", translateString("���� ����� �����"), "3982"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("0");
		lid.add("52");
		rid.add("608");
		directionsNames.add(translateString("���� ����� ����� - ��.�������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0476", translateString("���� ����� �����"), "3986"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0375", translateString("����� ���� �������"), "4088"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0547", translateString("���.�����"), "4005"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0727", translateString("��� �������"), "4025"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0745", translateString("������� ��"), "4031"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0754", translateString("������ ����� �������"), "4039"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1636", translateString("������ ��������"), "4104"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1915", translateString("��.�������"), "4124"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"1987", translateString("��.����� �������"), "4136"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.TRAM,
				"0847", translateString("��.�������"), "4150"));
		directionsList.add(stationsList);

		return new DirectionsEntity(vehicle, 0, 0, vt, lid, rid,
				directionsNames, directionsList);
	}

	/**
	 * Create a direction entity in case the vehicle is a bus with number 44-B
	 * 
	 * @param vehicle
	 *            the selected vehicle with number 44-B
	 * @return a direction entity for this bus
	 */
	private DirectionsEntity createDirectionEntity44B(VehicleEntity vehicle) {
		ArrayList<String> vt = new ArrayList<String>();
		ArrayList<String> lid = new ArrayList<String>();
		ArrayList<String> rid = new ArrayList<String>();

		ArrayList<String> directionsNames = new ArrayList<String>();
		ArrayList<StationEntity> stationsList;
		ArrayList<ArrayList<StationEntity>> directionsList = new ArrayList<ArrayList<StationEntity>>();

		// Direction 1
		vt.add("1");
		lid.add("204");
		rid.add("2184");
		directionsNames
				.add(translateString("��. �������� - ���������� ���������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0832", translateString("��. ��������"), "23665"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"6374", translateString("��.�������-��������"), "28966"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"6351", translateString("���� �� ��.���������"), "22013"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1991", translateString("��. �������"), "22008"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0869", translateString("��. �������"), "22016"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1920", translateString("��. ���� �����"), "22020"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0445", translateString("��. �. ������������"), "22028"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1673", translateString("���������� ���������"), "22034"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("1");
		lid.add("204");
		rid.add("2183");
		directionsNames
				.add(translateString("���������� ��������� - ��. ��������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1674", translateString("���������� ���������"), "22033"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0446", translateString("��. ����� ������������"), "22027"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1921", translateString("��. ���� �����"), "22019"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0870", translateString("��. �������"), "22015"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"1992", translateString("��. �������"), "22007"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0982", translateString("���� �� ��.���������"), "22006"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"6375", translateString("��.�������-��������"), "28919"));
		stationsList.add(createPublicTransportStation(VehicleTypeEnum.BUS,
				"0832", translateString("��. ��������"), "23665"));
		directionsList.add(stationsList);

		return new DirectionsEntity(vehicle, 0, 0, vt, lid, rid,
				directionsNames, directionsList);
	}

	/**
	 * Translate the string if needed
	 * 
	 * @param input
	 *            the input string
	 * @return the translated string (if needed)
	 */
	private String translateString(String input) {
		String output;
		if (!"bg".equals(language)) {
			output = TranslatorCyrillicToLatin.translate(context, input);
		} else {
			output = input;
		}

		return output;
	}

	/**
	 * Create a PublicTransportStationEntity using the given number, name and id
	 * 
	 * @param number
	 *            the station number
	 * @param name
	 *            the station name
	 * @param id
	 *            the station id
	 * @return the PublicTransportStation entity
	 */
	private PublicTransportStationEntity createPublicTransportStation(
			VehicleTypeEnum type, String number, String name, String id) {
		stationsDatasource.open();

		StationEntity station = stationsDatasource.getStation(number);
		if (station == null) {
			station = new StationEntity();
		}
		station.setNumber(number);
		station.setName(name);
		station.setType(type);

		stationsDatasource.close();

		return new PublicTransportStationEntity(station, id);
	}

	/**
	 * Retrieve information about the selected vehicle in the special cases
	 * 
	 * @param ptDirectionsEntity
	 *            the DirectionEntity in the special case
	 */
	private void proceedSpecialCase(DirectionsEntity ptDirectionsEntity) {
		// Get the fragment manager to start the dialog fragment
		FragmentManager fragmentManager;
		if (callerInstance instanceof ScheduleVehicleFragment) {
			fragmentManager = ((ScheduleVehicleFragment) callerInstance)
					.getChildFragmentManager();
		} else {
			fragmentManager = ((History) callerInstance)
					.getSupportFragmentManager();
		}

		DialogFragment dialogFragment = ChooseDirectionDialog
				.newInstance(ptDirectionsEntity);
		dialogFragment.show(fragmentManager, "dialog");
	}

	/**
	 * Retrieve information about the selected vehicle in the standard cases
	 * 
	 * @param vehicle
	 *            the selected vehicle
	 * @param rowCaption
	 *            the selectedrow caption
	 */
	private void proceedStandardCase(VehicleEntity vehicle, String rowCaption) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(Html.fromHtml(String.format(
				context.getString(R.string.pt_item_loading_schedule),
				rowCaption)));
		RetrievePublicTransportDirection retrievePublicTransportDirection = new RetrievePublicTransportDirection(
				context, callerInstance, progressDialog, vehicle);
		retrievePublicTransportDirection.execute();
	}

}