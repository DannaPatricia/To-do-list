package danna.ToDoList.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import danna.ToDoList.repository.ListRepository;
import danna.ToDoList.dto.ListDto;
import danna.ToDoList.model.ListEntity;
import danna.ToDoList.model.TaskEntity;
import danna.ToDoList.dto.TaskDto;

@Service
public class ListService {
    
    private final ListRepository listRepository;

    public ListService(ListRepository listRepository) {
        this.listRepository = listRepository;
    }
    
    public List<ListDto> getAllLists() {
        //obtener todas las entidades
        List<ListEntity> entities = listRepository.findAll();
        

        //Creo la lista para devolver despues 
        List<ListDto> dtos = new ArrayList<>();
        
        // Recorrer todas las entidades en un for para transformarlas en un dto
        for (ListEntity entity : entities) {
            // Para extraer solo lo que  nesecito del entity
            ListDto dto = convertToDtoList(entity);
            dtos.add(dto);
        }
        
        return dtos;
    }
    
    // Metodo separado para convertir una entidad a dto
    private ListDto convertToDtoList(ListEntity entity) {
        // Obtener el nombre del usuario
        String userName = null;
        if (entity.getUser() != null) {
            // se puede devolver el dto de user si lo nesecitamos depues
            userName = entity.getUser().getUsername();
        }

        // Crear y devolver el DTO
        return new ListDto(
            entity.getId(),
            entity.getName(),
            userName,
            covertToDtoTask(entity.getTasks())
        );
    }

    private List<TaskDto> covertToDtoTask(List<TaskEntity> entityListTask) {
    List<TaskDto> listTaskDtos = new ArrayList<>();
    
    for (TaskEntity entityTask : entityListTask) {
        // Crear DTO con constructor
        TaskDto taskDto = new TaskDto(
            entityTask.getId(),
            entityTask.getTitle(),
            entityTask.getDescription()
        );
        listTaskDtos.add(taskDto);
    }
    
    return listTaskDtos;
}
}