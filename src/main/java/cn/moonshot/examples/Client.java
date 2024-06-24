package cn.moonshot.examples;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

enum ChatMessageRole {
    SYSTEM, USER, ASSISTANT;

    public String value() {
        return this.name().toLowerCase();
    }
}

class ChatCompletionMessage {
    private String role;
    private String content;

    @JsonCreator
    public ChatCompletionMessage(@JsonProperty("role") String role, @JsonProperty("content") String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

class ChatCompletionRequest {
    public String model;
    public List<ChatCompletionMessage> messages;
    public float temperature;
    public boolean stream;

    public ChatCompletionRequest(String model, List<ChatCompletionMessage> messages, float temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.stream = true;
    }
}

class ChatCompletionStreamChoiceDelta {
    private final String content;

    ChatCompletionStreamChoiceDelta(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

class ChatCompletionStreamChoice {
    private final ChatCompletionStreamChoiceDelta delta;

    ChatCompletionStreamChoice(ChatCompletionStreamChoiceDelta delta) {
        this.delta = delta;
    }

    public ChatCompletionStreamChoiceDelta getDelta() {
        return delta;
    }
}

class ChatCompletionStreamResponse {
    private final List<ChatCompletionStreamChoice> choices;

    ChatCompletionStreamResponse(List<ChatCompletionStreamChoice> choices) {
        this.choices = choices;
    }

    public List<ChatCompletionStreamChoice> getChoices() {
        return choices;
    }
}

public class Client {
    private static final String DEFAULT_BASE_URL = "https://api.moonshot.cn/v1";
    private static final String CHAT_COMPLETION_SUFFIX = "/chat/completions";

    private final String baseUrl;
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;

    public Client(String apiKey) {
        this(apiKey, DEFAULT_BASE_URL);
    }

    public Client(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public String getChatCompletionUrl() {
        return baseUrl + CHAT_COMPLETION_SUFFIX;
    }

    public List<String> chatCompletionStream(ChatCompletionRequest request) {
        List<String> result = new ArrayList<>();
        try {
            request.stream = true;
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    gson.toJson(request)
            );

            Request httpRequest = new Request.Builder()
                    .url(getChatCompletionUrl())
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IOException("Response body is null");
                }

                try (okio.BufferedSource source = responseBody.source()) {
                    String line;
                    while ((line = source.readUtf8Line()) != null) {
                        if (line.startsWith("data:")) {
                            line = line.substring(5).trim();
                        }
                        if (Objects.equals(line, "[DONE]")) {
                            break;
                        }
                        if (!line.isEmpty()) {
                            ChatCompletionStreamResponse streamResponse = gson.fromJson(line, ChatCompletionStreamResponse.class);
                            if (!streamResponse.getChoices().isEmpty()) {
                                ChatCompletionStreamChoiceDelta delta = streamResponse.getChoices().get(0).getDelta();
                                if (delta != null && delta.getContent() != null) {
                                    result.add(delta.getContent());
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
