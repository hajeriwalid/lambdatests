package util;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class ConfigurationHelper {
	private final static String PROP_FILE_NAME = "config.properties";

	/* Should not need to edit these values */
	
	private final static String BASE_API_ENDPOINT       = "https://api.syncplicity.com/";
	private final static String SEARCH_API_ENDPOINT       = "https://search.syncplicity.com/";
	private final static String OAUTH_TOKEN_URL         = BASE_API_ENDPOINT + "oauth/token";
	private final static String OAUTH_REVOKE_TOKEN_URL  = BASE_API_ENDPOINT + "oauth/revoke";

	private final static String SIMPLE_PASSWORD = "123123aA";
    private final static String GROUP_NAME      = "SampleAppGroup-";

	private static java.util.Properties settings = null;

	/**
	 * If the config files hasn't been loaded, load it from disk
	 */
	private static java.util.Properties getSettings() {
		
		if (settings == null) {
			settings = new java.util.Properties();
			
			InputStream inputStream = ConfigurationHelper.class.getClassLoader().getResourceAsStream("resources/" + PROP_FILE_NAME);
			
			if (inputStream != null) {
				try {
					settings.load(inputStream);
				} 
				catch (IOException e) {
					System.err.println( "Error reading configuration file: ");
					e.printStackTrace();
				}
			}
		}
		return settings;
	}

	/**
	 * Url for retrieving the OAuth token associated with this app's current session with the api gateway.
	 */
	public static String getOAuthTokenUrl() {
		return OAUTH_TOKEN_URL;
	}

	/**
	 * URL for logging the application out of the api gateway and explicitly invalidating the OAuth token.
	 */
	public static String getOAuthRevokeTokenUrl() {
		return OAUTH_REVOKE_TOKEN_URL;
	}

	/**
	 * Returns the application key as defined by the developer portal.
	 * You as a developer must log in to the developer portal and define an application
	 * which then allocates a developer application key and secret keys.
	 */
	public static String getApplicationKey() {
		return getPropertyValueWithoutPlaceholder("appKey", "<App Key>", "");
	}

	/**
	 * Returns the application secret as defined by the developer portal.
	 * You as a developer must log in to the developer portal and define an application
	 * which then allocates a developer application key and secret keys.
	 */
	public static String getApplicationSecret() {
		return getPropertyValueWithoutPlaceholder("appSecret", "<App Secret>", "");
	}
	
	/**
	 * Returns the syncplicity admin key.
	 * The Syncplicity admin key is configured per user in the Syncplicity admin console.
	 * The key allows this application to authenticate as the token owner.
	 */
	public static String getSyncplicityAdminKey() {
		return getPropertyValueWithoutPlaceholder("syncplicityAdminToken", "<Admin Token>", "");
	}

	/**
	 * The ownerEmail should be set to the email of a Company user.
	 * This email is used to determine the Privacy Region (ROL) of the Company.
	 * Therefore, you can use the email of the admin token owner,
	 * or the email of another user in the company.
	 */
	public static String getOwnerEmail() {
		return getPropertyValueWithoutPlaceholder("ownerEmail", "<Owner Email>", "");
	}

	/**
	 * The Storage Token is needed to run the content sample against an SVA-protected Storage Vault.
	 * To obtain the token, follow the 'Setup Procedure' from https://developer.syncplicity.com/content-migration-guide
	 */
	public static String getStorageToken() {
		return getPropertyValueWithoutPlaceholder("storageToken", "<Storage Token>", "");
	}

	/**
	 * The Machine Token is needed to run the content sample against an SVA-protected Storage Vault.
	 * To obtain the token, follow the 'Setup Procedure' from https://developer.syncplicity.com/content-migration-guide
	 */
	public static String getMachineToken() {
		return getPropertyValueWithoutPlaceholder("machineToken", "<Machine Token>", "");
	}

	/**
	 * The Machine Id is needed to run the content sample against an SVA-protected Storage Vault.
	 * To obtain the token, follow the 'Setup Procedure' from https://developer.syncplicity.com/content-migration-guide
	 */
	public static String getMachineId() {
		return getPropertyValueWithoutPlaceholder("machineId", "<Machine Id>", "");
	}

	public static boolean isMachineTokenAuthenticationEnabledForStorageVaults() {
		String machineToken = getMachineToken();
		return machineToken != null && !machineToken.isEmpty();
	}

	/**
	 * The method retrieves a property value ensuring that placeholder values are removed.
	 * Placeholder values are values that we commit to the repo in config.properties file.
	 * @param key Property key
	 * @param placeholder Property value placeholder
	 * @param defaultValue Property default value
	 * @return Property value. If the config file contains the placeholder, then the default value is returned.
	 */
	private static String getPropertyValueWithoutPlaceholder(String key, String placeholder, String defaultValue) {
		String propertyValue = getSettings().getProperty(key, defaultValue);

		return propertyValue.equals(placeholder) ? defaultValue : propertyValue;
	}

	/**
	 * Default group name used for creating user groups
	 */
	public static String getGroupName() {
		return GROUP_NAME;
	}
	
	/**
	 * Returns the base url of the api gateway
	 */
	public static String getBaseApiEndpointUrl() {
		return BASE_API_ENDPOINT;
	}
	
	public static String getBaseSearchEndpointUrl() {
		return SEARCH_API_ENDPOINT;
	}

	/**
	 * Returns a simple password used for the reporting service
	 */
	public static String getSimplePassword() {
		return SIMPLE_PASSWORD;
	}

	public static void ValidateConfiguration() {
		Collection<String> errors = EvaluateConfigValidationRules();
		if(errors.isEmpty()) return;

		System.out.println("Configuration is invalid, cannot continue.");
		errors.forEach(System.out::println);

		System.exit(1);
	}

	private static Collection<String> EvaluateConfigValidationRules() {
		ArrayList<String> errors = new ArrayList<>();

		if(StringUtils.isWhitespace(getApplicationKey())) {
			errors.add("appKey is not specified");
		}

		if(StringUtils.isWhitespace(getApplicationSecret())) {
			errors.add("appSecret is not specified");
		}

		if(StringUtils.isWhitespace(getSyncplicityAdminKey())) {
			errors.add("syncplicityAdminToken is not specified");
		}

		if(StringUtils.isWhitespace(getOwnerEmail())) {
			errors.add("ownerEmail is not specified");
		}

		return errors;
	}
}