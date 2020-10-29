public class ResponseLibrary {
    public static final String status_200 = "200: OK";
    public static final String status_201 = "201: Created";
    public static final String status_400 = "400: Bad request";
    public static final String status_404 = "404: Not Found";
    public static final String status_500 = "500: Internal Error";
    public static final String status_66 = "Execute Order 66";


    //response method components
    public String date, status, http_version, content_type, body;
    public int content_length;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHttp_version() {
        return http_version;
    }

    public void setHttp_version(String http_version) {
        this.http_version = http_version;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getContent_length() {
        return content_length;
    }

    public void setContent_length(int content_length) {
        this.content_length = content_length;
    }

    //default

    public ResponseLibrary(String date, String status, String http_version, String content_type, String body, int content_length) {
        this.date = date;
        this.status = status;
        this.http_version = http_version;
        this.content_type = content_type;
        this.body = body;
        this.content_length = content_length;
    }

    @Override
    public String toString() {
        return http_version + " " + status + "\r\n" +
                ((date != null) ? "Date: " + date + "\r\n" : "") +
                "Content-Length: " + content_length + "\r\n" +
                ((content_type != null)? "Content-Type: " + content_type + "\r\n" : "") +
                "\r\n" + // end of header
                ((content_length > 0 && body != null)? body : "" );




    }


}
