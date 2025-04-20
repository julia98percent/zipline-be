package com.zipline.controller.publicitem;

import com.zipline.global.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.zipline.service.publicItem.ProxyListUploadService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/upload-proxy-list")
public class ProxyListUploadController {

    @Autowired
    private ProxyListUploadService proxyListUploadService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> uploadProxyList(@RequestParam("file") MultipartFile file) {
            String result = proxyListUploadService.saveFile(file);
            ApiResponse<String> response = ApiResponse.ok("파일 업로드 성공", result);
            return ResponseEntity.status(HttpStatus.OK).body(response); 
    }
}

