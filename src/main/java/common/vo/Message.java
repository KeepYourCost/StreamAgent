package common.vo;

public record Message(
        int statusCode,
        String message,
        Object data
) {
    private static String DEFAULT_RESPONSE = "Request processed successfully";
    public static Message of(
            int statusCode,
            String message
    ) {
        return new Message(
                statusCode,
                message,
                DEFAULT_RESPONSE
        );
    }

    public static Message of(
            int statusCode,
            String message,
            Object data
    ) {
        return new Message(
                statusCode,
                message,
                data
        );
    }

}
