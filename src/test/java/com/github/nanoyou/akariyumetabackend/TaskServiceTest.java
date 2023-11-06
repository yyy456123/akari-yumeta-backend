package com.github.nanoyou.akariyumetabackend;

import com.github.nanoyou.akariyumetabackend.dao.TaskDao;
import com.github.nanoyou.akariyumetabackend.dao.TaskDynamicDao;
import com.github.nanoyou.akariyumetabackend.dao.TaskRecordDao;
import com.github.nanoyou.akariyumetabackend.entity.enumeration.TaskRecordStatus;
import com.github.nanoyou.akariyumetabackend.entity.task.Task;
import com.github.nanoyou.akariyumetabackend.entity.task.TaskDynamic;
import com.github.nanoyou.akariyumetabackend.entity.task.TaskRecord;
import com.github.nanoyou.akariyumetabackend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskDao taskDao;

    @Mock
    private TaskDynamicDao taskDynamicDao;

    @Mock
    private TaskRecordDao taskRecordDao;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTask_shouldSaveTaskAndReturnOptionalTask() {
        // Arrange
        Task task = new Task();
        task.setId("1");
        task.setTaskName("Test Task");
        task.setTaskUploaderID("user123");
        // ... set other properties

        when(taskDao.save(task)).thenReturn(task);

        // Act
        Optional<Task> result = taskService.addTask(task);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(task.getId(), result.get().getId());
        assertEquals(task.getTaskName(), result.get().getTaskName());
        assertEquals(task.getTaskUploaderID(), result.get().getTaskUploaderID());
        // ... assert other properties

        verify(taskDao, times(1)).save(task);
    }

    @Test
    void getTask_shouldReturnOptionalTask() {
        // Arrange
        String taskId = "1";
        Task task = new Task();
        task.setId(taskId);
        // ... set other properties

        when(taskDao.findById(taskId)).thenReturn(Optional.of(task));

        // Act
        Optional<Task> result = taskService.getTask(taskId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(task.getId(), result.get().getId());
        // ... assert other properties

        verify(taskDao, times(1)).findById(taskId);
    }

    // ... write tests for other methods
    @Test
    void existTask_shouldReturnTrueIfTaskExists() {
        // Arrange
        String taskId = "1";
        when(taskDao.existsById(taskId)).thenReturn(true);

        // Act
        boolean result = taskService.existTask(taskId);

        // Assert
        assertTrue(result);
        verify(taskDao, times(1)).existsById(taskId);
    }

    @Test
    void getAllTasks_shouldReturnListOfTasks() {
        // Arrange
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        tasks.add(new Task());
        when(taskDao.findAll()).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertEquals(tasks.size(), result.size());
        verify(taskDao, times(1)).findAll();
    }

    @Test
    void getMyTasks_shouldReturnListOfTasks() {
        // Arrange
        String childId = "child123";
        List<TaskRecord> taskRecords = new ArrayList<>();
        taskRecords.add(new TaskRecord());
        taskRecords.add(new TaskRecord());
        when(taskRecordDao.findByTaskRecordCombinedPrimaryKeyChildID(childId)).thenReturn(taskRecords);
        when(taskDao.findById(anyString())).thenReturn(Optional.of(new Task()));

        // Act
        List<Task> result = taskService.getMyTasks(childId);

        // Assert
        assertEquals(taskRecords.size(), result.size());
        verify(taskRecordDao, times(1)).findByTaskRecordCombinedPrimaryKeyChildID(childId);
        verify(taskDao, times(taskRecords.size())).findById(anyString());
    }

    @Test
    void addTaskDynamic_shouldSaveTaskDynamicAndReturnOptionalTaskDynamic() {
        // Arrange
        TaskDynamic taskDynamic = new TaskDynamic();
        when(taskDynamicDao.saveAndFlush(taskDynamic)).thenReturn(taskDynamic);

        // Act
        Optional<TaskDynamic> result = taskService.addTaskDynamic(taskDynamic);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(taskDynamic, result.get());
        verify(taskDynamicDao, times(1)).saveAndFlush(taskDynamic);
    }

    @Test
    void getTaskDynamicIdList_shouldReturnListOfDynamicIds() {
        // Arrange
        String taskId = "1";
        List<TaskDynamic> taskDynamics = new ArrayList<>();
        taskDynamics.add(new TaskDynamic());
        taskDynamics.add(new TaskDynamic());
        when(taskDynamicDao.findByTaskDynamicTaskID(taskId)).thenReturn(taskDynamics);

        // Act
        List<String> result = taskService.getTaskDynamicIdList(taskId);

        // Assert
        assertEquals(taskDynamics.size(), result.size());
        verify(taskDynamicDao, times(1)).findByTaskDynamicTaskID(taskId);
    }

    @Test
    void getRecord_shouldReturnOptionalTaskRecord() {
        // Arrange
        TaskRecord._TaskRecordCombinedPrimaryKey primaryKey = new TaskRecord._TaskRecordCombinedPrimaryKey();
        Optional<TaskRecord> taskRecord = Optional.of(new TaskRecord());
        when(taskRecordDao.findByTaskRecordCombinedPrimaryKey(primaryKey)).thenReturn(taskRecord);

        // Act
        Optional<TaskRecord> result = taskService.getRecord(primaryKey);

        // Assert
        assertEquals(taskRecord, result);
        verify(taskRecordDao, times(1)).findByTaskRecordCombinedPrimaryKey(primaryKey);
    }

    @Test
    void addRecord_shouldSaveTaskRecordAndReturnOptionalTaskRecord() {
        // Arrange
        TaskRecord taskRecord = new TaskRecord();
        when(taskRecordDao.save(taskRecord)).thenReturn(taskRecord);

        // Act
        Optional<TaskRecord> result = taskService.addRecord(taskRecord);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(taskRecord, result.get());
        verify(taskRecordDao, times(1)).save(taskRecord);
    }

    @Test
    void validateMyTask_shouldReturnTrueIfTaskRecordExists() {
        // Arrange
        TaskRecord._TaskRecordCombinedPrimaryKey primaryKey = new TaskRecord._TaskRecordCombinedPrimaryKey();
        when(taskRecordDao.findByTaskRecordCombinedPrimaryKey(primaryKey)).thenReturn(Optional.of(new TaskRecord()));

        // Act
        boolean result = taskService.validateMyTask(primaryKey);

        // Assert
        assertTrue(result);
        verify(taskRecordDao, times(1)).findByTaskRecordCombinedPrimaryKey(primaryKey);
    }

    @Test
    void getRecords_shouldReturnListOfTaskRecords() {
        // Arrange
        String childId = "child123";
        TaskRecordStatus status = TaskRecordStatus.COMPLETED;
        List<TaskRecord> taskRecords = new ArrayList<>();
        taskRecords.add(new TaskRecord());
        taskRecords.add(new TaskRecord());
        when(taskRecordDao.findByTaskRecordCombinedPrimaryKeyChildIDAndStatus(childId, status)).thenReturn(taskRecords);

        // Act
        List<TaskRecord> result = taskService.getRecords(childId, status);

        // Assert
        assertEquals(taskRecords.size(), result.size());
        verify(taskRecordDao, times(1)).findByTaskRecordCombinedPrimaryKeyChildIDAndStatus(childId, status);
    }

    @Test
    void getBonuses_shouldReturnSumOfBonuses() {
        // Arrange
        List<String> taskIds = Arrays.asList("1", "2", "3");
        List<Task> tasks = new ArrayList<>();
        tasks.add(Task.builder().bonus(10).build());
        tasks.add(Task.builder().bonus(20).build());
        tasks.add(Task.builder().bonus(30).build());
        when(taskDao.findById(anyString())).thenReturn(Optional.empty());
        for (int i = 0; i < taskIds.size(); i++) {
            when(taskDao.findById(taskIds.get(i))).thenReturn(Optional.of(tasks.get(i)));
        }

        // Act
        Integer result = taskService.getBonuses(taskIds);

        // Assert
        assertEquals(60, result);
        verify(taskDao, times(taskIds.size())).findById(anyString());
    }

}
