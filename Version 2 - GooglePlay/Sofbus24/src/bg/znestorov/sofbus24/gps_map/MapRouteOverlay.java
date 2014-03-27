package bg.znestorov.sofbus24.gps_map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import bg.znestorov.sofbus24.utils.Constants;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MapRouteOverlay extends Overlay {

	private GeoPoint geoPoint1;
	private GeoPoint geoPoint2;

	private int mode = 0;
	private int defaultColor;

	public MapRouteOverlay(GeoPoint geoPoint1, GeoPoint geoPoint2, int mode) {
		this.geoPoint1 = geoPoint1;
		this.geoPoint2 = geoPoint2;
		this.mode = mode;

		// Set the default color to NONE
		this.defaultColor = 999;
	}

	public MapRouteOverlay(GeoPoint geoPoint1, GeoPoint geoPoint2, int mode,
			int defaultColor) {
		this.geoPoint1 = geoPoint1;
		this.geoPoint2 = geoPoint2;
		this.mode = mode;
		this.defaultColor = defaultColor;
	}

	public int getMode() {
		return mode;
	}

	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		Projection projection = mapView.getProjection();

		// Check if there is any shadow
		if (shadow == false) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setDither(true);
			Point point = new Point();
			projection.toPixels(geoPoint1, point);

			// Check the mode of the route
			if (mode == 2) {
				if (defaultColor == 999) {
					paint.setColor(Constants.ROUTE_DEFAULT_COLOR);
				} else {
					paint.setColor(defaultColor);
					Point point2 = new Point();
					projection.toPixels(geoPoint2, point2);
					paint.setStrokeWidth(Constants.ROUTE_STROKE_WIDTH);
					paint.setAlpha(Constants.ROUTE_ALPHA);
					canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
				}
			}
		}

		return super.draw(canvas, mapView, shadow, when);
	}
}
