package pt.isel.alsmon.security;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;


public class ServerCallback implements CallbackHandler {


	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		
		Properties prop = new Properties();
		String configFileName = "config.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFileName);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("Config file '" + configFileName + "' not found in the classpath");
		}
 
		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];

		if (prop.getProperty("username").equals(pc.getIdentifier()))
			pc.setPassword(prop.getProperty("password"));
		else
			throw new UnsupportedCallbackException(callbacks[0],
					"Credenciais invalidas.");
	}
}
