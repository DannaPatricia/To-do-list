package danna.ToDoList.dto;

public class TaskDto {
    private Long id;
    private String title;
    private String description;
    // de momento esto luego meto de que mande la list de dto de task
    
    public TaskDto() {}
    
    // Constructor con todos los campos
    public TaskDto(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}