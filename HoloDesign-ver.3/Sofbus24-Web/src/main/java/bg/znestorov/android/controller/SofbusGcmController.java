package bg.znestorov.android.controller;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import bg.znestorov.android.jpa.Dao;
import bg.znestorov.android.model.Notification;
import bg.znestorov.android.model.NotificationWrapper;
import bg.znestorov.android.model.RegistrationError;
import bg.znestorov.android.model.RegistrationSuccess;
import bg.znestorov.android.model.WebServiceResult;
import bg.znestorov.android.utils.Constants;
import bg.znestorov.android.utils.Utils;

import com.google.gson.Gson;

@Controller
@RequestMapping(value = "/Sofbus24/gcm")
public class SofbusGcmController {

	private static final Logger log = Logger
			.getLogger(TestSofbusGcmController.class.getName());

	@RequestMapping(value = "/register", headers = "Accept=application/json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody WebServiceResult registerGcm(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "sec", required = false, defaultValue = "") String sec,
			@RequestBody(required = false) String params) {

		try {
			params = URLDecoder.decode(params, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Should not go here (UTF-8 is supported encoding)
		}

		// Get the parameters from the request body
		String regId = Utils.getRequestParamValue("regId", params);
		String deviceModel = Utils.getRequestParamValue("deviceModel", params);
		String deviceOsVersion = Utils.getRequestParamValue("deviceOsVersion",
				params);

		// Get the URL address and construct the secret value
		String urlAddress = request.getRequestURL().toString();
		String urlAddressSecretValue = Utils.getSha1Digest(urlAddress + regId
				+ deviceModel + deviceOsVersion
				+ Constants.GCM_REGISTRATION_SECRET_VALUE);

		WebServiceResult serviceResult;
		if (sec.equals(urlAddressSecretValue)) {
			boolean isSuccessfullyAdded = Dao.INSTANCE.addRegistration(regId,
					deviceModel, deviceOsVersion);

			if (isSuccessfullyAdded) {
				serviceResult = new RegistrationSuccess(regId);
			} else {
				log.warning(Constants.GCM_REGISTRATION_DUPLICATE_ERROR);
				serviceResult = new RegistrationError(
						Constants.GCM_REGISTRATION_DUPLICATE_ERROR);
			}
		} else {
			log.warning(Constants.GCM_REGISTRATION_UNAUTHORIZED_ERROR);
			serviceResult = new RegistrationError(
					Constants.GCM_REGISTRATION_UNAUTHORIZED_ERROR);
		}

		return serviceResult;
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public ModelAndView showGcmMessage() {

		ModelAndView modelView = new ModelAndView("gcm-send-message");
		modelView.addObject("notification", new Notification());
		modelView.addObject("notificationTypes", getNotificationTypes());
		modelView.addObject("sharedStatus", SharedStatus.INIT);

		return modelView;
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public ModelAndView showGcmMessage(
			@ModelAttribute("notification") Notification notification,
			ModelAndView modelView) {

		Boolean isSharedSuccessful;
		HttpURLConnection httpConnection = null;

		try {
			URL serverUrl = new URL(Constants.GCM_NOTIFICATION_URL);

			// Configure the HTTP connection and send it to the server
			httpConnection = (HttpURLConnection) serverUrl.openConnection();
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			httpConnection.setUseCaches(false);
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty(
					Constants.GCM_NOTIFICATION_URL_CONTENT_TYPE_ATT,
					Constants.GCM_NOTIFICATION_URL_CONTENT_TYPE_VALUE);
			httpConnection.setRequestProperty(
					Constants.GCM_NOTIFICATION_URL_AUTHORIZATION_ATT,
					Constants.GCM_NOTIFICATION_URL_AUTHORIZATION_VALUE);
			httpConnection.connect();

			List<String> registrationIds = Dao.INSTANCE.listRegistrationIds();
			OutputStream os = httpConnection.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			osw.write(new Gson()
					.toJson(new NotificationWrapper(notification,
							registrationIds.toArray(new String[registrationIds
									.size()]))));
			osw.flush();
			osw.close();

			// Check if the status of the HTTP request is successful
			int status = httpConnection.getResponseCode();
			if (status == 200) {
				isSharedSuccessful = true;
			} else {
				isSharedSuccessful = false;
			}

		} catch (Exception e) {
			isSharedSuccessful = false;
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}

		modelView.setViewName("gcm-send-message");
		modelView.addObject("notification", new Notification());
		modelView.addObject("notificationTypes", getNotificationTypes());
		modelView
				.addObject("sharedStatus",
						isSharedSuccessful ? SharedStatus.SUCCESS
								: SharedStatus.FAILED);

		return modelView;
	}

	/**
	 * Get the notification types
	 * 
	 * @return the notification types
	 */
	private List<String> getNotificationTypes() {

		List<String> notificationTypes = new ArrayList<String>();
		notificationTypes.add("UPDATE_APP");
		notificationTypes.add("UPDATE_DB");
		notificationTypes.add("RATE_APP");
		notificationTypes.add("INFO");

		return notificationTypes;
	}

	/**
	 * Initialize the status of the push notification
	 */
	private enum SharedStatus {
		/**
		 * Initial status (only the gcm/send page is opened)
		 */
		INIT,

		/**
		 * The notification is successfully sent
		 */
		SUCCESS,

		/**
		 * There is a problem with sending the notification
		 */
		FAILED;
	}

}
