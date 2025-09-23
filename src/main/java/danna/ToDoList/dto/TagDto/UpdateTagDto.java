package danna.ToDoList.dto.TagDto;

public class UpdateTagDto {
    private Long id;
    private String name;

    // Constructores
    public UpdateTagDto() {}
    public UpdateTagDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isValid() {
        return id != null && name != null && !name.isEmpty();
    }
}
