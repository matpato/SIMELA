package pt.isel.alsmon.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import pt.isel.alsmon.file.FileInformation;

@WebService(targetNamespace = "http://service.alsmon.isel.pt/", endpointInterface = "pt.isel.alsmon.service.UploadFile", portName = "UploadFilePort", serviceName = "UploadFileService")
@MTOM(enabled = true, threshold = 10240)
public class UploadFileImpl implements UploadFile {

	// private WebServiceContext wsContext;
	//
	// @Resource
	// public void setContext(WebServiceContext context) {
	// this.wsContext = context;
	// }
	@WebMethod
	public Boolean uploadFile(FileInformation file) {

		try {
			DataHandler handler = file.getDfile();
			//InputStream stream = handler.getDataSource().getInputStream();
			// MessageContext mContext = wsContext.getMessageContext();
			// ServletContext sContext = (ServletContext) mContext
			// .get(MessageContext.SERVLET_CONTEXT);
			
			//get uploadLocation from config
			Properties prop = new Properties();
			String configFileName = "config.properties";
			InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream(configFileName);
			prop.load(inputStream);
			inputStream.close();
			//save file
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy_MM_dd_HH-mm-ss-SSS");
			String fileName = sdf.format(new Date());
			OutputStream os = new FileOutputStream(new File(
					prop.getProperty("uploadLocation") + fileName + "." + file.getFileType()));

            handler.writeTo(os);
            os.flush();
			os.close();
			
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
