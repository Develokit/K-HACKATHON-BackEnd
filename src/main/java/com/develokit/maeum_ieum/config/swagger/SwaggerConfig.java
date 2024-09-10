package com.develokit.maeum_ieum.config.swagger;

import com.develokit.maeum_ieum.util.api.ApiError;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class SwaggerConfig{

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

    protected Schema<?> wrapSchema(Schema originalSchema, String responseCode) {
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
            apiErrorSchema.addProperty("msg", new Schema<>().type("string").example("유효성 검사 실패")); //유효성 검사 실패
            wrapperSchema.addProperty("apiError", apiErrorSchema);
        }
        else if("401".equals(responseCode)){ //토큰 기간 만료에 대한 401 에러

            wrapperSchema.addProperty("data", null);

            Schema apiErrorSchema = new Schema<>();
            apiErrorSchema.addProperty("status", new Schema<>().type("integer").example(401));
            apiErrorSchema.addProperty("msg", new Schema<>().type("string").example("토큰 기간 만료 | 로그인 실패 | Authorization 헤더 재확인 바람 | 유효하지 않은 토큰 서명" ));
            wrapperSchema.addProperty("apiError", apiErrorSchema);
        }
        else if("403".equalsIgnoreCase(responseCode)){ //해당 사용자가 접근할 수 있는 자원이 아닌 경우
            wrapperSchema.addProperty("data", null);

            Schema apiErrorSchema = new Schema<>();
            apiErrorSchema.addProperty("status", new Schema<>().type("integer").example(403));
            apiErrorSchema.addProperty("msg", new Schema<>().type("string").example("해당 사용자의 AI 어시스턴트가 아닙니다 | 코드를 다시 확인해주세요 | 해당 사용자의 관리 대상이 아닙니다" ));
            wrapperSchema.addProperty("apiError", apiErrorSchema);
        }
        else if("404".equals(responseCode)){
            wrapperSchema.addProperty("data", null);

            Schema apiErrorSchema = new Schema<>();
            apiErrorSchema.addProperty("status", new Schema<>().type("integer").example(404));
            apiErrorSchema.addProperty("msg", new Schema<>().type("string").example("등록된 AI 어시스턴트가 없습니다 | 등록되지 않은 노인 사용자입니다 | 관리하는 요양사 사용자가 존재하지 않습니다" ));
            wrapperSchema.addProperty("apiError", apiErrorSchema);
        }
        else if("500".equals(responseCode)) {
            wrapperSchema.addProperty("data", null);

            Schema apiErrorSchema = new Schema<>();
            apiErrorSchema.addProperty("status", new Schema<>().type("integer").example(500));
            apiErrorSchema.addProperty("msg", new Schema<>().type("string").example("OPENAI_SERVER_ERROR | INTERNAL_SERVER_ERROR"));
            wrapperSchema.addProperty("apiError", apiErrorSchema);
        }
        else if("409".equals(responseCode)){
            wrapperSchema.addProperty("data", null);

            Schema apiErrorSchema = new Schema<>();
            apiErrorSchema.addProperty("status", new Schema<>().type("integer").example(409));
            apiErrorSchema.addProperty("msg", new Schema<>().type("string").example("이미 존재하는 아이디입니다" ));
            wrapperSchema.addProperty("apiError", apiErrorSchema);
        }
        return wrapperSchema;
    }



    private void addPossibleValidationErrors(Schema dataSchema){
        dataSchema.addProperty("username", new Schema<>().type("string").example("영문자와 숫자를 포함하여 6자에서 12자 사이여야 합니다"));
        dataSchema.addProperty("password", new Schema<>().type("string").example("영문자와 숫자를 포함하여 6자에서 12자 사이여야 합니다"));
        //TODO aop에 걸리는 가능한 필드 오류 너무 많음 -> 다른 방안 찾아볼것
    }
    protected OpenAPI createBaseOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }


    @Bean
    public OpenAPI openAPI() {
        return createBaseOpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    public Info apiInfo(){
        return new Info()
                .title("Maeum-Ieum API Document")
                .description("기능 요청을 위한 API 명세")
                .version("1.0");
    }
}
