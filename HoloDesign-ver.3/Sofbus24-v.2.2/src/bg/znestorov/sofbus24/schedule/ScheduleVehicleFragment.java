package bg.znestorov.sofbus24.schedule;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.PublicTransportStationEntity;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleEntity;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.publictransport.ChooseDirectionDialog;
import bg.znestorov.sofbus24.publictransport.RetrievePublicTransportDirection;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.TranslatorCyrillicToLatin;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.utils.activity.DrawableClickListener;
import bg.znestorov.sofbus24.utils.activity.SearchEditText;

/**
 * Schedule Vehiles Fragment containing information about the public transport
 * vehicles
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ScheduleVehicleFragment extends ListFragment implements
		OnItemClickListener {

	private Activity context;
	private StationsDataSource stationsDatasource;
	private String language;

	private SearchEditText searchEditText;
	private GridView gridViewScheduleVehicles;
	private View emptyView;
	private TextView emptyTextView;

	private int currentVehicle;
	private ScheduleLoadVehicles slv;

	private ScheduleVehicleAdapter scheduleVehicleAdapter;
	private ArrayList<VehicleEntity> stationsList = new ArrayList<VehicleEntity>();

	private String searchText;
	private static final String BUNDLE_SEARCH_TEXT = "SEARCH TEXT";

	public static ScheduleVehicleFragment newInstance(int currentVehicle) {
		ScheduleVehicleFragment scheduleStationFragment = new ScheduleVehicleFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE,
				currentVehicle);
		scheduleStationFragment.setArguments(bundle);

		return scheduleStationFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(
				R.layout.activity_schedule_vehicle_fragment, container, false);

		// Set the context (activity) associated with this fragment
		context = getActivity();
		stationsDatasource = new StationsDataSource(context);
		language = LanguageChange.getUserLocale(context);

		// Get the needed fragment information
		initInformation(savedInstanceState);

		// Find all of TextView and SearchEditText tabs in the layout
		initLayoutFields(fragmentView);

		return fragmentView;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString(BUNDLE_SEARCH_TEXT, searchText);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ScheduleVehicleAdapter scheduleStationAdapter = (ScheduleVehicleAdapter) getListAdapter();
		onListItemClick(scheduleStationAdapter, position);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		ScheduleVehicleAdapter scheduleStationAdapter = (ScheduleVehicleAdapter) gridViewScheduleVehicles
				.getAdapter();
		onListItemClick(scheduleStationAdapter, position);
	}

	/**
	 * Retieve an information about the selected vehicle
	 * 
	 * @param scheduleStationAdapter
	 *            the ScheduleVehicleAdapter
	 * @param position
	 *            the position of the selected vehicle
	 */
	private void onListItemClick(ScheduleVehicleAdapter scheduleStationAdapter,
			int position) {
		VehicleEntity vehicle = (VehicleEntity) scheduleStationAdapter
				.getItem(position);
		String rowCaption = scheduleStationAdapter.getVehicleCaption(context,
				vehicle);

		String vehicleNumber = vehicle.getNumber();
		if ("6-�".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity6A(vehicle));
		} else if ("12-�".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity12A(vehicle));
		} else if ("10-��".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity10TM(vehicle));
		} else if ("11-��".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity11TM(vehicle));
		} else if ("21-22".equals(vehicleNumber)) {
			vehicle.setNumber("22");
			proceedStandardCase(vehicle, rowCaption);
		} else if ("44-�".equals(vehicleNumber)) {
			proceedSpecialCase(createDirectionEntity44B(vehicle));
		} else {
			proceedStandardCase(vehicle, rowCaption);
		}
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
		stationsList.add(createPublicTransportStation("0679",
				translateString("�. �. �����-2"), "4405"));
		stationsList.add(createPublicTransportStation("0013",
				translateString("140 ���"), "4479"));
		stationsList.add(createPublicTransportStation("0872",
				translateString("��. �����"), "4467"));
		stationsList.add(createPublicTransportStation("0059",
				translateString("����������� �����"), "4902"));
		stationsList.add(createPublicTransportStation("1710",
				translateString("��������� �-� �����"), "4447"));
		stationsList.add(createPublicTransportStation("0593",
				translateString("�. �. ��������-1"), "4356"));
		stationsList.add(createPublicTransportStation("1838",
				translateString("��. ���� �����"), "4346"));
		stationsList.add(createPublicTransportStation("0334",
				translateString("���. ������ ����"), "5379"));
		stationsList.add(createPublicTransportStation("0222",
				translateString("���. 458 �.�. ������� 4"), "5397"));
		stationsList.add(createPublicTransportStation("0019",
				translateString("24-�� ���"), "5385"));
		stationsList.add(createPublicTransportStation("0002",
				translateString("102-�� ��"), "5398"));
		stationsList.add(createPublicTransportStation("1901",
				translateString("��. ���. ������ �����"), "5391"));
		stationsList.add(createPublicTransportStation("2575",
				translateString("���. �����"), "5400"));
		stationsList.add(createPublicTransportStation("0540",
				translateString("����� ���� �������"), "3996"));
		stationsList.add(createPublicTransportStation("0374",
				translateString("����� ���� �������"), "3993"));
		stationsList.add(createPublicTransportStation("0477",
				translateString("���� ����� �����"), "3982"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("0");
		lid.add("49");
		rid.add("1317");
		directionsNames.add(translateString("���� ����� ����� - �.�.����� 2"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("0476",
				translateString("���� ����� �����"), "3986"));
		stationsList.add(createPublicTransportStation("0375",
				translateString("����� ���� �������"), "4088"));
		stationsList.add(createPublicTransportStation("0547",
				translateString("���.�����"), "4005"));
		stationsList.add(createPublicTransportStation("2574",
				translateString("���.���.������ �����"), "5358"));
		stationsList.add(createPublicTransportStation("2578",
				translateString("102-�� ��"), "5399"));
		stationsList.add(createPublicTransportStation("0018",
				translateString("24-�� ���"), "5364"));
		stationsList.add(createPublicTransportStation("0221",
				translateString("��. 458 �.�. ������� 4"), "5396"));
		stationsList.add(createPublicTransportStation("2576",
				translateString("���.������ ����"), "5371"));
		stationsList.add(createPublicTransportStation("2577",
				translateString("��.���� �����"), "4349"));
		stationsList.add(createPublicTransportStation("0592",
				translateString("�.�. ��������"), "4416"));
		stationsList.add(createPublicTransportStation("1709",
				translateString("��������� �������� �����"), "4448"));
		stationsList.add(createPublicTransportStation("0060",
				translateString("����������� �����"), "4458"));
		stationsList.add(createPublicTransportStation("0873",
				translateString("��.�����"), "4468"));
		stationsList.add(createPublicTransportStation("0012",
				translateString("140 ���"), "4478"));
		stationsList.add(createPublicTransportStation("0680",
				translateString("�.�.����� 2"), "4403"));
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
		stationsList.add(createPublicTransportStation("0848",
				translateString("��.�������"), "4145"));
		stationsList.add(createPublicTransportStation("1988",
				translateString("��.����� �������"), "4135"));
		stationsList.add(createPublicTransportStation("1916",
				translateString("��.�������"), "4123"));
		stationsList.add(createPublicTransportStation("1635",
				translateString("������ ��������"), "4101"));
		stationsList.add(createPublicTransportStation("0755",
				translateString("������ ����� �������"), "4036"));
		stationsList.add(createPublicTransportStation("0744",
				translateString("������� ��"), "4030"));
		stationsList.add(createPublicTransportStation("0728",
				translateString("��� �������"), "4022"));
		stationsList.add(createPublicTransportStation("0546",
				translateString("���.�����"), "4008"));
		stationsList.add(createPublicTransportStation("0540",
				translateString("����� ���� �������"), "3996"));
		stationsList.add(createPublicTransportStation("0374",
				translateString("����� ���� �������"), "3993"));
		stationsList.add(createPublicTransportStation("0477",
				translateString("���� ����� �����"), "3982"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("0");
		lid.add("52");
		rid.add("608");
		directionsNames.add(translateString("���� ����� ����� - ��.�������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("0476",
				translateString("���� ����� �����"), "3986"));
		stationsList.add(createPublicTransportStation("0375",
				translateString("����� ���� �������"), "4088"));
		stationsList.add(createPublicTransportStation("0547",
				translateString("���.�����"), "4005"));
		stationsList.add(createPublicTransportStation("0727",
				translateString("��� �������"), "4025"));
		stationsList.add(createPublicTransportStation("0745",
				translateString("������� ��"), "4031"));
		stationsList.add(createPublicTransportStation("0754",
				translateString("������ ����� �������"), "4039"));
		stationsList.add(createPublicTransportStation("1636",
				translateString("������ ��������"), "4104"));
		stationsList.add(createPublicTransportStation("1915",
				translateString("��.�������"), "4124"));
		stationsList.add(createPublicTransportStation("1987",
				translateString("��.����� �������"), "4136"));
		stationsList.add(createPublicTransportStation("0847",
				translateString("��.�������"), "4150"));
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
		stationsList.add(createPublicTransportStation("0064",
				translateString("����������� ����������"), "13095"));
		stationsList.add(createPublicTransportStation("0909",
				translateString("��.����������"), "13085"));
		stationsList.add(createPublicTransportStation("2654",
				translateString("��.����������"), "17308"));
		stationsList.add(createPublicTransportStation("0342",
				translateString("���.������ ��������"), "17294"));
		stationsList.add(createPublicTransportStation("2039",
				translateString("��.��������"), "25748"));
		stationsList.add(createPublicTransportStation("0923",
				translateString("��������� ����� ���������"), "26999"));
		stationsList.add(createPublicTransportStation("2330",
				translateString("����� �����"), "18120"));
		stationsList.add(createPublicTransportStation("0397",
				translateString("����� ������"), "18115"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("1");
		lid.add("145");
		rid.add("1032");
		directionsNames
				.add(translateString("����� ������ - ����������� ����������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("0397",
				translateString("����� ������"), "18115"));
		stationsList.add(createPublicTransportStation("1322",
				translateString("����� �����"), "25739"));
		stationsList.add(createPublicTransportStation("0922",
				translateString("��������� ����� ���������"), "25756"));
		stationsList.add(createPublicTransportStation("2038",
				translateString("��.��������"), "25747"));
		stationsList.add(createPublicTransportStation("0343",
				translateString("���.������ ��������"), "25751"));
		stationsList.add(createPublicTransportStation("2655",
				translateString("��.����������"), "17311"));
		stationsList.add(createPublicTransportStation("0912",
				translateString("��.����������"), "13084"));
		stationsList.add(createPublicTransportStation("0064",
				translateString("����������� ����������"), "13095"));
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
		stationsList.add(createPublicTransportStation("1328",
				translateString("������ ��������� ����"), "15026"));
		stationsList.add(createPublicTransportStation("1326",
				translateString("��.����������"), "15029"));
		stationsList.add(createPublicTransportStation("0382",
				translateString("���.������ �����"), "2844"));
		stationsList.add(createPublicTransportStation("2081",
				translateString("��. ����������"), "2854"));
		stationsList.add(createPublicTransportStation("2101",
				translateString("��. �����"), "16896"));
		stationsList.add(createPublicTransportStation("0323",
				translateString("���. ������� ����� �����"), "16899"));
		stationsList.add(createPublicTransportStation("1114",
				translateString("������ �������"), "6463"));
		stationsList.add(createPublicTransportStation("0539",
				translateString("����� ���� �������"), "16545"));
		stationsList.add(createPublicTransportStation("2674",
				translateString("���.�����"), "16110"));
		stationsList.add(createPublicTransportStation("0727",
				translateString("��� �������"), "16104"));
		stationsList.add(createPublicTransportStation("0745",
				translateString("������� ��"), "16098"));
		stationsList.add(createPublicTransportStation("0754",
				translateString("������ ����� �������"), "16090"));
		stationsList.add(createPublicTransportStation("1634",
				translateString("������ ��������"), "16993"));
		stationsList.add(createPublicTransportStation("0938",
				translateString("������� ��. �������"), "17011"));
		stationsList.add(createPublicTransportStation("0058",
				translateString("����������� �������"), "7868"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("1");
		lid.add("208");
		rid.add("1150");
		directionsNames
				.add(translateString("����������� ������� - ������ ��������� ����"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("0058",
				translateString("����������� �������"), "7868"));
		stationsList.add(createPublicTransportStation("0939",
				translateString("����������� �������"), "17008"));
		stationsList.add(createPublicTransportStation("1633",
				translateString("������ ��������"), "16992"));
		stationsList.add(createPublicTransportStation("0755",
				translateString("������ ����� �������"), "16091"));
		stationsList.add(createPublicTransportStation("0744",
				translateString("������� ��"), "16099"));
		stationsList.add(createPublicTransportStation("0728",
				translateString("��� �������"), "16105"));
		stationsList.add(createPublicTransportStation("0546",
				translateString("���.�����"), "28841"));
		stationsList.add(createPublicTransportStation("0542",
				translateString("����� ���� �������"), "16542"));
		stationsList.add(createPublicTransportStation("2079",
				translateString("��. ����������"), "6608"));
		stationsList.add(createPublicTransportStation("1328",
				translateString("������ ��������� ����"), "15026"));
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
		rid.add("1874");
		directionsNames
				.add(translateString("����������� ����� - ��. ��������"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("0050",
				translateString("����������� �����"), "22111"));
		stationsList.add(createPublicTransportStation("0503",
				translateString("������� �����"), "21906"));
		stationsList.add(createPublicTransportStation("0965",
				translateString("��. ��������"), "22054"));
		stationsList.add(createPublicTransportStation("1433",
				translateString("��. ������"), "22045"));
		stationsList.add(createPublicTransportStation("1425",
				translateString("������ ��. ���������"), "22039"));
		stationsList.add(createPublicTransportStation("1674",
				translateString("���������� ���������"), "22033"));
		stationsList.add(createPublicTransportStation("0446",
				translateString("��. ����� ������������"), "22027"));
		stationsList.add(createPublicTransportStation("1921",
				translateString("��. ���� �����"), "22019"));
		stationsList.add(createPublicTransportStation("0870",
				translateString("��. �������"), "22015"));
		stationsList.add(createPublicTransportStation("1992",
				translateString("��. �������"), "22007"));
		stationsList.add(createPublicTransportStation("0982",
				translateString("���� �� �. ���������"), "22006"));
		stationsList.add(createPublicTransportStation("0832",
				translateString("��. ��������"), "23665"));
		directionsList.add(stationsList);

		// Direction 2
		vt.add("1");
		lid.add("145");
		rid.add("1982");
		directionsNames
				.add(translateString("��. �������� - ����������� �����"));
		stationsList = new ArrayList<StationEntity>();
		stationsList.add(createPublicTransportStation("0832",
				translateString("��. ��������"), "23665"));
		stationsList.add(createPublicTransportStation("6351",
				translateString("���� �� �. ���������"), "22013"));
		stationsList.add(createPublicTransportStation("1991",
				translateString("��.�������"), "22008"));
		stationsList.add(createPublicTransportStation("0869",
				translateString("��.�������"), "22016"));
		stationsList.add(createPublicTransportStation("1920",
				translateString("��.���� �����"), "22020"));
		stationsList.add(createPublicTransportStation("0445",
				translateString("��.����� ������������"), "22028"));
		stationsList.add(createPublicTransportStation("1673",
				translateString("���������� ���������"), "22034"));
		stationsList.add(createPublicTransportStation("1424",
				translateString("������ ��.���������"), "22040"));
		stationsList.add(createPublicTransportStation("1432",
				translateString("��.������"), "22046"));
		stationsList.add(createPublicTransportStation("0964",
				translateString("��.��������"), "22055"));
		stationsList.add(createPublicTransportStation("0502",
				translateString("������� �����"), "21907"));
		stationsList.add(createPublicTransportStation("0051",
				translateString("����������� �����"), "22109"));
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
			String number, String name, String id) {
		stationsDatasource.open();

		StationEntity station = stationsDatasource.getStation(number);
		if (station == null) {
			station = new StationEntity();
		}
		station.setNumber(number);
		station.setName(name);

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
		DialogFragment dialogFragment = ChooseDirectionDialog
				.newInstance(ptDirectionsEntity);
		dialogFragment.show(getChildFragmentManager(), "dialog");
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
				getString(R.string.pt_item_loading_schedule), rowCaption)));
		RetrievePublicTransportDirection retrievePublicTransportDirection = new RetrievePublicTransportDirection(
				context, this, progressDialog, vehicle);
		retrievePublicTransportDirection.execute();

	}

	/**
	 * Initialize the ScheduleLoadVehicles object and all the data from the
	 * SavedInstanceState object
	 * 
	 * @param savedInstanceState
	 *            object containing the state of the saved values
	 */
	private void initInformation(Bundle savedInstanceState) {
		// Get the values from the Bundle
		if (savedInstanceState != null) {
			searchText = savedInstanceState.getString(BUNDLE_SEARCH_TEXT);
		} else {
			searchText = "";
		}

		// Get the current vehicle from the Bundle
		currentVehicle = getArguments().getInt(
				Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE);

		// Get the information about the current direction
		slv = ScheduleLoadVehicles.getInstance(context);
		stationsList = slv.getVehiclesList(currentVehicle);
	}

	/**
	 * Initialize the layout fields and assign the appropriate listeners over
	 * them (vehicles tabs (TextViews), SerachEditText and EmptyList (TextView))
	 * 
	 * @param fragmentView
	 *            the current view of the fragment
	 */
	private void initLayoutFields(View fragmentView) {
		searchEditText = (SearchEditText) fragmentView
				.findViewById(R.id.schedule_vehicle_search);
		emptyView = fragmentView
				.findViewById(R.id.schedule_vehicle_list_empty_view);
		emptyTextView = (TextView) fragmentView
				.findViewById(R.id.schedule_vehicle_list_empty_text);

		// Set on click listener over the grid view and hide the empty view in
		// the bgining (if the ListFragment uses a GridView)
		gridViewScheduleVehicles = (GridView) fragmentView
				.findViewById(R.id.schedule_vehicle_list_grid_view);
		if (gridViewScheduleVehicles != null) {
			gridViewScheduleVehicles.setOnItemClickListener(this);
			emptyView.setVisibility(View.GONE);
		}

		// Use custom ArrayAdapter to show the elements in the ListView
		setAdapter();

		// Set the actions over the SearchEditText
		actionsOverSearchEditText();
	}

	/**
	 * According to the current vehicle assign the appropriate adapter to the
	 * list fragment
	 */
	private void setAdapter() {
		scheduleVehicleAdapter = new ScheduleVehicleAdapter(context, emptyView,
				emptyTextView, getVehicleName(), stationsList);

		if (gridViewScheduleVehicles == null) {
			setListAdapter(scheduleVehicleAdapter);
		} else {
			gridViewScheduleVehicles.setAdapter(scheduleVehicleAdapter);
		}
	}

	/**
	 * Modify the Search EditText field and activate the listeners
	 */
	private void actionsOverSearchEditText() {
		// TODO: Find a way to set an alphanumeric keyboard with numeric as
		// default
		searchEditText.setFilters(new InputFilter[] { ActivityUtils
				.createInputFilter() });
		searchEditText.setText(searchText);

		// Add on focus listener
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ActivityUtils.hideKeyboard(context, searchEditText);
				}
			}
		});

		// Add on text changes listener
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				searchText = searchEditText.getText().toString();
				scheduleVehicleAdapter.getFilter().filter(searchText);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});

		// Add a drawable listeners (search and clear icons)
		searchEditText.setDrawableClickListener(new DrawableClickListener() {
			@Override
			public void onClick(DrawablePosition target) {
				switch (target) {
				case LEFT:
					searchEditText.requestFocus();
					searchEditText.setSelection(searchEditText.getText()
							.length());
					ActivityUtils.showKeyboard(context, searchEditText);
					break;
				case RIGHT:
					searchEditText.setText("");
					break;
				default:
					break;
				}
			}

		});
	}

	/**
	 * Get the name of the tab according to the currentVehicle
	 * 
	 * @return the name of the tab
	 */
	private String getVehicleName() {
		String activeTabName = "";

		switch (currentVehicle) {
		case 0:
			activeTabName = getString(R.string.sch_search_tab_bus);
			break;
		case 1:
			activeTabName = getString(R.string.sch_search_tab_trolley);
			break;
		default:
			activeTabName = getString(R.string.sch_search_tab_tram);
			break;
		}

		return activeTabName;
	}
}
