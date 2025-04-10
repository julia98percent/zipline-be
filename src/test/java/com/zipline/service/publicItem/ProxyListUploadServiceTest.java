package com.zipline.service.publicItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ProxyListUploadServiceTest {

    @InjectMocks
    private ProxyListUploadService proxyListUploadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Override the UPLOAD_DIR with our temp directory for testing
        ReflectionTestUtils.setField(proxyListUploadService, "UPLOAD_DIR", tempDir.toString() + "/");
    }

    @Test
    void saveFile_ValidContent_ShouldSaveFile() throws IOException {
        // Arrange
        String validContent = "192.168.1.1:8080\n10.0.0.1:3128";
        MultipartFile file = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                "text/plain",
                validContent.getBytes()
        );

        // Act
        proxyListUploadService.saveFile(file);

        // Assert
        Path savedFilePath = Paths.get(tempDir.toString(), "proxy-list.txt");
        assertTrue(Files.exists(savedFilePath));
        String savedContent = Files.readString(savedFilePath);
        assertEquals(validContent, savedContent);
    }

    @Test
    void saveFile_EmptyFile_ShouldThrowException() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                "text/plain",
                new byte[0]
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> proxyListUploadService.saveFile(emptyFile)
        );
        assertEquals("파일이 비어 있습니다", exception.getMessage());
    }

    @Test
    void saveFile_InvalidContent_ShouldThrowException() {
        // Arrange
        String invalidContent = "invalid-content\n192.168.1.1:8080";
        MultipartFile file = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                "text/plain",
                invalidContent.getBytes()
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> proxyListUploadService.saveFile(file)
        );
        assertEquals("잘못된 파일 형식입니다. 각 줄은 ip:port 형식이어야 합니다", exception.getMessage());
    }
}
package com.zipline.service.publicItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ProxyListUploadServiceTest {

    @InjectMocks
    private ProxyListUploadService proxyListUploadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Override the UPLOAD_DIR with our temp directory for testing
        ReflectionTestUtils.setField(proxyListUploadService, "UPLOAD_DIR", tempDir.toString() + "/");
    }

    @Test
    void saveFile_ValidContent_ShouldSaveFile() throws IOException {
        // Arrange
        String validContent = "192.168.1.1:8080\n10.0.0.1:3128";
        MultipartFile file = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                "text/plain",
                validContent.getBytes()
        );

        // Act
        proxyListUploadService.saveFile(file);

        // Assert
        Path savedFilePath = Paths.get(tempDir.toString(), "proxy-list.txt");
        assertTrue(Files.exists(savedFilePath));
        String savedContent = Files.readString(savedFilePath);
        assertEquals(validContent, savedContent);
    }

    @Test
    void saveFile_EmptyFile_ShouldThrowException() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                "text/plain",
                new byte[0]
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> proxyListUploadService.saveFile(emptyFile)
        );
        assertEquals("파일이 비어 있습니다", exception.getMessage());
    }

    @Test
    void saveFile_InvalidContent_ShouldThrowException() {
        // Arrange
        String invalidContent = "invalid-content\n192.168.1.1:8080";
        MultipartFile file = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                "text/plain",
                invalidContent.getBytes()
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> proxyListUploadService.saveFile(file)
        );
        assertEquals("잘못된 파일 형식입니다. 각 줄은 ip:port 형식이어야 합니다", exception.getMessage());
    }
}
package com.zipline.service.publicItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ProxyListUploadServiceTest {

    @InjectMocks
    private ProxyListUploadService proxyListUploadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Override the UPLOAD_DIR with our temp directory for testing
        ReflectionTestUtils.setField(proxyListUploadService, "UPLOAD_DIR", tempDir.toString() + "/");
    }

    @Test
    void saveFile_ValidContent_ShouldSaveFile() throws IOException {
        // Arrange
        String validContent = "192.168.1.1:8080\n10.0.0.1:3128";
        MultipartFile file = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                "text/plain",
                validContent.getBytes()
        );

        // Act
        proxyListUploadService.saveFile(file);

        // Assert
        Path savedFilePath = Paths.get(tempDir.toString(), "proxy-list.txt");
        assertTrue(Files.exists(savedFilePath));
        String savedContent = Files.readString(savedFilePath);
        assertEquals(validContent, savedContent);
    }

    @Test
    void saveFile_EmptyFile_ShouldThrowException() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                "text/plain",
                new byte[0]
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> proxyListUploadService.saveFile(emptyFile)
        );
        assertEquals("파일이 비어 있습니다", exception.getMessage());
    }

    @Test
    void saveFile_InvalidContent_ShouldThrowException() {
        // Arrange
        String invalidContent = "invalid-content\n192.168.1.1:8080";
        MultipartFile file = new MockMultipartFile(
                "file",
                "proxy-list.txt",
                "text/plain",
                invalidContent.getBytes()
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> proxyListUploadService.saveFile(file)
        );
        assertEquals("잘못된 파일 형식입니다. 각 줄은 ip:port 형식이어야 합니다", exception.getMessage());
    }
}

