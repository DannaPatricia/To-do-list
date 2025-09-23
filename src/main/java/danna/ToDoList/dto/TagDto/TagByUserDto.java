package danna.ToDoList.dto.TagDto;

import danna.ToDoList.model.TagEntity;

public class TagByUserDto {
    private Long id;
    private String name;
    private String userName;
    
    // Constructores
    public TagByUserDto() {}

    // Constructor para pasar de entity a dto
    public TagByUserDto(TagEntity tagEntity) {
        this.id = tagEntity.getId();
        this.name = tagEntity.getName();
        this.userName = tagEntity.getUser().getUsername();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}