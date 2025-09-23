package danna.ToDoList.dto.TagDto;

public class CreateTagDto {
    private String name;
    private Long taskId;

    public CreateTagDto() {}

    public CreateTagDto(String name, Long taskId) {
        this.name = name;
        this.taskId = taskId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public boolean isValid() {
        return name != null && !name.isEmpty() && taskId != null;
    }
}