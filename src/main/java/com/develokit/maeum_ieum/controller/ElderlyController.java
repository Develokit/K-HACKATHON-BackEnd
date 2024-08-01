package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.service.ElderlyService;
import com.develokit.maeum_ieum.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/elderlys")
@RequiredArgsConstructor
public class ElderlyController {

    private final ElderlyService elderlyService;

    @GetMapping("/{assistantId}")
    public ResponseEntity<?> mainHome(@PathVariable(name = "assistantId")Long assistantId){ //db의 어시스턴트 pk
        return new ResponseEntity<>(ApiUtil.success(elderlyService.mainHome(assistantId)), HttpStatus.OK);
    }



}
