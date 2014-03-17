package gov.pnnl.stucco.doc_service_client;

public class DocServiceException extends Exception {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 6931699502909335146L;

    public DocServiceException(String message) {
        super(message);
    }
    
    public DocServiceException(Throwable cause) {
        super(cause);
    }
    
    public DocServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
