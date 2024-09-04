package com.develokit.maeum_ieum.config.swagger;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
@Import(SwaggerConfig.class)
@RequiredArgsConstructor
public class LoginSwaggerConfig {

    private final SwaggerConfig swaggerConfig;

    @PostConstruct
    public void customizeOpenAPI() {
        OpenAPI openAPI = swaggerConfig.openAPI();
        if (openAPI.getPaths() == null) {
            openAPI.setPaths(new Paths());
        }
        openAPI.setPaths(openAPI.getPaths().addPathItem("/caregivers/login", createLoginPathItem()));
        defineLoginDtoSchemas(openAPI);
    }

    private void defineLoginDtoSchemas(OpenAPI openAPI) {
        Components components = openAPI.getComponents();
        if (components == null) {
            components = new Components();
            openAPI.setComponents(components);
        }
        components.addSchemas("LoginReqDto", createLoginReqDtoSchema());
        components.addSchemas("LoginRespDto", createLoginRespDtoSchema());
    }

//    protected Paths loginPath(){
//        return new Paths()
//                .addPathItem("/caregivers/login", createLoginPathItem());
//    }
    private PathItem createLoginPathItem(){
        return new PathItem()
                .post(new Operation()
                        .summary("요양사 로그인")
                        .description("username과 password를 사용해 요양사 로그인")
                        .tags(List.of("요양사 API"))
                        .requestBody(createLoginReqBody())
                        .responses(createLoginResponses())
                );
    }

    private RequestBody createLoginReqBody(){
        return new RequestBody().content(
                new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/LoginReqDto")))
        );
    }
    private Schema<?> createLoginSuccessSchema() {
        Schema<?> loginRespSchema = new Schema<>().$ref("#/components/schemas/LoginRespDto");
        return swaggerConfig.wrapSchema(loginRespSchema, "200");
    }

    private Schema<?> createLoginFailureSchema() {
        return swaggerConfig.wrapSchema(null, "401");
    }

    private ApiResponses createLoginResponses() {
        ApiResponses responses = new ApiResponses();
        responses.addApiResponse("200", createApiResponse("로그인 성공", createLoginSuccessSchema()));
        responses.addApiResponse("401", createApiResponse("로그인 실패", createLoginFailureSchema()));
        return responses;
    }

    private ApiResponse createApiResponse(String description, Schema<?> schema) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(schema)));
    }

    private Schema<?> createLoginReqDtoSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("username", new Schema<>().type("string").example("user1234"))
                .addProperty("password", new Schema<>().type("string").example("password1234"));
    }

    private Schema<?> createLoginRespDtoSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("username", new Schema<>().type("string").example("user1234"));
    }


}
