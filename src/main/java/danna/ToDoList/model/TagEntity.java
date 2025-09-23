package danna.ToDoList.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import danna.ToDoList.dto.TagDto.CreateTagDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="tags")
@EntityListeners(AuditingEntityListener.class) // escucha para createdAt/updatedAt

public class TagEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @NotBlank
    private String name;
    
    // RELACIONES FORANEAS
    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity user;

    // No crea tabla intermedia, dice "Este lado no es propietario de la relación; usa la relación que está declarada en la otra entidad"
    // para ello hay que poner el nombre del atributo de la otra entidad que contiene la relación. 
    // Entidad propietario de la relacion: TaskEntity    
    @ManyToMany(mappedBy="tags")
    private List<TaskEntity> tasks;

    // Constructores
    public TagEntity(){}
    // Para crear una nueva etiqueta
    public TagEntity(CreateTagDto createTagDto, UserEntity user, TaskEntity task) {
        this.name = createTagDto.getName();
        this.user = user;
        // Solo colocar una tarea en la lista de tareas
        this.tasks = List.of(task);
    }

    // Getters y Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public UserEntity getUser() {return user;}
    public void setUser(UserEntity user) {this.user = user;}

    public List<TaskEntity> getTasks() {
        return tasks;
    }
    public void setTasks(List<TaskEntity> tasks) {
        this.tasks = tasks;
    }
}
