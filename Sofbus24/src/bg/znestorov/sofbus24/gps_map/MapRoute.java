package bg.znestorov.sofbus24.gps_map;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Handler;

import com.google.android.maps.GeoPoint;

public class MapRoute {
	private GeoPoint gpSrc = null;
	private GeoPoint gpDest = null;
	private ArrayList<GeoPoint> alRoute = new ArrayList<GeoPoint>();
	private Handler haRoute = new Handler();

	public interface RouteListener {
		public void onDetermined(ArrayList<GeoPoint> alPoint);

		public void onError();
	}

	private RouteListener oRoute = null;

	public MapRoute(GeoPoint gpSrc, GeoPoint gpDest) {
		this.gpSrc = gpSrc;
		this.gpDest = gpDest;
	}

	public void getPoints(RouteListener oRoute) {
		this.oRoute = oRoute;
		new Thread(ruFetch).start();
	}

	private Runnable ruFetchOk = new Runnable() {
		public void run() {
			oRoute.onDetermined(alRoute);
		}
	};

	private Runnable ruFetchError = new Runnable() {
		public void run() {
			oRoute.onDetermined(alRoute);
		}
	};

	private Runnable ruFetch = new Runnable() {
		public void run() {
			String szUrl = "http://maps.googleapis.com/maps/api/directions/xml";
			szUrl += "?origin=" + (gpSrc.getLatitudeE6() / 1e6) + ","
					+ (gpSrc.getLongitudeE6() / 1e6);
			szUrl += "&destination=" + (gpDest.getLatitudeE6() / 1e6) + ","
					+ (gpDest.getLongitudeE6() / 1e6);
			szUrl += "&sensor=true";

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String szXml = null;

			try {
				response = httpclient.execute(new HttpGet(szUrl));
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					szXml = out.toString();
				} else {
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (Exception e) {
				haRoute.post(ruFetchError);
			}

			try {
				XmlPullParserFactory xppfFactory = XmlPullParserFactory
						.newInstance();
				xppfFactory.setNamespaceAware(true);
				XmlPullParser xppParses = xppfFactory.newPullParser();

				xppParses.setInput(new StringReader(szXml));
				int iEventType = xppParses.getEventType();
				String szTag = "";
				String szText = "";
				boolean bStep = false;
				int iLat = 0;
				int iLong = 0;

				while (iEventType != XmlPullParser.END_DOCUMENT) {
					iEventType = xppParses.next();

					if (iEventType == XmlPullParser.START_TAG) {
						szTag = xppParses.getName();

						if (szTag.equals("step"))
							bStep = true;
					} else if (iEventType == XmlPullParser.TEXT) {
						if (szTag.equals("points"))
							szText = "";
						else
							szText = xppParses.getText().trim();
					} else if (iEventType == XmlPullParser.END_TAG) {
						if (xppParses.getName().equals("step")) {
							bStep = false;
						} else if (bStep
								&& xppParses.getName().equals("start_location")
								|| xppParses.getName().equals("end_location")) {
							GeoPoint gpPoint = new GeoPoint(iLat, iLong);
							alRoute.add(gpPoint);
						} else if (bStep && xppParses.getName().equals("lat")) {
							iLat = (int) (Double.parseDouble(szText) * 1e6);
						} else if (bStep && xppParses.getName().equals("lng")) {
							iLong = (int) (Double.parseDouble(szText) * 1e6);
						}
					}
				}
			} catch (Exception e) {
				haRoute.post(ruFetchError);
			}

			if (alRoute.size() == 0) {
				haRoute.post(ruFetchError);
			} else {
				haRoute.post(ruFetchOk);
			}
		}
	};
}
