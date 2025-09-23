package danna.ToDoList.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// DTos
import danna.ToDoList.dto.ListDto.ListDto;
import danna.ToDoList.dto.ListDto.GetListDto;
import danna.ToDoList.dto.ListDto.ShareListDto;

import danna.ToDoList.model.ListEntity;
import danna.ToDoList.model.UserEntity;
import danna.ToDoList.repository.ListRepository;
import danna.ToDoList.repository.UserRepository;
import danna.ToDoList.security.CustomUserDetails;

@Service
public class ListService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;

    public ListService(ListRepository listRepository, UserRepository userRepository) {
        this.listRepository = listRepository;
        this.userRepository = userRepository;
    }

    // Metodo para obtener todas las listas del usuario, incluidas las compartidas
    public ResponseEntity<List<GetListDto>> getListsByUser(Long userId) {
        // obtener todas las listas del user
        List<ListEntity> entities = listRepository.findByUserId(userId);
        // se añaden las listas compartidas a la List de listas del user
        entities.addAll(listRepository.findBySharedWith_Id(userId));

        // Se convierte las entidades a Dto, mediante su constructor, para poder
        // retornarlo
        List<GetListDto> lists = entities
                .stream()
                .map(GetListDto::new)
                .toList();

        // Se retorna el ResponseEntity adecuado en funcion si hay contenido o no
        return lists.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(lists);
    }

    // Metodo para obtener la entidad de usuario mediante el id del post (luego
    // cambiar por la session)
    public ResponseEntity<ListDto> createList(ListDto dto, Long userId) {
        // Validacion de datos no nulos
        if (dto == null || dto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Buscar el usuario por el id y obtener la entidad
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + userId));

        // setear la entidad lista
        ListEntity list = new ListEntity(dto.getName(), user);

        // guardarla en la base de datos
        list = listRepository.save(list);

        return ResponseEntity.ok(new ListDto(list));
    }

    // metodo para actualizar el nombre de una lista ( solo el nombre ya e revisa
    // antes para que no este vacio ni nulo)
    public ResponseEntity<String> updateList(Long listId, String newName, Long userId) {
        try {
            // Buscar la lista por el id y obtener la entidad
            ListEntity listToUpdate = getAndValidateList(listId, userId);
            // Actualizar el nombre de la lista
            listToUpdate.setName(newName);
            // Guardar los cambios
            listRepository.save(listToUpdate);
            return ResponseEntity.ok("Lista actualizada correctamente");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // Metodo para eliminar una lista mediate su id
    public ResponseEntity<String> deleteList(Long listId, Long userId) {
        try {
            // Buscar la lista por el id y obtener la entidad
            ListEntity listToDelete = getAndValidateList(listId, userId);
            // Eliminar la lista
            listRepository.delete(listToDelete);
            return ResponseEntity.ok("Lista eliminada correctamente");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    } 

    // Metodo para compartir una lista ya creada busncando el usuario y la lista por
    // el id
    public ResponseEntity<Void> createShareList(ShareListDto dto, Long userId) {
        try {
            // Buscar el usuario con quien se va a compartir
            UserEntity userToShare = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + dto.getUserId()));

            // Usamos el método auxiliar para validar que la lista pertenece al usuario
            ListEntity listToShare = getAndValidateList(dto.getListId(), userId);

            // Agregar el usuario a la lista de compartidos
            listToShare.getSharedWith().add(userToShare);

            // Guardar cambios -> JPA detecta la relacion modificada y actualiza la tabla
            // intermedia
            listRepository.save(listToShare);

            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            // luego canbiar por un response entity String con el mensaje
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // metodo para descompartir una lista ( no se si eso es un verbo )
    public ResponseEntity<Void> unshareList(ShareListDto dto, Long userId) {
        try {
            // Buscar el usuario al que se va a quitar de la lista
            UserEntity userToUnshare = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + dto.getUserId()));

            // Usamos el método auxiliar para traer la lista y validar propietario
            ListEntity listToUnshare = getAndValidateList(dto.getListId(), userId);

            // Remover el usuario de la lista de compartidos
            listToUnshare.getSharedWith().remove(userToUnshare);

            // Spring JPA detecta el cambio en la relación y actualiza la tabla intermedia
            listRepository.save(listToUnshare);

            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Metodo privado para validar que la lista existe y que el usuario es el dueño
    private ListEntity getAndValidateList(Long listId, Long userId) {
        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Lista no encontrada con id " + listId));
        // Validar que el usuario que accede es el dueño de la lista
        if (!list.getUser().getId().equals(userId)) {
            throw new SecurityException("No tienes permiso para esta lista");
        }
        // Enviar la lista validada para su uso
        return list;
    }

}
