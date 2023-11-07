package com.github.nanoyou.akariyumetabackend.serviceTest;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.nanoyou.akariyumetabackend.entity.enumeration.TaskCategory.ANIMAL_HUSBANDRY;
import static com.github.nanoyou.akariyumetabackend.entity.enumeration.TaskCategory.SCIENCE;
import static com.github.nanoyou.akariyumetabackend.entity.enumeration.TaskStatus.FINISHED;
import static com.github.nanoyou.akariyumetabackend.entity.enumeration.TaskStatus.IN_PROGRESS;
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
    void testGetMyTasks() {
        // 创建测试数据
        String childID = "123";

        // 创建模拟的taskRecords和tasks列表
        List<TaskRecord> taskRecords = Arrays.asList(
                TaskRecord.builder()
                        .taskRecordCombinedPrimaryKey(new TaskRecord._TaskRecordCombinedPrimaryKey("task1", childID))
                        .status(TaskRecordStatus.COMPLETED)
                        .build(),
                TaskRecord.builder()
                        .taskRecordCombinedPrimaryKey(new TaskRecord._TaskRecordCombinedPrimaryKey("task2", childID))
                        .status(TaskRecordStatus.UNCOMPLETED)
                        .build()
        );

        List<Task> tasks = Arrays.asList(
                new Task("task1", "Task 1", "uploader1", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), IN_PROGRESS, "Description 1", ANIMAL_HUSBANDRY, 100),
                new Task("task2", "Task 2", "uploader2", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), FINISHED, "Description 2", SCIENCE, 200)
        );

        // 模拟taskRecordDao的findByTaskRecordCombinedPrimaryKeyChildID()方法返回taskRecords
        when(taskRecordDao.findByTaskRecordCombinedPrimaryKeyChildID(childID)).thenReturn(taskRecords);

        // 模拟taskDao的findById()方法根据taskID返回对应的task对象
        when(taskDao.findById("task1")).thenReturn(Optional.of(tasks.get(0)));
        when(taskDao.findById("task2")).thenReturn(Optional.of(tasks.get(1)));

        // 调用getMyTasks()方法
        List<Task> result = taskService.getMyTasks(childID);

        // 验证返回的任务列表是否符合预期
        assertEquals(tasks, result);

        // 验证taskRecordDao的findByTaskRecordCombinedPrimaryKeyChildID()方法是否被调用一次，并且传入了正确的参数
        verify(taskRecordDao, times(1)).findByTaskRecordCombinedPrimaryKeyChildID(childID);

        // 验证taskDao的findById()方法是否被调用两次，并且传入了正确的参数
        verify(taskDao, times(1)).findById("task1");
        verify(taskDao, times(1)).findById("task2");
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
    void testGetTaskDynamicIdList() {
        // 创建测试数据
        String taskID = "123";

        // 创建模拟的taskDynamicList和dynamicIDList
        List<TaskDynamic> taskDynamicList = Arrays.asList(
                TaskDynamic.builder()
                        .taskDynamic(TaskDynamic._TaskDynamicCombinedPrimaryKey.builder()
                                .taskID(taskID)
                                .dynamicID("1")
                                .build())
                        .build(),
                TaskDynamic.builder()
                        .taskDynamic(TaskDynamic._TaskDynamicCombinedPrimaryKey.builder()
                                .taskID(taskID)
                                .dynamicID("2")
                                .build())
                        .build(),
                TaskDynamic.builder()
                        .taskDynamic(TaskDynamic._TaskDynamicCombinedPrimaryKey.builder()
                                .taskID(taskID)
                                .dynamicID("3")
                                .build())
                        .build()
        );

        List<String> dynamicIDList = taskDynamicList.stream()
                .map(taskDynamic -> taskDynamic.getTaskDynamic().getDynamicID())
                .collect(Collectors.toList());

        // 模拟taskDynamicDao的findByTaskDynamicTaskID()方法返回taskDynamicList
        when(taskDynamicDao.findByTaskDynamicTaskID(taskID)).thenReturn(taskDynamicList);

        // 调用getTaskDynamicIdList()方法
        List<String> result = taskService.getTaskDynamicIdList(taskID);

        // 验证返回的dynamicIDList是否符合预期
        assertEquals(dynamicIDList, result);

        // 验证taskDynamicDao的findByTaskDynamicTaskID()方法是否被调用一次，并且传入了正确的参数
        verify(taskDynamicDao, times(1)).findByTaskDynamicTaskID(taskID);
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
