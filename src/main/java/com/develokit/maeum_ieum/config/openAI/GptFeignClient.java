package com.develokit.maeum_ieum.config.openAI;

import com.develokit.maeum_ieum.config.openAI.header.FeignHeaderConfig;
import com.develokit.maeum_ieum.dto.openAi.gpt.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.gpt.RespDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import static com.develokit.maeum_ieum.dto.openAi.gpt.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.gpt.RespDto.*;

@FeignClient(name = "GptFeignClient", url = "https://api.openai.com/v1", configuration = FeignHeaderConfig.class)
public interface GptFeignClient {

    @PostMapping("/chat/completions")
    CreateGptMessageRespDto createGptMessage(CreateGptMessageReqDto createGptMessageReqDto);
}
