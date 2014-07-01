# Install
    mvn install

# Maven Dependency
After Maven installs document-service-client-java, another Maven project can use it by including a dependency:

    <dependency>
      <groupId>gov.pnnl.stucco</groupId>
      <artifactId>doc-server-client-java</artifactId>
      <version>1.0.0</version>
    </dependency>

# Quick start

    // client a client with specified host and port
    DocServiceClient client = new DocServiceClient("10.10.10.100", 8118);
    
    // store data specifying content-type
    byte[] data = ...
    String contentType = "application/pdf";
    String docId = docServiceClient.store(data, contentType);
    
    // fetch data from document service
    DocumentObject doc = docServiceClient.fetch(docId);
    
    // check the content-type
    String contentType = doc.getContentType();
    
    // get data as byte array
    byte[] data = doc.getDataAsBytes();
    
    // get data as String
    String dataStr = doc.getDataAsString();

    // get extracted text from document
    JSONObject json = docServiceClient.fetchExtractedText(docId);
    String extractedText = json.getString("text");
