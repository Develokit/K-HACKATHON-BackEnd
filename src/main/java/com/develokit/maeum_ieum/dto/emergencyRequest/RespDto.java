package com.develokit.maeum_ieum.dto.emergencyRequest;

import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyType;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class RespDto {
    @NoArgsConstructor
    @Getter
    @Schema(description = "요양사에게 긴급 알림 전송 응답 DTO")
    public static class EmergencyRequestCreateRespDto{
        @Schema(description = "노인 아이디")
        private Long elderlyId;
        @Schema(description = "긴급 유형: 현재는 긴급 알림만 있음")
        private EmergencyType emergencyType;
        @Schema(description = "전송 성공 여부")
        private boolean isSent;

        @JsonProperty("isSent")
        public boolean isSent() {
            return isSent;
        }

        public EmergencyRequestCreateRespDto(EmergencyRequest emergencyRequest, boolean isSent) {
            this.elderlyId = emergencyRequest.getElderly().getId();
            this.emergencyType = emergencyRequest.getEmergencyType();
            this.isSent = isSent;
        }
    }
    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Schema(description = "요양사 알림 내역 화면 응답 DTO")
    public static class EmergencyRequestListRespDto{
        public EmergencyRequestListRespDto(Page<EmergencyRequest> emergencyRequestList, int size) {
            this.emergencyRequests =  emergencyRequestList.stream()
                    .map(EmergencyRequestRespDto::new)
                    .collect(Collectors.toList());
            this.currentPage = emergencyRequestList.getNumber();
            this.totalPages = emergencyRequestList.getTotalPages();
            this.size = this.emergencyRequests.size();
            this.previous = emergencyRequestList.hasPrevious()?new PageInfo(this.currentPage-1, size):null;
            this.next = emergencyRequestList.hasNext()?new PageInfo(this.currentPage+1, size):null;
        }
        @Schema(description = "현재 페이지 번호")
        private int currentPage;
        @Schema(description = "전체 페이지 수")
        private int totalPages;
        @Schema(description = "이전 페이지 정보")
        private PageInfo previous;

        @Schema(description = "다음 페이지 정보")
        private PageInfo next;

        @Schema(description = "현재 페이지에 반환되는 알림 내역 수")
        private int size;

        @Schema(description = "알림 내역 응답 DTO 리스트")
        private List<EmergencyRequestRespDto> emergencyRequests;
        @Getter
        @NoArgsConstructor
        @Schema(description = "페이지 정보")
        public static class PageInfo {
            @Schema(description = "페이지 번호")
            private int page;
            @Schema(description = "페이지에 반환되는 수")

            private int size;

            public PageInfo(int page, int size) {
                this.page = page;
                this.size = size;
            }
        }

        @NoArgsConstructor
        @Getter
        @Schema(description = "알림 내역 응답 DTO")
        public static class EmergencyRequestRespDto{
            public EmergencyRequestRespDto(EmergencyRequest emergencyRequest) {
                this.homeAddress = emergencyRequest.getElderly().getHomeAddress();
                this.contact = emergencyRequest.getElderly().getContact();
                this.createdDate = CustomUtil.convertToRelativeTimeString(emergencyRequest.getCreatedDate());
                this.message = emergencyRequest.getMessage();
                this.imgUrl = emergencyRequest.getElderly().getImgUrl();
            }
            @Schema(description = "노인 프로필 사진")
            private String imgUrl; //노인 사용자 이름
            @Schema(description = "노인 주거지")
            private String homeAddress; //노인 사용자 주거지
            @Schema(description = "노인 연락처")
            private String contact; //노인 사용자 연락처
            @Schema(description = "노인 알림 발생 시간: 'n일 전'으로 반환")
            private String createdDate; // 긴급 알람이 생성된 시간
            @Schema(description = "긴급 알림 메시지: ex) 홍길동 어르신으로부터 긴급 알림이 왔습니다")
            private String message;
        }
    }
}
