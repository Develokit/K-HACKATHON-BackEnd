package com.develokit.maeum_ieum.config.swagger;

import com.develokit.maeum_ieum.util.api.ApiError;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
public class SwaggerConfig {

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            this.addCommonResponseBody(operation);
            return operation;
        };
    }

    private void addCommonResponseBody(Operation operation) {

        operation.getResponses().forEach((responseCode, apiResonse) -> {
            Content content = apiResonse.getContent();
            if(content != null){
                content.forEach((mediaTypeKey, mediaType) -> {
                    Schema originalSchema = mediaType.getSchema();
                    Schema<?> wrappedSchema = wrapSchema(originalSchema, responseCode);
                    mediaType.setSchema(wrappedSchema);
                });
            }
        });
    }

    private Schema<?> wrapSchema(Schema originalSchema, String responseCode) {
        final Schema<?> wrapperSchema = new Schema<>();

        wrapperSchema.addProperty("success", new Schema<>().type("boolean").example("200".equals(responseCode)));
        wrapperSchema.addProperty("timeStamp", new Schema<>().type("string").format("date-time").example(
                LocalDateTime.now().toString()));

        if("200".equals(responseCode)) {
            wrapperSchema.addProperty("data", originalSchema);
            wrapperSchema.addProperty("apiError", new Schema<>().type(ApiError.class.toString()).example(null));
        } else if ("400".equals(responseCode)) {
            Schema dataSchema = new Schema<>();

            //validationAdvice에서 처리하는 모든 가능한 필드 오류를 dataSchema에 담아내야 함 -> TODO 개선 방안 찾아볼것
            addPossibleValidationErrors(dataSchema);
            wrapperSchema.addProperty("data", dataSchema);


            Schema apiErrorSchema = new Schema<>();
            apiErrorSchema.addProperty("status", new Schema<>().type("integer").example(400));
            apiErrorSchema.addProperty("msg", new Schema<>().type("string").example("유효성 검사 실패"));
            wrapperSchema.addProperty("apiError", apiErrorSchema);

        }
        return wrapperSchema;
    }

    private void addPossibleValidationErrors(Schema dataSchema){
        dataSchema.addProperty("username", new Schema<>().type("string").example("영문자와 숫자를 포함하여 6자에서 12자 사이여야 합니다"));
        dataSchema.addProperty("password", new Schema<>().type("string").example("영문자와 숫자를 포함하여 6자에서 12자 사이여야 합니다"));
        //TODO aop에 걸리는 가능한 필드 오류 너무 많음 -> 다른 방안 찾아볼것
    }
    @Bean
    public OpenAPI openAPI(){
//        String jwt = "JWT";
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
//        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
//                .name(jwt)
//                .type(SecurityScheme.Type.HTTP)
//                .scheme("bearer")
//                .bearerFormat("JWT")
//        );
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }
    public Info apiInfo(){
        return new Info()
                .title("Maeum-Ieum API Document")
                .description("기능 요청을 위한 API 명세")
                .version("1.0");
    }


}
