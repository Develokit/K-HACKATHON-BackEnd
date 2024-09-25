package com.develokit.maeum_ieum.domain.message;

import com.develokit.maeum_ieum.domain.base.BaseEntity;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import jakarta.persistence.*;
import lombok.*;

import java.awt.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Message extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elderly_id", nullable = false)
    private Elderly elderly;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType; //USER or AI

    @Column(length = 2048)
    private String content; //메시지 내용
    @Builder
    public Message(Long id,Elderly elderly, MessageType messageType, String content) {
        this.elderly = elderly;
        this.messageType = messageType;
        this.content = content;
        this.id = id;
    }
}


