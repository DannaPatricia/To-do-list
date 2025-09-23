package danna.ToDoList.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// DTOs
import danna.ToDoList.dto.TagDto.TagByUserDto;
import danna.ToDoList.dto.TagDto.UpdateTagDto;
import danna.ToDoList.dto.TagDto.CreateTagDto;
// Modelos y repositorios
import danna.ToDoList.model.TagEntity;
import danna.ToDoList.model.TaskEntity;
import danna.ToDoList.model.UserEntity;

// repositorios
import danna.ToDoList.repository.TagRepository;
import danna.ToDoList.repository.UserRepository;
import danna.ToDoList.repository.TaskRespository;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TaskRespository taskRepository;

    public TagService(TagRepository tagRepository, UserRepository userRepository, TaskRespository taskRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    // Mostrar etiquetas del usuario
    public ResponseEntity<List<TagByUserDto>> tagsByUser(Long userId) {
        List<TagEntity> tagEntity = tagRepository.findByUserId(userId);
        if (tagEntity.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // pasar de entity a dto
        List<TagByUserDto> tagsDto = tagEntity
                .stream()
                .map(TagByUserDto::new)
                .toList();

        return ResponseEntity.ok(tagsDto);
    }

    // Crear una nueva etiqueta
    public ResponseEntity<String> createTag(CreateTagDto createTagDto, Long userId) {
        // Obtener el entity usuario
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        // Obtener la entidad tarea
        TaskEntity task = taskRepository.findById(createTagDto.getTaskId()).orElse(null);
        if (task == null) {
            return ResponseEntity.status(404).body("Tarea no encontrada");
        }

        // Crear una nueva etiqueta con valores por defecto
        TagEntity newTag = new TagEntity(createTagDto, user, task);

        // Guardar la nueva etiqueta en la base de datos
        tagRepository.save(newTag);

        return ResponseEntity.status(201).body("Etiqueta creada con exito");
    }

    // Cambiar el nombre de una etiqueta
    public ResponseEntity<String> updateTag(Long userId, UpdateTagDto updateTagDto) {
        // Buscar la etiqueta por id
        TagEntity tag = tagRepository.findById(updateTagDto.getId()).orElse(null);
        if (tag == null) {
            return ResponseEntity.status(404).body("Etiqueta no encontrada");
        }
        // Verificar que la etiqueta pertenezca al usuario
        // luego miro si se puede modificar etiquetas de otros que tengan listas
        // compartidas
        if (!tag.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("No tienes permiso para modificar esta etiqueta");
        }
        // Actualizar el nombre de la etiqueta
        tag.setName(updateTagDto.getName());
        tagRepository.save(tag);

        return ResponseEntity.ok("Etiqueta actualizada con exito");
    }

    // Eliminar una etiqueta
    public ResponseEntity<String> deleteTag(Long userId, Long tagId) {
        // Buscar la etiqueta por id
        TagEntity tag = tagRepository.findById(tagId).orElse(null);
        if (tag == null) {
            return ResponseEntity.status(404).body("Etiqueta no encontrada");
        }
        // Verificar que la etiqueta pertenezca al usuario
        if (!tag.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar esta etiqueta");
        }
        // Eliminar la etiqueta
        tagRepository.deleteById(tagId);
        return ResponseEntity.ok("Etiqueta eliminada con exito");
    }
}
