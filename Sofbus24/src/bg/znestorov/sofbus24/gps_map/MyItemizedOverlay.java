package bg.znestorov.sofbus24.gps_map;

import static bg.znestorov.sofbus24.utils.Utils.getValueAfter;
import static bg.znestorov.sofbus24.utils.Utils.getValueBefore;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import bg.znestorov.sofbus24.gps.HtmlRequestSumc;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private ArrayList<OverlayItem> m_temp_overlays = new ArrayList<OverlayItem>();
	private Context context;
	int br = 0;

	public MyItemizedOverlay(Drawable defaultMarker, MapView mapView,
			Context context) {
		super(boundCenter(defaultMarker), mapView);
		this.context = context;
	}

	public void addOverlay(OverlayItem overlay) {
		m_temp_overlays.add(overlay);
	}

	public void populateNow() {
		m_overlays.addAll(m_temp_overlays);
		m_temp_overlays.clear();
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		br++;
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index) {
		String balloonTitle = m_overlays.get(index).getTitle();
		String stationID = getValueAfter(balloonTitle, "(");
		stationID = getValueBefore(stationID, ")");

		Toast.makeText(context, balloonTitle, Toast.LENGTH_SHORT).show();

		new HtmlRequestSumc().getInformation(context, stationID, stationID,
				null);

		return true;
	}

}
