


public class Request {
    private final String id;
    private final String path;
    private final String method;
    private final long timestamp;
    private final Object payload;

   
    public Request(String id, String path, String method, Object payload) {
        this.id = id;
        this.path = path;
        this.method = method;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return String.format("Request{id='%s', method='%s', path='%s', timestamp=%d}",
                id, method, path, timestamp);
    }
}
