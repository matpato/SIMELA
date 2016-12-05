package pt.isel.alsmon.webclient;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FilenameUtils;

import pt.isel.alsmon.service.UploadFile;
import pt.isel.alsmon.service.UploadFileService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.MTOMFeature;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.isel.alsmon.service.FileInformation;

import javax.activation.DataHandler;

import java.util.List;

public class UploadFileWebClient extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static QName SERVICE_NAME;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		SERVICE_NAME = new QName("http://service.alsmon.isel.pt/","UploadFileService");
		URL wsdlURL = UploadFileService.WSDL_LOCATION;
		UploadFileService ss = new UploadFileService(wsdlURL, SERVICE_NAME);
		UploadFile port = ss.getUploadFile(new MTOMFeature(10240));
		Map<String, Object> ctx = ((BindingProvider) port).getRequestContext();
		ctx.put("ws-security.username", "alsmon");
		ctx.put("ws-security.password", "Alsmisel2015");
		FileInformation _uploadFile = new FileInformation();

		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);
				if (!multiparts.isEmpty()) {
					for (FileItem item : multiparts) {
						if (!item.isFormField()) {
							_uploadFile.setName(item.getName());
							_uploadFile.setFileType(FilenameUtils.getExtension(item.getName()));
							_uploadFile.setDfile(new DataHandler(item.get(),
									"application/octet-stream"));
							try {
								port.uploadFile(_uploadFile);
							} catch (Exception e) {
								request.setAttribute("message", "File Upload Failed due to "+ e);
							}
						}
					}

				}

			} catch (Exception ex) {
				request.setAttribute("message", "File Upload Failed due to "+ ex);
			}
			
			
		}
	}
}
