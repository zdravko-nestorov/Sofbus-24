package bg.znestorov.sofbus24.utils;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpConnections {

	/**
	 * Create a DefaultHttpClient with "ConnectionTimeout" and "SoTimeout"
	 * parameters and "Thread Safe" connection (it is not used anymore, because
	 * of strange behaviour - showing the user that the device is not connected
	 * to the Internet)
	 * 
	 * @return DefaultHttpClient
	 */
	public static DefaultHttpClient createDefaultHttpClient() {
		// Creating timeout parameters
		HttpParams httpParameters = new BasicHttpParams();

		// Set the timeout in milliseconds until a connection is
		// established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.GLOBAL_TIMEOUT_CONNECTION);

		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, Constants.GLOBAL_TIMEOUT_SOCKET);
		ConnManagerParams.setTimeout(httpParameters, Constants.GLOBAL_TIMEOUT_SOCKET);

		// Creating ThreadSafeClientConnManager
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);

		// HTTP Client - created once and using cookies
		DefaultHttpClient client = new DefaultHttpClient(cm, httpParameters);

		return client;
	}
}
