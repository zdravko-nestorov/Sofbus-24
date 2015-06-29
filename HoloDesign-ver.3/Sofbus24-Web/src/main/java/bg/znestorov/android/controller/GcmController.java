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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import bg.znestorov.android.entity.PhoneUser;
import bg.znestorov.android.jpa.PhoneUserRegistry;
import bg.znestorov.android.pojo.Notification;
import bg.znestorov.android.pojo.NotificationStatus;
import bg.znestorov.android.pojo.RegistrationServiceResult;
import bg.znestorov.android.utils.Constants;
import bg.znestorov.android.utils.Utils;

@Controller
@RequestMapping(value = "/gcm")
public class GcmController {

	@Autowired
	private PhoneUserRegistry userRegistry;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(GcmController.class
			.getName());

	@RequestMapping(value = "/register", headers = "Accept=application/json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody RegistrationServiceResult registerGcm(
			HttpServletRequest request,
			@RequestParam(value = "sec", required = false, defaultValue = "") String sec,
			@RequestBody(required = false) String params) {

		try {
			params = params == null ? "" : params;
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

		RegistrationServiceResult serviceResult;
		if (sec.equals(urlAddressSecretValue)) {
			boolean isSuccessfullyAdded = userRegistry
					.registerPhoneUser(new PhoneUser(regId, deviceModel,
							deviceOsVersion));

			if (isSuccessfullyAdded) {
				serviceResult = new RegistrationServiceResult("0",
						Constants.GCM_REGISTRATION_SUCCESS, regId, deviceModel,
						deviceOsVersion);
			} else {
				log.warning(Constants.GCM_REGISTRATION_DUPLICATE_ERROR);
				serviceResult = new RegistrationServiceResult("1",
						Constants.GCM_REGISTRATION_DUPLICATE_ERROR);
			}
		} else {
			log.warning(Constants.GCM_REGISTRATION_UNAUTHORIZED_ERROR);
			serviceResult = new RegistrationServiceResult("2",
					Constants.GCM_REGISTRATION_UNAUTHORIZED_ERROR);
		}

		return serviceResult;
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public ModelAndView sendGcmMessage() {

		ModelAndView modelView = new ModelAndView("gcm-send-message");
		modelView.addObject("notification", new Notification());
		modelView.addObject("notificationTypes", getNotificationTypes());
		modelView.addObject("notificationStatus", NotificationStatus.INIT);

		return modelView;
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public ModelAndView sendGcmMessage(HttpServletRequest request,
			@ModelAttribute("notification") Notification notification,
			ModelAndView modelView) {

		HttpURLConnection httpConnection = null;

		Boolean isTestPushNotification = false;
		NotificationStatus notificationStatus;

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

			// Check if the user wants to send GCM only to some devices
			String[] registrationIds = notification.getRegistration_ids();
			if (registrationIds != null && !Utils.isEmpty(registrationIds[0])) {
				isTestPushNotification = true;
				notification.setRegistration_ids(registrationIds[0]);
			} else {
				notification.setRegistration_ids(userRegistry
						.findAllPhoneUserRegistrationIds());
			}

			// Check if there are any registered IDs in the datastore
			if (Utils.isEmpty(notification.getRegistration_ids())) {
				notificationStatus = NotificationStatus.FAILED_NO_IDS;
			} else {
				OutputStream os = httpConnection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
				osw.write(notification.toJson());
				osw.flush();
				osw.close();

				// Check if the status of the HTTP request is successful
				int status = httpConnection.getResponseCode();
				if (status == 200) {
					notificationStatus = NotificationStatus.SUCCESS;
				} else {
					notificationStatus = NotificationStatus.FAILED_CONNECTION;
				}
			}
		} catch (Exception e) {
			notificationStatus = NotificationStatus.FAILED_UNKNOWN;
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}

		// Check if the datastore should be updated (only if the notification is
		// sent to all users)
		if (!isTestPushNotification && isSharedSuccessful(notificationStatus)) {
			PhoneUser firstPhoneUser = userRegistry.findFirstPhoneUser();
			userRegistry.updatePhoneUser(firstPhoneUser);
		}

		modelView.setViewName("gcm-send-message");
		modelView.addObject("notification", new Notification());
		modelView.addObject("notificationData",
				getNotificationData(request, notification, notificationStatus));
		modelView.addObject("notificationTypes", getNotificationTypes());
		modelView.addObject("notificationStatus", notificationStatus);

		return modelView;
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ModelAndView registerContact(ModelMap model) {

		ModelAndView modelView = new ModelAndView("gcm-registered-users");
		modelView.addObject("phoneUsersList", userRegistry.findAllPhoneUsers());

		return modelView;
	}

	/**
	 * Check if the notification is successfully sent
	 * 
	 * @param notificationStatus
	 *            the notification status
	 * @return if the notification is successfully sent
	 */
	private boolean isSharedSuccessful(NotificationStatus notificationStatus) {

		boolean isSharedSuccessful;

		switch (notificationStatus) {
		case FAILED_CONNECTION:
		case FAILED_NO_IDS:
		case FAILED_UNKNOWN:
			isSharedSuccessful = false;
			break;
		default:
			isSharedSuccessful = true;
			break;
		}

		return isSharedSuccessful;
	}

	/**
	 * Get the notification data that will be shown as a tooltip in the JSP
	 * (notification data or error message)
	 * 
	 * @param request
	 *            the http servlet request
	 * @param notification
	 *            the notification
	 * @param notificationStatus
	 *            the status of the notification
	 * @return the notification data
	 */
	private String getNotificationData(HttpServletRequest request,
			Notification notification, NotificationStatus notificationStatus) {

		String notificationData;

		switch (notificationStatus) {
		case FAILED_CONNECTION:
			notificationData = messageSource.getMessage(
					"gcm-send-message.notif-failed-connection", null,
					request.getLocale());
			break;
		case FAILED_NO_IDS:
			notificationData = messageSource.getMessage(
					"gcm-send-message.notif-failed-no-ids", null,
					request.getLocale());
			break;
		case FAILED_UNKNOWN:
			notificationData = messageSource.getMessage(
					"gcm-send-message.notif-failed-unknown", null,
					request.getLocale());
			break;
		default:
			notificationData = notification.toTooltip();
			break;
		}

		return notificationData;
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

}