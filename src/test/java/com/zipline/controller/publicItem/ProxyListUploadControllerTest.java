package com.zipline.controller.publicItem;

import com.zipline.global.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import service.publicitem.ProxyListUploadService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProxyListUploadControllerTest {

    @Mock
    private ProxyListUploadService proxyListUploadService;

    @InjectMocks
    private ProxyListUploadController proxyListUploadController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(proxyListUploadController).build();
    }

    @Test
    void uploadProxyList_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "192.168.1.1:8080\n10.0.0.1:3128".getBytes()
        );

        when(proxyListUploadService.saveFile(any(MultipartFile.class))).thenReturn("File uploaded successfully");

        // Act
        ResponseEntity<ApiResponse<String>> response = proxyListUploadController.uploadProxyList(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("파일 업로드 성공", response.getBody().getMessage());
        verify(proxyListUploadService, times(1)).saveFile(any(MultipartFile.class));
    }

    @Test
    void uploadProxyList_ServiceThrowsException() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "invalid content".getBytes()
        );

        when(proxyListUploadService.saveFile(any(MultipartFile.class)))
                .thenThrow(new IOException("Test exception"));

        // Act
        ResponseEntity<ApiResponse<String>> response = proxyListUploadController.uploadProxyList(file);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("파일 업로드 실패: Test exception", response.getBody().getMessage());
        verify(proxyListUploadService, times(1)).saveFile(any(MultipartFile.class));
    }

    @Test
    void uploadProxyList_MvcTest_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "192.168.1.1:8080\n10.0.0.1:3128".getBytes()
        );

        when(proxyListUploadService.saveFile(any(MultipartFile.class))).thenReturn("File uploaded successfully");

        // Act & Assert
        mockMvc.perform(multipart("/api/admin/upload-proxy-list")
                        .file(file))
                .andExpect(status().isOk());

        verify(proxyListUploadService, times(1)).saveFile(any(MultipartFile.class));
    }
}
