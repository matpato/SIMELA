package pt.isel.alsmon.service;

import pt.isel.alsmon.file.FileInformation;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.cxf.annotations.Policies;
import org.apache.cxf.annotations.Policy;

@WebService(name = "UploadFile", targetNamespace = "http://service.alsmon.isel.pt/")
@Policies({ @Policy(uri = "SecurityPolicy.xml", placement = Policy.Placement.SERVICE) })
public interface UploadFile {
	Boolean uploadFile(@WebParam(name="file") FileInformation file);
}
