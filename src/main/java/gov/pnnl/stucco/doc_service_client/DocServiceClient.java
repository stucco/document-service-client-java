package gov.pnnl.stucco.doc_service_client;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Client to add and get objects to and from a document service
 */
public class DocServiceClient {

    // IP address or host name
    private String host = "localhost";
    
    // TCP port number
    private int port = 8118;

    /**
     * Default constructor 
     */
    public DocServiceClient()
    {}
    
    /**
     * Constructs client with specified connection information
     * @param host the IP address or host name
     * @param port the TCP port number
     */
    public DocServiceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    /**
     * Constructs client with specified connection information
     * Supported configuration items are "host" and "port" (both are Strings)
     * @param config options that contain information on client connection to server
     * @throws DocServiceException
     */
    public DocServiceClient(Map<String, Object> config) throws DocServiceException {
        if (config == null) {
		throw new DocServiceException("config is null");
	}

        this.host = (String) config.get("host");
        if (this.host == null) {
		throw new DocServiceException("host is null");
	}

        String portStr = (String) config.get("port");
        try {
            this.port = Integer.parseInt(portStr);
        }
        catch (NumberFormatException e) {
            throw new DocServiceException("invalid port", e);
        }
    }

    /**
     * Stores document in the document service
     * @param doc the document to store
     * @return Document ID
     * @throws DocServiceException
     */
    public String store(DocumentObject doc) throws DocServiceException {
        return store(doc, "");
    }

    /**
     * Convenience method to allow caller to store String directly.
     * @param text the text of the document to store
     * @return Document ID
     * @throws DocServiceException
     */
    public String store(String text) throws DocServiceException {
        return store(new DocumentObject(text));
    }
    
    /**
     * Convenience method to allow caller to store bytes directly and specify
     * the content-type
     * @param text the text of the document to store
     * @param contentType 
     * @return Document ID
     * @throws DocServiceException
     */
    public String store(byte[] bytes, String contentType) throws DocServiceException {
        return store(new DocumentObject(bytes, contentType));
    }
    
    /**
     * Convenience method to allow caller to store String directly and specify
     * the content-type
     * @param text the text of the document to store
     * @param contentType 
     * @return Document ID
     * @throws DocServiceException
     */
    public String store(String text, String contentType) throws DocServiceException {
        return store(new DocumentObject(text, contentType));
    }
    
    /**
     * Stores document in the document service and specifies an ID to use
     * @param doc the document to store
     * @param id the document ID
     * @return Document ID
     * @throws DocServiceException
     */
    public String store(DocumentObject doc, String id) throws DocServiceException {
        String idFromServer;
        try {
            InputStream response = HttpHelper.post(makeURL(id, false), doc.getContentType(), doc.getDataAsBytes());
            idFromServer = getId(IOUtils.toString(response));
        } catch (IOException e) {
            throw new DocServiceException("Cannot store to document server", e);
        } catch (JSONException e) {
            throw new DocServiceException("Cannot store to document server", e);
        }
        return idFromServer;
    }

    /**
     * Fetches document from the document service
     * @param id the id of document to fetch
     * @return Document
     * @throws DocServiceException
     */
    public DocumentObject fetch(String id) throws DocServiceException {
        return fetch(id, "application/octet-stream", false);
    }

    /**
     * Fetches extracted text from a document from the document service
     * @param id the id of document to fetch
     * @return Extracted text (in JSONObject) from fetched document
     * @throws DocServiceException
     * @throws JSONException
     */
    public JSONObject fetchExtractedText(String id) throws DocServiceException, JSONException {
        DocumentObject doc = fetch(id, "application/json", true);
        JSONObject json = new JSONObject(doc.getDataAsString());
        return json;
    }

    /**
     * Fetches document from the document service
     * @param id the id of document to fetch
     * @param acceptType type of data to accept from server
     * @param extractText if true, then ask the document server to the extract text
     * @return Document
     * @throws DocServiceException
     */
    public DocumentObject fetch(String id, String acceptType, boolean extractText) throws DocServiceException {
        DocumentObject doc;
        try {
            InputStream stream = HttpHelper.get(makeURL(id, extractText), acceptType);
            doc = new DocumentObject(stream);
        } catch (IOException e) {
            throw new DocServiceException("Cannot fetch from document server", e);
        }
        return doc;
    }

    /**
     * Gets document ID from server response
     * @param serverResponse
     * @return Document ID
     * @throws JSONException
     */
    private String getId(String serverResponse) throws JSONException {
        JSONObject json = new JSONObject(serverResponse);
        return json.get("key").toString();
    }
    
    /**
     * Makes a URL given a document ID
     * @param id the document ID
     * @param extractText if true, then ask the document server to the extract text
     * @return URL for the client to connect to
     * @throws MalformedURLException
     */
    private URL makeURL(String id, boolean extractText) throws MalformedURLException  {
        String urlString = "http://" + host + ":" + Integer.toString(port) + "/document";
        if (!id.isEmpty()) {
            urlString = urlString + "/" + id;
        }
        if (extractText) {
            urlString += "?extract=true";
        }
        return new URL(urlString);
    }
}   
    
