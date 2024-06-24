package cn.moonshot.examples;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final Client client;

    public ChatController(@Value("${api.key}") String apiKey) {
        this.client = new Client(apiKey);
    }

    @PostMapping("/chat")
    public List<String> chat(@RequestBody List<ChatCompletionMessage> messages) {
        logger.info("Received messages: {}", messages);

        ChatCompletionRequest request = new ChatCompletionRequest(
                "moonshot-v1-8k",
                messages,
                0.3f
        );

        List<String> responses = client.chatCompletionStream(request);
        logger.info("Sending responses: {}", responses);
        return responses;
    }
}
