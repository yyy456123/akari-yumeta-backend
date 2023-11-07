package com.github.nanoyou.akariyumetabackend.serviceTest;

import com.github.nanoyou.akariyumetabackend.dao.FileItemDao;
import com.github.nanoyou.akariyumetabackend.entity.filestore.FileItem;
import com.github.nanoyou.akariyumetabackend.service.FileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @MockBean
    private FileItemDao fileItemDao;

    @Test
    public void testGetFile() {
        // 给定
        FileItem fileItem = new FileItem();
        fileItem.setId("testId");
        given(fileItemDao.findById("testId")).willReturn(Optional.of(fileItem));
        // 当
        Optional<FileItem> returnedFileItem = fileService.getFile("testId");
        // 则
        assertThat(returnedFileItem).isNotEmpty();
        assertThat(returnedFileItem.get().getId()).isEqualTo("testId");
    }

    @Test
    public void testUpload() {
        // 给定
        FileItem fileItem = new FileItem();
        fileItem.setId("testId");
        given(fileItemDao.saveAndFlush(any(FileItem.class))).willReturn(fileItem);
        // 当
        Optional<FileItem> returnedFileItem = fileService.upload(fileItem);
        // 则
        assertThat(returnedFileItem).isNotEmpty();
        assertThat(returnedFileItem.get().getId()).isEqualTo("testId");
        verify(fileItemDao, times(1)).saveAndFlush(fileItem);
    }
}
