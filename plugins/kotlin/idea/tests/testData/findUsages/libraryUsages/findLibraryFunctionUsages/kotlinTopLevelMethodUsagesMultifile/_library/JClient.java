package server.usages;

import server.RequestProcessor;

class JClient {
    String s = RequestProcessor.processRequest();

    String sendRequest() {
        return RequestProcessor.processRequest();
    }
}