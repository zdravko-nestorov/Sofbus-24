package bg.znestorov.sofbus24.info_station;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

// Creating the balloon overlay
public abstract class BalloonItemizedOverlay<Item> extends
		ItemizedOverlay<OverlayItem> {

	private MapView mapView;
	private BalloonOverlayView balloonView;
	private int viewOffset;
	final MapController mc;

	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker);
		this.mapView = mapView;
		viewOffset = 13;
		mc = mapView.getController();
	}

	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}

	@Override
	protected final boolean onTap(int index) {

		boolean isRecycled;
		GeoPoint point;
		point = createItem(index).getPoint();

		if (balloonView == null) {
			balloonView = new BalloonOverlayView(mapView.getContext(),
					viewOffset);
			isRecycled = false;
		} else {
			isRecycled = true;
		}

		balloonView.setVisibility(View.GONE);

		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}

		balloonView.setData(createItem(index));

		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;

		balloonView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			balloonView.setLayoutParams(params);
		} else {
			mapView.addView(balloonView, params);
		}

		mc.animateTo(point);

		return true;
	}

	// Sets the visibility of this overlay's balloon view to GONE
	private void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
	}

	private void hideOtherBalloons(List<Overlay> overlays) {

		for (Overlay overlay : overlays) {
			if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
			}
		}

	}

}
