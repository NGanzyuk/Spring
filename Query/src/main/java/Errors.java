public class Errors {
    public static String returnError(int error, String mimeType, long length){
        if (error == 200){
            return "HTTP/1.1 "+ error +" OK\r\n" +
                    "Content-Type: " + mimeType + "\r\n" +
                    "Content-Length: " + length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
        }
        if(error == 404){
            return  "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
        }

        return "";
    }
}
