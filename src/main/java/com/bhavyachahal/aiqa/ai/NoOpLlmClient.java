package com.bhavyachahal.aiqa.ai;

import org.springframework.stereotype.Service;

@Service
public class NoOpLlmClient
        implements LlmClient {

    @Override
    public String generate(
            String prompt) {

        return "[]";
    }
}